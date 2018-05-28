package com.android.taskallo.user.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.taskallo.App;
import com.android.taskallo.R;
import com.android.taskallo.activity.BaseFgActivity;
import com.android.taskallo.adapter.HubAdapter;
import com.android.taskallo.bean.JsonResult;
import com.android.taskallo.bean.PostsInfo;
import com.android.taskallo.bean.User;
import com.android.taskallo.core.net.GsonRequest;
import com.android.taskallo.core.utils.Constant;
import com.android.taskallo.core.utils.KeyConstant;
import com.android.taskallo.core.utils.Log;
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
 * 圈子
 * Created by liguoliang on 2017/11/23 0023.
 */
public class SendBindCodeActivity extends BaseFgActivity {
    protected static final String TAG = SendBindCodeActivity.class.getSimpleName();
    private SendBindCodeActivity content;
    private RecyclerView mRecyclerView;
    private HubAdapter mAdapter;
    private TextView headerLastUpdateTv;
    private List<PostsInfo.DataBean> mDatas = new ArrayList<>();
    private EditText tv_nickname;
    private String nickName;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private String m_EDIT_TYPE;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        setContentView(R.layout.activity_send_code);
        content = this;
        preferences = getSharedPreferences(Constant.CONFIG_FILE_NAME, MODE_PRIVATE);
        m_EDIT_TYPE = getIntent().getStringExtra(KeyConstant.EDIT_TYPE);
        editor = preferences.edit();
        init();
    }

    private void init() {
        tv_nickname = (EditText) findViewById(R.id.tv_nickname);

        findViewById(R.id.left_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView centerTv = (TextView) findViewById(R.id.center_tv);
        centerTv.setText(R.string.me_profile);

        nickName = App.nickName;
        tv_nickname.setText(nickName);
        tv_nickname.setSelection(nickName.length());

        TextView titleRightBt = (TextView) findViewById(R.id.title_right_tv);
        titleRightBt.setVisibility(View.VISIBLE);
        titleRightBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nickNameStr = tv_nickname.getText().toString();
                if (nickNameStr.length() == 0) {
                    ToastUtil.show(content, "昵称为空哦！");
                    return;
                }
                if (nickNameStr.equals(nickName)) {
                    ToastUtil.show(content, "您未修改任何资料哦");
                    //content.finish();
                } else {
                    nickName = nickNameStr;
                    //setName();
                }
            }

        });

    }

    private void setName() {
        String url = Constant.WEB_SITE + Constant.URL_MODIFY_USER_DATA;
        Response.Listener<JsonResult<User>> successListener =
                new Response.Listener<JsonResult<User>>() {
                    @Override
                    public void onResponse(JsonResult<User> result) {
                        int code = result.code;
                        if (code == 0) {
                            ToastUtil.show(content, "资料修改成功!");
                            User user = result.data;
                            editor.putString(Constant.CONFIG_NICK_NAME, nickName);
                            editor.apply();

                            App.token = user.email;
                            App.userHeadUrl = user.headPortrait;
                            App.nickName = nickName;
                            App.userCode = user.userCode;
                            content.finish();
                        } else if (code >= -4 && code <= -1) {
                            android.util.Log.d(TAG, "ic_back: " + code + result.msg);
                            if (content != null && !content.isFinishing()) {
                            }
                            //需要重新登录
                            //logoutClearData();
                            //UserCenterActivity.this.finish();
                        } else {
                            ToastUtil.show(content, "修改失败");
                            Log.d(TAG, "HTTP请求成功：修改失败！" + code + result.msg);
                        }
                        //隐藏提示框
                    }
                };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                ToastUtil.show(content, "修改失败，网络连接异常");
                Log.d(TAG, "HTTP请求失败：网络连接错误！" + volleyError.getMessage());
            }
        };

        Request<JsonResult<User>> versionRequest = new GsonRequest<JsonResult<User>>(Request
                .Method.POST, url,
                successListener, errorListener, new TypeToken<JsonResult<User>>() {
        }.getType()) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(KeyConstant.USER_CODE, App.userCode);
                params.put(KeyConstant.NICK_NAME, nickName);
                params.put(KeyConstant.TOKEN, App.token);

                return params;
            }
        };
   /*     versionRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));*/

        App.requestQueue.add(versionRequest);
    }

}