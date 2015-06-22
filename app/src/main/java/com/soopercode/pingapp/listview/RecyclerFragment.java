package com.soopercode.pingapp.listview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.soopercode.pingapp.MainActivity;
import com.soopercode.pingapp.PrefsManager;
import com.soopercode.pingapp.R;
import com.soopercode.pingapp.background.BackgroundPingManager;
import com.soopercode.pingapp.utils.Utility;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ria on 6/15/15.
 */
public class RecyclerFragment extends Fragment implements OnAsyncCompleted{

    private static final String TAG = RecyclerFragment.class.getSimpleName();

    private RecyclerAdapter recyclerAdapter;
    private List<PingItem> pingList;
    private Activity activity;
    private boolean nerdViewOn;
    private SwipeRefreshLayout refreshLayout;

    // FOR TESTING:
    private static final String[] dummyHosts = {
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

        // initialize ping list
        pingList = new ArrayList<>();
        loadList();
        // check for nerd view
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(activity);
        nerdViewOn = sharedPrefs.getBoolean("checkbox_nerdview", false);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_recyclerview, container, false);

        recyclerAdapter = new RecyclerAdapter(activity, pingList, nerdViewOn);
        RecyclerView pingListRecycler = (RecyclerView) view.findViewById(R.id.recyclerview_pinglist);
        pingListRecycler.setHasFixedSize(true);
        pingListRecycler.setLayoutManager(new LinearLayoutManager(activity));
        pingListRecycler.setAdapter(recyclerAdapter);

        refreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh_layout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh(){
                Toast.makeText(activity, "refreshed!", Toast.LENGTH_SHORT).show();
                //TODO: refresh list. ...and eventually build in iOS-overscroll effect!
                refresh();
            }
        });
        refreshLayout.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.primary_dark));

        return view;
    }

    private void refresh(){
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run(){
                refreshLayout.setRefreshing(false);
            }
        }, 2000);
    }


    private void loadList(){
        // FOR TESTING - TO FILL UP LIST.
        for(String host : dummyHosts){
            addHostToList(host);
        }
//        recyclerAdapter.notifyDataSetChanged();

//        InputStream in = null;
//        try{
//            in = context.openFileInput(MainActivity.FILENAME);
//            if(in !=null) {
//                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//                String hostname;
//                while ((hostname = reader.readLine()) != null) {
//                    addHostToList(hostname);
//                }
//                pingListAdapter.notifyDataSetChanged();
//            }
//        }catch(IOException ioe){
//            Log.e(TAG, "PLM: loading list from file failed " + ioe.toString());
//        }finally{
//            //if file was empty or not found, set list status empty:
//            if(pingList.isEmpty()){
//                PrefsManager.setPingListEmpty(context, true);
//            }
//            if(in != null){
//                try { in.close(); } catch (IOException ioe) { ioe.printStackTrace(); }
//            }
//        }
    }

    private void addHostToList(String validatedHostname){
        // if this is the first host in the list, set list status from empty to not-empty:
        if(pingList.isEmpty()){
            PrefsManager.setPingListEmpty(activity, false);
        }
        // make new Ping-Item, add to list, save list to file, ping host
        PingItem host = new PingItem(validatedHostname);
        pingList.add(host);
//        recyclerAdapter.notifyDataSetChanged();
        //saveList();
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
    }

    @Override
    public void onAsyncPingCompleted(){
        recyclerAdapter.notifyDataSetChanged();
    }


    /***************** STUFF LEFT OVER FROM PingListManager ************************/

//
////    @Override
////    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
////        pingHostFromList(pingList.get(position));
////    }
//
//
////    @Override
////    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
////        new AlertDialog.Builder(context)
////                .setTitle("Confirm")
////                .setMessage("Delete '" + pingList.get(position).getHostname() + "'?")
////                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
////                    @Override
////                    public void onClick(DialogInterface dialog, int which) {
////                        pingList.remove(position);
////                        if(pingList.isEmpty()){
////                            clearList();
////                        }else{
////                            recyclerAdapter.notifyDataSetChanged();
////                            saveList();
////                        }
////                    }
////                })
////                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
////                    @Override
////                    public void onClick(DialogInterface dialog, int which) {
////                        dialog.cancel();
////                    }
////                })
////                .setCancelable(true)
////                .show();
////        return true;
////    }
//
//
//
//    /**
//     * Writes the hostname of each item currently contained in our
//     * {@code ArrayList} to a local file.
//     */
//    private void saveList(){
//        OutputStreamWriter out = null;
//        try{
//            out = new OutputStreamWriter(mainActivity.openFileOutput(MainActivity.FILENAME, Context.MODE_PRIVATE));
//            for(int i=0; i<pingList.size(); i++){
//                out.write(pingList.get(i).getHostname() + "\n");
//            }
//        }catch(IOException ioe){
//            Log.e(TAG, "PLM: writing to file failed", ioe);
//        }finally{
//            if(out !=null){
//                try{ out.close(); }catch(IOException ioe){ ioe.printStackTrace(); }
//            }
//        }
//    }
//
//    /**
//     * Called whenever the user has deleted all hosts from the watchlist.
//     * Clears all items from our {@code ArrayList}, deletes the local file
//     * containing the hostnames and makes sure all background pinging is stopped.
//     */
//    public void clearList(){
//        if(!pingList.isEmpty()){
//            pingList.clear();
//        }
//        PrefsManager.setPingListEmpty(mainActivity.getApplicationContext(), true);
//
//        //tell BGPManager to deactivate bg-pinging if it's active.
//        Intent intent = new Intent(mainActivity, BackgroundPingManager.class);
//        intent.putExtra("switchOn", 2); //2 = turn it off
//        mainActivity.sendBroadcast(intent);
//        //turn off light
////        ImageView light = (ImageView)context.findViewById(R.id.imgWatchlistOn);
////        light.setImageDrawable(context.getResources().getDrawable(R.drawable.off_30x30));
////        pingListAdapter.notifyDataSetChanged();
//        //delete file
//        mainActivity.deleteFile(MainActivity.FILENAME);
//    }
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
//
//    /**
//     * Causes the specified item in the watchlist to be pinged
//     * using {@link AsyncListPing}
//     *
//     * @param item  The {@link PingItem} representing the host to be pinged
//     */
//    private void pingHostFromList(PingItem item){
//
//        if(Utility.gotConnection(mainActivity.getApplicationContext())){
//            AsyncListPing asyncPinger = new AsyncListPing(this);
//            asyncPinger.execute(item);
//        }else{
//            Toast.makeText(mainActivity, mainActivity.getString(R.string.offline_toast),
//                    Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    /**
//     * Causes all hosts in the watchlist to be pinged
//     * using {@link AsyncListPing}.
//     */
//    public void pingListNow(){
//
//        if(Utility.gotConnection(mainActivity.getApplicationContext())){
//            AsyncListPing asyncPinger = new AsyncListPing(this);
//            asyncPinger.execute(pingList.toArray(new PingItem[pingList.size()]));
//        }else{
//            Toast.makeText(mainActivity, mainActivity.getString(R.string.offline_toast),
//                    Toast.LENGTH_SHORT).show();
//        }
//    }
}
