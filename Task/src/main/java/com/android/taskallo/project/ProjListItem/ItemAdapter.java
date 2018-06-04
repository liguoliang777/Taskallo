package com.android.taskallo.project.ProjListItem;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.taskallo.R;
import com.android.taskallo.project.view.ProjListActivity;


public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {


    ProjListActivity context;
    private LayoutInflater from;

    public ItemAdapter(Context context) {
        from = LayoutInflater.from(context);
    }

    public void setContext(ProjListActivity context) {
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return 5;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(from.inflate(R.layout.layout_proj_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {
        holder.recyclerView.setLayoutManager(new ItemLayoutManager(holder.itemView.getContext()));

        RecyclerView.Adapter adapter = new ItemItemAdapter(position, 10);
        holder.recyclerView.setAdapter(adapter);

        holder.mItemAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("", "点击"+context);
            }
        });
        if (holder.recyclerView.getItemDecorationAt(0) == null) {
            holder.recyclerView.addItemDecoration(new ItemLineDecoration());
        }
        holder.recyclerView.getItemAnimator().setAddDuration(0);
        holder.recyclerView.getItemAnimator().setRemoveDuration(0);
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        RecyclerView recyclerView;
        TextView mItemTitle;
        Button mItemAdd;
        private EditText mItemEnterEt;

        ItemViewHolder(View itemView) {
            super(itemView);
            init(itemView);
        }

        private void init(View itemView) {
            recyclerView = itemView.findViewById(R.id.item_recycler_view);
            mItemTitle = itemView.findViewById(R.id.item_title);
            mItemAdd = itemView.findViewById(R.id.item_add);
        }
    }

}
