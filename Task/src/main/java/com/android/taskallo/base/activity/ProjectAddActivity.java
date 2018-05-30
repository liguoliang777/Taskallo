package com.android.taskallo.base.activity;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.taskallo.App;
import com.android.taskallo.R;
import com.android.taskallo.activity.BaseFgActivity;
import com.android.taskallo.bean.JsonResult;
import com.android.taskallo.core.net.GsonRequest;
import com.android.taskallo.core.utils.Constant;
import com.android.taskallo.core.utils.KeyConstant;
import com.android.taskallo.core.utils.TextUtil;
import com.android.taskallo.core.utils.UrlConstant;
import com.android.taskallo.util.ToastUtil;
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
    private Button mPublicPrivateBt;
    private PopupWindow popupWindow;
    private boolean PUBLIC_PRIVATE = true; //0 共有 1私有
    private EditText mProjNameTv, mProjSubtitleTv;
    private String mImgId="1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        setContentView(R.layout.activity_add_project);
        context = this;
        Button saveBt = (Button) findViewById(R.id.title_right_bt);
        mPublicPrivateBt = (Button) findViewById(R.id.proj_public_or_private);
        mProjNameTv = (EditText) findViewById(R.id.proj_name_et);
        mProjSubtitleTv = (EditText) findViewById(R.id.proj_subtitle_et);
        saveBt.setVisibility(View.VISIBLE);
        saveBt.setOnClickListener(mOnClickListener);
        mPublicPrivateBt.setOnClickListener(mOnClickListener);
        findViewById(R.id.left_bt).setOnClickListener(mOnClickListener);
        ((TextView) findViewById(R.id.center_tv)).setText(R.string.project_add);
    }

    private void setPopupWindow() {
        LinearLayout window = new LinearLayout(context);
        window.setBackgroundResource(R.drawable.ic_rank_popup_bg);
        window.setGravity(Gravity.CENTER);
        TextView tv = new TextView(context);
        tv.setText(PUBLIC_PRIVATE ? R.string.str_private : R.string
                .str_public);
        tv.setTextColor(ContextCompat.getColor(context, R.color.color_808080));
        window.addView(tv);

        popupWindow = new PopupWindow(window, 220, 120);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mPublicPrivateBt.setSelected(false);
            }
        });

        window.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PUBLIC_PRIVATE = !PUBLIC_PRIVATE;
                popupWindow.dismiss();
                mPublicPrivateBt.setText(PUBLIC_PRIVATE ? R.string.str_public : R.string
                        .str_private);
            }
        });
    }

    /**
     * 上传资料
     */
    public void postAddProjectData() {
        final String projName = mProjNameTv.getText().toString();
        final String projSubtitle = mProjSubtitleTv.getText().toString();
        if (TextUtil.isEmpty(projName)) {
            ToastUtil.show(context, getString(R.string.proj_cannot_empty));
            return;
        }
        Log.d(TAG, "提交:" + projSubtitle);
        String url = Constant.WEB_SITE + UrlConstant.URL_ADD_PROJECT;
        Response.Listener<JsonResult> successListener = new Response
                .Listener<JsonResult>() {
            @Override
            public void onResponse(JsonResult result) {
                if (result == null || result.code != 0) {
                    ToastUtil.show(context, getString(R.string.create_proj_faild));
                    return;
                }
                ToastUtil.show(context, getString(R.string.create_proj_success));
                finish();
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
            }
        };

        Request<JsonResult> versionRequest = new
                GsonRequest<JsonResult>(Request.Method.POST, url,
                        successListener, errorListener, new TypeToken<JsonResult>() {
                }.getType()) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put(KeyConstant.name, projName);
                        params.put(KeyConstant.desc, projSubtitle);
                        params.put(KeyConstant.imgId, mImgId);
                        params.put(KeyConstant.privacy, PUBLIC_PRIVATE ? "0" : "1");
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
                    postAddProjectData();
                    break;
                case R.id.left_bt:
                    finish();
                    break;
                case R.id.proj_public_or_private:
                    setPopupWindow();
                    popupWindow.showAsDropDown(mPublicPrivateBt);
                    mPublicPrivateBt.setSelected(!mPublicPrivateBt.isSelected());
                    break;

            }
        }
    };
}
