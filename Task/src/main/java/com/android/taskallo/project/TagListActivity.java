package com.android.taskallo.project;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.android.taskallo.App;
import com.android.taskallo.R;
import com.android.taskallo.activity.BaseFgActivity;
import com.android.taskallo.activity.main.TagEditActivity;
import com.android.taskallo.adapter.TopicsListAdapter;
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

    private Button tv_title, addTagBt;
    List<TagInfo> tagList = new ArrayList<>();
    TopicsListAdapter tagAdapter;
    private TagListActivity context;
    private GridView gview;
    private String mProjId, mBoardId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        setContentView(R.layout.selected_topics_activity);
        context = TagListActivity.this;
        mProjId = getIntent().getStringExtra(KeyConstant.projectId);
        mBoardId = getIntent().getStringExtra(KeyConstant.boardId);
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

        addTagBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo 添加标签

            }
        });
        gview = (GridView) findViewById(R.id.gview);
        tagAdapter = new TopicsListAdapter(this, tagList);
        gview.setAdapter(tagAdapter);

        tv_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.finish();
            }
        });
        gview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent();
                i.setClass(context, TagEditActivity.class);
                TagInfo dataBean = tagList.get(position);
                i.putExtra(KeyConstant.category_Id, dataBean.labelId);
                i.putExtra(KeyConstant.TITLE, dataBean.labelName);
                i.putExtra(KeyConstant.DESC, dataBean.labelColour);
                startActivity(i);
            }
        });
        getData();
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
        tagList=result;
        tagList.add(new TagInfo());
        tagList.add(new TagInfo());
        tagList.add(new TagInfo());
        tagList.add(new TagInfo());
        tagAdapter.setList(tagList);
    }

}
