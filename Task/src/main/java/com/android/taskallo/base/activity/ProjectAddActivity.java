package com.android.taskallo.base.activity;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.taskallo.App;
import com.android.taskallo.R;
import com.android.taskallo.activity.BaseFgActivity;
import com.android.taskallo.bean.ImgInfo;
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
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 显示 关于信息的 界面
 * Created by zeng on 2016/5/23.
 */
public class ProjectAddActivity extends BaseFgActivity {

    private static final String TAG = ProjectAddActivity.class.getSimpleName();
    private ProjectAddActivity context;
    private Button mPublicPrivateBt;
    private PopupWindow popupWindow;
    private boolean PUBLIC_PRIVATE = true; //0 共有 1私有
    private EditText mProjNameTv, mProjSubtitleTv;
    private String mImgId = "1";
    private GridLayout imgLaout;
    private List<ImgInfo> gameLogoList;

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

        imgLaout = (GridLayout) findViewById(R.id.proj_add_def_bg_img_layout);

        getImgList();
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

    /**
     * 上传资料
     */
    public void getImgList() {
        final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup
                .LayoutParams.WRAP_CONTENT, ViewGroup
                .LayoutParams.WRAP_CONTENT);
        params.width = getResources().getDimensionPixelSize(R.dimen.dm136);
        params.height = getResources().getDimensionPixelSize(R.dimen.dm068);



        int dmMargin = getResources().getDimensionPixelSize(R.dimen.dm008);
        params.setMargins(dmMargin, dmMargin, dmMargin, dmMargin);

        final String url = Constant.WEB_SITE + UrlConstant.URL_ADD_PROJECT_IMG;
        final Response.Listener<JsonResult<List<ImgInfo>>> successListener = new Response
                .Listener<JsonResult<List<ImgInfo>>>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onResponse(JsonResult<List<ImgInfo>> result) {
                if (result == null || result.code != 0) {
                    ToastUtil.show(context, getString(R.string.get_img_faild));
                    // return;
                }

                //gameLogoList = result.data;
                gameLogoList = new ArrayList<>();
                gameLogoList.add(new ImgInfo());
                gameLogoList.add(new ImgInfo());
                gameLogoList.add(new ImgInfo());
                gameLogoList.add(new ImgInfo());
                gameLogoList.add(new ImgInfo());
                gameLogoList.add(new ImgInfo());
                gameLogoList.add(new ImgInfo());
                gameLogoList.add(new ImgInfo());
                if (gameLogoList != null) {
                    for (int i = 0; i < gameLogoList.size(); i++) {
                        ImgInfo imgInfo = gameLogoList.get(i);
                        if (imgInfo != null) {
                            SimpleDraweeView picassoImageView = new SimpleDraweeView(context);
                            final GenericDraweeHierarchy hierarchy = GenericDraweeHierarchyBuilder.newInstance(getResources())
                                    //设置圆形圆角参数
                                    .setRoundingParams(new RoundingParams().setCornersRadius(10)).build();
                            picassoImageView.setHierarchy(hierarchy);
                            picassoImageView.setLayoutParams(params);
                            picassoImageView.setScaleType(ImageView.ScaleType.CENTER);
                            picassoImageView.setLayoutParams(params);
                            //picassoImageView.setImageURI(imgInfo.imgUrl);
                            picassoImageView.setImageURI("http://res" +
                                    ".youzi4.cc/photo_files/news/20180221/d6502340-e340-3b6d-f75d" +
                                    "-01c51e2ef108.jpg");
                            imgLaout.addView(picassoImageView);
                        }
                    }
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
            }
        };

        Request<JsonResult<List<ImgInfo>>> versionRequest = new
                GsonRequest<JsonResult<List<ImgInfo>>>(Request.Method.POST, url,
                        successListener, errorListener, new TypeToken<JsonResult<List<ImgInfo>>>() {
                }.getType()) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        return null;
                    }
                };
        App.requestQueue.add(versionRequest);
    }
}