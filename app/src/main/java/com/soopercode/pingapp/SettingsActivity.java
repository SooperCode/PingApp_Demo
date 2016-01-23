package com.soopercode.pingapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.soopercode.pingapp.background.BackgroundPingManager;

/**
 * Represents the Settings-Screen of this App.
 * Holds the {@code PreferenceFragment} that contains the settings.
 *
 * @author Ria
 */

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_settings);
        toolbar.showOverflowMenu();
        setSupportActionBar(toolbar);
    }

    /**
     * {@code Fragment} containing the content of this Settings Screen.
     *
     * @author Ria
     */
    public static class PrefsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

        private final static String TAG = PrefsFragment.class.getSimpleName();

        private Context context;

        /**
         * Initializes this {@code Fragment}, adding the {@code Preference} widgets.
         *
         * @param savedInstanceState If the activity is being re-initialized after
         *                           previously being shut down then this Bundle contains the data it most
         *                           recently supplied in {@link #onSaveInstanceState}.
         *                           Otherwise it is null. [SDK quote]
         */
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            context = getActivity();
            addPreferencesFromResource(R.xml.preferences);
        }

        /**
         * Called every time the user navigates back to the Settings Screen.
         * Sets up specific {@code Preference} widgets according to the
         * current configurations.
         */
        @Override
        public void onResume() {
            super.onResume();
            final SwitchPreference switchPref = (SwitchPreference) findPreference("switch_watchlist");

            //if ping list is empty -> disable bg-ping-switch
            if (PrefsManager.isPingListEmpty(context)) {
                switchPref.setChecked(false);
                switchPref.setEnabled(false);
                Log.d(TAG, "set switch disabled.");
            } else {
                boolean bgPingingIsOn = PrefsManager.isBgPingingActive(context);
                switchPref.setChecked(bgPingingIsOn);
                Log.d(TAG, "bgpinging_active is: " + (bgPingingIsOn ? "on" : "off"));
                switchPref.setOnPreferenceChangeListener(this);
            }
            findPreference("listprefs_intervals").setOnPreferenceChangeListener(this);
        }

        /**
         * Called when a Preference has been changed by the user,
         * before the state of the Preference has been updated.
         *
         * @param preference The changed Preference
         * @param newValue   The new value of the Preference
         * @return true to update the state of the Preference with the new value
         */
        @Override
        public boolean onPreferenceChange(final Preference preference, final Object newValue) {
            Log.d(TAG, "onPreferenceChange()");
            // prepare message for BGPManager
            final Intent intent = new Intent(context, BackgroundPingManager.class);
            int msg = BackgroundPingManager.MESSAGE_EMPTY;

            // check which preference has changed:
            if (preference instanceof SwitchPreference) {
                // if it was watchlist switch, send BGPManager message about it
                final String toastText;
                if((Boolean)newValue){
                    msg = BackgroundPingManager.MESSAGE_ACTIVATE;
                    toastText = getString(R.string.pinging_activated);
                } else {
                    msg = BackgroundPingManager.MESSAGE_DEACTIVATE;
                    toastText = getString(R.string.pinging_deactivated);
                }
                Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();

            } else if (preference instanceof ListPreference) {
                // if ping interval has changed, update summary
                final ListPreference pref = (ListPreference) preference;
                final int index = pref.findIndexOfValue(newValue.toString());
                pref.setSummary(String.format(
                        getString(R.string.pref_interval_summary),
                        pref.getEntries()[index].toString()));
            }
            // notify BGPManager
            intent.putExtra(BackgroundPingManager.EXTRA_PING_SWITCH, msg);
            context.sendBroadcast(intent);
            return true;
        }
    }


        /* removing the help pages for 2.0
     leave code commented out in case we build them in again soon... */

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_settings, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.settingsmenu_help:
//                startActivity(new Intent(this, HelpActivity.class));
//                return true;
//
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }
}
