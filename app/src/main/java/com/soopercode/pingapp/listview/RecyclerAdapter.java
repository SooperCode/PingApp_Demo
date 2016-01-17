package com.soopercode.pingapp.listview;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
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

    private final List<PingItem> pingItems;
    private final boolean nerdViewOn;
    private final Context context;

    public RecyclerAdapter(final Context context, final List<PingItem> pingItems, final boolean nerdViewOn) {
        this.context = context;
        this.pingItems = pingItems;
        this.nerdViewOn = nerdViewOn;
    }

    /* Create new views - invoked by recycler view's layout manager */
    @Override
    public RecyclerHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {

        final View view = LayoutInflater.from(parent.getContext()).inflate(
                nerdViewOn ? R.layout.cardview_nerd_layout : R.layout.cardview_layout,
                parent, false);
        //    view.setOnClickListener(new OnCardClickListener(this));

        return new RecyclerHolder(view);
    }

    /* Replace the contents of a view (invoked by the layout manager) */
    @Override
    public void onBindViewHolder(final RecyclerHolder holder, final int position) {

        // if last item is visible, create "padding" on the bottom
        // to enable scrolling the last item above the floating action button
        holder.invisibleView.setVisibility(position == pingItems.size() - 1 ?
                View.INVISIBLE : View.GONE);

        final PingItem pingItem = pingItems.get(position);
        if (!nerdViewOn) {
            holder.host.setText(pingItem.getHostname());
            if (pingItem.isAvailable()) {
                holder.status.setImageResource(R.drawable.green_tick_60x49);
                holder.host.setTextColor(Color.WHITE);
            } else {
                holder.status.setImageResource(R.drawable.red_cross_48x48);
                holder.host.setTextColor(Color.RED);
            }
        } else {
            holder.hostNerd.setText(pingItem.getHostname());
            int responseCode = pingItem.getResponseCode();
            holder.hostNerd.setTextColor(pingItem.isAvailable() ? Color.WHITE : Color.RED);
            holder.responseCode.setText(String.format("%03d", responseCode));
            holder.responseCode.setTextColor(getResponseColor(responseCode));
            holder.hostIp.setText(pingItem.getIp());
            // use the evaluator to get the appropriate text to display
            holder.responseText.setText(
                    ResponseCodeEvaluator.generateResponse(context, responseCode));
        }
    }

    @Override
    public int getItemCount() {
        return pingItems.size();
    }

    private int getResponseColor(final int responseCode) {
        // choose Color according to status code category:
        if (100 <= responseCode && responseCode <= 199) {
            return Color.GRAY;
        } else if (200 <= responseCode && responseCode <= 399) {
            return Color.GREEN;
        } else if (400 <= responseCode && responseCode <= 499) {
            return Color.YELLOW;
        } else if (500 <= responseCode && responseCode <= 599) {
            return Color.BLUE;
        } else {
            return Color.RED;
        }
    }

    /* ********************* VIEW HOLDER ********************** */

    public static class RecyclerHolder extends RecyclerView.ViewHolder {

        // normal view
        final TextView host;
        final ImageView status;

        // nerd view
        final TextView hostNerd;
        final TextView hostIp;
        final TextView responseCode;
        final TextView responseText;

        // invisible view - simulates bottom margin in last item
        final View invisibleView;

        public RecyclerHolder(final View itemView) {
            super(itemView);

            host = (TextView) itemView.findViewById(R.id.listview_host);
            status = (ImageView) itemView.findViewById(R.id.listview_status);

            hostNerd = (TextView) itemView.findViewById(R.id.nerdview_hostname);
            hostIp = (TextView) itemView.findViewById(R.id.nerdview_hostIP);
            responseCode = (TextView) itemView.findViewById(R.id.nerdview_responseCode);
            responseText = (TextView) itemView.findViewById(R.id.nerdview_responseText);

            invisibleView = itemView.findViewById(R.id.view_invisible);
        }
    }

}
