package com.android.taskallo.project.view;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.android.taskallo.App;
import com.android.taskallo.R;
import com.android.taskallo.activity.BaseFgActivity;
import com.android.taskallo.bean.JsonResult;
import com.android.taskallo.bean.ProjDetailInfo;
import com.android.taskallo.core.net.GsonRequest;
import com.android.taskallo.core.utils.Constant;
import com.android.taskallo.core.utils.ImageUtil;
import com.android.taskallo.core.utils.KeyConstant;
import com.android.taskallo.core.utils.NetUtil;
import com.android.taskallo.core.utils.UrlConstant;
import com.android.taskallo.project.Item.ItemView;
import com.android.taskallo.project.Item.ItemViewCallback;
import com.android.taskallo.util.ToastUtil;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;

public class ProjListActivity extends BaseFgActivity {
    private String mProjectId;
    private ProjListActivity context;
    private Button mTitleBackBt;
    private ItemView mBoardView;
    private String mProjectName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        setContentView(R.layout.activity_project_list);

        mProjectId = getIntent().getStringExtra(KeyConstant.ID);
        mProjectName = getIntent().getStringExtra(KeyConstant.name);
        context = this;
        mTitleBackBt = (Button) findViewById(R.id.proj_detail_title_back);
        mTitleBackBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mTitleBackBt.setText(mProjectName);

        mBoardView = (ItemView) findViewById(R.id.boardview);
        mBoardView.setCallback(new ItemViewCallback());
        mBoardView.setContext(context);

        getListInfo();
    }

    //获取列表数据
    private void getListInfo() {
        String url = Constant.WEB_SITE1 + UrlConstant.URL_PROJECT_DETAIL + "/" + mProjectId;
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, "网络异常,请检查网络设置");
            return;
        }

        Response.Listener<JsonResult<ProjDetailInfo>> successListener = new Response
                .Listener<JsonResult<ProjDetailInfo>>() {
            @Override
            public void onResponse(JsonResult<ProjDetailInfo> result) {
                if (result == null) {
                    ToastUtil.show(context, getString(R.string.server_exception));
                    return;
                }

                ProjDetailInfo projDetailInfo = result.data;
                if (result.code == 0 && context != null && projDetailInfo != null) {
                    setData(projDetailInfo);
                }
            }
        };

        Request<JsonResult<ProjDetailInfo>> versionRequest = new
                GsonRequest<JsonResult<ProjDetailInfo>>(
                        Request.Method.GET, url,
                        successListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                        Log.d(TAG, "网络连接错误！" + volleyError.getMessage());
                    }
                }, new TypeToken<JsonResult<ProjDetailInfo>>() {
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

    //设置数据
    private void setData(ProjDetailInfo projDetailInfo) {
        String projectImg = projDetailInfo.projectImg;
        Log.d(TAG, "列表详情:" + projectImg);
    }


    public void showPercentDialog() {
        final Dialog mUnboundDialog = new Dialog(context);
        mUnboundDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //填充对话框的布局
        View percentView = LayoutInflater.from(context).inflate(R.layout
                .layout_percentage_dialog, null);

        //用户头像+昵称
        SimpleDraweeView iconIv = (SimpleDraweeView) percentView.findViewById(R.id.ic_percent_icon);
        TextView nameTv = (TextView) percentView.findViewById(R.id.ic_percent_user_name);
        iconIv.setImageURI(App.userHeadUrl);
        nameTv.setText(App.nickName);

        TextView sumbitTv = (TextView) percentView.findViewById(R.id.ic_percent_sumbit_bt);
        sumbitTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取评分的值
                mUnboundDialog.dismiss();
                //提交评分
                //submitPercent(mUnboundDialog);
            }
        });
        mUnboundDialog.setContentView(percentView);//将布局设置给Dialog
        Window dialogWindow = mUnboundDialog.getWindow(); //获取当前Activity所在的窗体
        dialogWindow.setBackgroundDrawableResource(R.color.transparent);
        //dialogWindow.setGravity(Gravity.CENTER);//设置Dialog从窗体顶部弹出
        WindowManager.LayoutParams params = dialogWindow.getAttributes();
        params.width = getResources().getDimensionPixelSize(R.dimen.dm445);
        dialogWindow.setAttributes(params); //将属性设置给窗体
        if (context != null && !mUnboundDialog.isShowing()) {
            mUnboundDialog.show();//显示对话框
        }
    }

    // 菜单
    public void onProjListTopMenuClick(View view) {
        final Dialog dialog = new Dialog(context, R.style.Dialog_right_left);
        dialog.setCanceledOnTouchOutside(true);
        //填充对话框的布局
        View inflate = LayoutInflater.from(this).inflate(R.layout.layout_proj_detail_menu, null);

        View.OnClickListener mDialogClickLstener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.proj_list_menu_dialog_favorite_bt:
                        favoriteProj(dialog);
                        break;
                    case R.id.proj_list_menu_dialog_empty_view:
                        dialog.cancel();
                        break;
                }
            }
        };
        inflate.findViewById(R.id.proj_list_menu_dialog_favorite_bt).setOnClickListener
                (mDialogClickLstener);
        inflate.findViewById(R.id.proj_list_menu_dialog_empty_view).setOnClickListener
                (mDialogClickLstener);
        dialog.setContentView(inflate);//将布局设置给Dialog

        setDialogWindow(dialog);
    }

    //请求数据
    private void favoriteProj(final Dialog dialog) {
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, "网络异常,请检查网络设置");
            return;
        }
        // 0 默认状态，1 已删除，2  收藏，3 已完成
        String url = Constant.WEB_SITE1 + UrlConstant.url_project_favorite + "/" + mProjectId;

        Response.Listener<JsonResult> successListener = new Response
                .Listener<JsonResult>() {
            @Override
            public void onResponse(JsonResult result) {
                if (result == null) {
                    ToastUtil.show(context, getString(R.string.server_exception));
                    return;
                }
                Log.d(TAG, result.msg + ",请求主界面数据:" + result.data);
                if (result.code == 0 && result.data != null) {
                    dialog.cancel();
                }
            }
        };

        Request<JsonResult> versionRequest = new
                GsonRequest<JsonResult>(
                        Request.Method.PUT, url,
                        successListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                        Log.d(TAG, "网络连接错误！" + volleyError.getMessage());
                    }
                }, new TypeToken<JsonResult>() {
                }.getType()) {
                    @Override
                    public Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("ID", mProjectId);
                        return params;
                    }

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

    private void setDialogWindow(Dialog dialog) {
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.RIGHT);
        WindowManager.LayoutParams params = dialogWindow.getAttributes();
        //params.y = 20;  Dialog距离底部的距离
        params.width = ImageUtil.getScreenWidth(context) - 250;
        params.height = ImageUtil.getScreenHeight(context);
        dialogWindow.setAttributes(params);
        dialog.show();
    }

}
