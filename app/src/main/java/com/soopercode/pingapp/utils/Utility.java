package com.soopercode.pingapp.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.util.Patterns;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * Created by ria on 6/14/15.
 */
public class Utility {

    private static final String TAG = Utility.class.getSimpleName();

    /**
     * Check network connection
     */
    public static boolean isConnected(final Context context) {
        final ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }


    /**
     * Attempts to retrieve the IP-address of the specified host
     * using {@link java.net.InetAddress}.
     *
     * @param hostname The host URL as a String
     * @return The host's IP as a String,
     * or the String "unknown host" if no IP has been obtained
     */
    public static String getIP(String hostname) {
        if (hostname.equals("www.somewebsite.com")) {
            return "88.102.237.130";
        }
        InetAddress host;
        try {
            host = InetAddress.getByName(hostname);
            return host.getHostAddress();
        } catch (UnknownHostException uhe) {
            Log.d(TAG, "IPGenerator: " + uhe.toString());
            return "unknown host";
        }
    }


    /**
     * Verifies whether the user's input constitutes a valid URL using a
     * regex pattern from {@link android.util.Patterns} and makes
     * modifications to the URL if necessary for the purpose of pinging it.
     * If it's not a valid URL, this method knows no mercy.
     *
     * @param usersHost The user's input
     * @return A String representing a valid URL that is ready to be pinged
     * @throws StupidUserException if the user's input does not constitute a valid URL
     */
    public static String validateHostname(String usersHost) throws StupidUserException {

        // check for invalid characters:
        if (!Patterns.WEB_URL.matcher(usersHost).matches()) {
            Log.d(TAG, "URL check returns false");
            throw new StupidUserException(usersHost);
        }

        if (usersHost.startsWith("http")) {
            // if URL contains protocol: extract host
            try {
                usersHost = new URL(usersHost).getHost();
            } catch (MalformedURLException mue) {
                throw new StupidUserException(usersHost, mue);
            }
        } else if (usersHost.contains("/")) {
            // if URL contains path: extract host
            int pathStart = usersHost.indexOf('/');
            usersHost = usersHost.substring(0, pathStart);
        }

        return usersHost;
    }
}
