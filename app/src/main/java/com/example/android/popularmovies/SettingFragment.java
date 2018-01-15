package com.example.android.popularmovies;
import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

/**
 * Created by sonny on 1/14/18.
 */

public class SettingFragment extends PreferenceFragmentCompat{


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
    }
}
