
package com.android.taskallo.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.taskallo.R;
import com.android.taskallo.activity.manager.Fragment1;
import com.android.taskallo.bean.NecessaryItemData;
import com.android.taskallo.core.fileload.FileLoadManager;
import com.android.taskallo.core.fileload.IFileLoad;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class Fragment1Adapter extends BaseAdapter implements StickyListHeadersAdapter {
    private Timer timer = new Timer();
    private List<TimerTask> timerTasks = new ArrayList<>();
    private List<NecessaryItemData> mPlanDetails;
    private Context context;
    private FragmentManager fm;
    private Handler uiHandler = new Handler();
    private String mPositionGameId = "";

    protected final static String TAG = Fragment1.class.getSimpleName();

    public Fragment1Adapter(Context context, FragmentManager fm, List<TimerTask>
            timerTasks) {
        super();
        this.context = context;
        this.fm = fm;
        this.timerTasks = timerTasks;

    }

    /**
     * 设置ListView中的数据
     *
     * @param fileInfoList 下载文件信息
     */
    public void setDate(List<NecessaryItemData> fileInfoList) {
        //uiHandler = new Handler();
        this.mPlanDetails = fileInfoList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mPlanDetails != null) {
            return mPlanDetails.size();
        }
        return 0;
    }

    public String getItemGameId() {
        return mPositionGameId;
    }

    @Override
    public Object getItem(int position) {
        if (mPlanDetails != null) {
            return mPlanDetails.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void clean() {
        if (mPlanDetails != null) {
            mPlanDetails.clear();
            uiHandler = null;
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder(context, fm);
            convertView = LayoutInflater.from(context).inflate(R.layout
                    .item_fragment1_item, parent, false);
            holder.headPhoto = (SimpleDraweeView) convertView.findViewById(R.id
                    .fragment1_item_head_photo_iv);
            holder.msgTypeOrUser = (TextView) convertView.findViewById(R.id
                    .fragment1_item_msg_type_user);
            holder.msgContent = (TextView) convertView.findViewById(R.id
                    .fragment1_item_msg_content);
            holder.proCardName = (TextView) convertView.findViewById(R.id
                    .fragment1_item_project_name_card_name);
            holder.msgTime = (TextView) convertView.findViewById(R.id.fragment1_item_msg_time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final NecessaryItemData planDetail = (mPlanDetails == null) ? null : mPlanDetails.get
                (position);
        if (planDetail != null) {
            holder.update(planDetail);
        }

        return convertView;
    }

    /**
     * 设置头部
     */
    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;
        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout
                    .necessary_list_item_header, parent, false);
            holder.itemParentTv = (TextView) convertView.findViewById(R.id
                    .necessary_list_header_item_tv);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }

        String parentText = this.mPlanDetails.get(position).getParentName();
        holder.itemParentTv.setText("消息项目名字");
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        return Long.parseLong(this.mPlanDetails.get(position).getParentId());
        // getHeaderId决定header出现.如果当前的headerid和前一个headerid不同时，就会显示
    }


    class HeaderViewHolder {
        TextView itemParentTv;
    }

    public class ViewHolder {
        private Context context;
        private FragmentManager fm;
        private NecessaryItemData toolInfo;
        private SimpleDraweeView headPhoto;
        private TextView msgTypeOrUser, proCardName, msgContent, msgTime;
        private IFileLoad fileLoad;

        public ViewHolder(Context context, FragmentManager fm) {
            this.context = context;
            this.fm = fm;
            fileLoad = FileLoadManager.getInstance(context);
            init();
        }

        private void init() {
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    if (uiHandler == null) {
                        this.cancel();
                        return;
                    }
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            //每秒执行
                        }
                    });
                }
            };
            timerTasks.add(task);
            timer.schedule(task, 0, 5000);
        }

        public void update(final NecessaryItemData toolInfo) {
            this.toolInfo = toolInfo;
        /*    headPhoto.setImageURI(toolInfo.getToolLogo());
            msgTypeOrUser.setText(toolInfo.getToolName());
            msgContent.setText(toolInfo.getToolVersion());
            proCardName.setText(toolInfo.getToolSize());
            msgTime.setText(toolInfo.getToolDesc());  */
            headPhoto.setImageURI("");
            msgTypeOrUser.setText("通知类型/用户名");
            msgContent.setText("通知的内容详情提要,通知的内容详情提要");
            proCardName.setText("项目名" + "―――" + "卡片名");
            msgTime.setText("10:53:05");
        }
    }

}














