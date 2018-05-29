package com.android.taskallo.user.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.taskallo.App;
import com.android.taskallo.R;
import com.android.taskallo.activity.BaseFgActivity;
import com.android.taskallo.adapter.HubAdapter;
import com.android.taskallo.bean.JsonResult;
import com.android.taskallo.bean.PostsInfo;
import com.android.taskallo.core.net.GsonRequest;
import com.android.taskallo.core.utils.Constant;
import com.android.taskallo.core.utils.DialogHelper;
import com.android.taskallo.core.utils.KeyConstant;
import com.android.taskallo.core.utils.Log;
import com.android.taskallo.core.utils.TextUtil;
import com.android.taskallo.core.utils.UrlConstant;
import com.android.taskallo.fragment.SimpleDialogFragment;
import com.android.taskallo.util.ToastUtil;
import com.android.taskallo.view.BaseTitleBar;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

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
    private EditText et_name;
    private EditText et_captcha;
    private Button bt_register;
    private TextView tv_captcha;

    private String mToken;
    private static final int WAIT_TIME = 61;
    private int second = 60;
    private Timer timer = new Timer();
    private Handler handler = new Handler();
    private DialogHelper dialogHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        setContentView(R.layout.activity_send_code);
        content = this;
        preferences = getSharedPreferences(Constant.CONFIG_FILE_NAME, MODE_PRIVATE);
        m_EDIT_TYPE = getIntent().getStringExtra(KeyConstant.EDIT_TYPE);

        editor = preferences.edit();
        mToken = App.token;
        dialogHelper = new DialogHelper(getSupportFragmentManager(), content);
        init();
    }

    private void init() {
        BaseTitleBar titleBar = (BaseTitleBar) findViewById(R.id.title_bar);
        titleBar.setOnLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        titleBar.setTitleText(m_EDIT_TYPE.equals(Constant.PHONE) ? TextUtil.isEmpty(App.phone) ?
                "绑定手机" : "更改手机" : TextUtil.isEmpty(App.email) ? "绑定邮箱" : "更改邮箱");

        et_name = (EditText) findViewById(R.id.et_login_user);
        et_name.setHint(m_EDIT_TYPE.equals(Constant.PHONE) ? "输入手机号" : "输入邮箱");
        et_captcha = (EditText) findViewById(R.id.et_captcha);

        bt_register = (Button) findViewById(R.id.register);
        bt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = et_name.getText().toString();
                String captcha = et_captcha.getText().toString();

                if (userName == null && userName.equals("")) {
                    ToastUtil.show(content, "手机号/邮箱不能为空");
                    return;
                }
                boolean isMobile = TextUtil.isMobile(userName);
                if (!isMobile && !TextUtil.isEmail(userName)) {
                    ToastUtil.show(content, "请输入正确的手机号/邮箱");
                    return;
                }
                if (captcha == null || captcha.equals("")) {
                    ToastUtil.show(content, "验证码不能为空");
                    return;
                }
                m_EDIT_TYPE = isMobile ? Constant.loginMode_Phone : Constant.loginMode_Email;
                doRegister(userName, captcha);
            }
        });

        tv_captcha = (TextView) findViewById(R.id.tv_captcha);
        tv_captcha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userName = et_name.getText().toString();
                if (userName != null && !"".equals(userName)) {
                    boolean mobile = TextUtil.isMobile(userName);
                    if (!mobile && !TextUtil.isEmail(userName)) {
                        ToastUtil.show(content, "请输入正确的邮箱/手机号");
                    } else {
                        m_EDIT_TYPE = mobile ? Constant.loginMode_Phone : Constant.loginMode_Email;
                        getSMSCode(userName);
                    }
                } else {
                    ToastUtil.show(content, "邮箱/手机号不能为空");
                }
            }
        });


    }

    String url = Constant.WEB_SITE + UrlConstant.URL_BINDING_PHONE_NUMBER;
    String key_bind_type = KeyConstant.phoneNumber;

    private void doRegister(final String userName, final String captcha) {
        dialogHelper.showAlert("加载中...", true);
        if (m_EDIT_TYPE.equals(Constant.EMAIL)) {
            url = Constant.WEB_SITE + UrlConstant.URL_BINDING_EMAIL;
            key_bind_type = KeyConstant.email;
        }
        Response.Listener<JsonResult> successListener = new Response.Listener<JsonResult>() {
            @Override
            public void onResponse(JsonResult result) {
                if (null != content && !content.isFinishing()) {
                    dialogHelper.hideAlert();
                }
                if (result == null) {
                    ToastUtil.show(content, "绑定失败,服务端异常");
                    return;
                }

                if (result.code == 0) {
                    String token = (String) result.data;
                    editor.putString(Constant.CONFIG_TOKEN, token);
                    if (m_EDIT_TYPE.equals(Constant.PHONE)) {
                        App.phone = userName;
                        editor.putString(Constant.CONFIG_USER_PHONE, userName);
                    } else {
                        App.email = userName;
                        editor.putString(Constant.CONFIG_USER_EMAIL, userName);
                    }
                    editor.apply();
                    App.token = token;

                    showDialog("绑定成功!");
                } else {
                    ToastUtil.show(content, result.msg);
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                ToastUtil.show(content, "网络连接错误,");
                if (null != content && !content.isFinishing()) {
                    dialogHelper.hideAlert();
                }
                Log.d(TAG, "HTTP请求失败：网络连接错误！");
            }
        };

        Request<JsonResult> versionRequest = new GsonRequest<JsonResult>(Request.Method.POST, url,
                successListener, errorListener, new TypeToken<JsonResult>() {
        }.getType()) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(key_bind_type, userName);
                params.put(KeyConstant.smsCode, captcha);
                params.put(KeyConstant.TOKEN, mToken);
                params.put(KeyConstant.APP_TYPE_ID, Constant.APP_TYPE_ID_0_ANDROID);
                return params;
            }
        };
        App.requestQueue.add(versionRequest);
    }

    /**
     * 显示注册结果对话框
     */
    private void showDialog(String msg) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        final SimpleDialogFragment dialogFragment = new SimpleDialogFragment();
        dialogFragment.setDialogWidth(220);
        TextView tv = new TextView(content);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, ViewGroup
                .LayoutParams.MATCH_PARENT);
        params.setMargins(0, 20, 0, 0);
        params.gravity = Gravity.CENTER;
        tv.setLayoutParams(params);
        tv.setGravity(Gravity.CENTER);
        tv.setText(msg);
        tv.setTextColor(getResources().getColor(R.color.color000000));
        dialogFragment.setContentView(tv);

        int stringId = R.string.sure;


        dialogFragment.setNegativeButton(stringId, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogFragment.dismiss();
                        finish();
                    }
                }
        );
        dialogFragment.show(ft, "successDialog");
    }

    /**
     * 执行倒计时操作
     */
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            for (int i = 0; i < WAIT_TIME; i++) {
                if (second <= 1) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            tv_captcha.setText(R.string
                                    .register_get_captcha);
                            tv_captcha.setBackgroundResource(R.drawable
                                    .shape_bg_verif_code_bt_send);
                            tv_captcha.setClickable(true);
                            return;
                        }
                    });
                } else {
                    second--;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            tv_captcha.setText("重新发送(" + second + "s)");
                            tv_captcha.setBackgroundResource(R.drawable
                                    .shape_bg_verif_code_bt_waiting);
                            tv_captcha.setClickable(false);
                        }
                    });
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    /**
     * 获取手机验证码
     */
    private void getSMSCode(final String userName) {
        dialogHelper.showAlert("加载中...", true);
        String url = Constant.WEB_SITE + UrlConstant.URL_SEND_BINDING_CODE;
        Response.Listener<JsonResult<Object>> successListener = new Response
                .Listener<JsonResult<Object>>() {
            @Override
            public void onResponse(JsonResult result) {
                if (null != content && !content.isFinishing()) {
                    dialogHelper.hideAlert();
                }
                if (result == null) {
                    ToastUtil.show(content, "服务端异常");
                    return;
                }
                if (result.code == 0) {
                    second = 60;
                    new Thread(runnable).start();

                    ToastUtil.show(content, "验证码已发送，请查收");
                    Log.d(TAG, "HTTP请求成功：服务端返回：" + result.data);
                    mToken = (String) result.data;
                    App.token = mToken;
                    editor.putString(Constant.CONFIG_TOKEN, mToken).apply();

                } else {
                    ToastUtil.show(content, result.msg);
                    Log.d(TAG, "HTTP请求成功：服务端返回错误：" + result.msg);
                    //showDialog(false, result.msg);
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                if (null != content && !content.isFinishing()) {
                    dialogHelper.hideAlert();
                }
                ToastUtil.show(content, "请检查网络连接");
                Log.d(TAG, "HTTP请求失败：网络连接错误！");
            }
        };

        Request<JsonResult<Object>> versionRequest = new GsonRequest<JsonResult<Object>>(Request
                .Method.POST, url,
                successListener, errorListener, new TypeToken<JsonResult>() {
        }.getType()) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(KeyConstant.loginName, userName);
                params.put(KeyConstant.loginMode, m_EDIT_TYPE);
                params.put(KeyConstant.APP_TYPE_ID, Constant.APP_TYPE_ID_0_ANDROID);
                params.put(KeyConstant.TOKEN, mToken);
                return params;
            }
        };
        App.requestQueue.add(versionRequest);
    }
}