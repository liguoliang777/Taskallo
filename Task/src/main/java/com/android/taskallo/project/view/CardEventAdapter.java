
package com.android.taskallo.project.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.taskallo.R;
import com.android.taskallo.project.bean.LogsBean;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * @author gp
 */
public class CardEventAdapter extends RecyclerView.Adapter<CardEventAdapter.ViewHolder> {

    private final LayoutInflater mInflater;
    private Context context;
    private List<LogsBean> list;

    public interface OnItemClickLitener {
        void onItemClick(View view, int position, int text);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setmOnItemClickListener(OnItemClickLitener mOnItemClickListener) {
        this.mOnItemClickLitener = mOnItemClickListener;
    }

    public CardEventAdapter(Context context, List<LogsBean> list) {
        super();
        this.context = context;
        this.list = list;
        mInflater = LayoutInflater.from(context);
    }

    public void setList(List<LogsBean> list) {
        this.list = list;
        notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int vieype) {
        ViewHolder holder = new ViewHolder(mInflater.inflate(R.layout.item_card_detail_event,
                parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final LogsBean projInfo = list.get(position);

        if (projInfo == null) {
            return;
        }
        final String name = projInfo.nickName;
        holder.tvName.setText(name == null ? "" : name);
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
        private SimpleDraweeView evnetSdv;
        private TextView tvName;

        public ViewHolder(View itemView) {
            super(itemView);
            evnetSdv = itemView.findViewById(R.id.classify_item_iv);
            tvName = itemView.findViewById(R.id.singer_item_tv);
        }
    }
}














