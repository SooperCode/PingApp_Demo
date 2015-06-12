package com.soopercode.pingapp.listview;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.soopercode.pingapp.MainActivity;
import com.soopercode.pingapp.OnAsyncCompleted;
import com.soopercode.pingapp.PrefsManager;
import com.soopercode.pingapp.R;
import com.soopercode.pingapp.background.BackgroundPingManager;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages the watchlist displayed by {@link MainActivity}.
 * Can be viewed as MainActivity's Little Helper.
 *
 * @author  Ria
 */
public class PingListManager implements OnItemClickListener,
                                            OnItemLongClickListener, OnAsyncCompleted {

    private static final int MODE_NORMAL = 1;
    private static final int MODE_NERD = 2;

    private Activity context;
    private ArrayAdapter pingListAdapter;
    private List<PingItem> pingList = new ArrayList<>();
    private RecyclerView pingListRecycler;
    private boolean nerdViewOn;

    // FOR TESTING:
    private String[] dummyHosts = {
            "www.google.de",
            "www.google.nl",
            "www.google.dk",
            "www.google.com",
            "www.google.at",
            "www.yahoo.dk",
            "www.yahoo.com",
            "www.yahoo.nl",
            "www.test.com",
            "www.lalala.de",
            "www.hostwithnoname.com",
            "google.com",
            "google.de",
            "google.nl",
            "google.dk"
    };

    /**
     * Creates a new PingListManager with reference to MainActivity
     * and the {@link ListView} this manager is going to manage.
     *
     * @param context       MainActivity's Context
     * @param pingListView  A reference to the ListView containing the watchlist
     */
    public PingListManager(Activity context, RecyclerView pingListView){
        this.context = context; //TODO: refactor this code - it hurts people.
        this.pingListRecycler = pingListView;
    }

    /**
     * Sets up the ListView that displays the user's watchlist.
     *
     * @param nerdViewOn    States whether Nerd View is activated or not
     */
    public void createListView(boolean nerdViewOn){

        if(!nerdViewOn) {
            pingListAdapter = new PingListAdapter(context, pingList);
            this.nerdViewOn = false;
        }else{
            pingListAdapter = new PingListNerdAdapter(context, pingList);
            this.nerdViewOn = true;
        }
//        pingListView.setAdapter(pingListAdapter);
//        pingListView.setOnItemClickListener(this);
//        pingListView.setOnItemLongClickListener(this);
        pingListRecycler.setHasFixedSize(true);
        pingListRecycler.setLayoutManager(new LinearLayoutManager(context));
        pingListRecycler.setAdapter(new RecyclerAdapter(dummyHosts));
    }

    /**
     * Called when an item in the watchlist has been clicked.
     * Refreshes the item's state by calling {@code pingHostFromList}.
     *
     * @param parent    The AdapterView where the click happened [quote]
     * @param view      The View within the AdapterView that was clicked [quote]
     * @param position  The position of the item clicked
     * @param id        The row id of the item clicked
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        pingHostFromList(pingList.get(position));
    }

    /**
     * Called when an item in the watchlist has been clicked and held.
     * Opens a dialog with the option to delete this item from the list.
     *
     * @param parent    The AdapterView where the click happened [quote]
     * @param view      The View within the AdapterView that was clicked [quote]
     * @param position  The position of the item clicked
     * @param id        The row id of the item clicked
     * @return          true to indicate that this callback has consumed the long click
     *                  (false would cause {@code onItemClick} to be invoked)
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        new AlertDialog.Builder(context)
                .setTitle("Confirm")
                .setMessage("Delete '" + pingList.get(position).getHostname() + "'?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pingList.remove(position);
                        if(pingList.isEmpty()){
                            clearList();
                        }else{
                            pingListAdapter.notifyDataSetChanged();
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
        return true;
    }

    /**
     * Reads in the hostnames from a locally saved file
     * and calls {@code addHostToList} for each host.
     */
    public void loadList(){
        // FOR TESTING - TO FILL UP LIST.
        for(String host : dummyHosts){
            addHostToList(host);
        }
        pingListAdapter.notifyDataSetChanged();

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
//            Log.e(MainActivity.TAG, "PLM: loading list from file failed " + ioe.toString());
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

    /**
     * Writes the hostname of each item currently contained in our
     * {@code ArrayList} to a local file.
     */
    private void saveList(){
        OutputStreamWriter out = null;
        try{
            out = new OutputStreamWriter(context.openFileOutput(MainActivity.FILENAME, Context.MODE_PRIVATE));
            for(int i=0; i<pingList.size(); i++){
                out.write(pingList.get(i).getHostname() + "\n");
            }
        }catch(IOException ioe){
            Log.e(MainActivity.TAG, "PLM: writing to file failed", ioe);
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
        PrefsManager.setPingListEmpty(context, true);

        //tell BGPManager to deactivate bg-pinging if it's active.
        Intent intent = new Intent(context, BackgroundPingManager.class);
        intent.putExtra("switchOn", 2); //2 = turn it off
        context.sendBroadcast(intent);
        //turn off light
//        ImageView light = (ImageView)context.findViewById(R.id.imgWatchlistOn);
//        light.setImageDrawable(context.getResources().getDrawable(R.drawable.off_30x30));
//        pingListAdapter.notifyDataSetChanged();
        //delete file
        context.deleteFile(MainActivity.FILENAME);
    }

    /**
     * Checks if the specified hostname is already contained in our {@code ArrayList}
     * and calls {@code addHostToList} if it's not.
     *
     * @param validatedHostname A valid URL or IP-address
     */
    public void addNewHost(String validatedHostname){
        for(PingItem item : pingList){
            if(validatedHostname.equals(item.getHostname())){
                Toast.makeText(context, context.getString(R.string.doublehost_toast), Toast.LENGTH_SHORT).show();
                return;
            }
        }
        addHostToList(validatedHostname);
    }

    /**
     * Creates a new {@link PingItem} using the specified hostname
     * and adds it to the watchlist.
     *
     * @param validatedHostname     A valid URL or IP-address
     */
    private void addHostToList(String validatedHostname){
        // if this is the first host in the list, set list status from empty to not-empty:
        if(pingList.isEmpty()) {
            PrefsManager.setPingListEmpty(context, false);
        }
        // make new Ping-Item, add to list, save list to file, ping host
        PingItem host = new PingItem(validatedHostname);
        pingList.add(host);
        pingListAdapter.notifyDataSetChanged();
        saveList();
        pingHostFromList(host);
    }

    /**
     * Causes the specified item in the watchlist to be pinged
     * using {@link AsyncListPing}
     *
     * @param item  The {@link PingItem} representing the host to be pinged
     */
    private void pingHostFromList(PingItem item){
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if(netInfo !=null && netInfo.isConnected()){
            AsyncListPing asyncPinger = new AsyncListPing(context, this,
                                    (nerdViewOn)? MODE_NERD : MODE_NORMAL);
            asyncPinger.execute(item);
        }else{
            Toast.makeText(context, context.getString(R.string.offline_toast), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Causes all hosts in the watchlist to be pinged
     * using {@link AsyncListPing}.
     */
    public void pingListNow(){
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if(netInfo !=null && netInfo.isConnected()){
            AsyncListPing asyncPinger = new AsyncListPing(context, this,
                                    (nerdViewOn)? MODE_NERD : MODE_NORMAL);
            asyncPinger.execute(pingList.toArray(new PingItem[pingList.size()]));
        }else{
            Toast.makeText(context, context.getString(R.string.offline_toast), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Called after {@link AsyncListPing} has completed a pinging task.
     * Specifies stuff to be done in case pinging was successful, in this
     * case our watchlist view is caused to be updated.
     *
     * @param okay  boolean indicating that pinging operation was successfully completed
     */
    @Override
    public void onAsyncPingCompleted(boolean okay) {
        if(okay){
            pingListAdapter.notifyDataSetChanged();
        }

    }

}
