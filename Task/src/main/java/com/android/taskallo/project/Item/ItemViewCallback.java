package com.android.taskallo.project.Item;

import android.support.v7.widget.RecyclerView;

import com.android.taskallo.bean.BoardVOListBean;


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
    public BoardVOListBean onRemoved(RecyclerView recyclerView, int position) {
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        BoardVOListBean data = new BoardVOListBean();
        if (adapter instanceof ItemItemAdapter) {
            data = ((ItemItemAdapter) adapter).remove(position);
        }
        adapter.notifyItemRemoved(position);
        return data;
    }

    @Override
    public void onInserted(RecyclerView recyclerView, int position, BoardVOListBean data) {
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        if (adapter instanceof ItemItemAdapter) {
            ((ItemItemAdapter) adapter).add(position, data);
        }
        adapter.notifyItemInserted(position);
    }
}
