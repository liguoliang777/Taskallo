package com.android.taskallo.user.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.taskallo.R;
import com.android.taskallo.activity.BaseFgActivity;
import com.android.taskallo.bean.JsonResult;
import com.android.taskallo.core.net.GsonRequest;
import com.android.taskallo.core.utils.Constant;
import com.android.taskallo.core.utils.KeyConstant;
import com.android.taskallo.core.utils.UrlConstant;
import com.android.taskallo.fragment.SimpleDialogFragment;
import com.android.taskallo.App;
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
public class ChangePwdActivity extends BaseFgActivity {

    public static final String TAG = "777";

    private Button bt_find_pwd;
    private EditText et_old_pwd, newPwdET1, ensurePwdEt;

    private boolean isShowPwd = false;
    private Handler handler = new Handler();
    private static final int WAIT_TIME = 61;
    private int second = 60;


    private SharedPreferences.Editor editor;
    private ChangePwdActivity context;
    private String tokenSp = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_change_pwd);
        SharedPreferences preferences = getSharedPreferences(Constant.CONFIG_FILE_NAME,
                MODE_PRIVATE);
        tokenSp = preferences.getString(Constant.CONFIG_TOKEN, "");
        editor = preferences.edit();
        BaseTitleBar titleBar = (BaseTitleBar) findViewById(R.id.title_bar);
        titleBar.setOnLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChangePwdActivity.this, UserCenterActivity.class));
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);//左进,右出
            }
        });

        bt_find_pwd = (Button) findViewById(R.id.bt_find_pwd);
        et_old_pwd = (EditText) findViewById(R.id.old_pwd_et);
        newPwdET1 = (EditText) findViewById(R.id.new_pwd_et1);

        ensurePwdEt = (EditText) findViewById(R.id.ensure_pwd_et);

        bt_find_pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPwdStr = et_old_pwd.getText().toString().trim();
                String ensurePwdStr = ensurePwdEt.getText().toString().trim();
                String newPwdETStr1 = newPwdET1.getText().toString().trim();

                if (oldPwdStr == null || oldPwdStr.length() <= 0) {
                    context = ChangePwdActivity.this;
                    ToastUtil.show(context, "旧密码不能为空哦");
                    return;
                }
                if (oldPwdStr.equals(newPwdETStr1)) {
                    ToastUtil.show(context, "新密码和旧密码不能一致哦");
                    return;
                }
                if (newPwdETStr1 == null || newPwdETStr1.length() <= 0) {
                    ToastUtil.show(context, "请输入新密码");
                    return;
                }
                if (newPwdETStr1.length() < 6) {
                    ToastUtil.show(context, "新密码不能少于六位哦");
                    return;
                }
                if (ensurePwdStr == null || ensurePwdStr.length() <= 0) {
                    ToastUtil.show(context, "请确认新密码");
                    return;
                }
                if (!newPwdETStr1.equals(ensurePwdStr)) {
                    ToastUtil.show(context, "两次输入的新密码不一致哦");
                    return;
                }

                doFindPwd(oldPwdStr, newPwdETStr1);
            }
        });
    }

    /**
     * 处理HTTP找回密码操作
     */
    private void doFindPwd(final String oldPwdStr, final String newPwdETStr1) {
        String url = Constant.WEB_SITE + UrlConstant.URL_MODIFY_PASSWORD;
        Response.Listener<JsonResult> successListener = new Response.Listener<JsonResult>() {
            @Override
            public void onResponse(JsonResult result) {
                if (result == null) {
                    ToastUtil.show(context, "服务端异常");
                    return;
                }
                int code = result.code;
                if (code == 0) {
                    String token = (String) result.data;
                    editor.putString(Constant.CONFIG_TOKEN, token).commit();
                    showDialog(true, "密码重置成功！");
                    //logoutClearData();
                } else if (code >= -4 && code <= -1) {
                    showReLoginDialog();
                    //需要重新登录
                    logoutClearData();
                    //UserCenterActivity.this.finish();
                } else {
                    showDialog(false, result.msg);
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                ToastUtil.show(context, "修改密码失败，请检查网络连接!");
            }
        };

        Request<JsonResult> versionRequest = new GsonRequest<JsonResult>(Request.Method.POST, url,
                successListener, errorListener, new TypeToken<JsonResult>() {
        }.getType()) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //设置POST请求参数
                Map<String, String> params = new HashMap<>();
                params.put(KeyConstant.APP_TYPE_ID, Constant.APP_TYPE_ID_0_ANDROID);
                params.put(KeyConstant.TOKEN, tokenSp);
                params.put(KeyConstant.old_Password, oldPwdStr);
                params.put(KeyConstant.newPassword, newPwdETStr1);
                return params;
            }
        };
        App.requestQueue.add(versionRequest);
    }

    /**
     * 显示注册结果对话框
     */
    private void showReLoginDialog() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        final SimpleDialogFragment dialogFragment = new SimpleDialogFragment();
        dialogFragment.setDialogWidth(220);
        TextView tv = new TextView(ChangePwdActivity.this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, ViewGroup
                .LayoutParams.MATCH_PARENT);
        params.setMargins(0, 20, 0, 0);
        params.gravity = Gravity.CENTER;
        tv.setLayoutParams(params);
        tv.setGravity(Gravity.CENTER);
        tv.setText("当前设备的登录信息已失效,\n需要重新登录后,才能执行修改操作");
        tv.setTextColor(getResources().getColor(R.color.color000000));
        dialogFragment.setContentView(tv);

        dialogFragment.setNegativeButton("去登录", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogFragment.dismiss();
                Intent intent = new Intent(ChangePwdActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        dialogFragment.show(ft, "successDialog");
    }

    //退出登录
    private void logoutClearData() {
        editor.putString(Constant.CONFIG_USER_PWD, "");
        editor.putBoolean(KeyConstant.AVATAR_HAS_CHANGED, true);
        editor.apply();

        App.userHeadUrl = "";
        App.nickName = "";
        App.userCode = "";
        App.userName = "";
        App.passWord = "";
        App.token = null;
        App.user = null;
    }

    /**
     * 显示注册结果对话框
     */
    private void showDialog(final boolean isSuccess, String msg) {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        final SimpleDialogFragment dialogFragment = new SimpleDialogFragment();
        dialogFragment.setDialogWidth(220);

        TextView tv = new TextView(ChangePwdActivity.this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, ViewGroup
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
            stringId = R.string.sure;
        } else {
            stringId = R.string.sure;
        }

        dialogFragment.setNegativeButton(stringId, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogFragment.dismiss();
              /*  if (isSuccess) {
                    Intent intent = new Intent(ChangePwdActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }*/
                finish();
            }
        });
        dialogFragment.show(ft, "successDialog");
    }
}
