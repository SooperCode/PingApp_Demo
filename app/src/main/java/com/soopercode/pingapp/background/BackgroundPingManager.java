package com.soopercode.pingapp.background;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.soopercode.pingapp.PrefsManager;

/**
 * Starts and Stops all background pinging activity at the scheduled intervals.
 * This BroadcastReceiver is notified each time the phone has finished rebooting
 * in order to make sure background pinging stays active when it's supposed to.
 *
 * @author Ria
 */
public class BackgroundPingManager extends BroadcastReceiver {

    public static final String EXTRA_PING_SWITCH = "ping_switch";
    public static final int MESSAGE_EMPTY = 0;
    public static final int MESSAGE_ACTIVATE = 1;
    public static final int MESSAGE_DEACTIVATE = 2;
    public static final int MESSAGE_INVALID = -1;

    private static final String TAG = BackgroundPingManager.class.getSimpleName();
    private static final String DEFAULT_INTERVAL_3HOURS = "10800000";

    private Context context;
    private AlarmManager alarmManager;
    private Intent backgroundPinging;

    private PendingIntent pendingIntent;
    private long pingInterval;
    /* INTERVALS IN MILLISECONDS:
     *  15min = 900_000
     *  30min = 1_800_000
     *  1h = 3_600_000
     *  3h = 10_800_000
     *  6h = 21_600_000
     *  12h = 43_200_000
     *  24h = 86_400_000
     */

    /**
     * Called each time this BroadcastReceiver receives an Intent broadcast.
     * This happens every time the phone has finished rebooting,
     * when SettingsActivity has detected configurations changes,
     * or when PingListManager has cleared the watchlist.
     *
     * @param context The Context in which the receiver is running
     * @param intent  The Intent being received
     */
    @Override
    public void onReceive(final Context context, final Intent intent) {
        this.context = context;

        // get ping interval:
        final SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        final String stringValue = sharedPrefs.getString("listprefs_intervals", DEFAULT_INTERVAL_3HOURS);
        pingInterval = Long.parseLong(stringValue);

        // check for message from SettingsActivity or PingListManager:
        final Bundle extras = intent.getExtras();
        int msg = MESSAGE_INVALID;
        if (extras != null) {
            msg = extras.getInt(EXTRA_PING_SWITCH, MESSAGE_INVALID);
        }
        switch (msg) {
            case MESSAGE_ACTIVATE: //turn on bg pinging
                activateBackgroundPinging();
                break;
            case MESSAGE_DEACTIVATE: //turn off bg pinging
                deactivateBackgroundPinging();
                break;
            // else the interval has changed OR we must have been revived after reboot:
            default:
                //check if bg-pinging is supposed to be active:
                if (PrefsManager.isBgPingingActive(context)) {
                    activateBackgroundPinging();
                }
        }
    }

    /**
     * Registers an Intent with the {@link android.app.AlarmManager} to start
     * the {@link BackgroundPinger} service at the currently defined ping intervals
     */
    private void activateBackgroundPinging() {
        backgroundPinging = new Intent(context, BackgroundPinger.class);
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        pendingIntent = PendingIntent.getService(context, 0, backgroundPinging, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 2000, pingInterval, pendingIntent);
        Log.i(TAG, "Background Pinging has been activated.");

        // set pinging status to active:
        PrefsManager.setBgPingingActive(context, true);
    }

    /**
     * Deactivates background pinging by canceling the scheduled alarms.
     */
    private void deactivateBackgroundPinging() {
        // re-use objects if possible
        if (alarmManager != null && pendingIntent != null && backgroundPinging != null) {
            alarmManager.cancel(pendingIntent);
        } else {
            alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            backgroundPinging = new Intent(context, BackgroundPinger.class);
            pendingIntent = PendingIntent.getService(context, 0, backgroundPinging, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.cancel(pendingIntent);
        }
        // set pinging status to inactive
        PrefsManager.setBgPingingActive(context, false);
        Log.i(TAG, "Background Pinging has been deactivated.");
    }
}
