package com.soopercode.pingapp.help;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.soopercode.pingapp.R;

import java.util.List;

/**
 * Represents the Help Pages of this App.
 *
 * @author  Ria
 */
public class HelpActivity extends AppCompatActivity{

    private List<HelpPages.HelpContent> contents;

    /**
     * Initializes the Help Screen of the App by setting up a
     * {@link android.support.v4.view.ViewPager}.
     * Called every time the user navigates to the Help Section.
     *
     * @param savedInstanceState    If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.
     *     <b><i>Note: Otherwise it is null.</i></b>
     *                              [quoted from Android source code]
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_help);
        toolbar.showOverflowMenu();
        setSupportActionBar(toolbar);

        ViewPager viewPager = (ViewPager)findViewById(R.id.viewpager_help);
        viewPager.setPageMargin(30);
        contents = HelpPages.getInstance(this).getContents();

        FragmentManager fragMan = getSupportFragmentManager();
        // FragmentPagerAdapter uses a Fragment to manage each page
        viewPager.setAdapter(new FragmentPagerAdapter(fragMan) {

            // gets the specific help page from the right position in our array list
            @Override
            public Fragment getItem(int position) {
                // Fragment needs to know the page's index -> attach it as Bundle
                Bundle args = new Bundle();
                args.putInt(HelpPageFragment.HELP_PAGE_INDEX, position);
                HelpPageFragment helpPageFragment = new HelpPageFragment();
                helpPageFragment.setArguments(args);
                /* why we pass the ID like that:
                * when Fragment is recreated, no-arg constructor will be called,
                * so need to pass our arguments with the setArguments() method
                * instead of through constructor. */

                return helpPageFragment;
            }

            // returns the number of items in our array list
            @Override
            public int getCount() {
                return contents.size();
            }
        });

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            /* not implemented */
            @Override public void onPageScrolled(int position, float positionOffset,
                                                 int positionOffsetPixels) {  }

            // changes the action bar title to display the current page number
            @Override
            public void onPageSelected(int position) {
                setTitle(String.format("Help \t %d/%d", position + 1, contents.size()));
            }

            /* not implemented */
            @Override public void onPageScrollStateChanged(int state) { }
        });
    }

}
