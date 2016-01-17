package com.soopercode.pingapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.soopercode.pingapp.background.BackgroundPingManager;
import com.soopercode.pingapp.listview.RecyclerFragment;

/**
 * Represents the Main Screen of this Application.
 * Started every time the User opens or navigates back to this App.
 *
 * @author Ria
 */

public class MainActivity extends AppCompatActivity {

    /**
     * name of the local file containing the hosts in the watchlist.
     */
    public static final String FILENAME = "pingapp";
    public static final String EXTRA_HOSTNAME = "extra_hostname";

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String RECYCLER_FRAGMENT_TAG = "RF_TAG";
    private static final int CHANGE_SETTINGS_REQUEST = 1;
    private static final int ADD_HOST_REQUEST = 2;

    private AppBarLayout appBarLayout;


    /**
     * Initializes and sets up the App's Main Screen.
     * Called every time the App is started.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.
     *                           Otherwise it is null. [SDK quote]
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);
        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                startActivityForResult(new Intent(MainActivity.this, AddAndPingActivity.class),
                        ADD_HOST_REQUEST);
            }
        });

        // initialize toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.showOverflowMenu();
        setSupportActionBar(toolbar);

        // this prevents the title (app name) from showing up on top of the image
        // for some strange android reason this doesn't work in xml.
        ((CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar))
                .setExpandedTitleColor(getResources().getColor(android.R.color.transparent));

        // after installation of the app, set example host in the list:
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isFirstRun = sharedPrefs.getBoolean("firstRun", true);
        if (isFirstRun) {
            //TODO: .addNewHost("www.google.com");
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putBoolean("firstRun", false).apply();
        }

        // add recycler view fragment
        if (savedInstanceState == null) {
            addFragment(new RecyclerFragment());
        }
    }

    private void addFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_fragment_recycler, fragment, RECYCLER_FRAGMENT_TAG)
                .commitAllowingStateLoss();
        /* commitAllowingStateLoss() prevents app from crashing
           with "IllegalStateException: Cannot perform this action
           after onSaveInstanceState" -> http://stackoverflow.com/q/7575921  */
    }


    /**
     * Called after {@link #onCreate} &mdash; or after {@link #onRestart} when
     * the activity had been stopped, but is now again being displayed to the
     * user. [SDK quote]
     * Checks 'dummyCounter' status and sets prompt-text back to normal if necessary.
     */
    @Override
    protected void onStart() {
        super.onStart();
        appBarLayout.addOnOffsetChangedListener((RecyclerFragment) getSupportFragmentManager()
                .findFragmentByTag(RECYCLER_FRAGMENT_TAG));
    }

    @Override
    protected void onStop() {
        super.onStop();
        appBarLayout.removeOnOffsetChangedListener((RecyclerFragment) getSupportFragmentManager()
                .findFragmentByTag(RECYCLER_FRAGMENT_TAG));
    }


    /* ------------- MENU ------------- */

    /**
     * Initializes the contents of this Activity's options menu.
     *
     * @param menu The options menu
     * @return true to make the menu visible
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {

        final boolean turnOffVisible = PrefsManager.isBgPingingActive(this);
        (menu.findItem(R.id.menu_pinging_on)).setVisible(!turnOffVisible);
        (menu.findItem(R.id.menu_pinging_off)).setVisible(turnOffVisible);

        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Called whenever a menu item is selected and processes
     * the request as defined.
     *
     * @param item The menu item that was selected
     * @return true to signal that request has been consumed and
     * no further processing is necessary
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_settings:
                Intent prefsIntent = new Intent(this, SettingsActivity.class);
                startActivityForResult(prefsIntent, CHANGE_SETTINGS_REQUEST);
                return true;

            case R.id.menu_pinging_on:
                Log.d(TAG, "menu: turn pinging on clicked");
                sendMessageToBackgroundPingManager(BackgroundPingManager.MESSAGE_ACTIVATE);
                return true;

            case R.id.menu_pinging_off:
                Log.d(TAG, "menu: turn pinging off clicked");
                sendMessageToBackgroundPingManager(BackgroundPingManager.MESSAGE_DEACTIVATE);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void sendMessageToBackgroundPingManager(final int message) {
        final Intent intent = new Intent(this, BackgroundPingManager.class);
        intent.putExtra(BackgroundPingManager.EXTRA_PING_SWITCH, message);
        sendBroadcast(intent);
    }

    /* -------- SETTINGS CALLBACKS --------
     * Handle changes to the "NerdView" preference.
     */

    /**
     * Callback method that is invoked when user returns from {@link SettingsActivity}
     * to this Activity, as a result of having started SettingsActivity through
     * {@link #startActivityForResult}.
     * Handles changes that were made to the Nerd View Preference.
     *
     * @param requestCode The integer request code supplied when SettingsActivity
     *                    was started using {@link #startActivityForResult}
     * @param resultCode  The integer result code returned by the child activity
     *                    through its setResult() [SDK quote]
     *                    (not used in this code)
     * @param data        An Intent, which can return result data to the caller [SDK quote]
     *                    (not used in this code)
     */
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {

        if (requestCode == CHANGE_SETTINGS_REQUEST) {
            // "refresh" recycler view fragment after user has changed
            // the view settings & navigates back using phone's back button
            addFragment(new RecyclerFragment());

        } else if (resultCode == RESULT_OK && requestCode == ADD_HOST_REQUEST) {
            // pass the new host to the pinglist fragment
            final String hostname = data.getStringExtra(EXTRA_HOSTNAME);
            ((RecyclerFragment) getSupportFragmentManager()
                    .findFragmentByTag(RECYCLER_FRAGMENT_TAG)).addNewHost(hostname);
        }
    }


}
