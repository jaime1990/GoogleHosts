package com.jeffreymor.googlehosts;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.jeffreymor.googlehosts.util.CheckUtil;
import com.jeffreymor.googlehosts.util.DownloadUtil;
import com.stericson.RootShell.RootShell;
import com.stericson.RootTools.RootTools;

import java.io.File;
import java.io.IOException;

public class HostsService extends IntentService {
    private static final String TAG = "HostsService";
    private static final long UPDATE_BASE_INTERVAL = 60 * 60 * 1000;
    private final int mRootMethod = CheckUtil.checkRootMethod();



    public static Intent newIntent(Context context) {
        return new Intent(context, HostsService.class);
    }

    public HostsService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable final Intent intent) {
        if (mRootMethod == MyConstants.ROOT_MAGISK_HOSTS_OFF) {
            Log.d(TAG, "onHandleIntent: systemless hosts does not exists");
            Notification notification = new NotificationCompat.Builder(HostsService.this)
                    .setSmallIcon(R.drawable.ic_no_systemless_hosts)
                    .setTicker("Check hosts file update success!")
                    .setContentTitle("Update hosts file failed!")
                    .setContentText("Please enable magisk systemless hosts.")
                    .build();
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(HostsService.this);
            notificationManager.notify(0, notification);
            return;
        }
        Log.i(TAG, "Service starts");
        DownloadUtil.downloadHostFile(getApplicationContext(), new DownloadUtil.DownloadListener() {
            @Override
            public void success(final File file) {
                Log.e(TAG, "success: Thread = " + Thread.currentThread());
                // 需要host
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        boolean isUpToDate = false;
                        String remoteUpdateTime = null;
                        String lastUpdatedTime = null;
                        try {
                            remoteUpdateTime = CheckUtil.readLineOfUpdateTime(file); //读取下载文件第三行检查远程hosts更新时间
                            Log.d(TAG, "run: remote update time: " + remoteUpdateTime);
                            File localHosts = new File(MyConstants.SYSTEM_HOST_FILE_PATH_NORMAL);
                            lastUpdatedTime = CheckUtil.readLineOfUpdateTime(localHosts); //读取本地文件检查更新时间, 如果为默认文件返回null
                            isUpToDate = remoteUpdateTime.equals(lastUpdatedTime);
                            Log.d(TAG, "run: " + lastUpdatedTime);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (isUpToDate) {
                            Log.d(TAG, "run: " + "Hosts file is already up to date.");
                            Notification notification = new NotificationCompat.Builder(HostsService.this)
                                    .setSmallIcon(R.drawable.ic_update_success)
                                    .setTicker("Check hosts file update success!")
                                    .setContentTitle("No newer hosts file found!")
                                    .setContentText("Hosts file is already up to date.")
                                    .setSubText(remoteUpdateTime)
//                                    .setStyle(new NotificationCompat.BigTextStyle().bigText(time))
                                    .setAutoCancel(true)
                                    .build();
                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(HostsService.this);
                            notificationManager.notify(0, notification);
                            return;
                        }


                        try {
                            //拿到root权限
                            RootShell.getShell(true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            //拷贝文件
                            if (mRootMethod == MyConstants.ROOT_NORMAL) {
                                RootTools.copyFile(file.getAbsolutePath(), MyConstants.SYSTEM_HOST_FILE_PATH_NORMAL, true, false);
                            } else if (mRootMethod == MyConstants.ROOT_MAGISK) {
                                RootTools.copyFile(file.getAbsolutePath(), MyConstants.SYSTEM_HOST_FILE_PATH_MAGISK, true, false);
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Intent i = new Intent(getApplicationContext(), DialogActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 1, i, 0);

                        Notification notification = new NotificationCompat.Builder(HostsService.this)
                                .setSmallIcon(R.drawable.ic_new_hosts)
                                .setTicker("Check hosts file update success!")
                                .setContentTitle("New hosts found!")
                                .setContentText("Hosts has been updated! Reboot to enable the new file.")
                                .setSubText(remoteUpdateTime)
                                .setAutoCancel(true)
                                .addAction(R.drawable.ic_update_success, "REBOOT", pi)
                                .build();

                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(HostsService.this);
                        notificationManager.notify(0, notification);

                    }
                }).start();
            }

            @Override
            public void error() {
            }
        });
    }

    public static void setAutoUpdateHosts(Context context, int hour) {
        Intent i = HostsService.newIntent(context);
        PendingIntent pi = PendingIntent.getService(context, 0, i, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        if (hour != 0) {
            Log.d(TAG, "setAutoUpdateHosts: set auto interval: " + hour + " hours");
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), UPDATE_BASE_INTERVAL * hour, pi);
        } else {
            Log.d(TAG, "setAutoUpdateHosts: set auto off");
            alarmManager.cancel(pi);
            pi.cancel();
        }

    }

    public static boolean isAutoOn(Context context) {
        Intent i = HostsService.newIntent(context);
        PendingIntent pi = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_NO_CREATE);
        return pi != null;
    }
}