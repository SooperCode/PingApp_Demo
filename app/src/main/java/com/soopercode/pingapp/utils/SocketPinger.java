package com.soopercode.pingapp.utils;

import android.util.Log;

import com.soopercode.pingapp.MainActivity;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Pings a given host by attempting to establish a HTTP connection
 * using {@link java.net.Socket}.
 *
 * @author  Ria
 */
public class SocketPinger {

    /**
     * Attempts to make a {@link java.net.Socket} connection
     * to the specified host URL, using port 80 (HTTP).
     *
     * @param hostname  String of the URL to connect to
     * @return          true if the connection was successfully made
     */
    public boolean checkConnection(String hostname){
        Socket socket = new Socket();
        /* TODO ******** FOR DEMO ********* */
        if(hostname.equals("www.unavailable.com") ||
                hostname.equals("www.serverdown.nl") ||
                hostname.equals("thepiratebay.org") ||
                hostname.equals("www.somewebsite.com") ||
                hostname.equals("www.examplehost.org")){
            return false;
        } /* ****************************** */
        try{
            socket.connect(new InetSocketAddress(hostname, 80), 7000); //port 80, 7sec timeout.
            return true;
        }catch (IOException ioe){
            Log.e(MainActivity.TAG, "Socket ex: " + ioe.toString());
            return false;
        }finally{
            if(socket.isConnected()){
                try{ socket.close(); }catch(IOException ioe){ ioe.printStackTrace(); }
            }
        }
    }
}
