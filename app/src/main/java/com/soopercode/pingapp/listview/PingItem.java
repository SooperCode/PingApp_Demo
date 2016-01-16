package com.soopercode.pingapp.listview;

/**
 * Holds the data for each host in the watchlist.
 *
 * @author Ria
 */
public class PingItem {

    private String hostname = "";
    private int responseCode = 0;
    private boolean isAvailable;
    private String ip = "";

    public PingItem(String hostname) {
        this.hostname = hostname;
    }

    public String getHostname() {
        return hostname;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }


}
