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
 * Fragment that holds the individual help page on the App's Help Screen.
 *
 * @author  Ria
 */
public class HelpPageFragment extends Fragment {

    private static final String HELP_PAGE_ID = "com.soopercode.pingapp.page_id";
    private HelpContent onePage;

    /**
     * Creates a new instance of this fragment, attaches the specified
     * help page ID to it and returns this instance to the caller.
     *
     * @param pageId    The integer ID of the help page this fragment will hold
     * @return          An instance of this class
     */
    public static HelpPageFragment getNewHelpPageFragment(int pageId){
        Bundle passedData = new Bundle();
        passedData.putInt(HELP_PAGE_ID, pageId);
        HelpPageFragment helpPageFragment = new HelpPageFragment();
        helpPageFragment.setArguments(passedData);
        return helpPageFragment;
            /* why we pass the ID like that:
             * when Fragment is recreated, no-arg constructor will be called,
             * so need to pass our arguments with the setArguments() method
             * instead of through constructor. */
    }

    /**
     * Initializes the state of this fragment.
     * Retrieves the ID set at creation of this instance and sets up
     * the help page associated with this ID.
     *
     * @param savedInstanceState    If the fragment is being re-created from
     *                              a previous saved state, this is the state. [SDK quote]
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int pageId = getArguments().getInt(HELP_PAGE_ID);
        onePage = HelpPages.getInstance(getActivity()).getPage(pageId);
    }

    /**
     * Initializes the UI view of this fragment.
     *
     * @param inflater              The LayoutInflater object that is used to inflate
     *                              the views in this fragment
     * @param container             If non-null, this is the parent view that the fragment's
     *                              UI should be attached to [SDK quote]
     * @param savedInstanceState    If non-null, this fragment is being re-constructed
     *                              from a previous saved state as given here [SDK quote]
     * @return                      The View for the fragment's UI
     */
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //inflate the View from our layout
        View view = inflater.inflate(R.layout.help_fragments, container, false);
        TextView descriptionText = (TextView) view.findViewById(R.id.helpFrag_text);
        ImageView imageView = (ImageView) view.findViewById(R.id.helpFrag_img);
        descriptionText.setPadding(25, 20, 25, 0);
        //set the text & image for this page
        descriptionText.setText(Html.fromHtml(onePage.description));
        imageView.setImageDrawable(onePage.image);

        return view;
    }

}
