package com.android.taskallo.project.view;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.taskallo.App;
import com.android.taskallo.R;
import com.android.taskallo.activity.BaseFgActivity;
import com.android.taskallo.bean.JsonResult;
import com.android.taskallo.bean.ListItemVOListBean;
import com.android.taskallo.bean.ProjDetailInfo;
import com.android.taskallo.core.net.GsonRequest;
import com.android.taskallo.core.utils.Constant;
import com.android.taskallo.core.utils.ImageUtil;
import com.android.taskallo.core.utils.KeyConstant;
import com.android.taskallo.core.utils.NetUtil;
import com.android.taskallo.core.utils.TextUtil;
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
import java.util.List;
import java.util.Map;

public class ProjListActivity extends BaseFgActivity {
    private String mProjectId;
    private ProjListActivity context;
    private Button mTitleBackBt;
    private ImageButton mTitleSearchBt, mTitleMsgBt, mTitleMenuBt;
    private ItemView mItemView;
    private String mProjectName = "";
    private String mProjectImg = "";
    private TextView mFeildEmptyTv;
    private Dialog dialog;
    private TextView mPublicPrivateTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        setContentView(R.layout.activity_project_list);

        mProjectId = getIntent().getStringExtra(KeyConstant.id);
        mProjectName = getIntent().getStringExtra(KeyConstant.name);
        mProjectImg = getIntent().getStringExtra(KeyConstant.projectImg);
        context = this;
        mTitleBackBt = (Button) findViewById(R.id.proj_detail_title_back);
        mTitleMenuBt = (ImageButton) findViewById(R.id.proj_detail_top_menu_back);
        mTitleMsgBt = (ImageButton) findViewById(R.id.proj_detail_top_msg_bt);
        mTitleSearchBt = (ImageButton) findViewById(R.id.proj_detail_top_search_bt);
        mFeildEmptyTv = (TextView) findViewById(R.id.proj_feild_empty_tv);
        mTitleBackBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mTitleBackBt.setText(mProjectName == null ? "" : mProjectName);

        mItemView = (ItemView) findViewById(R.id.boardview);
        mItemView.setCallback(new ItemViewCallback());
        mItemView.setContext(context, mProjectId);

        setBackground();

    }

    @Override
    protected void onStart() {
        super.onStart();
        getListInfo();

    }

    private void setBackground() {
        if (!NetUtil.isNetworkConnected(context)) {
            return;
        }
        new AsyncTask<String, Void, Drawable>() {
            @Override
            protected Drawable doInBackground(String... strings) {
                return ImageUtil.loadImageFromNetwork(mProjectImg);
            }

            @Override
            protected void onPostExecute(Drawable drawable) {
                mItemView.setBackground(drawable);
            }
        }.execute();
    }

    //获取列表数据
    private void getListInfo() {
        String url = Constant.WEB_SITE1 + UrlConstant.URL_PROJECT_DETAIL + "/" + mProjectId;
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, "网络异常,请检查网络设置");
            mItemView.setPadding(100, 0, 0, 0);
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
                    List<ListItemVOListBean> listItemVOList = projDetailInfo.listItemVOList;
                    if (listItemVOList != null && listItemVOList.size() > 0) {
                        mItemView.setData(listItemVOList);
                    } else {
                        mItemView.setPadding(100, 0, 0, 0);
                    }

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

    private void closeInputMethod() {
        View currentFocus = context.getCurrentFocus();
        if (currentFocus != null) {
            ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(currentFocus.getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void setPopupWindow() {
        LinearLayout window = new LinearLayout(context);
        window.setBackgroundResource(R.drawable.ic_rank_popup_bg);
        window.setGravity(Gravity.CENTER);
        TextView tv = new TextView(context);
        tv.setText(IS_PUBLIC ? R.string
                .str_private : R.string.str_public);
        tv.setTextColor(ContextCompat.getColor(context, R.color.color_808080));
        window.addView(tv);

        final PopupWindow popupWindow = new PopupWindow(window, 220, 120);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        window.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IS_PUBLIC = !IS_PUBLIC;
                popupWindow.dismiss();
                mPublicPrivateTv.setText(IS_PUBLIC ? R.string.str_public : R.string
                        .str_private);
            }
        });
        popupWindow.showAsDropDown(mPublicPrivateTv);
    }

    // 菜单
    public void onProjListTopMenuClick(View view) {
        dialog = new Dialog(context, R.style.Dialog_right_left);
        dialog.setCanceledOnTouchOutside(true);
        //填充对话框的布局
        View inflate = LayoutInflater.from(this).inflate(R.layout.layout_proj_detail_menu, null);

        final EditText projNameEt = (EditText) inflate.findViewById(R.id.proj_list_projName_et);
        View.OnClickListener mDialogClickLstener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (projNameEt != null) {
                    projNameEt.clearFocus();
                    closeInputMethod();
                }
                switch (v.getId()) {
                    case R.id.proj_menu_dialog_favorite_bt:
                        favoriteProj(dialog);
                        break;
                    case R.id.proj_list_menu_filed_bt:
                        //归档项目
                        filedProj(dialog);
                        break;
                    case R.id.proj_list_menu_delete_proj_bt:
                        //删除项目
                        deleteProj(dialog);
                        break;
                    case R.id.proj_list_menu_dialog_empty_view:
                        dialog.cancel();
                        break;
                    //私有,公开
                    case R.id.proj_list_public_private_tv:
                        setPopupWindow();
                        break;
                    //成员列表
                    case R.id.layout_memeber_list:
                        break;
                }
            }
        };
        inflate.findViewById(R.id.proj_menu_dialog_favorite_bt).setOnClickListener
                (mDialogClickLstener);
        inflate.findViewById(R.id.proj_list_menu_filed_bt).setOnClickListener(mDialogClickLstener);
        inflate.findViewById(R.id.layout_memeber_list).setOnClickListener(mDialogClickLstener);
        inflate.findViewById(R.id.proj_list_menu_delete_proj_bt).setOnClickListener
                (mDialogClickLstener);
        projNameEt.setText(mProjectName == null ? "" : mProjectName);

        mPublicPrivateTv = (TextView) inflate.findViewById(R.id.proj_list_public_private_tv);
        mPublicPrivateTv.setOnClickListener(mDialogClickLstener);
        mPublicPrivateTv.setText(IS_PUBLIC ? R.string.str_public : R.string
                .str_private);

        projNameEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String newprojNameStr = projNameEt.getText().toString();
                    if (TextUtil.isEmpty(newprojNameStr)) {
                        return;
                    }
                    //修改项目的名字
                    changeProjName(projNameEt, newprojNameStr);

                }

            }
        });
        inflate.findViewById(R.id.proj_list_menu_dialog_empty_view).setOnClickListener
                (mDialogClickLstener);
        dialog.setContentView(inflate);//将布局设置给Dialog

        setDialogWindow(dialog);
    }

    private void changeProjName(final EditText projNameEt, final String newProjNameStr) {
        if (!NetUtil.isNetworkConnected(context)) {
            return;
        }
        String url = Constant.WEB_SITE1 + UrlConstant.URL_PROJECT + "/" + mProjectId;

        Response.Listener<JsonResult> successListener = new Response
                .Listener<JsonResult>() {
            @Override
            public void onResponse(JsonResult result) {
                if (result.code == 0 && result.data != null) {
                    //修改成功
                    if (projNameEt != null && context != null) {
                        projNameEt.setText(newProjNameStr);
                        mTitleBackBt.setText(newProjNameStr);
                    }
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
                        Log.d("", "修改项目信息,网络错误:" + volleyError.getMessage());
                    }
                }, new TypeToken<JsonResult>() {
                }.getType()) {
                    @Override
                    public Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put(KeyConstant.privacy, "0");
                        params.put(KeyConstant.name, newProjNameStr);
                        params.put(KeyConstant.desc, "描述");
                        params.put(KeyConstant.imgId, "0");
                        return params;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put(KeyConstant.Content_Type, Constant.application_json);
                        params.put(KeyConstant.Authorization, App.token);
                        params.put(KeyConstant.appType, "0");
                        return params;
                    }
                };
        App.requestQueue.add(versionRequest);
    }

    private boolean IS_PUBLIC = true; //0 共有 1私有

    //删除项目
    private void deleteProj(final Dialog dialog) {
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, "网络异常,请检查网络设置");
            return;
        }
        String url = Constant.WEB_SITE1 + UrlConstant.URL_PROJECT + "/1";

        Response.Listener<JsonResult> successListener = new Response
                .Listener<JsonResult>() {
            @Override
            public void onResponse(JsonResult result) {
                if (result == null) {
                    ToastUtil.show(context, context.getString(R.string.server_exception));
                    return;
                }
                if (result.code == 0 && context != null) {
                    dialog.cancel();
                    finish();
                } else if (result.code == -3) {
                    ToastUtil.show(context, result.msg);
                }

            }
        };

        Request<JsonResult> versionRequest = new
                GsonRequest<JsonResult>(Request.Method.DELETE, url,
                        successListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                        ToastUtil.show(context, context.getString(R.string.server_exception));

                    }
                }, new TypeToken<JsonResult>() {
                }.getType()) {
                    @Override
                    public Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put(KeyConstant.id, mProjectId);
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

    //归档项目
    private void filedProj(final Dialog dialog) {
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, "网络异常,请检查网络设置");
            return;
        }
        String url = Constant.WEB_SITE1 + UrlConstant.url_project_failed + "/" + mProjectId;

        Response.Listener<JsonResult> successListener = new Response
                .Listener<JsonResult>() {
            @Override
            public void onResponse(JsonResult result) {
                if (result == null) {
                    ToastUtil.show(context, getString(R.string.server_exception));
                    return;
                }
                Log.d(TAG, result.msg + ",归档成功:" + result.data);
                if (result.code == 0) {
                    mFeildEmptyTv.setVisibility(View.VISIBLE);
                    mTitleMenuBt.setVisibility(View.GONE);
                    mTitleMsgBt.setVisibility(View.GONE);
                    mTitleSearchBt.setVisibility(View.GONE);
                    dialog.cancel();
                } else {
                    ToastUtil.show(context, getString(R.string.server_exception));
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
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put(KeyConstant.id, mProjectId);
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

    //收藏项目
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
                if (result.code == 0 && result.data != null) {
                    ToastUtil.show(context, "项目收藏成功");
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
