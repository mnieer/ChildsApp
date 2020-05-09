package com.childs.childsapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import com.childs.operations.GeneralFunctions;
import com.childs.operations.LocaleManager;
import com.childs.session.SessionManager;

public class SettingsPreferencesActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }

    public static class MyPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener

    {
        public static final String LANG_KEY = "language";

        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_main);
        }


        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {


            //IT NEVER GETS IN HERE!
            if (key.equals(LANG_KEY))
            {
                // Set summary to be the user-description for the selected value
                ListPreference pref = (ListPreference) findPreference(LANG_KEY);
                String langPref = pref.getValue();
                SessionManager sessionManager = new SessionManager(getActivity());
                sessionManager.setStringValue("app_lang",langPref);

                Intent intent = new Intent(getActivity(),SpashUI.class);
                startActivity(intent);

            }
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        }

        @Override
        public void onPause() {
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
            super.onPause();
        }


    }

    @Override
    protected void attachBaseContext(Context base) {
        SessionManager sessionManager = new SessionManager(base);
        super.attachBaseContext(LocaleManager.setLocale(base,sessionManager.getStringValue("app_lang")));
    }


}