package com.soopercode.pingapp;

import android.app.Activity;
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
import android.view.Menu;
import android.view.MenuItem;

import com.soopercode.pingapp.background.BackgroundPingManager;
import com.soopercode.pingapp.help.HelpActivity;

/**
 * Represents the Settings-Screen of this App.
 * Holds the {@code PreferenceFragment} that contains the settings.
 *
 * @author  Ria
 */

public class SettingsActivity extends AppCompatActivity{

    private static final String TAG = SettingsActivity.class.getSimpleName();

    /**
     * Initializes this Activity.
     *
     * @param savedInstanceState    If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.
     *     <b><i>Note: Otherwise it is null.</i></b>
     *                              [Android source code]
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_settings);
        toolbar.showOverflowMenu();
        setSupportActionBar(toolbar);
    }

    /**
     * Initializes the contents of this Activity's options menu.
     *
     * @param menu      The options menu
     * @return          true to make the menu visible
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    /**
     * Called whenever a menu item is selected and processes
     * the request as defined.
     *
     * @param item      The menu item that was selected
     * @return          true to signal that request has been consumed and
     *                  no further processing is necessary
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.settingsmenu_help:
                startActivity(new Intent(this, HelpActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * {@code Fragment} containing the content of this Settings Screen.
     *
     * @author  Ria
     */
    public static class PrefsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener{

        private Context context;

        /**
         * Initializes this {@code Fragment}, adding the {@code Preference} widgets.
         *
         * @param savedInstanceState    If the activity is being re-initialized after
         *     previously being shut down then this Bundle contains the data it most
         *     recently supplied in {@link #onSaveInstanceState}.
         *     Otherwise it is null. [SDK quote]
         */
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            context = getActivity().getBaseContext();
        }

        /**
         * Called every time the user navigates back to the Settings Screen.
         * Sets up specific {@code Preference} widgets according to the
         * current configurations.
         */
        @Override
        public void onResume() {
            super.onResume();
            SwitchPreference switchPref = (SwitchPreference)findPreference("switch_watchlist");

            //if ping list is empty -> disable bg-ping-switch
            if(PrefsManager.isPingListEmpty(context)){
                switchPref.setChecked(false);
                switchPref.setEnabled(false);
                Log.d(TAG, "SA: set switch disabled.");
            }else{
                boolean bgPingingIsOn = PrefsManager.isBgPingingActive(context);
                switchPref.setChecked(bgPingingIsOn);
                Log.d(TAG, "SA: bgpinging_active is: " + (bgPingingIsOn? "on" : "off"));
                switchPref.setOnPreferenceChangeListener(this);
            }
            findPreference("listprefs_intervals").setOnPreferenceChangeListener(this);
        }

        /**
         * Called when a Preference has been changed by the user,
         * before the state of the Preference has been updated.
         *
         * @param preference    The changed Preference
         * @param newValue      The new value of the Preference
         * @return              true to update the state of the Preference with the new value
         */
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            Log.d(TAG, "SA-PrefsFragment: onPreferenceChange");
            // prepare message for BGPManager
            Activity currentActivity = getActivity();
            Intent intent = new Intent(currentActivity.getBaseContext(), BackgroundPingManager.class);
            int msg = 0;

            // check which preference has changed:
            if(preference instanceof SwitchPreference){
                // if it was watchlist switch, send BGPManager message about it
                if((Boolean)newValue){
                    msg = 1; //switch has been deactivated.
                }else{
                    msg = 2; //switch has been activated
                }
            }else if(preference instanceof ListPreference){
                // if ping interval has changed, update summary
                ListPreference pref = (ListPreference)preference;
                int index = pref.findIndexOfValue(newValue.toString());
                pref.setSummary(String.format(
                        getString(R.string.pref_interval_summary),
                        pref.getEntries()[index].toString()));
            }
            // notify BGPManager
            intent.putExtra("switchOn", msg);
            currentActivity.sendBroadcast(intent);
            return true;
        }
    }
}
