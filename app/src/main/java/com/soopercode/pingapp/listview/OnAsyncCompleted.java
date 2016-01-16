package com.soopercode.pingapp.listview;

/**
 * Interface definition for a callback to be invoked when AsyncListPing
 * has completed its background operation.
 *
 * @author Ria
 */
public interface OnAsyncCompleted {

    /**
     * Called when the implementing class has been notified
     * that {@link com.soopercode.pingapp.listview.AsyncListPing} has finished its task.
     *
     * @param okay Signals that pinging operation was successfully completed
     */
    void onAsyncPingCompleted();
}
