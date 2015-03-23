package com.soopercode.pingapp.utils;

import android.util.Log;
import android.util.Patterns;

import com.soopercode.pingapp.MainActivity;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Verifies whether the user's input represents a valid URL.
 *
 * @author Ria
 */
public class URLValidator {

    /**
     * Verifies whether the user's input constitutes a valid URL using a
     * regex pattern from {@link android.util.Patterns} and makes
     * modifications to the URL if necessary for the purpose of pinging it.
     * If it's not a valid URL, this method knows no mercy.
     *
     * @param usersHost     The user's input
     * @return              A String representing a valid URL that is ready to be pinged
     * @throws              StupidUserException if the user's input does not constitute a valid URL
     */
    public static String validateHostname(String usersHost) throws StupidUserException {

        // check for invalid characters:
        if(! Patterns.WEB_URL.matcher(usersHost).matches()){
            Log.d(MainActivity.TAG, "URLValidator: URL check returns false");
            throw new StupidUserException(usersHost);
        }

        if(usersHost.startsWith("http")){
            // if URL contains protocol: extract host
            try{
                usersHost = new URL(usersHost).getHost();
            }catch(MalformedURLException mue){
                throw new StupidUserException(usersHost, mue);
            }
        }else if(usersHost.contains("/")){
            // if URL contains path: extract host
            int pathStart = usersHost.indexOf('/');
            usersHost = usersHost.substring(0, pathStart);
        }

        return usersHost;
    }
}

