
package com.android.taskallo.adapter;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.taskallo.R;
import com.android.taskallo.bean.TagInfo;
import com.android.taskallo.project.TagListActivity;

import java.util.List;


/**
 * @author gp
 */
public class TagListAdapter extends BaseAdapter {

    private TagListActivity context;
    private List<TagInfo> list;
    private ShapeDrawable drawable;
    float[] outerRadian = new float[]{10, 10, 10, 10, 10, 10, 10, 10};

    public TagListAdapter(TagListActivity context, List<TagInfo> list) {
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
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.tag_list_item, parent,
                    false);
            holder.tv_title = (TextView) convertView.findViewById(R.id.tag_item_tv);
            holder.itemColorBt = (Button) convertView.findViewById(R.id.tag_item_bg);
            holder.itemEditBt = (Button) convertView.findViewById(R.id.tag_item_edit);
            holder.itemSelectedTag = (ImageView) convertView.findViewById(R.id.tag_item_selected_tag);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final TagInfo item = list.get(position);
        if (item != null) {
            String labelColour = item.labelColour;
            if (labelColour != null) {
                drawable = new ShapeDrawable(new RoundRectShape(outerRadian, null, null));
                drawable.getPaint().setStyle(Paint.Style.FILL);
                drawable.getPaint().setColor(Color.parseColor(labelColour));
                //构建Controller
                holder.itemColorBt.setBackground(drawable);
            }
            holder.itemColorBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int visibility = holder.itemSelectedTag.getVisibility();
                    if (visibility == View.VISIBLE) {
                        //取消关联
                        holder.itemSelectedTag.setVisibility(View.INVISIBLE);
                    } else {
                        //关联
                        holder.itemSelectedTag.setVisibility(View.VISIBLE);

                    }
                }
            });

            //标题
            String labelName = item.labelName;
            if (labelName != null) {
                holder.tv_title.setText(labelName);
            }

            //编辑修改标签
            holder.itemEditBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.updateTag(item);

                }
            });

        }


        return convertView;
    }

    class ViewHolder {
        private Button itemColorBt, itemEditBt;
        private TextView tv_title;
        private ImageView itemSelectedTag;

    }
}














