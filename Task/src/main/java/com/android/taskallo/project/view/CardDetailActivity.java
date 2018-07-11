package com.android.taskallo.project.view;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.taskallo.App;
import com.android.taskallo.R;
import com.android.taskallo.adapter.FileListAdapter;
import com.android.taskallo.bean.BoardVOListBean;
import com.android.taskallo.bean.FileListInfo;
import com.android.taskallo.bean.JsonResult;
import com.android.taskallo.bean.MemberInfo;
import com.android.taskallo.bean.SubtaskInfo;
import com.android.taskallo.bean.SubtaskItemInfo;
import com.android.taskallo.bean.TagInfo;
import com.android.taskallo.core.net.GsonRequest;
import com.android.taskallo.core.net.RetrofitUtil;
import com.android.taskallo.core.utils.Constant;
import com.android.taskallo.core.utils.ImageUtil;
import com.android.taskallo.core.utils.KeyConstant;
import com.android.taskallo.core.utils.NetUtil;
import com.android.taskallo.core.utils.TextUtil;
import com.android.taskallo.core.utils.UrlConstant;
import com.android.taskallo.gamehub.bean.PictureBean;
import com.android.taskallo.gamehub.view.CommonBaseActivity;
import com.android.taskallo.project.Item.ExRadioGroup;
import com.android.taskallo.project.TagListActivity;
import com.android.taskallo.project.bean.EventInfo;
import com.android.taskallo.project.bean.LogsBean;
import com.android.taskallo.util.ToastUtil;
import com.android.taskallo.view.PullScrollView;
import com.android.taskallo.widget.mulpicture.MulPictureActivity;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.reflect.TypeToken;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;

import org.feezu.liuli.timeselector.TimeSelector;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class CardDetailActivity extends CommonBaseActivity implements PopupMenu
        .OnMenuItemClickListener {
    private Button mTopFinishedBT, mCancelBT, mTopEditSaveBt;
    private CardDetailActivity context;
    private EditText mCardTitleEt, mCardTalkEt, mCardDescEt;
    private TextView mListTitleTv;
    private BoardVOListBean mCardBean;
    private String mBoardId = "", mListItemId = "", mListTitle = "", mProjectId = "",
            mBoardName = "", postBoardName = "", postDesc = "", mBoardDesc = "", oldTitle = "";
    private int TYPE_TITLE = 1;
    private int TYPE_DESC = 2;
    private int TYPE_TALK = 3;
    private int EDITING_TYPE = 0;
    private LinearLayout mMemberLayout;
    private ExRadioGroup cardLayout;
    private LinearLayout.LayoutParams layoutParams;
    private int heightDM;
    private ExpandableListView subtaskLV;
    private boolean isTopClick = false;
    private String SUBTASK_DEF_NAME = "子任务";
    private MyExpandableListAdapter mSubtaskLvAdapter;
    private ArrayList<SubtaskItemInfo> itemInfos = new ArrayList<>();
    private TextView mExpiryTimeTv;
    private long mExpiryTime = 0;
    private SimpleDateFormat formatterStr;
    private RecyclerView mEventRV;
    private CardEventAdapter mEventRVAdapter;
    private List<LogsBean> mEventList = new ArrayList<>();
    private GridView mGridView;
    private FileListAdapter fileListAdapter;
    private List<FileListInfo> mFileListData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        setContentView(R.layout.activity_card_detail);

        itemInfos.add(new SubtaskItemInfo("-1", ""));

        context = this;
        formatterStr = new SimpleDateFormat(formatStr);

        Bundle bundle = getIntent().getExtras();
        Serializable serializable = null;
        if (bundle != null) {
            serializable = bundle.getSerializable(KeyConstant.cardBean);
            mListTitle = bundle.getString(KeyConstant.listItemName);
            mProjectId = bundle.getString(KeyConstant.projectId);
            mListItemId = bundle.getString(KeyConstant.listItemId);
        }

        if (serializable != null && serializable instanceof BoardVOListBean) {
            mCardBean = (BoardVOListBean) serializable;

            mBoardId = mCardBean.boardId;
            mBoardName = mCardBean.boardName;
            postBoardName = mBoardName;
            mExpiryTime = mCardBean.expiryTime;
            mBoardDesc = mCardBean.boardDesc == null ? "" : mCardBean.boardDesc;
        }
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
        if (mBoardName == null) {
            mBoardName = "";
        }
        mCardTitleEt.setText(mBoardName);
        mCardTitleEt.setSelection(mBoardName.length());

        //卡片描述
        mCardDescEt.setText(mBoardDesc);
        mCardDescEt.setSelection(mBoardDesc.length());


        mTopFinishedBT.setOnClickListener(onBtClickListener);
        findViewById(R.id.card_detail_file_bt).setOnClickListener(onBtClickListener);

        mTopEditSaveBt = (Button) findViewById(R.id.edit_right_save_bt);
        mExpiryTimeTv = (TextView) findViewById(R.id.crad_detail_time_tv);
        PullScrollView mPullSv = (PullScrollView) findViewById(R.id.card_pull_sv);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mPullSv.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int
                        oldScrollY) {
                    mTopFinishedBT.setText(scrollY > 150 ? mBoardName : "");
                }
            });
        }

        if (mExpiryTime != 0) {
            mExpiryTimeTv.setText(formatterStr.format(mExpiryTime) + " 到期");
            mExpiryTimeTv.getPaint().setFakeBoldText(true);
        }

        mCancelBT.setOnClickListener(onBtClickListener);
        mTopEditSaveBt.setOnClickListener(onBtClickListener);

        mCardTitleEt.setOnFocusChangeListener(onFocusChangeListener);//标题
        mCardDescEt.setOnFocusChangeListener(onFocusChangeListener);//描述
        mCardTalkEt.setOnFocusChangeListener(onFocusChangeListener);//讨论输入框

        cardLayout = (ExRadioGroup) findViewById(R.id.card_item_tag_layout);
        heightDM = getResources().getDimensionPixelSize(R.dimen.dm057);
        layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 10, 10);

        //----------------------------------------------------------------------------------------------------
        subtaskLV = (ExpandableListView) findViewById(R.id.expandable_lv);
        //给ExpandableListAdapter设置适配器---自定义适配器需要继承BaseExpandableListAdapter()实现其中的方法
        mSubtaskLvAdapter = new MyExpandableListAdapter(subtaskListData);
        //设置适配器
        subtaskLV.setAdapter(mSubtaskLvAdapter);

        reSetLVHeight(subtaskLV);
        //去掉group默认的箭头
        subtaskLV.setGroupIndicator(null);
        //设置组可拉伸的监听器,拉伸时会调用其中的onGroupExpand()方法
        subtaskLV.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                reSetLVHeight(subtaskLV);
                /*     *//**
                 * 实现打开只能打开一个组的功能,打开一个组,已将打开的组会自动收缩
                 *//*
                if(lastGroupPosition != groupPosition){
                    subtaskLV.collapseGroup(lastGroupPosition);
                }
                lastGroupPosition  = groupPosition;*/
            }
        });

        //设置组收缩的监听器,收缩时会调用其中的onGroupCollapse()方法
        subtaskLV.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                reSetLVHeight(subtaskLV);
            }
        });

        getSubTaskList();

        initEventRV();

        mGridView = (GridView) findViewById(R.id.horizontal_gridview);

        /*String fileUrl = "http://oss.fanglenet.cn/upload/1531127053591.jpg";
        FileListInfo fileListInfo = new FileListInfo("0", "112", fileUrl, 0, 0);
        mFileListData.add(fileListInfo);
        mFileListData.add(fileListInfo);
        mFileListData.add(fileListInfo);
        mFileListData.add(fileListInfo);*/

        fileListAdapter = new FileListAdapter(context, mFileListData, mBoardId);
        mGridView.setAdapter(fileListAdapter);
        //reSetLVHeight(mGridView);
    }

    private void initEventRV() {
        mEventRV = (RecyclerView) findViewById(R.id.crad_detail_event_rv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                context, LinearLayoutManager.VERTICAL, false);
        mEventRV.setLayoutManager(linearLayoutManager);

        mEventRVAdapter = new CardEventAdapter(context, mEventList);
        mEventRV.setHasFixedSize(true);
        mEventRV.setNestedScrollingEnabled(false);
        mEventRV.setAdapter(mEventRVAdapter);
    }

    //获取活动日志数据
    private void getFileListData() {
        if (!NetUtil.isNetworkConnected(context)) {
            return;
        }
        String url = Constant.WEB_SITE1 + UrlConstant.url_files + "/" +
                mBoardId;

        Response.Listener<JsonResult<List<FileListInfo>>> successListener = new Response
                .Listener<JsonResult<List<FileListInfo>>>() {
            @Override
            public void onResponse(JsonResult<List<FileListInfo>> result) {
                if (result == null) {
                    ToastUtil.show(context, getString(R.string.server_exception));
                    return;
                }

                mFileListData = result.data;
                if (result.code == 0 && context != null && mFileListData != null) {
                    fileListAdapter.setDate(mFileListData);
                    reSetLVHeight(mGridView);
                }
            }
        };

        Request<JsonResult<List<FileListInfo>>> versionRequest = new
                GsonRequest<JsonResult<List<FileListInfo>>>(
                        Request.Method.GET, url,
                        successListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.d(TAG, "服务器异常！");
                    }
                }, new TypeToken<JsonResult<List<FileListInfo>>>() {
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

    //获取活动日志数据
    private void getEventThread() {
        if (!NetUtil.isNetworkConnected(context)) {
            return;
        }
        String url = Constant.WEB_SITE1 + UrlConstant.url_logs + "/" +
                mBoardId;

        Response.Listener<JsonResult<EventInfo>> successListener = new Response
                .Listener<JsonResult<EventInfo>>() {
            @Override
            public void onResponse(JsonResult<EventInfo> result) {
                if (result == null) {
                    ToastUtil.show(context, getString(R.string.server_exception));
                    return;
                }

                EventInfo data = result.data;
                if (result.code == 0 && context != null && data != null) {
                    mEventList = data.logs.subList(0, data.count > 30 ? 30 : data.count);
                    if (mEventList != null) {
                        mEventRVAdapter.setList(mEventList);
                    }
                }
            }
        };

        Request<JsonResult<EventInfo>> versionRequest = new
                GsonRequest<JsonResult<EventInfo>>(
                        Request.Method.GET, url,
                        successListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                        Log.d(TAG, "网络连接错误！" + volleyError.getMessage());
                    }
                }, new TypeToken<JsonResult<EventInfo>>() {
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

    //获取子任务列表
    private void getSubTaskList() {
        String url = Constant.WEB_SITE1 + UrlConstant.url_subtask + "/" +
                mBoardId;
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, "网络异常,请检查网络设置");
            return;
        }

        Response.Listener<JsonResult<List<SubtaskInfo>>> successListener = new Response
                .Listener<JsonResult<List<SubtaskInfo>>>() {
            @Override
            public void onResponse(JsonResult<List<SubtaskInfo>> result) {
                if (result == null) {
                    ToastUtil.show(context, getString(R.string.server_exception));
                    return;
                }

                subtaskListData = result.data;
                if (result.code == 0 && context != null &&
                        subtaskListData != null) {
                    for (int i = 0; i < subtaskListData.size(); i++) {
                        childItemListData.add(i, itemInfos);
                        getChildInfo(i);
                    }
                } else {
                }
            }
        };

        Request<JsonResult<List<SubtaskInfo>>> versionRequest = new
                GsonRequest<JsonResult<List<SubtaskInfo>>>(
                        Request.Method.GET, url,
                        successListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                        Log.d(TAG, "网络连接错误！" + volleyError.getMessage());
                    }
                }, new TypeToken<JsonResult<List<SubtaskInfo>>>() {
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

    private void getChildInfo(final int i) {
        final SubtaskInfo subtaskInfo = subtaskListData.get(i);
        if (subtaskInfo == null) {
            return;
        }
        String url = Constant.WEB_SITE1 + UrlConstant.url_term + "/" + subtaskInfo.subtaskId;
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, "网络异常,请检查网络设置");
            return;
        }

        Response.Listener<JsonResult<List<SubtaskItemInfo>>> successListener = new Response
                .Listener<JsonResult<List<SubtaskItemInfo>>>() {
            @Override
            public void onResponse(JsonResult<List<SubtaskItemInfo>> result) {
                if (result == null) {
                    ToastUtil.show(context, getString(R.string.server_exception));
                    return;
                }

                List<SubtaskItemInfo> relationInfo = result.data;
                if (result.code == 0 && context != null) {
                    if (relationInfo != null && relationInfo.size() != 0) {
                        childItemListData.set(i, relationInfo);

                    } else {
                        childItemListData.set(i, itemInfos);
                    }

                    if (i == subtaskListData.size() - 1) {
                        if (childItemListData.size() > 1) {
                            itemInfos.clear();
                            itemInfos.add(new SubtaskItemInfo("-1", ""));
                            childItemListData.add(itemInfos);
                        }
                        mSubtaskLvAdapter.setData(subtaskListData, childItemListData);
                        reSetLVHeight(subtaskLV);
                    }
                }
            }
        };

        Request<JsonResult<List<SubtaskItemInfo>>> versionRequest = new
                GsonRequest<JsonResult<List<SubtaskItemInfo>>>(
                        Request.Method.GET, url,
                        successListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                        Log.d(TAG, "网络连接错误！" + volleyError.getMessage());
                    }
                }, new TypeToken<JsonResult<List<SubtaskItemInfo>>>() {
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

    //添加
    private void addSubtask(final String SUBTASK_DEF_NAME) {
        String url = Constant.WEB_SITE1 + UrlConstant.url_subtask;
        Response.Listener<JsonResult<SubtaskInfo>> successListener = new Response
                .Listener<JsonResult<SubtaskInfo>>() {
            @Override
            public void onResponse(JsonResult<SubtaskInfo> result) {
                if (result == null || result.code != 0) {
                    ToastUtil.show(context, getString(R.string.requery_failed));
                    return;
                }
                //添加子任务成功
                SubtaskInfo data = result.data;
                if (context != null) {
                    //把返回的集合添加到子任务集合里面去
                    subtaskListData.add(data);
                    itemInfos.clear();
                    itemInfos.add(new SubtaskItemInfo("-1", ""));
                    childItemListData.add(itemInfos);
                    if (subtaskListData != null) {
                        mSubtaskLvAdapter.setData(subtaskListData, childItemListData);
                        reSetLVHeight(subtaskLV);
                    }
                }

            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                ToastUtil.show(context, getString(R.string.requery_failed));
            }
        };

        Request<JsonResult<SubtaskInfo>> versionRequest = new
                GsonRequest<JsonResult<SubtaskInfo>>(Request.Method.POST, url,
                        successListener, errorListener, new TypeToken<JsonResult<SubtaskInfo>>() {
                }.getType()) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put(KeyConstant.subtaskName, SUBTASK_DEF_NAME);
                        params.put(KeyConstant.boardId, mBoardId);
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

    //卡片标题
    private View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
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
                    postTitleChange();
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

    public void reSetLVHeight(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        if (listAdapter == null) {
            // pre-condition
            return;
        }
        int totalHeight = 0;
        View view;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, null, listView);
            //宽度为屏幕宽度
            int i1 = View.MeasureSpec.makeMeasureSpec(ImageUtil.getScreenWidth(context),
                    View.MeasureSpec.EXACTLY);
            //根据屏幕宽度计算高度
            int i2 = View.MeasureSpec.makeMeasureSpec(i1, View.MeasureSpec.UNSPECIFIED);
            view.measure(i1, i2);
            totalHeight += view.getMeasuredHeight();
        }
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    private List<SubtaskInfo> subtaskListData = new ArrayList<SubtaskInfo>() {
    };
    private List<List<SubtaskItemInfo>> childItemListData = new ArrayList<List<SubtaskItemInfo>>() {
    };

    public void onCradSubTaskAddBtClick(View view) {
        addSubtask(SUBTASK_DEF_NAME + (subtaskListData.size() + 1));
    }

    public void onCradDetailTimeClick(View view) {
        ImageView expiryTimeLeftIv = (ImageView) findViewById(R.id.card_detail_expiry_time_iv);
        ImageView timeSeletedIv = (ImageView) view;
        boolean selected = timeSeletedIv.isSelected();
        timeSeletedIv.setSelected(!selected);
        expiryTimeLeftIv.setSelected(!selected);
    }

    String format = "yyyy-MM-dd HH:mm:ss";
    String formatStr = "yyyy年MM月dd日 HH:mm";

    public void onCradDetailTimeBtClick(View view) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        String currrteDate = formatter.format(new Date());
        //选择时间
        TimeSelector timeSelector = new TimeSelector(context, new TimeSelector
                .ResultHandler() {
            @Override
            public void handle(String time) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINESE);
                sdf.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
                Date date = null;
                try {
                    date = sdf.parse(time);

                    //发送修改的截止日期时间
                    postExpiryTime(date);
                } catch (Exception e) {
                }

            }
        }, currrteDate, "2022-12-31 23:59:59");

        timeSelector.setIsLoop(true);//设置不循环,true循环
        timeSelector.setMode(TimeSelector.MODE.YMDHM);//显示 年月日时分（默认）
        //timeSelector.setMode(TimeSelector.MODE.YMD);//只显示 年月日

        timeSelector.show();


    }

    //修改截止时间
    private void postExpiryTime(final Date date) {
        //传数值
        if (!NetUtil.isNetworkConnected(context)) {
            return;
        }
        final String title = mCardTitleEt.getText().toString();
        final String desc = mCardDescEt.getText().toString();
        final long timeLong = date.getTime();
        String url = Constant.WEB_SITE1 + UrlConstant.url_board + "/" + mBoardId;

        Response.Listener<JsonResult> successListener = new Response
                .Listener<JsonResult>() {
            @Override
            public void onResponse(JsonResult result) {
                if (result.code == 0 && result.data != null && context != null) {
                    //修改成功
                    mExpiryTimeTv.setText(formatterStr.format(date) + " 到期");
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
                        params.put(KeyConstant.listItemId, mListItemId);
                        params.put(KeyConstant.boardName, title);
                        params.put(KeyConstant.boardDesc, desc);
                        params.put(KeyConstant.expiryTime, String.valueOf(timeLong));
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

    //附件上传
    public void onCardDetailFileBtClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style
                .dialog_appcompat_theme);
        builder.setTitle("添加附件");
       /* TextView textView = new TextView(context);
        textView.setText("添加附件");
        textView.setTextSize(16);
        textView.getPaint().setFakeBoldText(true);
        textView.setTextColor(getResources().getColor(R.color.mainColorDrak));
        textView.setPadding(350, 40, 100, 0);
        builder.setCustomTitle(textView);*/
        //    指定下拉列表的显示数据
        final String[] cities = {"选择图片", "选择文件"};
        //    设置一个下拉的列表选择项
        builder.setItems(cities, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                switch (i) {
                    //文件
                    case 0:
                        choisePicture();
                        break;
                    //图片
                    case 1:
                        choiseFile();
                        break;
                }
            }
        });
        builder.show();
    }

    //选择文件
    private void choiseFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 1);
    }

    /**
     * 更新ui
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    //postImg(ConvUtil.NS(msg.obj));
                    break;
                case 0:
                    break;
            }
            //pictures.clear();
        }
    };

    private void postImg(final String file) {
        String url = Constant.WEB_SITE1 + UrlConstant.url_upFiles;

        // RetrofitUtil.upload(url, file);
        Response.Listener<JsonResult> successListener = new Response
                .Listener<JsonResult>() {
            @Override
            public void onResponse(JsonResult result) {
                if (result == null || result.code != 0) {
                    Log.d(TAG, "上传图片7777" + result);
                    ToastUtil.show(context, getString(R.string.requery_failed));
                    return;
                }
                Log.d(TAG, "上传图片7777" + result.msg);
                //添加子任务成功

            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d(TAG, "上传图片88:" + volleyError.getMessage());
                ToastUtil.show(context, getString(R.string.requery_failed));
            }
        };

        Request<JsonResult> versionRequest = new
                GsonRequest<JsonResult>(Request.Method.POST, url,
                        successListener, errorListener, new
                        TypeToken<JsonResult>() {
                        }.getType()) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put(KeyConstant.file, file);
                        params.put(KeyConstant.boardId, mBoardId);
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

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        Uri uri = arg2.getData();
        if (uri != null) {
            Log.d(TAG, "文件路径：" + uri.getPath());
            File file1 = new File(uri.getPath());
            uploadPictureThread(file1);
        }

        if (arg0 == 101) {
            setIntent(arg2);
            getBundleP();

            if (pictures != null && pictures.size() > 0) {
                File file = new File(pictures.get(0).getLocalURL());
                uploadPictureThread(file);
            }
            //setGridView();
        }
        super.onActivityResult(arg0, arg1, arg2);
    }

    private void uploadPictureThread(final File file) {
        final HashMap<String, String> map = new HashMap<String, String>();
        map.put(KeyConstant.boardId, mBoardId);

        final String url = Constant.WEB_SITE1 + UrlConstant.url_upFiles;
        new Thread() {
            @Override
            public void run() {
                try {
                    RetrofitUtil.upLoadByCommonPost(url, file, map, new RetrofitUtil
                            .FileUploadListener() {
                        @Override
                        public void onProgress(long pro, double precent) {
                            Log.d(TAG, "图片上传" + pro);

                        }

                        @Override
                        public void onFinish(int code, String res, Map<String, List<String>>
                                headers) {
                            Log.d(TAG, "图片上传1" + code);
                            Log.d(TAG, "图片上传1" + res);

                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }.start();
        //postImg(fileStr);
    }

    public void getBundleP() {
        if (getIntent() != null) {
            bundle = getIntent().getExtras();
            if (bundle != null) {
                pictures = (List<PictureBean>) bundle.getSerializable("pictures") != null ?
                        (List<PictureBean>) bundle.getSerializable("pictures") : new
                        ArrayList<PictureBean>();
            }
        }
    }

    //读取手机存储
    @PermissionGrant(MulPictureActivity.SDCARD_READ)
    public void requestSdcardReadSuccess() {
        int choose = 1 - pictures.size();
        intent = new Intent(this, MulPictureActivity.class);
        bundle = setBundle();
        bundle.putInt("imageNum", choose);
        bundle.putInt("imageAllNum", 1);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtras(bundle);
        startActivityForResult(intent, 101);
    }

    //读取手机存储
    @PermissionDenied(MulPictureActivity.SDCARD_READ)
    public void requestSdcardReadFailed() {
        ToastUtil.show(this, "用户拒绝读写手机存储!");
    }

    public List<PictureBean> pictures = new ArrayList<PictureBean>();//图片文件

    public void sendHandle(String message, int success) {
        Looper.prepare();
        Message msg = new Message();
        msg.what = success;
        msg.obj = message;
        handler.sendMessage(msg);
        Looper.loop();
    }

    private void choisePicture() {
        int choose = 9 - pictures.size();
        int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            Intent intent = new Intent(this, MulPictureActivity.class);
            bundle = setBundle();
            bundle.putInt("imageNum", choose);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtras(bundle);
            startActivityForResult(intent, 101);
        } else {
            MPermissions.requestPermissions(this, MulPictureActivity.SDCARD_READ, Manifest
                    .permission.READ_EXTERNAL_STORAGE);
        }
    }

    class MyExpandableListAdapter extends BaseExpandableListAdapter {
        private List<SubtaskInfo> mSubtaskData;
        private List<List<SubtaskItemInfo>> childListData;
        private int focusPosition = -1;
        private long lastTime = 0;

        public MyExpandableListAdapter(List<SubtaskInfo> subtaskData) {
            mSubtaskData = subtaskData;
        }

        @Override
        public int getGroupCount() {
            return mSubtaskData.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            int lengthInt = 1;
            if (childListData != null && childListData.size() != 0 && groupPosition <
                    childListData.size()) {
                lengthInt = childListData.get(groupPosition).size();
            }
            return lengthInt;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return mSubtaskData.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            if (childListData != null && childListData.size() != 0 && groupPosition <
                    childListData.size()) {
                return childListData.get(groupPosition).get(childPosition);

            } else {
                return new SubtaskItemInfo("-1", "");
            }
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        public void setData(List<SubtaskInfo> subtaskData, List<List<SubtaskItemInfo>> childList) {
            mSubtaskData = subtaskData;
            childListData = childList;
            notifyDataSetChanged();
        }

        /**
         * 确定一个组的展示视图--groupPosition表示的当前需要展示的组的索引
         */
        @Override
        public View getGroupView(final int groupPosition, final boolean isExpanded,
                                 View convertView, ViewGroup parent) {

            LayoutInflater systemService = (LayoutInflater) getSystemService(Context
                    .LAYOUT_INFLATER_SERVICE);
            View view = systemService.inflate(R.layout.expandable_group_item, null);
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    ((ViewGroup) v)
                            .setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
                    return false;
                }
            });
            final EditText mSubtaskTitleET = (EditText) view.findViewById(R.id.group_text);
            ImageView groupItemSubtaskJt = (ImageView) view.findViewById(R.id
                    .group_item_subtask_jt);
            ImageButton mMenuBt = (ImageButton) view.findViewById(R.id
                    .group_item_subtask_menu_bt);
            final SubtaskInfo subtaskInfo = mSubtaskData.get(groupPosition);
            if (subtaskInfo == null) {
                return null;
            }
            String text = subtaskInfo.subtaskName;
            mSubtaskTitleET.setText(text == null ? "" : text);

            //判断isExpanded就可以控制是按下还是关闭，同时更换图片
            groupItemSubtaskJt.setImageResource(isExpanded ?
                    R.drawable.ic_incard_subtask_up : R.drawable.ic_incard_subtask_down);

            mSubtaskTitleET.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    ((ViewGroup) v.getParent()).setDescendantFocusability
                            (ViewGroup.FOCUS_AFTER_DESCENDANTS);

                    return false;
                }
            });
            mSubtaskTitleET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        String newTitle = mSubtaskTitleET.getText().toString();
                        if (TextUtil.isEmpty(newTitle)) {
                            //mSubtaskTitleET.setText(oldTitle);
                            return;
                        }
                        //修改标题
                        if (focusPosition == groupPosition) {
                            changeSubtaskTitle(groupPosition, subtaskInfo, newTitle);
                        }
                    } else {
                        focusPosition = groupPosition;
                        oldTitle = mSubtaskTitleET.getText().toString();
                        mSubtaskTitleET.setSelection(oldTitle.length());
                    }
                }
            });
            mMenuBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeInputMethod();
                    mSubtaskTitleET.clearFocus();
                    showPopWindow(v, groupPosition);
                }
            });

            return view;
        }

        /**
         * 确定一个组的一个子的展示视图-
         * -groupPostion表示当前组的索引,childPosition表示的是需要展示的子的索引
         */
        @Override
        public View getChildView(final int groupPosition, final int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(Context
                    .LAYOUT_INFLATER_SERVICE);
            final String subtaskId = mSubtaskData.get(groupPosition).subtaskId;
            convertView = mLayoutInflater.inflate(R.layout.expandable_childe_item, null);

           /* if (childListData == null || childListData.size() == 0 || groupPosition >=
                    childListData.size()) {

            }*/
            final List<SubtaskItemInfo> childDatum = childListData.get(groupPosition);

            final EditText childTv = (EditText) convertView.findViewById(R.id.child_text);
            final ImageView childImageView = (ImageView) convertView.findViewById(R.id
                    .child_imageview);
            final EditText childAddEt = (EditText) convertView.findViewById(R.id.child_add_et);

            if (isLastChild) {
                childAddEt.setVisibility(View.VISIBLE);
                childImageView.setVisibility(View.INVISIBLE);
                childAddEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus) {
                            String subtskItemTitle = childAddEt.getText().toString();
                            if (TextUtil.isEmpty(subtskItemTitle)) {
                                return;
                            }
                            if (System.currentTimeMillis() - lastTime < 1000) {
                                return;
                            }
                            lastTime = System.currentTimeMillis();
                            addSubtaskItemThraed(subtskItemTitle.trim(), subtaskId, childDatum,
                                    groupPosition);
                        }
                    }
                });
            } else {
                childAddEt.setVisibility(View.GONE);
                if (childDatum != null && childDatum.size() != 0 && childPosition < childDatum
                        .size()) {
                    childImageView.setVisibility(View.VISIBLE);
                    String termDesc = childDatum.get(childPosition).termDesc;
                    childTv.setText(termDesc == null ? "" : termDesc);
                }
                childImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean selected = childImageView.isSelected();
                        if (selected) {//删除ing
                            //childTv.getPaint().setFlags(0 | Paint.ANTI_ALIAS_FLAG);
                            //childTv.setTextColor(getResources().getColor(R.color.color_333333));
                            ToastUtil.show(context, "该项已被删除");
                        } else {//不是删除
                            if (childDatum == null || childDatum.size() == 0 || childPosition >=
                                    childDatum.size()) {
                                return;
                            }
                            SubtaskItemInfo subtaskItemInfo = childDatum.get(childPosition);
                            if (subtaskItemInfo != null) {
                                deleteSubtaskTerm(childImageView, childTv, subtaskId,
                                        subtaskItemInfo.termId, childDatum, groupPosition,
                                        childPosition);
                            }
                        }
                    }
                });

                childTv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus) {
                            String newTitle = childTv.getText().toString();
                            if (TextUtil.isEmpty(newTitle)) {
                                childTv.setText(oldTermTtileStr);
                                return;
                            }
                            SubtaskItemInfo subtaskItemInfo = childDatum.get(childPosition);
                            if (subtaskItemInfo != null) {
                                changeTermThraed(childTv, subtaskId, subtaskItemInfo.termId,
                                        newTitle, groupPosition, childPosition, childDatum);
                            }
                        } else {
                            oldTermTtileStr = childTv.getText().toString();
                        }
                    }
                });
            }
            return convertView;
        }

        private void changeTermThraed(final EditText childTv, final String subtaskId, String
                termId, final String newTitle, final int groupPosition, final int childPosition,
                                      final List<SubtaskItemInfo> subtaskItemInfo) {
            if (!NetUtil.isNetworkConnected(context)) {
                ToastUtil.show(context, getString(R.string.no_network));
                return;
            }
            String url = Constant.WEB_SITE1 + UrlConstant.url_term + "/" + termId;
            Response.Listener<JsonResult<SubtaskItemInfo>> successListener = new Response
                    .Listener<JsonResult<SubtaskItemInfo>>() {
                @Override
                public void onResponse(JsonResult<SubtaskItemInfo> result) {
                    if (result == null || result.code != 0) {
                        ToastUtil.show(context, context.getString(R.string.requery_failed));
                        if (context != null) {
                            childTv.setText(oldTermTtileStr);
                        }
                        return;
                    }
                    List<SubtaskItemInfo> newSubtaskItemInfo = subtaskItemInfo;
                    newSubtaskItemInfo.set(childPosition, result.data);
                    childListData.set(groupPosition, newSubtaskItemInfo);
                }
            };

            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    volleyError.printStackTrace();
                    if (context != null) {
                        childTv.setText(oldTermTtileStr);
                    }
                    ToastUtil.show(context, context.getString(R.string.requery_failed));
                }
            };

            Request<JsonResult<SubtaskItemInfo>> versionRequest = new
                    GsonRequest<JsonResult<SubtaskItemInfo>>(Request.Method.PUT, url,
                            successListener, errorListener, new
                            TypeToken<JsonResult<SubtaskItemInfo>>() {
                            }.getType()) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put(KeyConstant.subtaskId, subtaskId);
                            params.put(KeyConstant.termDesc, newTitle);
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

        String oldTermTtileStr = "";

        private void deleteSubtaskTerm(final ImageView childImageView, final TextView childTv,
                                       String subtaskId, String termId, final List<SubtaskItemInfo>
                                               childDatum, final int groupPosition, final int
                                               childPosition) {
            if (!NetUtil.isNetworkConnected(context)) {
                ToastUtil.show(context, getString(R.string.no_network));
                return;
            }
            String url = Constant.WEB_SITE1 + UrlConstant.url_term + "/" + subtaskId + "/" +
                    termId;

            Response.Listener<JsonResult> successListener = new Response
                    .Listener<JsonResult>() {
                @Override
                public void onResponse(JsonResult result) {
                    if (result == null) {
                        ToastUtil.show(context, getString(R.string.server_exception));
                        return;
                    }
                    if (result.code == 0 && context != null) {
                        childImageView.setSelected(true);
                        childTv.setTextColor(getResources().getColor(R.color.cccccc));
                        childTv.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint
                                .ANTI_ALIAS_FLAG);
                        List<SubtaskItemInfo> newChildDatum = childDatum;
                        newChildDatum.remove(childPosition);
                        childListData.set(groupPosition, newChildDatum);

                    } else {
                        ToastUtil.show(context, getString(R.string.delete_faild) + "," + result
                                .msg);
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

        //添加项
        private void addSubtaskItemThraed(final String subtskItemTitle, final String subtaskId,
                                          final List<SubtaskItemInfo> childDatum, final int
                                                  groupPosition) {
            String url = Constant.WEB_SITE1 + UrlConstant.url_term;
            Response.Listener<JsonResult<SubtaskItemInfo>> successListener = new Response
                    .Listener<JsonResult<SubtaskItemInfo>>() {
                @Override
                public void onResponse(JsonResult<SubtaskItemInfo> result) {
                    if (result == null || result.code != 0) {
                        ToastUtil.show(context, getString(R.string.requery_failed));
                        return;
                    }
                    //添加子任务成功
                    SubtaskItemInfo data = result.data;
                    if (context != null && data != null) {
                        //把返回的集合添加到子任务集合里面去
                        List<SubtaskItemInfo> itemInfos1 = childDatum;
                        itemInfos1.set(itemInfos1.size() - 1, data);
                        itemInfos1.add(new SubtaskItemInfo("-1", ""));
                        Log.d(TAG, itemInfos1.size() + "返回数据" + groupPosition);
                        childListData.set(groupPosition, itemInfos1);
                        notifyDataSetChanged();
                        reSetLVHeight(subtaskLV);
                        //getSubTaskList();
                    }

                }
            };

            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    volleyError.printStackTrace();
                    ToastUtil.show(context, getString(R.string.requery_failed));
                }
            };

            Request<JsonResult<SubtaskItemInfo>> versionRequest = new
                    GsonRequest<JsonResult<SubtaskItemInfo>>(Request.Method.POST, url,
                            successListener, errorListener, new
                            TypeToken<JsonResult<SubtaskItemInfo>>() {
                            }.getType()) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put(KeyConstant.subtaskId, subtaskId);
                            params.put(KeyConstant.termDesc, subtskItemTitle);
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

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }

        public void setGroupData(List<SubtaskInfo> subtaskListData) {
            mSubtaskData = subtaskListData;
            //notifyDataSetChanged(); 不能加这个,不然标题无法编辑
        }
    }

    private void showPopWindow(View v, final int groupPosition) {
        View inflate = LayoutInflater.from(context).inflate(R.layout
                .layout_card_detail_subtask_menu, null);

        final PopupWindow popWindow = new PopupWindow(inflate, LinearLayout.LayoutParams
                .WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        inflate.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int[] location = new int[2];
        popWindow.setFocusable(true);
        popWindow.setOutsideTouchable(false);
        v.getLocationOnScreen(location);
        popWindow.setBackgroundDrawable(new BitmapDrawable());
        popWindow.setSoftInputMode(WindowManager.LayoutParams
                .SOFT_INPUT_ADJUST_RESIZE);

        popWindow.showAsDropDown(v);
        View.OnClickListener itemMenuPopupClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindow.dismiss();
                switch (view.getId()) {
                    case R.id.card_detail_subtalk_menu_delete_bt:
                        deleteSubtask(groupPosition);
                        break;
                }
            }
        };
        inflate.findViewById(R.id.card_detail_subtalk_menu_delete_bt).setOnClickListener
                (itemMenuPopupClickListener);

    }

    //删除子任务
    private void deleteSubtask(final int groupPosition) {
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, getString(R.string.no_network));
            return;
        }
        String url = Constant.WEB_SITE1 + UrlConstant.url_subtask + "/" + mBoardId + "/" +
                subtaskListData.get(groupPosition).subtaskId;

        Response.Listener<JsonResult> successListener = new Response
                .Listener<JsonResult>() {
            @Override
            public void onResponse(JsonResult result) {
                if (result == null) {
                    ToastUtil.show(context, getString(R.string.server_exception));
                    return;
                }
                if (result.code == 0) {
                    subtaskListData.remove(groupPosition);
                    childItemListData.remove(groupPosition);
                    mSubtaskLvAdapter.setData(subtaskListData, childItemListData);
                    reSetLVHeight(subtaskLV);
                } else {
                    ToastUtil.show(context, getString(R.string.delete_faild) + result.msg);
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

    private void changeSubtaskTitle(final int groupPosition, final SubtaskInfo subtaskInfo, final
    String newTitle) {
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, getString(R.string.no_network));
            return;
        }
        String url = Constant.WEB_SITE1 + UrlConstant.url_subtask + "/" + subtaskInfo.subtaskId;

        Response.Listener<JsonResult> successListener = new Response
                .Listener<JsonResult>() {
            @Override
            public void onResponse(JsonResult result) {
                if (result.code == 0 && result.data != null && context != null) {
                    //mSubtaskTitleET.setText(newTitle);
                    //getSubTaskList();
                    SubtaskInfo subtaskInfoNew = subtaskInfo;
                    subtaskInfoNew.subtaskName = newTitle;
                    subtaskListData.set(groupPosition, subtaskInfoNew);
                    mSubtaskLvAdapter.setGroupData(subtaskListData);
                    //reSetLVHeight(subtaskLV);

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
                        params.put(KeyConstant.subtaskName, newTitle);
                        params.put(KeyConstant.boardId, mBoardId);
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

    private void closeInputMethod() {
        View currentFocus = context.getCurrentFocus();
        if (currentFocus != null) {
            ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(currentFocus.getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    //------------------------------------------------------------------------------------------
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
        //获取关联标签
        getRelationData();
        //获取成员列表
        getMemberInfo();
        //获取活动数据
        getEventThread();
        //获取附件数据
        getFileListData();

    }

    private void initCancelOkVisibility(boolean b) {
        mTopEditSaveBt.setVisibility(b ? View.VISIBLE : View.GONE);
        mCancelBT.setVisibility(b ? View.VISIBLE : View.GONE);
    }

    private boolean isCancel = false;
    //取消
    private View.OnClickListener onBtClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            switch (id) {
                case R.id.edit_right_save_bt:
                    isCancel = false;
                    closeInputMethodClearFocus();
                    break;
                case R.id.top_left_delete_bt:
                    isCancel = true;
                    closeInputMethodClearFocus();
                    break;
                case R.id.top_left_finish_bt:
                    finish();
                    break;
                case R.id.card_detail_file_bt:
                    onCardDetailFileBtClick();
                    break;

            }
        }
    };

    private void postTitleChange() {
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
                        //params.put(KeyConstant.expiryTime, "");
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
                Log.d("", "获取成员列表:" + memberInfoList.size());
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
                        Log.d(TAG, "网络连接错误！");
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

    private void closeInputMethodClearFocus() {
        closeInputMethod();
        mCardDescEt.clearFocus();
        mCardTitleEt.clearFocus();
        mCardTalkEt.clearFocus();
    }

    public void onCradDetailTagAddBtClick(View view) {
        Intent intent = new Intent(context, TagListActivity.class);
        List<TagInfo> tagInfo = new ArrayList<>();
        if (mCardBean != null) {
            tagInfo = mCardBean.projectLabelVOList;
        }
        intent.putExtra(KeyConstant.tagInfo, (Serializable) tagInfo);
        Bundle bundle = new Bundle();
        bundle.putString(KeyConstant.projectId, mProjectId);
        bundle.putString(KeyConstant.boardId, mBoardId);
        intent.putExtras(bundle);
        startActivity(intent);

    }
}
