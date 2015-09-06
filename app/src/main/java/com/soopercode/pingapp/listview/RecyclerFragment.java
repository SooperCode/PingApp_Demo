package com.soopercode.pingapp.listview;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.soopercode.pingapp.MainActivity;
import com.soopercode.pingapp.PrefsManager;
import com.soopercode.pingapp.R;
import com.soopercode.pingapp.background.BackgroundPingManager;
import com.soopercode.pingapp.utils.Utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ria on 6/15/15.
 */
public class RecyclerFragment extends Fragment implements OnAsyncCompleted,
        RecyclerItemClickListener.OnCardClickListener,
        AppBarLayout.OnOffsetChangedListener{

    private static final String TAG = RecyclerFragment.class.getSimpleName();

    private RecyclerAdapter recyclerAdapter;
    private List<PingItem> pingList;
    private Activity activity;
    private boolean nerdViewOn;
    private SwipeRefreshLayout refreshLayout;

    // FOR TESTING:
    private static final String[] DUMMY_HOSTS = {
            "www.google.de",
            "www.google.nl",
            "www.somewebsite.com",
            "www.serverdown.nl",
            "www.hostwithnoname.com",
            "www.yahoo.dk",
            "www.yahoo.com",
            "www.yahoo.nl",
            "www.test.com",
            "www.lalala.de",
            "www.google.com",
            "google.com",
            "google.de",
            "google.nl",
            "google.dk"
    };


    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // ***** FOR TESTING - FILL UP LIST. ************
        PrintWriter printer = null;
        try{
            OutputStream out = activity.openFileOutput(MainActivity.FILENAME, Context.MODE_PRIVATE);
            printer = new PrintWriter(out);
            for(String host : DUMMY_HOSTS){
                printer.println(host);
            }
            printer.flush();
            Log.d(TAG, "written mock list to file");
        }catch (IOException ioe){
            Log.e(TAG, "writing mock host list failed");
        }finally{
            if (printer != null) {
                printer.close();
            }
        }
        // *************************************

        setHasOptionsMenu(true);

        // check for nerd view
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(activity);
        nerdViewOn = sharedPrefs.getBoolean("checkbox_nerdview", false);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_recyclerview, container, false);

        pingList = new ArrayList<>();
        recyclerAdapter = new RecyclerAdapter(activity, pingList, nerdViewOn);
        RecyclerView pingListRecycler = (RecyclerView) view.findViewById(R.id.recyclerview_pinglist);
        pingListRecycler.setHasFixedSize(true);
        pingListRecycler.setLayoutManager(new LinearLayoutManager(activity));
        pingListRecycler.setAdapter(recyclerAdapter);
        pingListRecycler.addOnItemTouchListener(new RecyclerItemClickListener(activity, pingListRecycler, this));

        loadList();

        refreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh_layout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh(){
                //TODO: eventually build in iOS-overscroll effect!
                refreshPingList();
            }
        });
        refreshLayout.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.primary_dark));
        refreshLayout.setEnabled(false);

        return view;
    }

        /**
     * Causes all hosts in the watchlist to be pinged
     * using {@link AsyncListPing}.
     */
    public void refreshPingList(){

        if(Utility.gotConnection(activity)){
            AsyncListPing asyncPinger = new AsyncListPing(this);
            asyncPinger.execute(pingList.toArray(new PingItem[pingList.size()]));
        }else{
            Toast.makeText(activity, getString(R.string.offline_toast),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void loadList(){

        InputStream in = null;
        try{
            in = activity.openFileInput(MainActivity.FILENAME);
            if(in !=null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String hostname;
                while ((hostname = reader.readLine()) != null) {
                    addHostToList(hostname, false);
                }
                recyclerAdapter.notifyDataSetChanged();
            }
        }catch(IOException ioe){
            Log.e(TAG, "loadList(): loading list from file failed " + ioe.toString());
        }finally{
            //if file was empty or not found, set list status empty:
            if(pingList.isEmpty()){
                Log.d(TAG, "loadList(): host list is empty");
                PrefsManager.setPingListEmpty(activity, true);
            }
            if(in != null){
                try { in.close(); } catch (IOException ioe) { ioe.printStackTrace(); }
            }
        }
    }

    private void addHostToList(String validatedHostname, boolean saveList){
        // if this is the first host in the list, set list status from empty to not-empty:
        if(pingList.isEmpty()){
            Log.d(TAG, "addHostToList() - host list is empty");
            PrefsManager.setPingListEmpty(activity, false);
        }
        // make new Ping-Item, add to list, save list to file, ping host
        PingItem host = new PingItem(validatedHostname);
        pingList.add(host);
        if(saveList){
            saveList();
        }
        pingHostFromList(host);
    }

    private void pingHostFromList(PingItem item){

        if(Utility.gotConnection(getActivity())){
            AsyncListPing asyncPinger = new AsyncListPing(this);
            asyncPinger.execute(item);
        }else{
            Toast.makeText(activity, getString(R.string.offline_toast),
                    Toast.LENGTH_SHORT).show();
        }
        recyclerAdapter.notifyDataSetChanged();
    }

    /**
     * Writes the hostname of each item currently contained in our
     * {@code ArrayList} to a local file.
     */
    private void saveList(){
        OutputStreamWriter out = null;
        try{
            out = new OutputStreamWriter(activity.openFileOutput(MainActivity.FILENAME, Context.MODE_PRIVATE));
            for(int i=0; i<pingList.size(); i++){
                out.write(pingList.get(i).getHostname() + "\n");
            }
        }catch(IOException ioe){
            Log.e(TAG, "PLM: writing to file failed", ioe);
        }finally{
            if(out !=null){
                try{ out.close(); }catch(IOException ioe){ ioe.printStackTrace(); }
            }
        }
    }

    /**
     * Called whenever the user has deleted all hosts from the watchlist.
     * Clears all items from our {@code ArrayList}, deletes the local file
     * containing the hostnames and makes sure all background pinging is stopped.
     */
    public void clearList(){
        if(!pingList.isEmpty()){
            pingList.clear();
        }
        PrefsManager.setPingListEmpty(activity, true);

        //tell BGPManager to deactivate bg-pinging if it's active.
        Intent intent = new Intent(activity, BackgroundPingManager.class);
        intent.putExtra("switchOn", 2); //2 = turn it off
        activity.sendBroadcast(intent);

        //delete file
        activity.deleteFile(MainActivity.FILENAME);
        recyclerAdapter.notifyDataSetChanged();
    }

    private void showClearListDialog(){
        AlertDialog dialog = new AlertDialog.Builder(activity).create();
        dialog.setMessage("Clear all hosts from the list?");
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                clearList();
                dialog.dismiss();
            }
        });
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onAsyncPingCompleted(){
        recyclerAdapter.notifyDataSetChanged();
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_deleteList:
                showClearListDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }

    /************ ON CARD CLICK *************/

    @Override
    public void onItemClick(final View view, final int position) {
        // NOT IMPLEMENTED.
        // PINGING SINGLE HOST DOESN'T REALLY MAKE SENSE,
        // SINCE THE WHOLE LIST IS UPDATED ANYWAY.
//        pingHostFromList(pingList.get(position));
    }

    @Override
    public void onCardLongClick(final View view, final int position) {
        new AlertDialog.Builder(activity)
                .setTitle("Confirm")
                .setMessage("Delete '" + pingList.get(position).getHostname() + "'?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pingList.remove(position);
                        if(pingList.isEmpty()){
                            clearList();
                        }else{
                            recyclerAdapter.notifyDataSetChanged();
                            saveList();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setCancelable(true)
                .show();

    }

    /*
    *  need this because we have a Collapsing Toolbar
    *  and only want to refresh if Toolbar is fully expanded. */
    @Override
    public void onOffsetChanged(final AppBarLayout pAppBarLayout, final int index) {
        Log.w(TAG, "onOffsetChanged(index==" + index + ")");
        refreshLayout.setEnabled(index == 0);
    }


    /***************** STUFF LEFT OVER FROM PingListManager ************************/



//
//    /**
//     * Checks if the specified hostname is already contained in our {@code ArrayList}
//     * and calls {@code addHostToList} if it's not.
//     *
//     * @param validatedHostname A valid URL or IP-address
//     */
//    public void addNewHost(String validatedHostname){
//        for(PingItem item : pingList){
//            if(validatedHostname.equals(item.getHostname())){
//                Toast.makeText(mainActivity, mainActivity.getString(R.string.doublehost_toast),
//                        Toast.LENGTH_SHORT).show();
//                return;
//            }
//        }
//        addHostToList(validatedHostname);
//    }
//
//


}
