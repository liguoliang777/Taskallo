
package com.android.taskallo.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.taskallo.R;
import com.android.taskallo.bean.TagInfo;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;


/**
 * @author gp
 */
public class TopicsListAdapter extends BaseAdapter {

    private Context context;
    private List<TagInfo> list;

    public TopicsListAdapter(Context context, List<TagInfo> list) {
        super();
        this.context = context;
        this.list = list;
    }

    public void setList(List<TagInfo> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.tag_list_item, parent,
                    false);
            holder.tv_title = (TextView) convertView.findViewById(R.id.tag_item_tv);
            holder.itemBgSDV = (SimpleDraweeView) convertView.findViewById(R.id.tag_item_bg);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        TagInfo item = list.get(position);
        if (item != null) {
            String labelColour = item.labelColour;
            if (labelColour != null) {
                holder.itemBgSDV.setBackgroundColor(Color.parseColor(labelColour));
            }
            String labelName = item.labelName;
            if (labelName != null) {
                holder.tv_title.setText(labelName);
            }
            
        }

        return convertView;
    }

    class ViewHolder {
        private SimpleDraweeView itemBgSDV;
        private TextView tv_title;

    }
}














