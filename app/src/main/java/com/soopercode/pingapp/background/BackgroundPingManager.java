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
 * @author  Ria
 */
public class BackgroundPingManager extends BroadcastReceiver{

    private static final String TAG = BackgroundPingManager.class.getSimpleName();

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
     * @param context   The Context in which the receiver is running
     * @param intent    The Intent being received
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

        // get ping interval:
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        String stringValue = sharedPrefs.getString("listprefs_intervals", "3h");
        pingInterval = Long.parseLong(stringValue);

        // check for message from SettingsActivity or PingListManager:
        Bundle extras = intent.getExtras();
        int msg = -1;
        if(extras !=null){
            msg = extras.getInt("switchOn", -1);
        }
        switch(msg){
            case 1: //turn on bg pinging
                activateBackgroundPinging();
                break;
            case 2: //turn off bg pinging
                deactivateBackgroundPinging();
                break;
            //else the interval has changed OR we must have been revived after reboot:
            default:
                //check if bg-pinging is supposed to be active:
                if(PrefsManager.isBgPingingActive(context)){
                    activateBackgroundPinging();
                }
        }
    }

    /**
     * Registers an Intent with the {@link android.app.AlarmManager} to start
     * the {@link BackgroundPinger} service at the currently defined ping intervals
     */
    private void activateBackgroundPinging(){
        backgroundPinging = new Intent(context, BackgroundPinger.class);
        alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        pendingIntent = PendingIntent.getService(context, 0, backgroundPinging, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+2000, pingInterval, pendingIntent);
        Log.d(TAG, "BGPingManager: Alarm is set!");

        // set pinging status to active:
        PrefsManager.setBgPingingActive(context, true);
    }

    /**
     * Deactivates background pinging by canceling the scheduled alarms.
     */
    private void deactivateBackgroundPinging(){
        //reuse objects if possible
        if(alarmManager !=null && pendingIntent !=null && backgroundPinging !=null){
            alarmManager.cancel(pendingIntent);
        }else{
            alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            backgroundPinging = new Intent(context, BackgroundPinger.class);
            pendingIntent = PendingIntent.getService(context, 0, backgroundPinging, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.cancel(pendingIntent);
        }
        // set pinging status to inactive
        PrefsManager.setBgPingingActive(context, false);
        Log.d(TAG, "BGPingManager: canceled bg-pinging");
    }
}
