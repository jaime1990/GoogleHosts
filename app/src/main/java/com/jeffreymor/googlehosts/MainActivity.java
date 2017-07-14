package com.jeffreymor.googlehosts;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jeffreymor.googlehosts.util.CheckUtil;
import com.jeffreymor.googlehosts.util.CloseUtil;
import com.jeffreymor.googlehosts.util.DownloadUtil;
import com.stericson.RootShell.RootShell;
import com.stericson.RootTools.RootTools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    Button mProxyHostBtn, mResetHostBtn, mReadHostBtn;
    Button mTaskButton;
    TextView mTipsView;
    TextView mTimeTextView;

    ProgressDialog mProgressDialog;
    Handler mHandler = new Handler();

    private boolean isMobileRoot = false;
    private int mRootMethod = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTimeTextView = (TextView) findViewById(R.id.last_update_time);
        mProxyHostBtn = (Button) findViewById(R.id.proxy_btn);
        mResetHostBtn = (Button) findViewById(R.id.clean_host);
        mReadHostBtn = (Button) findViewById(R.id.read_host);
        mTipsView = (TextView) findViewById(R.id.tips);
        mTaskButton = (Button) findViewById(R.id.task_button);


        setLastUpdateTime();

        mTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = HostsService.newIntent(getContext());
                startService(i);
            }
        });


        mProxyHostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (!isMobileRoot) {
//                    Toast.makeText(getContext(), R.string.get_root_fail, Toast.LENGTH_SHORT).show();
//                    return;
//                }
                showProgressDialog();
                mRootMethod = CheckUtil.checkRootMethod();
                if (mRootMethod == MyConstants.ROOT_MAGISK_HOSTS_OFF) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), R.string.systemless_hosts_not_exists, Toast.LENGTH_SHORT).show();
                            dismissDialog();
                        }
                    });
                    return;
                }

                DownloadUtil.downloadHostFile(MainActivity.this, new DownloadUtil.DownloadListener() {
                    @Override
                    public void success(final File file) {
                        Log.e(TAG, "success: Thread = " + Thread.currentThread());
                        // 需要host
                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    RootShell.getShell(true);
                                    if (mRootMethod == MyConstants.ROOT_NORMAL) {
                                        RootTools.copyFile(file.getAbsolutePath(), MyConstants.SYSTEM_HOST_FILE_PATH_NORMAL, true, false);
                                    } else {
                                        RootTools.copyFile(file.getAbsolutePath(), MyConstants.SYSTEM_HOST_FILE_PATH_MAGISK, true, false);
                                    }
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getContext(), R.string.get_last_hosts_tips, Toast.LENGTH_SHORT).show();
                                            dismissDialog();
                                            setLastUpdateTime();
                                        }
                                    });
                                } catch (Exception e) {
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            dismissDialog();
                                            Toast.makeText(getContext(), R.string.get_root_fail, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                    @Override
                    public void error() {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                                dismissDialog();
                            }
                        });
                    }
                });
            }
        });

        mResetHostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog();
                mRootMethod = CheckUtil.checkRootMethod();
                if (mRootMethod == 3) {
                    Toast.makeText(getContext(), R.string.systemless_hosts_not_exists, Toast.LENGTH_SHORT).show();
                    dismissDialog();
                    return;
                }
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            RootShell.getShell(true);
                            RootTools.copyFile(getVoidHostPath(), "/system/etc/hosts", true, false);
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getContext(), R.string.reset_host_tips, Toast.LENGTH_SHORT).show();
                                    dismissDialog();
                                    setLastUpdateTime();
                                }
                            });
                        } catch (Exception e) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getContext(), R.string.get_root_fail, Toast.LENGTH_SHORT).show();
                                    dismissDialog();
                                }
                            });
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        mReadHostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), HostsDetailActivity.class);
                startActivity(intent);
            }
        });

        initSubTitle();
        initVoidHost();
        checkMobileIsRoot();


    }

    private void initSubTitle() {
        if (HostsService.isAutoOn(getContext())) {
            getSupportActionBar().setSubtitle(getString(R.string.auto_update_subtitle, "On"));
            ;
        } else {
            getSupportActionBar().setSubtitle(getString(R.string.auto_update_subtitle, "Off"));
            PreferencesTool.setAutoUpdateInterval(this, "0"); //特殊原因导致设置为有自动更新但是没有alarm任务存在，重置更新为关闭状态

        }

    }

    private void setLastUpdateTime() {
        File hostsFile = new File(MyConstants.SYSTEM_HOST_FILE_PATH_NORMAL);
        String time = null;
        try {
            time = CheckUtil.readLineOfUpdateTime(hostsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (time == null) {
            mTimeTextView.setText(getString(R.string.no_update_time));
        } else {
            mTimeTextView.setText(time);
        }
    }

    private void checkMobileIsRoot() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                isMobileRoot = RootShell.isRootAvailable();
                if (!isMobileRoot) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mTipsView.setText(getString(R.string.your_mobile_have_no_root));
                            mProxyHostBtn.setEnabled(false);
                            mResetHostBtn.setEnabled(false);
                        }
                    });
                }
            }
        });
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getContext());
        }
        mProgressDialog.setTitle("");
        mProgressDialog.setMessage(getString(R.string.loading));
        mProgressDialog.show();
    }

    private void dismissDialog() {
        if (mProgressDialog == null) {
            return;
        }
        mProgressDialog.dismiss();
    }


    public String getRealFileDirPath() {
        File dir = getFilesDir();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir.getAbsolutePath();
    }

    private String getVoidHostPath() {
        return getRealFileDirPath() + File.separator + MyConstants.VOID_HOST_NAME;
    }

    private Context getContext() {
        return this;
    }

    private void initVoidHost() {
        File voidHostFile = new File(getVoidHostPath());
        if (voidHostFile.exists()) {
            return;
        }
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                File file = new File(getRealFileDirPath() + File.separator + MyConstants.VOID_HOST_NAME);
                FileWriter fileWriter = null;
                try {
                    fileWriter = new FileWriter(file);
                    fileWriter.write(MyConstants.VOID_HOST_VALUE);
                    fileWriter.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    CloseUtil.close(fileWriter);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        initSubTitle();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));

                break;

        }


        return super.onOptionsItemSelected(item);
    }


}
