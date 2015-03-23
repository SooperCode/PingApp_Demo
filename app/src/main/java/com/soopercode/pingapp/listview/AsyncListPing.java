package com.soopercode.pingapp.listview;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import com.soopercode.pingapp.OnAsyncCompleted;
import com.soopercode.pingapp.R;
import com.soopercode.pingapp.utils.HttpPinger;
import com.soopercode.pingapp.utils.IPGenerator;
import com.soopercode.pingapp.utils.SocketPinger;

/**
 * AsyncTask that handles all operations necessary for
 * the pinging of hosts in the watchlist, including
 * operations that run on a background thread.
 *
 * @author  Ria
 */
public class AsyncListPing extends AsyncTask<PingItem, Void, Boolean> {

    private Activity context;
    private OnAsyncCompleted completer;
    private int taskId;
    private ProgressBar progressWheel;

    /**
     * Creates a new instance of this class with the references necessary
     * for the operations to be performed, as well as an ID number indicating
     * the specific kind of task to be done. The ID number is chosen by
     * {@link PingListManager}, who instantiates and starts this AsyncTask,
     * according to whether the watchlist is currently set to Nerd View or not.
     *
     * @param context       The current Context: MainActivity's Context in this case
     * @param completer     Reference to the object to be notified after the task is done
     * @param taskId        ID number indicating the specific kind of task to be performed
     */
    public AsyncListPing(Activity context, OnAsyncCompleted completer, int taskId){
        this.context = context;
        this.completer = completer;
        this.taskId = taskId;
    }

    /**
     * Sets up a progress wheel to indicate to the user that
     * background work is being done. Runs on the UI thread.
     */
    @Override
    protected void onPreExecute() {
        progressWheel = (ProgressBar)context.findViewById(R.id.progressWheel);
        progressWheel.setVisibility(View.VISIBLE);
    }

    /**
     * Pings the specified hosts on a background thread using the appropriate
     * helper class, based on the task ID that was passed to the constructor.
     * The specified parameters are those passed to {@link #execute}
     * by {@link PingListManager} when calling this task.
     *
     * @param items The PingItems representing the hosts in the watchlist
     * @return      true after pinging is done
     */
    @Override
    protected Boolean doInBackground(PingItem... items) {

        if(taskId ==1){
            SocketPinger socketPinger = new SocketPinger();
            for(PingItem item : items){
                item.setAvailable(socketPinger.checkConnection(item.getHostname()));
            }
        }else if(taskId ==2){
            HttpPinger httpPinger = new HttpPinger();
            for(PingItem item : items){
                item.setResponseCode(httpPinger.getResponseCode(item.getHostname()));
                item.setIp(IPGenerator.getIP(item.getHostname()));
            }
        }
        return true;
    }

    /**
     * Runs on the UI thread after {@link #doInBackground}. The specified
     * parameter is the value returned by {@link #doInBackground}. [SDK quote]
     * Notifies the completer, which is {@link PingListManager} in this case,
     * of the completion of this task and hides the progress wheel.
     *
     * @param okay  Status received by doInBackground
     */
    @Override
    protected void onPostExecute(Boolean okay) {
        completer.onAsyncPingCompleted(okay);
        progressWheel.setVisibility(View.INVISIBLE);
    }
}
