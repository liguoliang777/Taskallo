/*
 * 	Flan.Zeng 2011-2016	http://git.oschina.net/signup?inviter=flan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.taskallo.base.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.FrameLayout;

import com.android.taskallo.App;
import com.android.taskallo.R;
import com.android.taskallo.activity.main.MainHomeActivity;
import com.android.taskallo.bean.JsonResult;
import com.android.taskallo.core.fileload.FileLoadService;
import com.android.taskallo.core.net.GsonRequest;
import com.android.taskallo.core.utils.Constant;
import com.android.taskallo.core.utils.KeyConstant;
import com.android.taskallo.core.utils.Log;
import com.android.taskallo.core.utils.SPUtils;
import com.android.taskallo.push.model.PushMessage;
import com.android.taskallo.user.view.LoginActivity;
import com.android.taskallo.util.ConvUtil;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * App启动时的等待窗口，处理进入home页时需要预先加载的内容
 *
 * @author flan
 * @since 2016年5月3日
 */
public class BeginActivity extends FragmentActivity {

    public static final String TAG = BeginActivity.class.getSimpleName();
    private boolean isFirstInstall = true;
    private Timer timer;
    private BeginActivity mContext;
    private long SHOW_TIME = 1000;
    private long SHOW_TIME_isFirstInstall = 1000;
    private Button skipBt;
    private FrameLayout adsParent;
    private String pwd;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置全屏
        setContentView(R.layout.activity_splash_fullscreen);
        mContext = this;
        //启动后台服务
        skipBt = (Button) findViewById(R.id.skip_bt);
        Intent serviceIntent = new Intent(this, FileLoadService.class);
        startService(serviceIntent);


        //判断是否是安装后第一次启动
        isFirstInstall = ConvUtil.NB(SPUtils.get(this, Constant.CONFIG_FIRST_INSTALL, true));
        //timer = new Timer();

        skip2Main();
    }

    private void go2Main() {
        final long pushMsgId = getIntent().getLongExtra("msgId", 0);
        final int pushMsgType = getIntent().getIntExtra("type", 0);
        final PushMessage msg = (PushMessage) getIntent().getSerializableExtra("msg");
        if (timer == null) {
            skip2Main();
            return;
        }
        if (isFirstInstall) {
            Log.d(TAG, "滑动页");
            final Intent intent = new Intent(mContext, GuideViewActivity.class);
            if (pushMsgId > 0) {
                intent.putExtra("msgId", pushMsgId);
                intent.putExtra("type", pushMsgType);
                intent.putExtra("msg", msg);
            }
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    startActivity(intent);
                    //更新安装状态值
                    SPUtils.put(mContext, Constant.CONFIG_FIRST_INSTALL, false);
                    finish();
                }
            }, SHOW_TIME_isFirstInstall);
        } else {
            final Intent intent = new Intent(mContext, MainHomeActivity.class);
            if (pushMsgId > 0) {
                intent.putExtra("msgId", pushMsgId);
                intent.putExtra("type", pushMsgType);

                intent.putExtra("msg", msg);
            }
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    startActivity(intent);
                    finish();
                }
            }, SHOW_TIME);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private String userName;

    public void skip2Main() {
      /*  if (timer != null) {
            timer.cancel();
            timer = null;
        }*/
        preferences = getSharedPreferences(Constant.CONFIG_FILE_NAME, MODE_PRIVATE);
        userName = preferences.getString(Constant.CONFIG_USER_PHONE, "");
        pwd = preferences.getString(Constant.CONFIG_USER_PWD, "");
        if (pwd != null && !"".equals(pwd)) {
            String url = Constant.WEB_SITE + Constant.URL_USER_LOGIN;

            Response.Listener<JsonResult> succesListener = new Response
                    .Listener<JsonResult>() {
                @Override
                public void onResponse(JsonResult result) {
                    if (result == null) {
                        startLoginActivity();
                        return;
                    }
                    if (result.code == 0) {
                        String token = (String) result.data;
                        preferences.edit().putString(Constant.CONFIG_TOKEN, token).apply();
                        App.token = token;
                        android.util.Log.d(TAG, "启动界面 修改token:" + token);
                        startActivity(new Intent(mContext, MainHomeActivity.class));
                        finish();
                    } else {
                        startLoginActivity();
                    }
                }
            };

            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    volleyError.printStackTrace();
                    startLoginActivity();
                    Log.d(TAG, "HTTP请求失败：网络连接错误！" + volleyError.getMessage());
                }
            };
            Request<JsonResult> versionRequest1 = new GsonRequest<JsonResult>(Request
                    .Method.POST, url,
                    succesListener, errorListener, new TypeToken<JsonResult>() {
            }.getType()) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    //设置POST请求参数
                    Map<String, String> params = new HashMap<>();
                    params.put(KeyConstant.loginName, userName);//uid
                    params.put(KeyConstant.passWord, pwd);//""
                    params.put(KeyConstant.APP_TYPE_ID, Constant.APP_TYPE_ID_0_ANDROID);  //
                    return params;
                }
            };
            App.requestQueue.add(versionRequest1);
        } else {
            startLoginActivity();
        }
        //去掉欢迎的滑动页
/*        if (isFirstInstall) {
            Log.d(TAG, "skip2Main 滑动页");
            final Intent intent = new Intent(content, GuideViewActivity.class);
            if (pushMsgId > 0) {
                intent.putExtra("msgId", pushMsgId);
                intent.putExtra("type", pushMsgType);
                intent.putExtra("msg", msg);
            }
            startActivity(intent);
            //更新安装状态值
            SPUtils.put(content, Constant.CONFIG_FIRST_INSTALL, false);
            finish();

        } else {*/
    }

    private void startLoginActivity() {
        Intent intent = new Intent(mContext, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return false;
    }
}