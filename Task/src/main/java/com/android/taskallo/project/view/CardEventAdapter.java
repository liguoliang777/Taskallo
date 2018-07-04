
package com.android.taskallo.project.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
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
        final String thing = projInfo.operation;
        final String thingName = projInfo.content;
        holder.tvName.setText(name == null ? "" : name);
        holder.tvThing.setText(thing == null ? "" : thing);
        holder.tvThingName.setText(thingName == null ? "" : thingName);
        holder.tvTime.setText(String.valueOf(DateUtils.getRelativeTimeSpanString(
                projInfo.createTime)).replace(" ", ""));
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
        private TextView tvName, tvThing, tvTime,tvThingName;

        public ViewHolder(View itemView) {
            super(itemView);
            evnetSdv = itemView.findViewById(R.id.card_item_event_left_iv);
            tvName = itemView.findViewById(R.id.card_item_event_name_tv);
            tvThing = itemView.findViewById(R.id.card_item_event_thing_tv);
            tvTime = itemView.findViewById(R.id.card_item_event_time_tv);
            tvThingName = itemView.findViewById(R.id.card_item_event_thing_name_tv);
        }
    }
}














