package com.android.taskallo.project.view;

import android.app.Dialog;
import android.os.Bundle;
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
import com.android.taskallo.bean.GameInfo;
import com.android.taskallo.bean.JsonResult;
import com.android.taskallo.core.net.GsonRequest;
import com.android.taskallo.core.utils.Constant;
import com.android.taskallo.core.utils.ImageUtil;
import com.android.taskallo.core.utils.KeyConstant;
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
    }

    private void getGameInfo() {
        String url = Constant.WEB_SITE + Constant.URL_GAME_DETAIL;
        Response.Listener<JsonResult<GameInfo>> successListener = new Response
                .Listener<JsonResult<GameInfo>>() {
            @Override
            public void onResponse(JsonResult<GameInfo> result) {
                if (result == null || result.code != 0) {
                    ToastUtil.show(context, getString(R.string.server_exception));
                    return;
                }


            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
            }
        };

        Request<JsonResult<GameInfo>> request = new GsonRequest<JsonResult<GameInfo>>(Request
                .Method.POST, url,
                successListener, errorListener, new TypeToken<JsonResult<GameInfo>>() {
        }.getType()) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put(KeyConstant.APP_TYPE_ID, Constant.APP_TYPE_ID_0_ANDROID);
                return params;
            }
        };
        App.requestQueue.add(request);
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

    //todo 菜单
    public void onProjListTopMenuClick(View view) {
        final Dialog dialog = new Dialog(context, R.style.Dialog_right_left);
        dialog.setCanceledOnTouchOutside(true);
        //填充对话框的布局
        View inflate = LayoutInflater.from(this).inflate(R.layout.layout_proj_list_menu, null);

        View.OnClickListener mDialogClickLstener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                switch (v.getId()) {
                    case R.id.proj_list_menu_dialog_favorite_bt:
                        dialog.cancel();
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
