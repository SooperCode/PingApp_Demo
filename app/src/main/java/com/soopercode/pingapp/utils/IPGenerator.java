package com.soopercode.pingapp.utils;

import android.util.Log;

import com.soopercode.pingapp.MainActivity;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Retrieves the IP address of a given URL.
 *
 * @author  Ria
 */
public class IPGenerator {

    /**
     * Attempts to retrieve the IP-address of the specified host
     * using {@link java.net.InetAddress}.
     *
     * @param hostname  The host URL as a String
     * @return          The host's IP as a String,
     *                  or the String "unknown host" if no IP has been obtained
     */
    public static String getIP(String hostname){
        InetAddress host;
        try{
            host = InetAddress.getByName(hostname);
            return host.getHostAddress();
        }catch(UnknownHostException uhe){
            Log.d(MainActivity.TAG, "IPGenerator: " + uhe.toString());
            return "unknown host";
        }
    }
}
