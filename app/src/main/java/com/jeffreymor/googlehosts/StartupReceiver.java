package com.jeffreymor.googlehosts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Mo on 2016/10/15/015.
 */

public class StartupReceiver extends BroadcastReceiver {

    private static final String TAG = "StartupReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Received broadcast intent: " + intent.getAction());

        int interval = PreferencesTool.getAutoUpdateInterval(context);
        HostsService.setAutoUpdateHosts(context, interval);

    }
}
