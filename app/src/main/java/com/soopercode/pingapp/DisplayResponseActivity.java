package com.soopercode.pingapp;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import com.soopercode.pingapp.utils.ResponseCodeEvaluator;

/**
 * Displays the server response in a dialog box when the user
 * pings a host using the Ping Now button in {@link MainActivity}.
 *
 * @author Ria
 */
public class DisplayResponseActivity extends Activity {

    /**
     * Initializes this activity, receives, evaluates and displays
     * the server response info from {@code MainActivity}.
     * Called every time this Activity is started.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.
     *                           Otherwise it is null. [SDK quote]
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        //unpack response bundle:
        final Bundle response = getIntent().getBundleExtra("response");
        final int responseCode = response.getInt("responseCode");
        final String responseMsg = response.getString("responseMsg");

        final TextView code = (TextView) findViewById(R.id.display_code);
        final TextView text = (TextView) findViewById(R.id.display_text);
        final TextView msg = (TextView) findViewById(R.id.display_msg);

        // set appearance according to status code
        if (200 <= responseCode && responseCode <= 399) {
            code.setTextColor(Color.GREEN);
            msg.setTextColor(Color.GREEN);
        } else if (500 <= responseCode && responseCode <= 599) {
            code.setTextColor(Color.BLUE);
            msg.setTextColor(Color.BLUE);
        } else {
            code.setTextColor(Color.RED);
            msg.setTextColor(Color.RED);
        }
        code.setText(getString(R.string.display_code_text, responseCode));
        msg.setText(getString(R.string.display_msg_text, responseMsg));
        text.setText(ResponseCodeEvaluator.generateResponse(this, responseCode));
    }
}
