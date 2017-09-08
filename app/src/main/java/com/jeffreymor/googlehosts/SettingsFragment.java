package com.jeffreymor.googlehosts;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.stericson.RootShell.RootShell;

import static com.jeffreymor.googlehosts.util.PreferencesTool.PREF_AUTO_UPDATE_INTERVAL;

/**
 * Created by Mor on 2017/7/10.
 */

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceChangeListener {
    private ListPreference mListPreference;
    private Context mContext;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        boolean isMobileRoot = RootShell.isRootAvailable();

        addPreferencesFromResource(R.xml.settings);

        mListPreference = (ListPreference) findPreference(PREF_AUTO_UPDATE_INTERVAL);

        if (!isMobileRoot) {
            mListPreference.setEnabled(false);
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        setSummary(sharedPreferences.getString(PREF_AUTO_UPDATE_INTERVAL, "关闭"));
        mListPreference.setOnPreferenceChangeListener(this);


    }

    private void setSummary(String value) {
        CharSequence[] chars = mListPreference.getEntries();
        int index = mListPreference.findIndexOfValue(value);
        mListPreference.setSummary(chars[index]);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {

        setSummary((String) newValue);
        return true;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        int hour = Integer.valueOf(mListPreference.getValue());
        HostsService.setAutoUpdateHosts(mContext, hour);
    }
}
