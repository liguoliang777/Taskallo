package com.android.taskallo.project.Item;


import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.taskallo.R;
import com.android.taskallo.core.utils.KeyConstant;
import com.android.taskallo.project.view.CardDetailActivity;
import com.android.taskallo.project.view.ProjListActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ItemItemAdapter extends RecyclerView.Adapter<ItemItemAdapter.SimpleViewHolder> {

    private List<String> mData = new ArrayList<>();
    ProjListActivity context;
    private int width, height, margin;
    private View codeBtn;
    private LinearLayout.LayoutParams layoutParams;

    ItemItemAdapter(ProjListActivity c, int index, int count) {
        context = c;
        width = context.getResources().getDimensionPixelSize(R.dimen.dm044);
        height = context.getResources().getDimensionPixelSize(R.dimen.dm023);
        margin = context.getResources().getDimensionPixelSize(R.dimen.dm012);
        layoutParams = new LinearLayout.LayoutParams(width, height);
        layoutParams.setMargins(0, 0, margin, margin);//4个参数按顺序分别是左上右下
        for (int i = 0; i < count; i++) {
            mData.add(String.valueOf(index) + String.valueOf(i));
        }
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_proj_item_item, parent,
                false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, final int position) {
        if (mData == null) {
            return;
        }
        String titleStr = mData.get(position);
        holder.mItemTitleTv.setText(titleStr == null ? "" : titleStr);
        if (Long.parseLong(titleStr) == ItemProvider.getInstance().getSelectedId()) {
            holder.itemView.setVisibility(View.INVISIBLE);
        } else {
            holder.itemView.setVisibility(View.VISIBLE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CardDetailActivity.class);
                intent.putExtra(KeyConstant.cardId, position + "");
                intent.putExtra(KeyConstant.cardTitle, position + "");
                context.startActivity(intent);
            }
        });
        if (labelColorIdArr == null || labelColorIdArr.length == 0) {
        } else {
            setLabelColorLayout(labelColorIdArr, holder.mItemItemLabelLayout);
        }
        int childCount = holder.mItemItemMentionPeopleLayout.getChildCount();

    }

    int[] labelColorIdArr = new int[]{R.color.red};

    //标签布局
    private void setLabelColorLayout(final int[] tab2StringArr, ExRadioGroup
            mItemItemLabelLayout) {
        mItemItemLabelLayout.removeAllViews();
        int length = tab2StringArr.length;
        for (int position = 0; position < length; position++) {
            codeBtn = new View(context);
            codeBtn.setBackgroundResource(tab2StringArr[position]);
            codeBtn.setLayoutParams(layoutParams);

            mItemItemLabelLayout.addView(codeBtn);

        }
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

    long getIdByPosition(int position) {
        if (position > mData.size() - 1) {
            return RecyclerView.NO_ID;
        }
        return Long.parseLong(mData.get(position));
    }

    void swap(int fromPos, int toPos) {
        Collections.swap(mData, fromPos, toPos);
    }

    static class SimpleViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout mItemItemMentionPeopleLayout;
        private ExRadioGroup mItemItemLabelLayout;
        TextView mItemTitleTv;

        SimpleViewHolder(View itemView) {
            super(itemView);
            mItemTitleTv = itemView.findViewById(R.id.item_item_title_tv);
            mItemItemLabelLayout = itemView.findViewById(R.id.item_item_label_layout);
            mItemItemMentionPeopleLayout = itemView.findViewById(R.id
                    .item_item_mention_people_layout);
        }
    }


}
