package com.jeffreymor.googlehosts;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by Mor on 2017/6/10.
 */

public class PreferencesTool {
    public static final String PREF_AUTO_UPDATE_INTERVAL = "autoUpdateInterval";

    public static void setAutoUpdateInterval(Context context, String interval) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_AUTO_UPDATE_INTERVAL, interval)
                .apply();
    }

    public static int getAutoUpdateInterval(Context context) {

        int interval = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_AUTO_UPDATE_INTERVAL, "0"));  //注意：因为listpreference以String键值对存储，必须转换
        return interval;
    }
}
