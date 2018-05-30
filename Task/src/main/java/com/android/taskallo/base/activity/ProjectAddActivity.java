package com.android.taskallo.base.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.taskallo.App;
import com.android.taskallo.R;
import com.android.taskallo.activity.BaseFgActivity;
import com.android.taskallo.bean.JsonResult;
import com.android.taskallo.bean.VersionInfo;
import com.android.taskallo.core.net.GsonRequest;
import com.android.taskallo.core.utils.Constant;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;

/**
 * 显示 关于信息的 界面
 * Created by zeng on 2016/5/23.
 */
public class ProjectAddActivity extends BaseFgActivity {

    private static final String TAG = ProjectAddActivity.class.getSimpleName();
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        setContentView(R.layout.activity_add_project);
        context = this;
        Button saveBt = (Button) findViewById(R.id.title_right_bt);
        saveBt.setVisibility(View.VISIBLE);
        saveBt.setOnClickListener(mOnClickListener);
        findViewById(R.id.left_bt).setOnClickListener(mOnClickListener);
        ((TextView) findViewById(R.id.center_tv)).setText(R.string.project_add);


    }

    /**
     * 上传资料
     */
    public void postProject() {
        String url = Constant.WEB_SITE + Constant.URL_APP_UPDATE;
        Response.Listener<JsonResult<VersionInfo>> successListener = new Response
                .Listener<JsonResult<VersionInfo>>() {
            @Override
            public void onResponse(JsonResult<VersionInfo> result) {

                if (result == null || result.code != 0) {
                    return;
                }

            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
            }
        };

        Request<JsonResult<VersionInfo>> versionRequest = new
                GsonRequest<JsonResult<VersionInfo>>(Request.Method.POST, url,
                        successListener, errorListener, new TypeToken<JsonResult<VersionInfo>>() {
                }.getType()) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("appType", "0");
                        return params;
                    }
                };
        App.requestQueue.add(versionRequest);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.title_right_bt:
                    //保存项目
                    break;
                case R.id.left_bt:
                    finish();
                    break;

            }
        }
    };
}
