package com.soopercode.pingapp.listview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.soopercode.pingapp.R;

/**
 * Created by ria on 6/12/15.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerHolder> {

    private String[] dummyItems;

    public RecyclerAdapter(String[] dummyItems){
        this.dummyItems = dummyItems;
    }

    /* create new views - invoked by the layout manager */
    @Override
    public RecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listview_normal_layout, parent, false);

        return new RecyclerHolder(view);
    }

    /* Replace the contents of a view (invoked by the layout manager) */
    @Override
    public void onBindViewHolder(RecyclerHolder holder, int position) {
        holder.host.setText(dummyItems[position]);
        holder.status.setImageResource(R.drawable.green_tick_60x49);
    }

    @Override
    public int getItemCount() {
        return dummyItems.length;
    }

    /* ********************* VIEW HOLDER ********************** */

    public static class RecyclerHolder extends RecyclerView.ViewHolder{

        TextView host;
        ImageView status;

        public RecyclerHolder(View itemView) {
            super(itemView);
            host = (TextView)itemView.findViewById(R.id.listview_host);
            status = (ImageView)itemView.findViewById(R.id.listview_status);
        }
    }
}
