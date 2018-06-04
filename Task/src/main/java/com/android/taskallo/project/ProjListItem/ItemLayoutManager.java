package com.android.taskallo.project.ProjListItem;


import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;


public class ItemLayoutManager extends LinearLayoutManager {

    ItemLayoutManager(Context context) {
        super(context);
    }

    ItemLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    @SuppressWarnings("unused")
    public ItemLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (Exception e) {
            Log.e(ItemProvider.TAG, "onLayoutChildren: " + e.getMessage());
        }
    }

}
