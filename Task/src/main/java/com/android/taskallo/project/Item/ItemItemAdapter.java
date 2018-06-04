package com.android.taskallo.project.Item;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.taskallo.R;
import com.android.taskallo.project.view.CardDetailActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ItemItemAdapter extends RecyclerView.Adapter<ItemItemAdapter.SimpleViewHolder> {

    private List<String> mData = new ArrayList<>();
    private Context context;

    ItemItemAdapter(int index, int count) {
        for (int i = 0; i < count; i++) {
            mData.add(String.valueOf(index) + String.valueOf(i));
        }
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.layout_proj_item_item, parent,
                false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, int position) {
        holder.mTextView.setText(mData.get(position));
        if (Long.parseLong(mData.get(position)) == ItemProvider.getInstance().getSelectedId()) {
            holder.itemView.setVisibility(View.INVISIBLE);
        } else {
            holder.itemView.setVisibility(View.VISIBLE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CardDetailActivity.class);
                // intent.putExtra(KeyConstant.id,);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    int getPositionFromId() {
        for (int i = 0; i < mData.size(); i++) {
            if (Long.parseLong(mData.get(i)) == ItemProvider.getInstance().getSelectedId()) {
                return i;
            }
        }
        return RecyclerView.NO_POSITION;
    }

    String remove(int position) {
        return mData.remove(position);
    }

    void add(int position, String data) {
        mData.add(position, data);
    }

    void swap(int fromPos, int toPos) {
        Collections.swap(mData, fromPos, toPos);
    }

    static class SimpleViewHolder extends RecyclerView.ViewHolder {

        TextView mTextView;

        SimpleViewHolder(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.content);
        }
    }

    long getIdByPosition(int position) {
        if (position > mData.size() - 1) {
            return RecyclerView.NO_ID;
        }
        return Long.parseLong(mData.get(position));
    }

}
