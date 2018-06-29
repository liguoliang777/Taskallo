package com.android.taskallo.project.view;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.taskallo.App;
import com.android.taskallo.R;
import com.android.taskallo.activity.BaseFgActivity;
import com.android.taskallo.bean.BoardVOListBean;
import com.android.taskallo.bean.JsonResult;
import com.android.taskallo.bean.MemberInfo;
import com.android.taskallo.bean.TagInfo;
import com.android.taskallo.core.net.GsonRequest;
import com.android.taskallo.core.utils.Constant;
import com.android.taskallo.core.utils.KeyConstant;
import com.android.taskallo.core.utils.NetUtil;
import com.android.taskallo.core.utils.TextUtil;
import com.android.taskallo.core.utils.UrlConstant;
import com.android.taskallo.project.Item.ExRadioGroup;
import com.android.taskallo.project.TagListActivity;
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

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CardDetailActivity extends BaseFgActivity implements PopupMenu
        .OnMenuItemClickListener {
    private Button mTopFinishedBT, mCancelBT, mTopEditSaveBt;
    private String mListTitle = "", mProjectId;
    private CardDetailActivity context;
    private EditText mCardTitleEt, mCardTalkEt, mCardDescEt;
    private TextView mListTitleTv;
    private BoardVOListBean mCardBean;
    private String mBoardId;
    private String mListItemId;
    private String mBoardName, postBoardName, postDesc;
    private String mBoardDesc;
    private int TYPE_TITLE = 1;
    private int TYPE_DESC = 2;
    private int TYPE_TALK = 3;
    private int EDITING_TYPE = 0;
    private String postStr, newStr;
    private LinearLayout mMemberLayout;
    private ExRadioGroup cardLayout;
    private LinearLayout.LayoutParams layoutParams;
    private int heightDM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        setContentView(R.layout.activity_card_detail);
        context = this;
        Bundle bundle = getIntent().getExtras();
        Serializable serializable = bundle.getSerializable(KeyConstant.cardBean);

        if (serializable != null && serializable instanceof BoardVOListBean) {
            mCardBean = (BoardVOListBean) serializable;

            mBoardId = mCardBean.boardId;
            mBoardName = mCardBean.boardName;
            postBoardName = mBoardName;
            mBoardDesc = mCardBean.boardDesc == null ? "" : mCardBean.boardDesc;
        }

        mListTitle = bundle.getString(KeyConstant.listItemName);
        mProjectId = bundle.getString(KeyConstant.projectId);
        mListItemId = bundle.getString(KeyConstant.listItemId);

        mTopFinishedBT = (Button) findViewById(R.id.top_left_finish_bt);
        mCancelBT = (Button) findViewById(R.id.top_left_delete_bt);
        mMemberLayout = (LinearLayout) findViewById(R.id.card_item_member_layout);

        mCardTitleEt = (EditText) findViewById(R.id.card_title_et);
        mCardDescEt = (EditText) findViewById(R.id.card_describe_et);
        mCardTalkEt = (EditText) findViewById(R.id.card_detail_talk_et);
        mListTitle = mListTitle == null ? "" : mListTitle;

        //列表标题
        mListTitleTv = (TextView) findViewById(R.id.card_in_list_tv);
        mListTitleTv.setText(mListTitle);

        //卡片标题
        mCardTitleEt.setText(mBoardName);
        mCardTitleEt.setSelection(mBoardName.length());

        //卡片描述
        mCardDescEt.setText(mBoardDesc);
        mCardDescEt.setSelection(mBoardDesc.length());


        mTopFinishedBT.setOnClickListener(onBtClickListener);

        mTopEditSaveBt = (Button) findViewById(R.id.edit_right_save_bt);

        mCancelBT.setOnClickListener(onBtClickListener);
        mTopEditSaveBt.setOnClickListener(onBtClickListener);

        //卡片标题
        View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                initCancelOkVisibility(b);
                int id = view.getId();
                if (b) {
                    isCancel = false;
                    EDITING_TYPE = id == R.id.card_title_et ?
                            TYPE_TITLE : id == R.id.card_describe_et ? TYPE_DESC : TYPE_TALK;
                } else {
                    //不是取消
                    if (!isCancel) {
                        postChange();
                    } else {
                        if (EDITING_TYPE == TYPE_TITLE) {
                            mCardTitleEt.setText(postBoardName);
                        } else if (EDITING_TYPE == TYPE_DESC) {
                            mCardDescEt.setText(postDesc);
                        }
                    }
                }

            }
        };
        mCardTitleEt.setOnFocusChangeListener(onFocusChangeListener);//标题
        mCardDescEt.setOnFocusChangeListener(onFocusChangeListener);//描述
        mCardTalkEt.setOnFocusChangeListener(onFocusChangeListener);//讨论输入框

        cardLayout = (ExRadioGroup) findViewById(R.id.card_item_tag_layout);

        heightDM = getResources().getDimensionPixelSize(R.dimen.dm057);
        layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 10, 10);

    }

    //获取标签数据
    private void getRelationData() {
        String url = Constant.WEB_SITE1 + UrlConstant.url_label + UrlConstant.url_label + "/" +
                mBoardId;
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, "网络异常,请检查网络设置");
            return;
        }

        Response.Listener<JsonResult<List<TagInfo>>> successListener = new Response
                .Listener<JsonResult<List<TagInfo>>>() {
            @Override
            public void onResponse(JsonResult<List<TagInfo>> result) {
                if (result == null) {
                    ToastUtil.show(context, getString(R.string.server_exception));
                    return;
                }

                List<TagInfo> relationInfo = result.data;
                if (result.code == 0 && context != null && relationInfo != null && relationInfo
                        .size() > 0) {
                    cardLayout.removeAllViews();
                    cardLayout.setVisibility(View.VISIBLE);
                    for (TagInfo tagInfo : relationInfo) {
                        TextView codeBtn = new TextView(context);
                        codeBtn.setGravity(Gravity.CENTER_VERTICAL);
                        codeBtn.setPadding(20, 3, 20, 6);
                        codeBtn.setMinWidth(heightDM);
                        codeBtn.setText(tagInfo.labelName == null ? "" : tagInfo.labelName);
                        codeBtn.setSingleLine();
                        codeBtn.setTextColor(Color.WHITE);
                        ShapeDrawable drawable = new ShapeDrawable(new RoundRectShape
                                (outerRadian, null,
                                null));
                        drawable.getPaint().setStyle(Paint.Style.FILL);
                        drawable.getPaint().setColor(Color.parseColor(tagInfo.labelColour));
                        codeBtn.setBackground(drawable);
                        codeBtn.setLayoutParams(layoutParams);
                        cardLayout.addView(codeBtn);
                    }
                } else {
                    cardLayout.setVisibility(View.INVISIBLE);
                }
            }
        };

        Request<JsonResult<List<TagInfo>>> versionRequest = new
                GsonRequest<JsonResult<List<TagInfo>>>(
                        Request.Method.GET, url,
                        successListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                        Log.d(TAG, "网络连接错误！" + volleyError.getMessage());
                    }
                }, new TypeToken<JsonResult<List<TagInfo>>>() {
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

    float[] outerRadian = new float[]{7, 7, 7, 7, 7, 7, 7, 7};

    @Override
    protected void onStart() {
        super.onStart();
        //获取成员列表
        getRelationData();
        getMemberInfo();
    }

    private void initCancelOkVisibility(boolean b) {
        mTopEditSaveBt.setVisibility(b ? View.VISIBLE : View.GONE);
        mCancelBT.setVisibility(b ? View.VISIBLE : View.GONE);
    }

    private boolean isCancel = false;
    //取消
    View.OnClickListener onBtClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            switch (id) {
                case R.id.edit_right_save_bt:
                    isCancel = false;
                    closeInputMethod();
                    break;
                case R.id.top_left_delete_bt:
                    isCancel = true;
                    closeInputMethod();
                    break;
                case R.id.top_left_finish_bt:
                    finish();
                    break;

            }
        }
    };

    private void postChange() {
        if (EDITING_TYPE == TYPE_TITLE) {
            String title = mCardTitleEt.getText().toString();
            if (!TextUtil.isEmpty(title)) {
                postBoardName = title;
            }
            mCardTitleEt.setText(postBoardName);
        }
        postDesc = mCardDescEt.getText().toString();
        //传数值
        if (!NetUtil.isNetworkConnected(context)) {
            return;
        }
        String url = Constant.WEB_SITE1 + UrlConstant.url_board + "/" + mBoardId;

        Response.Listener<JsonResult> successListener = new Response
                .Listener<JsonResult>() {
            @Override
            public void onResponse(JsonResult result) {
                if (result.code == 0 && result.data != null && context != null) {
                    //修改成功
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
                        Log.d("", "网络连接错误！" + volleyError.getMessage());
                    }
                }, new TypeToken<JsonResult>() {
                }.getType()) {
                    @Override
                    public Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put(KeyConstant.boardName, postBoardName);
                        params.put(KeyConstant.listItemId, mListItemId);
                        params.put(KeyConstant.boardDesc, postDesc);
                        params.put(KeyConstant.expiryTime, System.currentTimeMillis() + "");
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

    //获取看板成员
    private void getMemberInfo() {
        String url = Constant.WEB_SITE1 + UrlConstant.URL_MEMEBER + "/" + mBoardId;
        if (!NetUtil.isNetworkConnected(context)) {
            return;
        }

        Response.Listener<JsonResult<List<MemberInfo>>> successListener = new Response
                .Listener<JsonResult<List<MemberInfo>>>() {
            @Override
            public void onResponse(JsonResult<List<MemberInfo>> result) {
                if (result == null) {
                    ToastUtil.show(context, getString(R.string.server_exception));
                    return;
                }

                List<MemberInfo> memberInfoList = result.data;
                if (result.code == 0 && context != null && memberInfoList != null &&
                        memberInfoList.size() > 0) {
                    mMemberLayout.setVisibility(View.VISIBLE);
                    setMemberInfo(memberInfoList);
                } else {
                    mMemberLayout.setVisibility(View.INVISIBLE);
                }
            }
        };

        Request<JsonResult<List<MemberInfo>>> versionRequest = new
                GsonRequest<JsonResult<List<MemberInfo>>>(
                        Request.Method.GET, url,
                        successListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                        Log.d(TAG, "网络连接错误！" + volleyError.getMessage());
                    }
                }, new TypeToken<JsonResult<List<MemberInfo>>>() {
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

    private void setMemberInfo(List<MemberInfo> memberInfoList) {
        int size = memberInfoList.size();
        int widthHeight = getResources().getDimensionPixelOffset(R.dimen.dm050);
        for (int i = 0; i < size; i++) {
            MemberInfo img = memberInfoList.get(i);
            SimpleDraweeView picassoImageView = new SimpleDraweeView(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup
                    .LayoutParams.WRAP_CONTENT, ViewGroup
                    .LayoutParams.WRAP_CONTENT);

            params.width = widthHeight;
            params.height = widthHeight;
            params.setMargins(0, 0, widthHeight / 4, 0);
            picassoImageView.setLayoutParams(params);

            GenericDraweeHierarchy hierarchy = GenericDraweeHierarchyBuilder.newInstance
                    (getResources())
                    //设置圆形圆角参数；RoundingParams.asCircle()是将图像设置成圆形
                    .setRoundingParams(RoundingParams.asCircle())
                    //设置淡入淡出动画持续时间(单位：毫秒ms)
                    .setFadeDuration(0)
                    //构建
                    .build();
            picassoImageView.setHierarchy(hierarchy);

            picassoImageView.setImageURI(img.headPortrait);


            mMemberLayout.addView(picassoImageView);
        }
    }

    //顶部弹窗
    public void showCardPopupWindow(View v) {
        PopupMenu popupMenu = new PopupMenu(context, v);
        popupMenu.getMenuInflater().inflate(R.menu.card_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.show();

    }

    //删除卡片
    private void deleteCard() {
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, "网络异常,请检查网络设置");
            return;
        }
        String url = Constant.WEB_SITE1 + UrlConstant.url_board + "/" + mBoardId;

        Response.Listener<JsonResult> successListener = new Response
                .Listener<JsonResult>() {
            @Override
            public void onResponse(JsonResult result) {
                if (result == null) {
                    ToastUtil.show(context, context.getString(R.string.server_exception));
                    return;
                }
                if (result.code == 0 && context != null) {
                    finish();
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

    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.card_menu_0:
                Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
                break;
            case R.id.card_menu_1:
                Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
                break;
            case R.id.card_menu_5:
                deleteCard();
                break;

            default:
                break;
        }
        return true;
    }

    //添加子任务的item
    public void onSubTaskAddBtClick(View view) {

    }

    private void closeInputMethod() {
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(context
                                .getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);//关闭输入法
        mCardDescEt.clearFocus();
        mCardTitleEt.clearFocus();
        mCardTalkEt.clearFocus();
    }

    public void onCradDetailTagAddBtClick(View view) {
        Intent intent = new Intent(context, TagListActivity.class);
        List<TagInfo> tagInfo = mCardBean.projectLabelVOList;
        intent.putExtra(KeyConstant.tagInfo, (Serializable) tagInfo);
        Bundle bundle = new Bundle();
        bundle.putString(KeyConstant.projectId, mProjectId);
        bundle.putString(KeyConstant.boardId, mBoardId);
        intent.putExtras(bundle);
        startActivity(intent);

    }
}
