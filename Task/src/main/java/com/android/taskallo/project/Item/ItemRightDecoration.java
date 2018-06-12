package com.android.taskallo.project.Item;


import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;


public class ItemRightDecoration extends RecyclerView.ItemDecoration {

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(0, 0, 100, 0);
    }
}
