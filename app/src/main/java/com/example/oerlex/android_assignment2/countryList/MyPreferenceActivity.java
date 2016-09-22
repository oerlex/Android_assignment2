package com.example.oerlex.android_assignment2.countryList;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import com.example.oerlex.android_assignment2.R;

import java.util.List;

public class MyPreferenceActivity extends PreferenceActivity {

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }
    @Override
    protected boolean isValidFragment (String fragmentName) {
        return (MyPreferenceFragment.class.getName().equals(fragmentName));
    }

    public static class MyPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_screen);
        }
    }

}