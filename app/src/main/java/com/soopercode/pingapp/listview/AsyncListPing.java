package com.soopercode.pingapp.listview;

import android.os.AsyncTask;
import android.widget.ProgressBar;

import com.soopercode.pingapp.utils.HttpPinger;
import com.soopercode.pingapp.utils.SocketPinger;
import com.soopercode.pingapp.utils.Utility;

/**
 * AsyncTask that handles all operations necessary for
 * the pinging of hosts in the watchlist, including
 * operations that run on a background thread.
 *
 * @author Ria
 */
public class AsyncListPing extends AsyncTask<PingItem, Void, Void> {

    private OnAsyncCompleted completer;
    private ProgressBar progressWheel;

    /**
     * Creates a new instance of this class with the references necessary
     * for the operations to be performed.
     *
     * @param completer Reference to the object to be notified after the task is done
     */
    public AsyncListPing(OnAsyncCompleted completer) {
        this.completer = completer;
    }

    /**
     * Sets up a progress wheel to indicate to the user that
     * background work is being done. Runs on the UI thread.
     */
    @Override
    protected void onPreExecute() {
//        progressWheel = (ProgressBar)context.findViewById(R.id.progressWheel);
//        progressWheel.setVisibility(View.VISIBLE);
    }

    /**
     * Pings the specified hosts on a background thread using the appropriate
     * helper class.
     *
     * @param items The PingItems representing the hosts in the watchlist
     */
    @Override
    protected Void doInBackground(PingItem... items) {

        SocketPinger socketPinger = new SocketPinger();
        HttpPinger httpPinger = new HttpPinger();
        for (PingItem item : items) {
            String hostname = item.getHostname();
            item.setAvailable(socketPinger.checkConnection(hostname));
            item.setResponseCode(httpPinger.getResponseCode(hostname));
            item.setIp(Utility.getIP(hostname));
        }
        return null;
    }

    /**
     * Runs on the UI thread after {@link #doInBackground}.
     * Notifies the completer, which is {@link PingListManager} in this case,
     * of the completion of this task and hides the progress wheel.
     */
    @Override
    protected void onPostExecute(Void nix) {
        completer.onAsyncPingCompleted();
//        progressWheel.setVisibility(View.INVISIBLE);
    }
}
