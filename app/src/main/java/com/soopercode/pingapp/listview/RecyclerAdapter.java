package com.soopercode.pingapp.listview;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.soopercode.pingapp.R;
import com.soopercode.pingapp.utils.ResponseCodeEvaluator;

import java.util.List;

/**
 * Created by ria on 6/12/15.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerHolder> {

    private static final String TAG = RecyclerAdapter.class.getSimpleName();

    private List<PingItem> pingItems;
    private boolean nerdViewOn;
    private ResponseCodeEvaluator evaluator;

    public RecyclerAdapter(Context context, List<PingItem> pingItems, boolean nerdViewOn){
        this.pingItems = pingItems;
        this.nerdViewOn = nerdViewOn;
        evaluator = new ResponseCodeEvaluator(context);
    }

    /* Create new views - invoked by recycler view's layout manager */
    @Override
    public RecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        if(!nerdViewOn){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.cardview_layout, parent, false);
        }else{ // TODO: refactor into just one cardview layout...
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.cardview_nerd_layout, parent, false);
        }
        return new RecyclerHolder(view);
    }

    /* Replace the contents of a view (invoked by the layout manager) */
    @Override
    public void onBindViewHolder(RecyclerHolder holder, int position) {
        PingItem pingItem = pingItems.get(position);
        if(!nerdViewOn){
            holder.host.setText(pingItem.getHostname());
            if(pingItem.isAvailable()){
                holder.status.setImageResource(R.drawable.green_tick_60x49);
                holder.host.setTextColor(Color.WHITE);
            }else{
                holder.status.setImageResource(R.drawable.red_cross_48x48);
                holder.host.setTextColor(Color.RED);
            }
        }else{
            holder.hostNerd.setText(pingItem.getHostname());
            int responseCode = pingItem.getResponseCode();
            holder.responseCode.setText(String.format("%03d", responseCode));
            holder.responseCode.setTextColor(getResponseColor(responseCode));
            holder.hostIp.setText(pingItem.getIp());
            // use the evaluator to get the appropriate text to display
            holder.responseText.setText(evaluator.generateResponse(responseCode));
        }
    }

    @Override
    public int getItemCount() {
        return pingItems.size();
    }

    private int getResponseColor(int responseCode){
        // choose Color according to status code category:
        if(100 <= responseCode && responseCode <= 199){
            return Color.GRAY;
        }else if(200 <= responseCode && responseCode <= 399){
            return Color.GREEN;
        }else if(400 <= responseCode && responseCode <= 499){
            return Color.YELLOW;
        }else if(500 <= responseCode && responseCode <= 599){
            return Color.BLUE;
        }else{
            return Color.RED;
        }
    }

    /* ********************* VIEW HOLDER ********************** */

    public static class RecyclerHolder extends RecyclerView.ViewHolder{

        // normal view
        TextView host;
        ImageView status;

        // nerd view
        TextView hostNerd;
        TextView hostIp;
        TextView responseCode;
        TextView responseText;

        public RecyclerHolder(View itemView) {
            super(itemView);
            host = (TextView)itemView.findViewById(R.id.listview_host);
            status = (ImageView)itemView.findViewById(R.id.listview_status);

            hostNerd = (TextView)itemView.findViewById(R.id.nerdview_hostname);
            hostIp = (TextView)itemView.findViewById(R.id.nerdview_hostIP);
            responseCode = (TextView)itemView.findViewById(R.id.nerdview_responseCode);
            responseText = (TextView)itemView.findViewById(R.id.nerdview_responseText);
        }
    }
}
