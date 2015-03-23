package com.soopercode.pingapp.listview;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.soopercode.pingapp.R;
import com.soopercode.pingapp.utils.ResponseCodeEvaluator;

import java.util.List;

/**
 * Manages the layout of the watchlist displayed
 * in {@link com.soopercode.pingapp.MainActivity}
 * when the view of the list is set to "Nerd View".
 * Displays hostname, IP-address and server response messages.
 * This Adapter is used as the default adapter for the watchlist.
 *
 * @author  Ria
 */
public class PingListNerdAdapter extends ArrayAdapter<PingItem>{

    private Context context;
    private ResponseCodeEvaluator evaluator;

    /**
     * This ViewHolder is used for View caching;
     * it holds the widgets of each row.
     * The "ViewHolder Pattern" optimizes performance
     * and allows for smoother scrolling, according to Android.
     */
    private static class ViewHolder{
        TextView host;
        TextView hostIP;
        TextView responseCode;
        TextView responseText;
    }

    /**
     * Creates an instance of this Adapter, defining its layout.
     * Will be called by {@link PingListManager}.
     *
     * @param context   Current context (MainActivity's Context)
     * @param pingList  ArrayList of PingItems stored by PingListManager
     */
    public PingListNerdAdapter(Context context, List<PingItem> pingList){
        super(context, R.layout.listview_nerd_layout, pingList);
        this.context = context;
        // set up ResponseCodeEvaluator - is used each time getView() is called,
        // in order to get the appropriate string resources to be displayed
        evaluator = new ResponseCodeEvaluator(context);
    }

    /**
     * Creates the view for each item in the list,
     * according to the row layout defined and the specified list position.
     *
     * @param position      The position of the item to be displayed
     * @param convertView   The old view to reuse, if possible [SDK quote]
     * @param parent        The parent that this view will eventually be
     *                      attached to [SDK quote]
     * @return              A view corresponding to the data at the specified position [SDK quote]
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView==null){
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.listview_nerd_layout, parent, false);
            // set up view holder:
            viewHolder = new ViewHolder();
            viewHolder.host = (TextView)convertView.findViewById(R.id.nerdview_hostname);
            viewHolder.hostIP = (TextView)convertView.findViewById(R.id.nerdview_hostIP);
            viewHolder.responseCode = (TextView)convertView.findViewById(R.id.nerdview_responseCode);
            viewHolder.responseText = (TextView)convertView.findViewById(R.id.nerdview_responseText);
            convertView.setTag(viewHolder); //stores the holder with the view
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        PingItem pingItem = getItem(position);
        viewHolder.host.setText(pingItem.getHostname());
        int responseCode = pingItem.getResponseCode();
        viewHolder.responseCode.setText(String.format("%03d", responseCode));

        // set Color according to response code:
        if(100 <= responseCode && responseCode <= 199){
            viewHolder.responseCode.setTextColor(Color.GRAY);
        }else if(200 <= responseCode && responseCode <= 399){
            viewHolder.responseCode.setTextColor(Color.GREEN);
        }else if(400 <= responseCode && responseCode <= 499){
            viewHolder.responseCode.setTextColor(Color.YELLOW);
        }else if(500 <= responseCode && responseCode <= 599){
            viewHolder.responseCode.setTextColor(Color.BLUE);
        }else{
            viewHolder.responseCode.setTextColor(Color.RED);
        }
        viewHolder.hostIP.setText(pingItem.getIp());
        // use the evaluator to get the appropriate text to display
        viewHolder.responseText.setText(evaluator.generateResponse(responseCode));

        return convertView;
    }
}
