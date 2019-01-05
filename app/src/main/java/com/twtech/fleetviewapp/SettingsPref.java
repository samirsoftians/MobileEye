package com.twtech.fleetviewapp;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.MenuItem;

import java.util.HashMap;

/**
 * Created by Deepali Shinde on 15/4/18.
 */

public class SettingsPref extends AppCompatPrefernceActivity {
    private static final String TAG = SettingsPref.class.getSimpleName();
    SessionManager sf;
    String uName,uEmail,uPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            // load settings fragment
            getFragmentManager().beginTransaction().replace(android.R.id.content, new MainPreferenceFragment()).commit();

        sf = new SessionManager(SettingsPref.this);

        HashMap<String, String> user = sf.getUserDetails();
        uName = user.get(SessionManager.KEY_NAME);
        uEmail = user.get(SessionManager.KEY_EMAIL);
        uPhone = user.get(SessionManager.KEY_PHONE);

        }

        public static class MainPreferenceFragment extends PreferenceFragment {
            @Override
            public void onCreate(final Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                addPreferencesFromResource(R.xml.pref_main);

                // gallery EditText change listener
               /* bindPreferenceSummaryToValue(findPreference(getString(R.string.key_gallery_name)));

                // notification preference change listener
                bindPreferenceSummaryToValue(findPreference(getString(R.string.key_notifications_new_message_ringtone)));

                // feedback preference click listener
                Preference myPref = findPreference(getString(R.string.key_send_feedback));
                myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    public boolean onPreferenceClick(Preference preference) {
                       // sendFeedback(SettingsPref.this);
                        return true;
                    }
                });*/
            }
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            if (item.getItemId() == android.R.id.home) {
                onBackPressed();
            }
            return super.onOptionsItemSelected(item);
        }

    private static void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String stringValue = newValue.toString();

            if (preference instanceof ListPreference) {
                // For list preferenc.es, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else if (preference instanceof EditTextPreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (preference.getKey().equals("AddTocompany")) {
                    // Empty values correspond to 'silent' (no ringtone)
                    preference.setSummary(stringValue);

                } else  {
                    preference.setSummary(stringValue);
                }

            }

            else if (preference instanceof EditTextPreference) {
                if (preference.getKey().equals("key_gallery_name")) {
                    // update the changed gallery name to summary filed
                    preference.setSummary(stringValue);
                }
            } else {
                preference.setSummary(stringValue);
            }
            return true;
        }
    };


}


