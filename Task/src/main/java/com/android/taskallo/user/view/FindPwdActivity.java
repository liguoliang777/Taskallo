package com.android.taskallo.user.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.taskallo.App;
import com.android.taskallo.R;
import com.android.taskallo.activity.BaseFgActivity;
import com.android.taskallo.bean.JsonResult;
import com.android.taskallo.bean.User;
import com.android.taskallo.core.net.GsonRequest;
import com.android.taskallo.core.utils.Constant;
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

import java.util.HashMap;
import java.util.Map;

/**
 * 找回密码界面
 * Created by zeng on 2016/5/17.
 */
public class FindPwdActivity extends BaseFgActivity {

    public static final String TAG = "000";

    private Button bt_find_pwd;
    private ImageButton bt_show_pwd;
    private TextView tv_captcha;
    private EditText et_name, et_captcha, et_pwd;

    private boolean isShowPwd = false;
    private Handler handler = new Handler();
    private static final int WAIT_TIME = 61;
    private int second = 60;

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
                            tv_captcha.setText(getResources().getString(R.string.register_get_captcha));
                            tv_captcha.setBackgroundResource(R.drawable.shape_bg_verif_code_bt_send);
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
                            tv_captcha.setBackgroundResource(R.drawable.shape_bg_verif_code_bt_waiting);
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
    private FindPwdActivity context;
    private String LOGIN_MODE=Constant.loginMode_Phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        setContentView(R.layout.activity_findpwd);
        context = this;
        BaseTitleBar titleBar = (BaseTitleBar) findViewById(R.id.title_bar);
        titleBar.setOnLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        bt_find_pwd = (Button) findViewById(R.id.bt_find_pwd);
        et_name = (EditText) findViewById(R.id.et_login_user);
        et_captcha = (EditText) findViewById(R.id.et_captcha);

        bt_show_pwd = (ImageButton) findViewById(R.id.bt_show_pwd);
        et_pwd = (EditText) findViewById(R.id.et_login_pwd);

        tv_captcha = (TextView) findViewById(R.id.tv_captcha);
        tv_captcha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = et_name.getText().toString().trim();

                if (userName != null && !"".equals(userName)) {
                    boolean mobile = TextUtil.isMobile(userName);
                    if (!mobile &&!TextUtil.isEmail(userName)) {
                        ToastUtil.show(context,"请输入正确的邮箱/手机号");
                        return;
                    }
                    LOGIN_MODE = mobile ? Constant.loginMode_Phone : Constant.loginMode_Email;
                    tv_captcha.setBackgroundResource(R.drawable.shape_bg_verif_code_bt_waiting);
                    tv_captcha.setText("正在获取...");
                    tv_captcha.setClickable(false);
                    getVerifCode(userName);
                } else {
                    Toast.makeText(FindPwdActivity.this, "手机号不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });

        bt_find_pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = et_name.getText().toString().trim();
                String pwd = et_pwd.getText().toString().trim();
                String captcha = et_captcha.getText().toString().trim();

                if (userName == null || userName.length() <= 0) {
                    Toast.makeText(FindPwdActivity.this, "手机号不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!TextUtil.isMobile(userName)) {
                    Toast.makeText(context, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (captcha == null || captcha.length() <= 0) {
                    Toast.makeText(FindPwdActivity.this, "验证码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (pwd == null || pwd.length() <= 0) {
                    Toast.makeText(FindPwdActivity.this, "新密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (pwd.length() < 6) {
                    Toast.makeText(FindPwdActivity.this, "密码要大于六位", Toast.LENGTH_SHORT).show();
                    return;
                }

                doFindPwd(userName, pwd, captcha);
            }
        });
        bt_show_pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isShowPwd) {
                    isShowPwd = true;
                    bt_show_pwd.setSelected(true);
                    et_pwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    et_pwd.setSelection(et_pwd.getText().length());
                } else {
                    isShowPwd = false;
                    bt_show_pwd.setSelected(false);
                    et_pwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    et_pwd.setSelection(et_pwd.getText().length());
                }
            }
        });

    }

    /**
     * 获取验证码
     */
    private void getVerifCode(final String userName) {
        String url = Constant.WEB_SITE + Constant.URL_GET_AUTH_CODE;//新接口
        Response.Listener<JsonResult<Object>> successListener = new Response.Listener<JsonResult<Object>>() {
            @Override
            public void onResponse(JsonResult<Object> result) {
                if (result == null) {
                    tv_captcha.setClickable(true);
                    tv_captcha.setText(getResources().getString(R.string.register_get_captcha));
                    tv_captcha.setBackgroundResource(R.drawable.shape_bg_verif_code_bt_send);
                    ToastUtil.show(context,"服务器异常");
                    return;
                }

                if (result.code == 0) {
                    second = 60;
                    new Thread(runnable).start();
                    ToastUtil.show(context,"验证码已发送成功，请注意查收");
                } else {
                    tv_captcha.setClickable(true);
                    tv_captcha.setText(getResources().getString(R.string.register_get_captcha));
                    tv_captcha.setBackgroundResource(R.drawable.shape_bg_verif_code_bt_send);
                    Log.d(TAG, "获取验证码失败：服务端错误：" + result.msg);
                    showDialog(false, result.msg);
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                tv_captcha.setClickable(true);
                tv_captcha.setText(getResources().getString(R.string.register_get_captcha));
                tv_captcha.setBackgroundResource(R.drawable.shape_bg_verif_code_bt_send);
                ToastUtil.show(context,"服务器异常,获取验证码失败");
                Log.d(TAG, "HTTP请求失败：获取手机验证码失败！");
            }
        };

        Request<JsonResult<Object>> versionRequest = new GsonRequest<JsonResult<Object>>(Request.Method.POST, url,
                successListener, errorListener, new TypeToken<JsonResult<Object>>() {
        }.getType()) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(KeyConstant.loginName, userName);
                params.put(KeyConstant.loginMode, LOGIN_MODE);
                params.put(KeyConstant.APP_TYPE_ID, Constant.APP_TYPE_ID_0_ANDROID);
                params.put(KeyConstant.authType, Constant.authType_Find_Pwd);//type 短信类型（1注册，2忘记密码）
                return params;
            }
        };
        App.requestQueue.add(versionRequest);
    }

    /**
     * 处理HTTP找回密码操作
     */
    private void doFindPwd(final String userName, final String pwd, final String captcha) {
        String url = Constant.WEB_SITE + UrlConstant.URL_FORGOT_PASSWORD;
        Response.Listener<JsonResult<User>> successListener = new Response.Listener<JsonResult<User>>() {
            @Override
            public void onResponse(JsonResult<User> result) {
                if (result == null) {
                    Toast.makeText(FindPwdActivity.this, "网络异常,请稍后重试", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (result.code == 0) {
                    SharedPreferences preferences = getSharedPreferences(Constant.CONFIG_FILE_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(Constant.CONFIG_USER_NAME, userName);
                    editor.commit();
                    showDialog(true, "密码重置成功");
                } else {
                    showDialog(false, result.msg);
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                ToastUtil.show(context,"网络异常,请稍后重试");
            }
        };

        Request<JsonResult<User>> versionRequest = new GsonRequest<JsonResult<User>>(Request.Method.POST, url,
                successListener, errorListener, new TypeToken<JsonResult<User>>() {
        }.getType()) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //设置POST请求参数
                Map<String, String> params = new HashMap<>();
                params.put(KeyConstant.loginName, userName);
                params.put(KeyConstant.smsCode, captcha);
                params.put(KeyConstant.newPassword, pwd);
                return params;
            }
        };
        App.requestQueue.add(versionRequest);
    }

    /**
     * 显示登录对话框
     */
    private void showDialog(final boolean isSuccess, String msg) {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        final SimpleDialogFragment dialogFragment = new SimpleDialogFragment();
        dialogFragment.setDialogWidth(220);

        TextView tv = new TextView(FindPwdActivity.this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup
                .LayoutParams.MATCH_PARENT);
        params.setMargins(0, 0, 0, 0);
        params.gravity = Gravity.CENTER;
        tv.setLayoutParams(params);
        tv.setGravity(Gravity.CENTER);
        tv.setText(msg);
        tv.setTextColor(getResources().getColor(R.color.color000000));
        dialogFragment.setContentView(tv);

        int stringId;
        if (isSuccess) {
            stringId = R.string.login_now;
        } else {
            stringId = R.string.sure;
        }

        dialogFragment.setNegativeButton(stringId, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogFragment.dismiss();

                if (isSuccess) {
                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
        dialogFragment.show(ft, "successDialog");
    }
}
