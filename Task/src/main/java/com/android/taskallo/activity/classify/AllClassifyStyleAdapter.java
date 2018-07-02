
package com.android.taskallo.activity.classify;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.taskallo.R;
import com.jzt.hol.android.jkda.sdk.bean.classification.AllClassifyBean;

import java.util.List;

/**
 * 首页下方攻略列表
 *
 * @author gp
 */
public class AllClassifyStyleAdapter extends BaseAdapter {

    private Context context;
    private List<AllClassifyBean.DataBean.GameStyleListBean> list;

    public AllClassifyStyleAdapter(Context context, List<AllClassifyBean.DataBean.GameStyleListBean> list) {
        super();
        this.context = context;
        this.list = list;
    }

    public void setList(List<AllClassifyBean.DataBean.GameStyleListBean> list) {
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
        if (list != null) {
            return list.get(position);
        }
        return null;
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
            convertView = View.inflate(parent.getContext(), R.layout.classify_gridview_item, null);
            holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            holder.tv_content .setBackgroundResource(R.drawable.shape_corner8px_black);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (position > (list.size() - 1)) {
            holder.tv_content.setText("");
        } else {
            holder.tv_content.setText(list.get(position).getCName());
        }
        return convertView;
    }

    class ViewHolder {
        private TextView tv_content;
    }
}














