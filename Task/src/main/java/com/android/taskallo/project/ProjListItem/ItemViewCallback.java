package com.android.taskallo.project.ProjListItem;

import android.support.v7.widget.RecyclerView;


public class ItemViewCallback implements ItemView.Callback {

    @Override
    public void onMoved(RecyclerView recyclerView, int from, int to) {
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        if (adapter instanceof ItemItemAdapter) {
            ((ItemItemAdapter) adapter).swap(from, to);
        }
        adapter.notifyItemMoved(from, to);
    }

    @Override
    public String onRemoved(RecyclerView recyclerView, int position) {
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        String data = "error data";
        if (adapter instanceof ItemItemAdapter) {
            data = ((ItemItemAdapter) adapter).remove(position);
        }
        adapter.notifyItemRemoved(position);
        return data;
    }

    @Override
    public void onInserted(RecyclerView recyclerView, int position, String data) {
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        if (adapter instanceof ItemItemAdapter) {
            ((ItemItemAdapter) adapter).add(position, data);
        }
        adapter.notifyItemInserted(position);
    }
}
