package com.soopercode.pingapp.listview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Created by ria on 9/6/15.
 */
public class SwipeRefreshableRecyclerView extends RecyclerView{

    public SwipeRefreshableRecyclerView(Context context){
        super(context);
    }

    public SwipeRefreshableRecyclerView(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public SwipeRefreshableRecyclerView(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
    }

    @Override
    public boolean canScrollVertically(int direction){
        Log.wtf("TAG", "canScrollVertically original: " + super.canScrollVertically(direction));
        // check if scrolling up
        if(direction < 1){
            Log.w("TAG", "direction = " + direction);
            boolean original = super.canScrollVertically(direction);
            Log.w("TAG", "getChildAt(0): " + getChildAt(0));
            Log.w("TAG", "getChildAt(0).getTop(): " + getChildAt(0).getTop());
            Log.wtf("tag", "returning: " + (!original && getChildAt(0) != null && getChildAt(0).getTop() < 0 || original));
            return !original && getChildAt(0) != null && getChildAt(0).getTop() < 0 || original;
        }
        return super.canScrollVertically(direction);
    }
}
