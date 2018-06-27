package com.android.taskallo.project;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.taskallo.App;
import com.android.taskallo.R;
import com.android.taskallo.activity.BaseFgActivity;
import com.android.taskallo.adapter.TagListAdapter;
import com.android.taskallo.bean.JsonResult;
import com.android.taskallo.bean.TagInfo;
import com.android.taskallo.core.net.GsonRequest;
import com.android.taskallo.core.utils.Constant;
import com.android.taskallo.core.utils.KeyConstant;
import com.android.taskallo.core.utils.NetUtil;
import com.android.taskallo.core.utils.UrlConstant;
import com.android.taskallo.util.ToastUtil;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 全部专题
 * Created by gp on 2017/4/13 0013.
 */

public class TagListActivity extends BaseFgActivity {
    float[] outerRadian = new float[]{10, 10, 10, 10, 10, 10, 10, 10};
    private Button tv_title, addTagBt;
    List<TagInfo> tagList = new ArrayList<>();
    TagListAdapter tagAdapter;
    private TagListActivity context;
    private GridView gview;
    private String mProjId, mBoardId;
    private Dialog defAvatarDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        setContentView(R.layout.selected_topics_activity);
        context = TagListActivity.this;
        mProjId = getIntent().getStringExtra(KeyConstant.projectId);
        mBoardId = getIntent().getStringExtra(KeyConstant.boardId);
        initDefTagData();
        init();
    }

    private void init() {
        tv_title = (Button) findViewById(R.id.left_bt);
        TextView titleTv = (TextView) findViewById(R.id.center_tv);
        addTagBt = (Button) findViewById(R.id.title_right_bt);
        tv_title.setText("");

        titleTv.setText("标签");

        addTagBt.setVisibility(View.VISIBLE);
        addTagBt.setText("");
        Drawable dra = getResources().getDrawable(R.drawable.ic_add);
        dra.setBounds(0, 0, dra.getMinimumWidth(), dra.getMinimumHeight());
        addTagBt.setCompoundDrawables(null, null, dra, null);


        initDialog();
        addTagBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                defAvatarDialog.show();
            }
        });
        gview = (GridView) findViewById(R.id.gview);
        tagAdapter = new TagListAdapter(this, tagList);
        gview.setAdapter(tagAdapter);

        tv_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.finish();
            }
        });
        getData();
    }

    private void initDialog() {
        defAvatarDialog = new Dialog(this, R.style.Dialog_From_Bottom_Style);
        //填充对话框的布局
        View inflate = LayoutInflater.from(this).inflate(R.layout.layout_dialog_def_tag, null);
        GridView gridView = (GridView) inflate.findViewById(R.id.tag_add_grid_view);
        gridView.setAdapter(new AvatarAdapter());
        defAvatarDialog.setContentView(inflate);//将布局设置给Dialog
        Window dialogWindow = defAvatarDialog.getWindow(); //获取当前Activity所在的窗体
        dialogWindow.setGravity(Gravity.CENTER);//设置Dialog从窗体底部弹出
        WindowManager.LayoutParams params = dialogWindow.getAttributes();   //获得窗体的属性
        //params.y = 20;  Dialog距离底部的距离
        params.width = WindowManager.LayoutParams.MATCH_PARENT;//设置Dialog距离底部的距离
        dialogWindow.setAttributes(params); //将属性设置给窗体
    }


    public void getData() {
        String url = Constant.WEB_SITE1 + UrlConstant.url_label + "/" + mProjId + "/" + mBoardId;
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, "网络异常,请检查网络设置");
            return;
        }

        Response.Listener<JsonResult<List<TagInfo>>> successListener = new Response
                .Listener<JsonResult<List<TagInfo>>>() {
            @Override
            public void onResponse(JsonResult<List<TagInfo>> result) {
                if (result == null) {
                    ToastUtil.show(context, getString(R.string.server_exception));
                    return;
                }

                List<TagInfo> projDetailInfo = result.data;
                if (result.code == 0 && context != null && projDetailInfo != null) {
                    setData(projDetailInfo);
                }
            }
        };

        Request<JsonResult<List<TagInfo>>> versionRequest = new
                GsonRequest<JsonResult<List<TagInfo>>>(
                        Request.Method.GET, url,
                        successListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                        Log.d(TAG, "网络连接错误！" + volleyError.getMessage());
                    }
                }, new TypeToken<JsonResult<List<TagInfo>>>() {
                }.getType()) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put(KeyConstant.Content_Type, Constant.application_json);
                        params.put(KeyConstant.Authorization, App.token);
                        params.put(KeyConstant.appType, Constant.APP_TYPE_ID_0_ANDROID);
                        return params;
                    }
                };
        App.requestQueue.add(versionRequest);

    }

    private void setData(List<TagInfo> result) {
        if (result != null && result.size() > 0) {
            tagList = result;
            tagAdapter.setList(tagList);
        }
    }

    private void initDefTagData() {
        tagList.add(new TagInfo("0", "", "#f27979"));
        tagList.add(new TagInfo("0", "", "#fec055"));
        tagList.add(new TagInfo("0", "", "#95e645"));
        tagList.add(new TagInfo("0", "", "#4590e5"));
        tagList.add(new TagInfo("0", "", "#a87afb"));
        tagList.add(new TagInfo("0", "", "#d94bee"));
    }
    //默认头像适配器
    public class AvatarAdapter extends BaseAdapter {
        public AvatarAdapter() {
            super();
        }

        @Override
        public int getCount() {
            return tagList.size();
        }

        @Override
        public Object getItem(int position) {
            return tagList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            final AvatarAdapter.ViewHolder holder;
            if (convertView == null) {
                holder = new AvatarAdapter.ViewHolder();
                convertView = View.inflate(parent.getContext(), R.layout.gridview_rag_add, null);
                holder.mTagColorBt = (Button) convertView.findViewById(R.id.tag_add_color_bt);
                holder.itemSelectedTag = (ImageView) convertView.findViewById(R.id.tag_add_selected_tag);
                convertView.setTag(holder);
            } else {
                holder = (AvatarAdapter.ViewHolder) convertView.getTag();
            }
            String labelColour = tagList.get(position).labelColour;
            if (labelColour != null) {
                ShapeDrawable drawable = new ShapeDrawable(new RoundRectShape(outerRadian, null, null));
                drawable.getPaint().setStyle(Paint.Style.FILL);
                drawable.getPaint().setColor(Color.parseColor(labelColour));
                //构建Controller
                holder.mTagColorBt.setBackground(drawable);
            }
            holder.mTagColorBt.setOnClickListener(new View.OnClickListener() {
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
            return convertView;
        }

        class ViewHolder {
            private Button mTagColorBt;
            private ImageView itemSelectedTag;
        }
    }
}
