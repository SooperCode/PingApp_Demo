package com.soopercode.pingapp.listview;

/**
 * Holds the data for each host in the watchlist.
 *
 * @author Ria
 */
public class PingItem {

    private final String hostname;
    private int responseCode;
    private boolean isAvailable;
    private String ip = "";

    public PingItem(final String hostname) {
        this.hostname = hostname;
    }

    public String getHostname() {
        return hostname;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(final int responseCode) {
        this.responseCode = responseCode;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(final boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(final String ip) {
        this.ip = ip;
    }


}
