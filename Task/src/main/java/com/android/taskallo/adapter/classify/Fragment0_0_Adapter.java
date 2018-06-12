
package com.android.taskallo.adapter.classify;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.taskallo.R;
import com.android.taskallo.bean.ProjItemInfo;
import com.android.taskallo.core.utils.KeyConstant;
import com.android.taskallo.project.view.ProjListActivity;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * @author gp
 */
public class Fragment0_0_Adapter extends RecyclerView.Adapter<Fragment0_0_Adapter.ViewHolder> {

    private final LayoutInflater mInflater;
    private Context context;
    private List<ProjItemInfo> list;

    public interface OnItemClickLitener {
        void onItemClick(View view, int position, String tag);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setmOnItemClickListener(OnItemClickLitener mOnItemClickListener) {
        this.mOnItemClickLitener = mOnItemClickListener;
    }

    public Fragment0_0_Adapter(Context context, List<ProjItemInfo> list) {
        super();
        this.context = context;
        this.list = list;
        mInflater = LayoutInflater.from(context);
    }

    public void setList(List<ProjItemInfo> list) {
        this.list = list;
        notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int vieype) {
        ViewHolder holder = new ViewHolder(mInflater.inflate(R.layout.item_classify_tviv, parent,
                false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final ProjItemInfo itemInfo = list.get(position);
        holder.mTV.setText(itemInfo.name);
        holder.mIV.setImageURI(itemInfo.projectImg);
        //为ItemView设置监听器
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProjListActivity.class);
                intent.putExtra(KeyConstant.ID, itemInfo.projectId);
                intent.putExtra(KeyConstant.name, itemInfo.name);
                intent.putExtra(KeyConstant.projectImg, itemInfo.projectImg);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTV;
        private SimpleDraweeView mIV;

        public ViewHolder(View itemView) {
            super(itemView);
            mTV = (TextView) itemView.findViewById(R.id.tviv_item_tv);
            mIV = (SimpleDraweeView) itemView.findViewById(R.id.tviv_item_iv);
        }
    }
}














