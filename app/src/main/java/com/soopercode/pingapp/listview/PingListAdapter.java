package com.soopercode.pingapp.listview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.soopercode.pingapp.R;

import java.util.List;

/**
 * Manages the layout of the watchlist displayed
 * by {@link com.soopercode.pingapp.MainActivity}
 * when the view of the list is set to normal.
 * Displays hostname and status of availability for each item.
 * This Adapter is used as the default adapter for the watchlist.
 *
 * @author  Ria
 */
public class PingListAdapter extends ArrayAdapter<PingItem>{

    private Context context;
    private int defaultTextColor;

    /**
     * This ViewHolder is used for View caching;
     * it holds the widgets of each row.
     * The "ViewHolder Pattern" optimizes performance
     * and allows for smoother scrolling, according to Android.
     */
    private static class ViewHolder{
        TextView host;
        ImageView status;
    }

    /**
     * Creates an instance of this Adapter, defining its layout.
     * Will be called by {@link PingListManager}.
     *
     * @param context   Current context (MainActivity's Context)
     * @param pingList  ArrayList of PingItems stored by PingListManager
     */
    public PingListAdapter(Context context, List<PingItem> pingList) {
        super(context, R.layout.listview_normal_layout, pingList);
        this.context = context;
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
            convertView = inflater.inflate(R.layout.listview_normal_layout, parent, false);
            //set up view holder
            viewHolder = new ViewHolder();
            viewHolder.host = (TextView)convertView.findViewById(R.id.listview_host);
            defaultTextColor = viewHolder.host.getTextColors().getDefaultColor();
            viewHolder.status = (ImageView)convertView.findViewById(R.id.listview_status);
            convertView.setTag(viewHolder); //stores the holder with the view
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        PingItem pingItem = getItem(position);

        viewHolder.host.setText(pingItem.getHostname());
        Drawable redCross = context.getResources().getDrawable(R.drawable.red_cross_48x48);
        Drawable greenTick = context.getResources().getDrawable(R.drawable.green_tick_60x49);

        if(pingItem.isAvailable()){
            viewHolder.status.setImageDrawable(greenTick);
            if(defaultTextColor !=0){
                viewHolder.host.setTextColor(defaultTextColor);
            }else{
                defaultTextColor = viewHolder.host.getTextColors().getDefaultColor();
            }
        }else{
            viewHolder.status.setImageDrawable(redCross);
            viewHolder.host.setTextColor(Color.RED);
        }

        return convertView;
    }
}
