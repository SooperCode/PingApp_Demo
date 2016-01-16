package com.soopercode.pingapp.help;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.soopercode.pingapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton that stores our help pages in an ArrayList.
 *
 * @author Ria
 */
public class HelpPages {

    private static HelpPages helpPages; //the single instance of this class
    private List<HelpContent> contents; // list of help pages

    /**
     * Private Constructor that creates the one single instance
     * of this class, defines the content for each help page and
     * stores all pages in an ArrayList.
     *
     * @param applicationContext The Application Context
     */
    private HelpPages(Context applicationContext) {
        contents = new ArrayList<>();

        HelpContent page0 = new HelpContent();
        page0.description = applicationContext.getString(R.string.page0);
        page0.image = applicationContext.getResources().getDrawable(R.drawable.help_p0);
        contents.add(page0);

        HelpContent page1 = new HelpContent();
        page1.description = applicationContext.getString(R.string.page1);
        page1.image = applicationContext.getResources().getDrawable(R.drawable.help_p1);
        contents.add(page1);

        HelpContent page2 = new HelpContent();
        page2.description = applicationContext.getString(R.string.page2);
        page2.image = applicationContext.getResources().getDrawable(R.drawable.help_p2);
        contents.add(page2);
    }

    /**
     * Creates and returns the single instance of this class if it's not created,
     * or returns the cached version of it.
     *
     * @param context The Context of the activity calling this method
     * @return The single instance of this class
     */
    public static HelpPages getInstance(Context context) {
        if (helpPages == null) {
            helpPages = new HelpPages(context.getApplicationContext());
        }
        return helpPages;
    }

    /**
     * Returns the ArrayList storing out help pages.
     *
     * @return The ArrayList containing the help pages
     */
    public List<HelpContent> getContents() {
        return contents;
    }

    /**
     * Returns the HelpContent object representing the page with the specified ID
     *
     * @param pageIndex The individual help page's ID
     * @return The HelpContent object representing the page with the given ID
     */
    public HelpContent getPage(int pageIndex) {
        return contents.get(pageIndex);
    }


    /**
     * Represents the contents for each help page.
     *
     * @author Ria
     */
    public static class HelpContent {

        String description;
        Drawable image;

    }
}
