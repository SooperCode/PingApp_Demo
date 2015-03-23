package com.soopercode.pingapp.help;

import android.graphics.drawable.Drawable;

/**
 * Represents the contents for each help page.
 *
 * @author  Ria
 */
public class HelpContent {

    private static int idCounter;
    String description;
    Drawable image;
    int personalId;

    HelpContent() {
        //every page gets its own id
        personalId = idCounter++;
    }
}
