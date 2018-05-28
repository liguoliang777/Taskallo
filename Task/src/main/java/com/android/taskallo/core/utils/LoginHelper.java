package com.android.taskallo.core.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.taskallo.App;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;

import com.android.taskallo.bean.JsonResult;
import com.android.taskallo.bean.User;
import com.android.taskallo.core.net.GsonRequest;

/**
 * 用户登录辅助类
 * Created by zeng on 2016/7/26.
 */
public class LoginHelper {

    public static final String TAG = "777";

    private Context context;
    private SharedPreferences preferences;
    private String userName;
    private String passWord;

    public LoginHelper(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(Constant.CONFIG_FILE_NAME, Context.MODE_PRIVATE);
        userName = preferences.getString(Constant.CONFIG_USER_NAME, "");
        passWord = preferences.getString(Constant.CONFIG_USER_PWD, "");
    }

    public void reLoadSP() {
        App.phone = preferences.getString(Constant.CONFIG_USER_NAME, "");
        App.passWord = preferences.getString(Constant.CONFIG_USER_PWD, "");
        App.userCode = preferences.getString(Constant.CONFIG_USER_CODE, "");
        App.userHeadUrl = preferences.getString(Constant.CONFIG_USER_HEAD, "");
        App.token = preferences.getString(Constant.CONFIG_TOKEN, "");
        App.loginType = preferences.getString(Constant.CONFIG_LOGIN_TYPE, Constant.PHONE);
    }

    /**
     * 重新登录
     */
    public void reLogin() {
        String url = Constant.WEB_SITE + Constant.URL_USER_LOGIN;
        android.util.Log.d(TAG, "重新登录1:账号 " + App.phone);
        android.util.Log.d(TAG, "重新登录1密码: " + App.passWord);
        Response.Listener<JsonResult<User>> succesListener = new Response.Listener<JsonResult<User>>() {
            @Override
            public void onResponse(JsonResult<User> result) {
                if (result == null) {
                    return;
                }
                if (result.code == 0) {
                    User user = result.data;
                    App.user = user;

                /*    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(Constant.CONFIG_USER_HEAD, user.headPortrait);
                    editor.putString(Constant.CONFIG_NICK_NAME, user.nickName);
                    editor.putString(Constant.CONFIG_USER_NAME, user.loginName);
                    editor.putString(Constant.CONFIG_USER_PWD, App.passWord);
                    editor.putString(Constant.CONFIG_LOGIN_TYPE, App.loginType);
                    editor.apply();*/

                    //加载用户头像
                    Log.d(TAG, "重新登录.昵称: " + user.nickName);
                    Log.d(TAG, "重新登录.账号: " + user.loginName);
                    Log.d(TAG, "重新登录.密码: " + App.passWord);
                    Log.d(TAG, "重新.User对象密码: " + user.password);
                    android.util.Log.d(TAG, "userToken:" + user.email);
                } else {
                    Log.d(TAG, "重新登录 HTTP请求成功：服务端返回错误: " + result.msg);
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                Log.d(TAG, "重新登录 HTTP请求失败：网络连接错误！" + volleyError.getMessage());
            }
        };
        Request<JsonResult<User>> versionRequest1 = new GsonRequest<JsonResult<User>>(Request.Method.POST, url,
                succesListener, errorListener, new TypeToken<JsonResult<User>>() {
        }.getType()) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //设置POST请求参数
                Map<String, String> params = new HashMap<>();
                params.put(KeyConstant.NICK_NAME, App.nickName);
                params.put(KeyConstant.loginName, App.phone);
                params.put(KeyConstant.pass_word, App.passWord);
                android.util.Log.d(TAG, "重新登录: " + App.passWord);
                android.util.Log.d(TAG, "重新登录: " + App.phone);
                params.put(KeyConstant.TYPE, App.loginType); //（1手机，2QQ，3微信，4新浪微博）
                params.put(KeyConstant.head_Portrait, App.userHeadUrl);  //头像
                params.put(KeyConstant.APP_TYPE_ID, Constant.APP_TYPE_ID_0_ANDROID);  //
                return params;
            }
        };
        App.requestQueue.add(versionRequest1);
    }
}
