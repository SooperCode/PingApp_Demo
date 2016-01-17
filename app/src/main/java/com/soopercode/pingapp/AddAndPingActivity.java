package com.soopercode.pingapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.soopercode.pingapp.utils.HttpPinger;
import com.soopercode.pingapp.utils.StupidUserException;
import com.soopercode.pingapp.utils.Utility;

/**
 * Created by ria on 1/17/16-4.
 */
public class AddAndPingActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = AddAndPingActivity.class.getSimpleName();

    private static int dummyCounter = 0;

    private EditText usersHost;
    private TextView prompt;
    private String validatedHost;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_ping);

        // initialize toolbar
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_add_ping);
        setSupportActionBar(toolbar);

        usersHost = (EditText) findViewById(R.id.usersHost);
        prompt = (TextView) findViewById(R.id.promptDisplay);

        // set up buttons
        findViewById(R.id.buttonPingNow).setOnClickListener(this);
        findViewById(R.id.buttonAdd).setOnClickListener(this);
        findViewById(R.id.buttonErase).setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (dummyCounter != 0) {
            prompt.setTextAppearance(this, R.style.defaultStyle);
            prompt.setText(R.string.promptDisplay);
            dummyCounter = 0;
        }
    }

    /**
     * Verifies whether the text entered by the user constitutes a valid URL,
     * returns true if it does, or alerts the user if it does not.
     *
     * @param hostname The text entered by the user
     * @return true if a valid URL has been obtained
     */
    private boolean validateHostname(String hostname) {
        try {
            validatedHost = Utility.validateHostname(hostname);
            usersHost.setText(validatedHost);
            //check the dummy-state:
            if (dummyCounter != 0) {
                prompt.setTextAppearance(this, R.style.defaultStyle);
                prompt.setText(R.string.promptDisplay);
                dummyCounter = 0;
            }
            return true;
        } catch (StupidUserException sue) {
            switch (dummyCounter) {
                case 0:
                    prompt.setText("Oops! Try again...");
                    dummyCounter++;
                    break;
                case 1:
                    prompt.setText("Come on, it can't be that hard!");
                    dummyCounter++;
                    break;
                case 2:
                    prompt.setText("uhm... you sure you wanna do this??");
                    dummyCounter++;
                    break;
                case 3:
                    prompt.setText("Urrgh, I give up!");
                    //dummyCounter = 0; break;
                    dummyCounter++;
                    break;
                default:
                    prompt.setText(getString(R.string.promptDisplay));
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
     * @author Ria
     */
    private class QuickPing extends AsyncTask<String, Void, Bundle> {

        private ProgressDialog progressDialog;

        /**
         * Sets up a progress wheel to indicate to the user that
         * pinging is being performed. Runs on the UI thread.
         */
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(AddAndPingActivity.this);
            progressDialog.setMessage(getString(R.string.async_progress));
            progressDialog.show();
        }

        /**
         * Pings the specified host using a background thread.
         * The specified parameters are those passed to {@link #execute}
         * when MainActivity starts this task. In this case it will always
         * be a single {@code String} representing the URL to be pinged.
         *
         * @param hostnames The URL to be pinged
         * @return A Bundle containing response code
         * and response message
         */
        @Override
        protected Bundle doInBackground(String... hostnames) {
            return new HttpPinger().getResponse(hostnames[0]);
        }

        /**
         * Runs on the UI thread after {@link #doInBackground}. The specified
         * parameter is the value returned by {@link #doInBackground}. [SDK quote]
         * Starts {@link DisplayResponseActivity}, passing it the {@code Bundle}
         * containing response code and response message.
         *
         * @param response A Bundle containing response code and response message
         */
        @Override
        protected void onPostExecute(Bundle response) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            final Intent displayResponse = new Intent(AddAndPingActivity.this, DisplayResponseActivity.class);
            displayResponse.putExtra("response", response);
            startActivity(displayResponse);
        }
    }

    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.buttonErase:
                usersHost.setText("");
                if (dummyCounter == 0) {
                    prompt.setText(R.string.promptDisplay);
                    prompt.setTextAppearance(this, R.style.defaultStyle);
                }
                break;

            case R.id.buttonPingNow:
                if (validateHostname(usersHost.getText().toString())) {
                    // if we have a valid hostname: ping it
                    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo netInfo = cm.getActiveNetworkInfo();
                    if (netInfo != null && netInfo.isConnected()) {
                        new QuickPing().execute(validatedHost);
                    } else {
                        Toast.makeText(this, getString(R.string.offline_toast), Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case R.id.buttonAdd:
                final String hostname = usersHost.getText().toString();
                // validate hostname before we do anything:
                if (validateHostname(hostname)) {
                    Log.d(TAG, "finish with new hostname: " + validatedHost);
                    final Intent intent = new Intent();
                    intent.putExtra(MainActivity.EXTRA_HOSTNAME, validatedHost);
                    setResult(RESULT_OK, intent);
                    finish();

                    //hide the keyboard:
//                    final InputMethodManager inputMan = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//                    inputMan.hideSoftInputFromWindow(usersHost.getWindowToken(), 0);
                } else {
                    Log.d(TAG, "validation returned false");
                }
                break;

        }
    }
}
