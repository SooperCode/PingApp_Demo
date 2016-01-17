package com.soopercode.pingapp;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Organizes and manages important configuration data of this App
 * that is stored in {@link SharedPreferences}.
 * The various activities use the methods of this class to gain
 * information about or make changes to the current configurations.
 *
 * @author Ria
 */
public class PrefsManager {

    private static final String PREFKEY_PINGING_ACTIVE = "isActive";
    private static final String PREFKEY_WATCHLIST_PREFS = "watchlistPrefs";
    private static final String PREFKEY_LIST_EMPTY = "listIsEmpty";

    private static SharedPreferences watchlistPrefs;

    /**
     * Shows current status of background pinging which is set according
     * to whether or not {@link com.soopercode.pingapp.background.BackgroundPingManager}
     * has scheduled any background pinging tasks.
     *
     * @param context Current context, necessary for accessing {@code SharedPreferences}
     * @return true if background pinging is set to active
     */
    public static boolean isBgPingingActive(final Context context) {
        ensurePrefs(context);
        return watchlistPrefs.getBoolean(PREFKEY_PINGING_ACTIVE, false);
    }

    /**
     * Shows current status of the watchlist which is set based on
     * whether or not the list contains any hosts.
     *
     * @param context Current context, necessary for accessing {@code SharedPreferences}
     * @return true if the watchlist does not contain any hosts
     */
    public static boolean isPingListEmpty(final Context context) {
        ensurePrefs(context);
        return watchlistPrefs.getBoolean(PREFKEY_LIST_EMPTY, false);
    }

    /**
     * Sets background pinging status to active/not active.
     * Called whenever background pinging is switched on or off.
     *
     * @param context  Current context, necessary for accessing {@code SharedPreferences}
     * @param isActive The new status of background pinging: active or not
     */
    public static void setBgPingingActive(final Context context, final boolean isActive) {
        setPreference(context, PREFKEY_PINGING_ACTIVE, isActive);
    }

    /**
     * Sets watchlist status to empty/not empty.
     * Called when user has deleted all hosts from the watchlist
     * or added a new host to an empty watchlist.
     *
     * @param context Current context, neccessary for accessing {@code SharedPreferences}
     * @param isEmpty The new status of the watchlist: empty or not
     */
    public static void setPingListEmpty(final Context context, final boolean isEmpty) {
        setPreference(context, PREFKEY_LIST_EMPTY, isEmpty);
    }

    private static void ensurePrefs(final Context context) {
        if (watchlistPrefs == null) {
            watchlistPrefs = context.getSharedPreferences(PREFKEY_WATCHLIST_PREFS, Context.MODE_PRIVATE);
        }
    }

    private static void setPreference(final Context context, final String prefKey, final boolean prefState) {
        ensurePrefs(context);
        final SharedPreferences.Editor editor = watchlistPrefs.edit();
        editor.putBoolean(prefKey, prefState);
        editor.apply();
    }

}
