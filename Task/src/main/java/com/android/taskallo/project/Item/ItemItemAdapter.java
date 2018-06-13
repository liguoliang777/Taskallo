package com.android.taskallo.project.Item;


import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.taskallo.R;
import com.android.taskallo.bean.BoardVOListBean;
import com.android.taskallo.core.utils.KeyConstant;
import com.android.taskallo.project.view.CardDetailActivity;
import com.android.taskallo.project.view.ProjListActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ItemItemAdapter extends RecyclerView.Adapter<ItemItemAdapter.SimpleViewHolder> {

    private String mItemId, mListItemName;
    private List<BoardVOListBean> mItemItemList = new ArrayList<>();
    ProjListActivity context;
    private int width, height, margin;
    private View codeBtn;
    private LinearLayout.LayoutParams layoutParams;

    public ItemItemAdapter(ProjListActivity c, String itemId, String listItemName,
                           List<BoardVOListBean> boardVOList) {
        context = c;
        width = context.getResources().getDimensionPixelSize(R.dimen.dm044);
        height = context.getResources().getDimensionPixelSize(R.dimen.dm023);
        margin = context.getResources().getDimensionPixelSize(R.dimen.dm012);
        layoutParams = new LinearLayout.LayoutParams(width, height);
        layoutParams.setMargins(0, 0, margin, margin);

        //数据
        mItemId = itemId;
        mListItemName = listItemName;
        mItemItemList = boardVOList;
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_proj_item_item, parent,
                false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, final int position) {
        if (mItemItemList == null) {
            return;
        }
        BoardVOListBean boardVOListBean = mItemItemList.get(position);
        if (boardVOListBean == null) {
            return;
        }
        String cardTitle = boardVOListBean.boardName;
        Log.d("http", "卡片标题: " + cardTitle);
        holder.mItemTitleTv.setText(cardTitle == null ? "" : cardTitle);
        if (boardVOListBean.boardId.equals(ItemProvider.getInstance().getSelectedId())) {
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
        return mItemItemList == null ? 0 : mItemItemList.size();
    }

    public int getPositionFromId() {
        for (int i = 0; i < mItemItemList.size(); i++) {
            if (mItemItemList.get(i).boardId.equals(ItemProvider.getInstance()
                    .getSelectedId())) {
                return i;
            }
        }
        return RecyclerView.NO_POSITION;
    }

    public BoardVOListBean remove(int position) {
        if (mItemItemList == null) {
            return null;
        } else {
            return mItemItemList.remove(position);

        }
    }

    public void add(int position, BoardVOListBean data) {
        if (mItemItemList == null) {
            mItemItemList = new ArrayList<>();
        }
        mItemItemList.add(position, data);
    }

    public String getIdByPosition(int position) {
        if (mItemItemList == null || position > mItemItemList.size() - 1) {
            return RecyclerView.NO_ID + "";
        }
        return mItemItemList.get(position).boardId;
    }

    void swap(int fromPos, int toPos) {
        Collections.swap(mItemItemList, fromPos, toPos);
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
