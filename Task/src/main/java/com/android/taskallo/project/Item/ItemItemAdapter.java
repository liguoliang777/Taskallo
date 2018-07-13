package com.android.taskallo.project.Item;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.taskallo.R;
import com.android.taskallo.bean.BoardVOListBean;
import com.android.taskallo.bean.TagInfo;
import com.android.taskallo.core.utils.KeyConstant;
import com.android.taskallo.core.utils.TextUtil;
import com.android.taskallo.project.view.CardDetailActivity;
import com.android.taskallo.project.view.ProjListActivity;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class ItemItemAdapter extends RecyclerView.Adapter<ItemItemAdapter.SimpleViewHolder> {

    private String mItemId, mListItemName, mProjectId;
    private List<BoardVOListBean> mItemItemList = new ArrayList<>();
    ProjListActivity context;
    private int width, height, margin;
    private View codeBtn;
    private LinearLayout.LayoutParams layoutParams;
    float[] outerRadian = new float[]{5, 5, 5, 5, 5, 5, 5, 5};

    public ItemItemAdapter(ProjListActivity c, String projectId, String itemId, String listItemName,
                           List<BoardVOListBean> boardVOList) {
        context = c;
        width = context.getResources().getDimensionPixelSize(R.dimen.dm044);
        height = context.getResources().getDimensionPixelSize(R.dimen.dm022);
        margin = context.getResources().getDimensionPixelSize(R.dimen.dm012);
        layoutParams = new LinearLayout.LayoutParams(width, height);
        layoutParams.setMargins(0, 0, margin, margin);

        //数据
        mItemId = itemId;
        mProjectId = projectId;
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
        final BoardVOListBean boardVOListBean = mItemItemList.get(position);
        if (boardVOListBean == null) {
            return;
        }
        String cardTitle = boardVOListBean.boardName;
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

                Bundle bundle = new Bundle();
                bundle.putString(KeyConstant.listItemName, mListItemName);
                bundle.putString(KeyConstant.listItemId, mItemId);
                bundle.putString(KeyConstant.projectId, mProjectId);
                bundle.putSerializable(KeyConstant.cardBean, boardVOListBean);

                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
        //成员
        List<BoardVOListBean.UserBasicVOListBean> userBasicVOList = boardVOListBean.userBasicVOList;
        if (userBasicVOList != null && userBasicVOList.size() != 0) {
            setMemberLayout(holder.mMemberLayout,userBasicVOList);
        }

        //标签
        List<TagInfo> projectLabelVOList = boardVOListBean.projectLabelVOList;
        if (projectLabelVOList != null) {
            setLabelColorLayout(projectLabelVOList, holder.mItemItemLabelLayout);
        }
        //描述
        String boardDesc = boardVOListBean.boardDesc;
        holder.mSubheadTv.setVisibility(TextUtil.isEmpty(boardDesc) ? View.GONE : View.VISIBLE);
        //截止时间
        long expiryTime = boardVOListBean.expiryTime;
        if (expiryTime != 0) {
            String mmdd = new SimpleDateFormat("MM月dd日").format(new Date(expiryTime));
            holder.mExpiryTimeTv.setText(mmdd);
            holder.mExpiryTimeTv.setVisibility(View.VISIBLE);

        } else {
            holder.mExpiryTimeTv.setVisibility(View.GONE);
        }

        //附件数量
        List<Object> boardFileVOList = boardVOListBean.boardFileVOList;
        holder.mFileNumTv.setText(boardFileVOList == null ? "0" : boardFileVOList.size() + "");

        //子任务
        List<BoardVOListBean.SubtaskVOListBean> subtaskVOList = boardVOListBean.subtaskVOList;
        holder.mSubtaskFinishedTv.setText(subtaskVOList == null ? "0" : subtaskVOList.size() + "");


    }

    private void setMemberLayout(LinearLayout memberLayout,
                                 List<BoardVOListBean.UserBasicVOListBean> userBasicVOList) {
        memberLayout.setVisibility(View.VISIBLE);
        memberLayout.removeAllViews();
        int size = userBasicVOList.size();
        int widthHeight = context.getResources().getDimensionPixelOffset(R.dimen.dm050);
        for (int i = 0; i < size; i++) {
            BoardVOListBean.UserBasicVOListBean img = userBasicVOList.get(i);
            SimpleDraweeView picassoImageView = new SimpleDraweeView(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup
                    .LayoutParams.WRAP_CONTENT, ViewGroup
                    .LayoutParams.WRAP_CONTENT);

            params.width = widthHeight;
            params.height = widthHeight;
            params.setMargins(0, 0, widthHeight / 4, 0);
            picassoImageView.setLayoutParams(params);

            GenericDraweeHierarchy hierarchy = GenericDraweeHierarchyBuilder.newInstance
                    (context.getResources())
                    //圆形
                    .setRoundingParams(RoundingParams.asCircle())
                    .setFadeDuration(0)
                    .build();
            picassoImageView.setHierarchy(hierarchy);

            picassoImageView.setImageURI(img.headPortrait);

            memberLayout.addView(picassoImageView);
        }
    }

    static class SimpleViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout mMemberLayout;
        private ExRadioGroup mItemItemLabelLayout;
        private TextView mItemTitleTv, mFileNumTv, mSubtaskFinishedTv, mSubheadTv, mExpiryTimeTv;

        SimpleViewHolder(View itemView) {
            super(itemView);
            mItemTitleTv = itemView.findViewById(R.id.item_item_title_tv);
            mItemItemLabelLayout = itemView.findViewById(R.id.item_item_label_layout);
            mMemberLayout = itemView.findViewById(R.id
                    .item_item_mention_people_layout);
            mSubheadTv = itemView.findViewById(R.id.item_item_exist_subhead_tv);
            mExpiryTimeTv = itemView.findViewById(R.id.item_item_exist_time_tv);
            mFileNumTv = itemView.findViewById(R.id.item_item_exist_link);
            mSubtaskFinishedTv = itemView.findViewById(R.id.item_item_exist_subtask_finished_tv);
        }
    }

    //标签布局
    private void setLabelColorLayout(final List<TagInfo> tab2StringArr, ExRadioGroup
            mItemItemLabelLayout) {
        mItemItemLabelLayout.removeAllViews();
        for (TagInfo tagInfo : tab2StringArr) {
            codeBtn = new View(context);
            ShapeDrawable drawable = new ShapeDrawable(new RoundRectShape(outerRadian, null,
                    null));
            drawable.getPaint().setStyle(Paint.Style.FILL);
            drawable.getPaint().setColor(Color.parseColor(tagInfo.labelColour));
            //构建Controller
            codeBtn.setBackground(drawable);
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

    public void setList(List<BoardVOListBean> boardVOList) {
        mItemItemList = boardVOList;
        notifyDataSetChanged();
    }

}
