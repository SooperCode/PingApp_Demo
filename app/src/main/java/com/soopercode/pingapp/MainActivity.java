package com.soopercode.pingapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.soopercode.pingapp.help.HelpActivity;
import com.soopercode.pingapp.listview.PingListManager;
import com.soopercode.pingapp.utils.HttpPinger;
import com.soopercode.pingapp.utils.StupidUserException;
import com.soopercode.pingapp.utils.URLValidator;

/**
 * Represents the Main Screen of this Application.
 * Started every time the User opens or navigates back to this App.
 *
 * @author  Ria
 */

public class MainActivity extends Activity implements View.OnClickListener {

    /** name of the local file containing the hosts in the watchlist. */
    public static final String FILENAME = "pingapp";

    /** Log tag for testing & debugging */
    public static final String TAG = "debugging";

    private static final int CHANGE_SETTINGS_REQUEST = 1;
    private static int dummyCounter = 0;

    private EditText usersHost;
    private TextView prompt;
    private ImageView onOfLight;
    private String validatedHost;
    private PingListManager pingListManager;

    /**
     * Initializes and sets up the App's Main Screen.
     * Called every time the App is started.
     *
     * @param savedInstanceState    If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.
     *     Otherwise it is null. [SDK quote]
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usersHost = (EditText) findViewById(R.id.usersHost);
        prompt = (TextView) findViewById(R.id.promptDisplay);

        // set up buttons
        Button pingButton = (Button) findViewById(R.id.buttonPingNow);
        pingButton.setOnClickListener(this);
        Button addButton = (Button) findViewById(R.id.buttonAdd);
        addButton.setOnClickListener(this);
        ImageButton refreshButton = (ImageButton) findViewById(R.id.buttonRefresh);
        refreshButton.setOnClickListener(this);
        ImageButton eraseButton = (ImageButton) findViewById(R.id.buttonErase);
        eraseButton.setOnClickListener(this);

        // set up list
        ListView pingListView = (ListView) findViewById(R.id.listview_pingList);
        pingListManager = new PingListManager(this, pingListView);
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean nerdPref = sharedPrefs.getBoolean("checkbox_nerdview", false);
        pingListManager.createListView(nerdPref);
        pingListManager.loadList();

        // after installation of the app, set example host in the list:
        boolean isFirstRun = sharedPrefs.getBoolean("firstRun", true);
        if(isFirstRun){
            pingListManager.addNewHost("www.google.com");
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putBoolean("firstRun", false).apply();
        }
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
        if(dummyCounter !=0){
            prompt.setTextAppearance(this, R.style.defaultStyle);
            prompt.setText(R.string.promptDisplay);
            dummyCounter = 0;
        }
    }

    /**
     * Called after {@link #onRestoreInstanceState}, {@link #onRestart}, or
     * {@link #onPause}. [SDK quote]
     * Checks if background pinging is activated & displays green light when it is.
     */
    @Override
    protected void onResume() {
        super.onResume();
        // set up watchlist light
        onOfLight = (ImageView) findViewById(R.id.imgWatchlistOn);
        if(PrefsManager.isBgPingingActive(this)){
            onOfLight.setImageDrawable(getResources().getDrawable(R.drawable.on_30x30));
        }else{
            onOfLight.setImageDrawable(getResources().getDrawable(R.drawable.off_30x30));
        }
    }

    /**
     * Called every time a View on the MainScreen is clicked
     * and responds as defined.
     *
     * @param view      The View that has been clicked
     */
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.buttonErase:
                usersHost.setText("");
                if(dummyCounter==0){
                    prompt.setText(R.string.promptDisplay);
                    prompt.setTextAppearance(this, R.style.defaultStyle);
                }
                break;

            case R.id.buttonPingNow:
                if(validateHostname(usersHost.getText().toString())){
                    // if we have a valid hostname: ping it
                    ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo netInfo = cm.getActiveNetworkInfo();
                    if(netInfo !=null && netInfo.isConnected()){
                        QuickPing quickPing = new QuickPing();
                        quickPing.execute(validatedHost);
                    }else{
                        Toast.makeText(this, getString(R.string.offline_toast), Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case R.id.buttonAdd:
                String hostname = usersHost.getText().toString();
                // validate hostname before we do anything:
                if(validateHostname(hostname)) {
                    pingListManager.addNewHost(validatedHost);
                    usersHost.setText("");
                    prompt.setText(R.string.promptDisplay);
                    prompt.setTextAppearance(this, R.style.defaultStyle);
                    //hide the keyboard:
                    InputMethodManager inputMan = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    inputMan.hideSoftInputFromWindow(usersHost.getWindowToken(), 0);
                }else{
                    Log.d(TAG, "MA: validation returned false");
                }
                break;

            case R.id.buttonRefresh:
                pingListManager.pingListNow();
                break;
        }
    }

    /**
     * Verifies whether the text entered by the user constitutes a valid URL,
     * returns true if it does, or alerts the user if it does not.
     *
     * @param hostname      The text entered by the user
     * @return              true if a valid URL has been obtained
     */
    private boolean validateHostname(String hostname){
        try{
            validatedHost = URLValidator.validateHostname(hostname);
            usersHost.setText(validatedHost);
            //check the dummy-state:
            if(dummyCounter !=0){
                prompt.setTextAppearance(this, R.style.defaultStyle);
                prompt.setText(R.string.promptDisplay);
                dummyCounter = 0;
            }
            return true;
        }catch (StupidUserException sue){
            switch (dummyCounter){
                case 0: prompt.setText("Oops! Try again...");
                    dummyCounter++; break;
                case 1: prompt.setText("Come on, it can't be that hard!");
                    dummyCounter++; break;
                case 2: prompt.setText("uhm... you sure you wanna do this??");
                    dummyCounter++; break;
                case 3: prompt.setText("Urrgh, I give up!");
                    //dummyCounter = 0; break;
                    dummyCounter++; break;
                default: prompt.setText(getString(R.string.promptDisplay));
                    dummyCounter = 0;
            }
            prompt.setTextAppearance(this, R.style.negativeStyle);
            usersHost.requestFocus();
            Log.e(TAG, sue.toString(), sue);
            return false;
        }
    }


    /**
     * AsyncTask that handles all operations necessary for
     * pinging the host in the text field, including
     * operations that run on a background thread.
     *
     * @author  Ria
     */
    private class QuickPing extends AsyncTask<String, Void, Bundle> {

        private ProgressDialog progressDialog;

        /**
         * Sets up a progress wheel to indicate to the user that
         * pinging is being performed. Runs on the UI thread.
         */
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage(getString(R.string.async_progress));
            progressDialog.show();
        }

        /**
         * Pings the specified host using a background thread.
         * The specified parameters are those passed to {@link #execute}
         * when MainActivity starts this task. In this case it will always
         * be a single {@code String} representing the URL to be pinged.
         *
         * @param hostnames     The URL to be pinged
         * @return              A Bundle containing response code
         *                      and response message
         */
        @Override
        protected Bundle doInBackground(String... hostnames) {
            String hostname = hostnames[0];
            HttpPinger httpPinger = new HttpPinger();

            return httpPinger.getResponse(hostname);
        }

        /**
         * Runs on the UI thread after {@link #doInBackground}. The specified
         * parameter is the value returned by {@link #doInBackground}. [SDK quote]
         * Starts {@link DisplayResponseActivity}, passing it the {@code Bundle}
         * containing response code and response message.
         *
         * @param response      A Bundle containing response code and response message
         */
        @Override
        protected void onPostExecute(Bundle response) {
            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }
            Intent displayResponse = new Intent(MainActivity.this, DisplayResponseActivity.class);
            displayResponse.putExtra("response", response);
            startActivity(displayResponse);
        }
    }

    /* ------------- MENU ------------- */

    /**
     * Initializes the contents of this Activity's options menu.
     *
     * @param menu      The options menu
     * @return          true to make the menu visible
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

            case R.id.menu_deleteList:
                pingListManager.clearList();
                onOfLight.setImageDrawable(getResources().getDrawable(R.drawable.off_30x30));
                return true;

            case R.id.menu_settings:
                Intent prefsIntent = new Intent(this, SettingsActivity.class);
                startActivityForResult(prefsIntent, CHANGE_SETTINGS_REQUEST);
                return true;

            case R.id.menu_help:
                startActivity(new Intent(this, HelpActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
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
     * @param requestCode   The integer request code supplied when SettingsActivity
     *                      was started using {@link #startActivityForResult}
     * @param resultCode    The integer result code returned by the child activity
     *                      through its setResult() [SDK quote]
     *                      (not used in this code)
     * @param data          An Intent, which can return result data to the caller [SDK quote]
     *                      (not used in this code)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult called");
        if(requestCode==CHANGE_SETTINGS_REQUEST){
            updateSettings();
        }
    }

    /**
     * Sets the watchlist to Nerd View or normal view according
     * to the current configuration.
     */
    private void updateSettings() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(sharedPrefs.getBoolean("checkbox_nerdview", false)){
            pingListManager.createListView(true);
        }else{
            pingListManager.createListView(false);
        }
        pingListManager.pingListNow();
    }

}
