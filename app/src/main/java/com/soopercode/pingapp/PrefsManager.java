package com.soopercode.pingapp;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Organizes and manages important configuration data of this App
 * that is stored in {@link SharedPreferences}.
 * The various activities use the methods of this class to gain
 * information about or make changes to the current configurations.
 *
 * @author  Ria
 */
public class PrefsManager {

    private static SharedPreferences watchlistPrefs;

    /**
     * Shows current status of background pinging which is set according
     * to whether or not {@link com.soopercode.pingapp.background.BackgroundPingManager}
     * has scheduled any background pinging tasks.
     *
     * @param context   Current context, neccessary for accessing {@code SharedPreferences}
     * @return          true if background pinging is set to active
     */
    public static boolean isBgPingingActive(Context context){
        if(watchlistPrefs==null){
            watchlistPrefs = context.getSharedPreferences("watchlistPrefs", Context.MODE_PRIVATE);
        }
        return watchlistPrefs.getBoolean("isActive", false);
    }

    /**
     * Shows current status of the watchlist which is set based on
     * whether or not the list contains any hosts.
     *
     * @param context   Current context, neccessary for accessing {@code SharedPreferences}
     * @return          true if the watchlist does not contain any hosts
     */
    public static boolean isPingListEmpty(Context context){
        if(watchlistPrefs==null){
            watchlistPrefs = context.getSharedPreferences("watchlistPrefs", Context.MODE_PRIVATE);
        }
        return watchlistPrefs.getBoolean("listIsEmpty", false);
    }

    /**
     * Sets background pinging status to active/not active.
     * Called whenever background pinging is switched on or off.
     *
     * @param context   Current context, neccessary for accessing {@code SharedPreferences}
     * @param isActive  The new status of background pinging: active or not
     */
    public static void setBgPingingActive(Context context, boolean isActive){
        if(watchlistPrefs==null){
            watchlistPrefs = context.getSharedPreferences("watchlistPrefs", Context.MODE_PRIVATE);
        }
        SharedPreferences.Editor editor = watchlistPrefs.edit();
        editor.putBoolean("isActive", isActive);
        editor.apply();
    }

    /**
     * Sets watchlist status to empty/not empty.
     * Called when user has deleted all hosts from the watchlist
     * or added a new host to an empty watchlist.
     *
     * @param context   Current context, neccessary for accessing {@code SharedPreferences}
     * @param isEmpty   The new status of the watchlist: empty or not
     */
    public static void setPingListEmpty(Context context, boolean isEmpty){
        if(watchlistPrefs==null){
            watchlistPrefs = context.getSharedPreferences("watchlistPrefs", Context.MODE_PRIVATE);
        }
        SharedPreferences.Editor editor = watchlistPrefs.edit();
        editor.putBoolean("listIsEmpty", isEmpty);
        editor.apply();
    }


}
