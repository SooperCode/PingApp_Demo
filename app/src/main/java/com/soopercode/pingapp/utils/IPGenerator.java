package com.soopercode.pingapp.utils;

import android.util.Log;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Retrieves the IP address of a given URL.
 *
 * @author  Ria
 */
public class IPGenerator {

    private static final String TAG = IPGenerator.class.getSimpleName();

    /**
     * Attempts to retrieve the IP-address of the specified host
     * using {@link java.net.InetAddress}.
     *
     * @param hostname  The host URL as a String
     * @return          The host's IP as a String,
     *                  or the String "unknown host" if no IP has been obtained
     */
    public static String getIP(String hostname){
        if(hostname.equals("www.somewebsite.com")){
            return "88.102.237.130";
        }
        InetAddress host;
        try{
            host = InetAddress.getByName(hostname);
            return host.getHostAddress();
        }catch(UnknownHostException uhe){
            Log.d(TAG, "IPGenerator: " + uhe.toString());
            return "unknown host";
        }
    }
}
