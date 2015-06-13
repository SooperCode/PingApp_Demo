package com.soopercode.pingapp.background;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.soopercode.pingapp.MainActivity;
import com.soopercode.pingapp.R;
import com.soopercode.pingapp.utils.SocketPinger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Background Service that pings hosts from user's watchlist
 * and sends a notification to the user if a host has responded positively.
 * Is called into action by {@link android.app.AlarmManager} which has been set up
 * by {@link BackgroundPingManager} at the scheduled intervals.
 * Finishes its activity as soon as it is done pinging all hosts from the list.
 *
 * @author  Ria
 */
public class BackgroundPinger extends Service {

    private static final String TAG = BackgroundPinger.class.getSimpleName();

    /* Handlers */
    private Handler notificationHandler;
    private Handler doneHandler;

    /**
     * Called by the system every time this Service is started by
     * the {@link android.app.AlarmManager} at the scheduled ping intervals.
     *
     * @param intent    The Intent that started this service (not relevant to this code)
     * @param flags     Additional data about this start request (not relevant to this code)
     * @param startId   A unique integer representing this specific request to start
     *                  (not relevant to this code)
     * @return          Call to overridden method in Service, which returns a value that
     *                  informs the system about details of this service's start/stop behavior
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //create Handlers:
        notificationHandler = new NotificationHandler(this);
        doneHandler = new DoneHandler(this);

        new Thread(runner).start();
        return super.onStartCommand(intent, flags, startId);
    }

    private Runnable runner = new Runnable(){
        @Override
        public void run() {
            ping(MainActivity.FILENAME);
        }
    };

    /**
     * Reads in the hostnames of the user's watchlist from a file,
     * checks availability of each host using {@link SocketPinger},
     * and reports the result to
     * {@link BackgroundPinger.NotificationHandler}.
     * Started on a separate thread.
     *
     * @param filename  The name of the local file that contains the list of hostnames
     */
    private void ping(String filename){
        //check if phone is online:
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if(netInfo !=null && netInfo.isConnected()) {
            InputStream in = null;
            try {
                in = openFileInput(filename);
                if (in != null) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    SocketPinger socketPinger = new SocketPinger();
                    String hostname;
                    while ((hostname = reader.readLine()) != null) {
                        if (socketPinger.checkConnection(hostname)) {
                            Bundle bundle = new Bundle();
                            bundle.putString("hostname", hostname);
                            Message msg = new Message();
                            msg.setData(bundle);
                            notificationHandler.sendMessage(msg);
                        }
                    }
                }
            } catch (IOException ioe) {
                Log.e(TAG, "BP: loading list from file failed", ioe);
            } finally {
                if (in != null) {
                    try { in.close(); } catch (IOException ioe) { ioe.printStackTrace(); }
                }
                doneHandler.sendEmptyMessage(0);
            }
        }
    }

    /** not implemented */
    @Override
    public IBinder onBind(Intent intent) { return null; }


    /* -------------- HANDLER CLASSES -------------- */

    /**
     * Sends a notification to the user whenever it is called from
     * {@link BackgroundPinger} after a host has responded
     * positively to a ping request.
     */
    private static class NotificationHandler extends Handler{

        private static Map<String, Integer> hostsNotified = new HashMap<>();
        private Context context;

        /**
         * Constructs a new NotificationHandler with a reference to
         * the BackgroundPinger service object that is calling this constructor.
         *
         * @param outer     A reference to the current BackgroundPinger object
         */
        public NotificationHandler(BackgroundPinger outer){
            context = outer.getApplicationContext();
        }

        /**
         * Receives the message sent from BackgroundPinger's background thread
         * each time a host has responded to the ping request
         *
         * @param msg   A {@link android.os.Message} object
         */
        @Override
        public void handleMessage(Message msg) {
            if(msg !=null){
                String hostname = msg.getData().getString("hostname");
                showNotification(hostname);

                // FOR TESTING: if Toast-switch is activated, show Toast.
                SharedPreferences sharedPrefs
                        = PreferenceManager.getDefaultSharedPreferences(context);
                if(sharedPrefs.getBoolean("toast_switch", false)){
                    Toast.makeText(context, hostname, Toast.LENGTH_SHORT).show();
                }
            }
        }

        /**
         * Sends a notification to the user with the message that the specified
         * host has responded positively to the ping request.
         *
         * @param host  The host URL that has responded to the ping request.
         */
        private void showNotification(String host){
            //check if host is already in the list:
            int notificationID = 0;
            if(hostsNotified.containsKey(host)){
                notificationID = hostsNotified.get(host);
            }else{
                notificationID = hostsNotified.size()+1;
                hostsNotified.put(host, notificationID);
            }
            // build the notification
            NotificationCompat.Builder notification = new NotificationCompat.Builder(context);
            notification.setAutoCancel(true); //dismiss when user has opened it.
            notification.setSmallIcon(R.mipmap.ic_launcher);
            notification.setTicker(host + context.getString(R.string.notif_ticker));
            notification.setWhen(System.currentTimeMillis());
            notification.setContentTitle(host + context.getString(R.string.notif_contentTitle));
            notification.setContentText("Host " + host + context.getString(R.string.notif_contentText));

            //check if sound on or muted (default: sound/vibrate)
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            boolean soundOn = sharedPrefs.getBoolean("notification_sound", true);
            if(soundOn){
                notification.setDefaults(Notification.DEFAULT_ALL);
            }

            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            notification.setContentIntent(pendingIntent);

            NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            nm.notify(notificationID, notification.build());

        }
    }

    /**
     * Stops the Service {@link com.soopercode.pingapp.background.BackgroundPinger}
     * after all hosts of the watchlist have been pinged.
     */
    private static class DoneHandler extends Handler{

        private BackgroundPinger outer; //reference to outer class

        /**
         * Constructs a new DoneHandler with a reference
         * to the BackgroundPinger service object that is calling this constructor.
         *
         * @param outer A reference to the current BackgroundPinger object
         */
        public DoneHandler(BackgroundPinger outer){
            this.outer = outer;
        }

        /**
         * Receives the message sent from BackgroundPinger's background thread
         * and stops the BackgroundPinger service.
         *
         * @param msg   A {@link android.os.Message} object
         */
        @Override
        public void handleMessage(Message msg) {
            outer.stopSelf();
        }
    }
}
