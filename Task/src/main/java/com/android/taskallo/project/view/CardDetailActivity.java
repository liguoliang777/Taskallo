package com.android.taskallo.project.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
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
import com.android.taskallo.core.utils.ImageUtil;
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
import java.util.ArrayList;
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
    private ExpandableListView subtaskLV;
    private boolean isTopClick = false;
    private String SUBTASK_DEF_NAME = "子任务";
    private MyExpandableListAdapter mSubtaskLvAdapter;
    private String oldTitle = "";

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
        mSubtaskLvAdapter = new MyExpandableListAdapter(subtaskData);
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


    }

    //添加标签
    private void addSubtask(final String SUBTASK_DEF_NAME) {
        String url = Constant.WEB_SITE1 + UrlConstant.url_subtask;
        Response.Listener<JsonResult> successListener = new Response
                .Listener<JsonResult>() {
            @Override
            public void onResponse(JsonResult result) {
                if (result == null || result.code != 0) {
                    ToastUtil.show(context, getString(R.string.requery_failed));
                    return;
                }
                //添加子任务成功
                if (context != null) {
                    //把返回的集合添加到子任务集合里面去
                    subtaskData.add("子任务");
                    if (subtaskData != null) {
                        mSubtaskLvAdapter.setData(subtaskData);
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

        Request<JsonResult> versionRequest = new
                GsonRequest<JsonResult>(Request.Method.POST, url,
                        successListener, errorListener, new TypeToken<JsonResult>() {
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

    private List<String> subtaskData = new ArrayList<String>() {
    };
    private String childData[][] = {{"小小", "小明", "饭饭", "流浪"}, {"李老师", "张老师", "吴老师", "肖老师",
            "柳老师"}, {"雯雯", "哔哔", "嘻嘻"}};

    public void onCradSubTaskAddBtClick(View view) {
        addSubtask(SUBTASK_DEF_NAME);
    }

    class MyExpandableListAdapter extends BaseExpandableListAdapter {
        /**
         * 得到组的数量
         */
        private List<String> mSubtaskData = new ArrayList<>();

        public MyExpandableListAdapter(List<String> subtaskData) {
            mSubtaskData = subtaskData;
        }

        @Override
        public int getGroupCount() {
            return mSubtaskData.size();
        }

        /**
         * 得到每个组的元素的数量
         */
        @Override
        public int getChildrenCount(int groupPosition) {
            int lengthInt = 0;
            if (groupPosition < childData.length) {
                lengthInt = childData[groupPosition].length;
            }
            return lengthInt;
        }

        /**
         * 获得组的对象
         */
        @Override
        public Object getGroup(int groupPosition) {
            return mSubtaskData.get(groupPosition);
        }

        /**
         * 获得子对象
         */
        @Override
        public Object getChild(int groupPosition, int childPosition) {
            if (groupPosition < childData.length) {
                return childData[groupPosition][childPosition];

            } else {
                return null;
            }
        }

        /**
         * 得到组id
         */
        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        /**
         * 得到子id
         */
        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        /**
         * 表示数据是否稳定,对监听事件有影响
         */
        @Override
        public boolean hasStableIds() {
            return false;
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
            String text = mSubtaskData.get(groupPosition);
            mSubtaskTitleET.setText(text == null ? "" : text);

            //判断isExpanded就可以控制是按下还是关闭，同时更换图片
            if (isExpanded) {
                groupItemSubtaskJt.setImageResource(R.drawable.ic_incard_subtask_up);
            } else {
                groupItemSubtaskJt.setImageResource(R.drawable.ic_incard_subtask_down);
            }

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
                            mSubtaskTitleET.setText(oldTitle);
                        }
                        //todo 提交标题
                    } else {
                        oldTitle = mSubtaskTitleET.getText().toString();
                    }
                }
            });
            mMenuBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeInputMethod();
                    mSubtaskTitleET.clearFocus();
                    ToastUtil.show(context, "点击菜单" + groupPosition);
                }
            });

            return view;
        }

        /**
         * 确定一个组的一个子的展示视图-
         * -groupPostion表示当前组的索引,childPosition表示的是需要展示的子的索引
         */
        @Override
        public View getChildView(int groupPosition, int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            LayoutInflater systemService = (LayoutInflater) getSystemService(Context
                    .LAYOUT_INFLATER_SERVICE);
            convertView = systemService.inflate(R.layout.expandable_childe_item, null);
            TextView child_text = (TextView) convertView.findViewById(R.id.child_text);
            if (groupPosition < childData.length) {
                String[] childDatum = childData[groupPosition];
                child_text.setText(childDatum[childPosition]);
            }
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }

        public void setData(List<String> subtaskData) {
            mSubtaskData = subtaskData;
            Log.d(TAG, "添加数据::" + mSubtaskData.size());
            notifyDataSetChanged();
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
                    closeInputMethodClearFocus();
                    break;
                case R.id.top_left_delete_bt:
                    isCancel = true;
                    closeInputMethodClearFocus();
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

    private void closeInputMethodClearFocus() {
        closeInputMethod();
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
