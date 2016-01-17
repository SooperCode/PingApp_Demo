package com.soopercode.pingapp.listview;

import android.os.AsyncTask;

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

    private final OnAsyncCompleted completer;

    /**
     * Creates a new instance of this class with the references necessary
     * for the operations to be performed.
     *
     * @param completer Reference to the object to be notified after the task is done
     */
    public AsyncListPing(final OnAsyncCompleted completer) {
        this.completer = completer;
    }

    /**
     * Pings the specified hosts on a background thread using the appropriate
     * helper class.
     *
     * @param items The PingItems representing the hosts in the watchlist
     */
    @Override
    protected Void doInBackground(final PingItem... items) {

        final SocketPinger socketPinger = new SocketPinger();
        final HttpPinger httpPinger = new HttpPinger();
        for (PingItem item : items) {
            final String hostname = item.getHostname();
            item.setAvailable(socketPinger.checkConnection(hostname));
            item.setResponseCode(httpPinger.getResponseCode(hostname));
            item.setIp(Utility.getIP(hostname));
        }
        return null;
    }

    @Override
    protected void onPostExecute(final Void nix) {
        completer.onAsyncPingCompleted();
    }
}
