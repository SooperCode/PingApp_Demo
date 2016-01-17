package com.soopercode.pingapp.help;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.soopercode.pingapp.R;

/**
 *
 * --> TEMPORARILY TAKING OUT THE HELP PAGES FOR VERSION 2.0
 *
 * Fragment that holds the individual help page on the App's Help Screen.
 *
 * @author Ria
 */
public class HelpPageFragment /* extends Fragment */ {

//    public static final String HELP_PAGE_INDEX = "helppage_index";
//    private HelpPages.HelpContent currentPage;
//
//    /**
//     * Initializes the state of this fragment.
//     * Retrieves the ID set at creation of this instance and sets up
//     * the help page associated with this ID.
//     *
//     * @param savedInstanceState If the fragment is being re-created from
//     *                           a previous saved state, this is the state. [SDK quote]
//     */
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        int pageIndex = getArguments().getInt(HELP_PAGE_INDEX);
//        currentPage = HelpPages.getInstance(getActivity()).getPage(pageIndex);
//    }
//
//    /**
//     * Initializes the UI view of this fragment.
//     *
//     * @param inflater           The LayoutInflater object that is used to inflate
//     *                           the views in this fragment
//     * @param container          If non-null, this is the parent view that the fragment's
//     *                           UI should be attached to [SDK quote]
//     * @param savedInstanceState If non-null, this fragment is being re-constructed
//     *                           from a previous saved state as given here [SDK quote]
//     * @return The View for the fragment's UI
//     */
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
//                             @Nullable Bundle savedInstanceState) {
//        //inflate the View from our layout
//        View view = inflater.inflate(R.layout.help_fragments, container, false);
//        TextView descriptionText = (TextView) view.findViewById(R.id.helpFrag_text);
//        ImageView imageView = (ImageView) view.findViewById(R.id.helpFrag_img);
//        descriptionText.setPadding(25, 20, 25, 0);
//        //set the text & image for this page
//        descriptionText.setText(Html.fromHtml(currentPage.description));
//        imageView.setImageDrawable(currentPage.image);
//
//        return view;
//    }

}
