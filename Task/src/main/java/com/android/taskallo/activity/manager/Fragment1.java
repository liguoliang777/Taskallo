package com.android.taskallo.activity.manager;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListPopupWindow;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.taskallo.App;
import com.android.taskallo.R;
import com.android.taskallo.activity.main.MainHomeActivity;
import com.android.taskallo.adapter.Fragment1Adapter;
import com.android.taskallo.base.fragment.BaseSearchFragment;
import com.android.taskallo.bean.JsonResult;
import com.android.taskallo.bean.NecessaryItemData;
import com.android.taskallo.bean.NecessaryListInfo;
import com.android.taskallo.bean.PageAction;
import com.android.taskallo.core.net.GsonRequest;
import com.android.taskallo.core.utils.Constant;
import com.android.taskallo.core.utils.KeyConstant;
import com.android.taskallo.core.utils.UrlConstant;
import com.android.taskallo.view.QuickAction;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.jzt.hol.android.jkda.sdk.bean.game.GameRankListBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;


/**
 * 必备
 * Created by gp on 2017/3/3 0003.
 */
@SuppressLint({"ValidFragment", "WrongConstant"})
public class Fragment1 extends BaseSearchFragment {

    private PageAction pageAction;
    public int PAGE_SIZE = 10;
    protected QuickAction mItemClickQuickAction;
    private GameRankListBean gameInfoBean;
    private MainHomeActivity content;
    private NecessaryListInfo.AuxiliaryToolsBean mToolInfo;
    private RelativeLayout mRadioGroup;
    private TextView mTopBt1, mTopBt2;
    private ListPopupWindow listPopupWindow;

    public Fragment1(MainHomeActivity activity) {
        content = activity;
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_1;
    }

    private List<TimerTask> timerTasks = new ArrayList<>();
    public StickyListHeadersListView mStickyLV;

    private Fragment1Adapter mNecessaryAdapter;

    private List<NecessaryItemData> mNecessaryList = new ArrayList<>();

    @Override
    protected void initViewsAndEvents(View view) {
        mStickyLV = (StickyListHeadersListView) view.findViewById(R.id.sticky_list_view);
        mRadioGroup = (RelativeLayout) view.findViewById(R.id.fragment_1_top_radio_gp);
        mTopBt1 = (TextView) view.findViewById(R.id.fragment_1_top_radio_rb_1);
        mTopBt2 = (TextView) view.findViewById(R.id.fragment_1_top_radio_rb_2);
        pageAction = new PageAction();
        pageAction.setCurrentPage(0);
        pageAction.setPageSize(PAGE_SIZE);

        mStickyLV.setOnItemClickListener(new OnItemClick());
        mStickyLV.setOnItemLongClickListener(new OnPlanItemLongClick());
        mStickyLV.setDividerHeight(0);
        mNecessaryAdapter = new Fragment1Adapter(getActivity(), getSupportFragmentManager
                (), timerTasks);
        mStickyLV.setAdapter(mNecessaryAdapter);
        getData();
        initGP();
    }

    private void getData() {
        String url = "http://opapi.xflqv.cn" + UrlConstant.URL_QUERY_NECESSARY;
        Response.Listener<JsonResult<List<NecessaryListInfo>>> successListener = new Response
                .Listener<JsonResult<List<NecessaryListInfo>>>() {
            @Override
            public void onResponse(JsonResult<List<NecessaryListInfo>> result) {

                if (result == null || result.code != 0) {
                    //"服务端异常"
                    return;
                }

                List<NecessaryListInfo> necessaryListInfoList = result.data;
                if (necessaryListInfoList != null && necessaryListInfoList.size() > 0) {
                    setData(necessaryListInfoList);
                } else {
                    Log.d(TAG, "HTTP请求成功：服务端返回错误！");
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
            }
        };

        Request<JsonResult<List<NecessaryListInfo>>> request = new
                GsonRequest<JsonResult<List<NecessaryListInfo>>>(Request
                        .Method.POST, url,
                        successListener, errorListener, new
                        TypeToken<JsonResult<List<NecessaryListInfo>>>() {
                        }.getType()) {

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {

                        Map<String, String> params = new HashMap<>();
                        params.put(KeyConstant.APP_TYPE_ID, Constant.APP_TYPE_ID_0_ANDROID);
                        params.put(KeyConstant.parentId, Constant.APP_TYPE_ID_0_ANDROID);
                        return params;
                    }
                };
        App.requestQueue.add(request);
    }


    //点击消息
    private class OnItemClick implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            long itemPosition = mNecessaryList.get(position).getToolId();
            String getItemTitle = mNecessaryList.get(position).getToolName();
            Log.d(TAG, getItemTitle + ",点击" + itemPosition);
        }
    }

    private class OnPlanItemLongClick implements AdapterView.OnItemLongClickListener {

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            //长按删除一行
           /* NecessaryItemData oLangyaSimple = mNecessaryList.get(position);
            mNecessaryList.remove(oLangyaSimple);
            if (mNecessaryAdapter != null && mNecessaryList != null) {
                mNecessaryAdapter.setDate(mNecessaryList);
                mNecessaryAdapter.notifyDataSetChanged();
            }*/
            return true;
        }
    }

    //设置数据
    private void setData(List<NecessaryListInfo> necessaryListInfoList) {
        mNecessaryList.clear();
        mNecessaryAdapter.setDate(mNecessaryList);
        for (int i = 0; i < necessaryListInfoList.size(); i++) {
            NecessaryListInfo necessaryListInfo = necessaryListInfoList.get(i);
            int id = necessaryListInfo.getId();
            String toolName = necessaryListInfo.getToolName();
            List<NecessaryListInfo.AuxiliaryToolsBean> singeToolList = necessaryListInfo
                    .getAuxiliaryTools();
            Log.d(TAG, "数据: " + singeToolList.size());
            if (singeToolList != null) {
                for (int j = 0; j < singeToolList.size(); j++) {
                    mToolInfo = singeToolList.get(j);
                    mNecessaryList.add(new NecessaryItemData(id + "", toolName, mToolInfo.getId()
                            , mToolInfo.getToolName()
                            , mToolInfo.getInstallDesc(), Formatter.formatFileSize(content,
                            mToolInfo.getFileSize()),
                            mToolInfo.getToolLogo(), mToolInfo.getToolURL(), mToolInfo.getMd5(),
                            mToolInfo.getFileName(),
                            mToolInfo
                                    .getPackages(), mToolInfo.getToolVersion()));
                }
            }
        }
        mNecessaryAdapter.setDate(mNecessaryList);
    }

    /**
     * 初始化数据时，数据必须proj_id必须按分组排列，
     * 即，不要将proj_id不同的数据      参差着放在集合中，
     * 否则容易造成列表显示多组相同组名的数据。
     */
/*    private void initDatas() {
        mNecessaryList.add(new NecessaryItemData("1", "谷歌", "4", "谷歌安装器", "未知", getString(R.string
                .necessary_content_desc)));
        mNecessaryList.add(new NecessaryItemData("1", "谷歌", "5", "谷歌安装器", "未知", getString(R.string
                .necessary_content_desc)));
        mNecessaryList.add(new NecessaryItemData("2", "腾讯", "54", "腾讯助手", "腾讯助手腾讯助手腾讯助手腾讯助手",
        getString(R.string
                .necessary_content_desc)));
        mNecessaryList.add(new NecessaryItemData("2", "腾讯", "10", "腾讯助手", "腾讯助手腾讯助手腾讯助手腾讯助手",
        getString(R.string
                .necessary_content_desc)));


        mNecessaryList.add(new NecessaryItemData("5", "百度", "11", "百度助手", "百度助手百度助手百度助手百度助手百度助手",
         getString(R.string
                .necessary_content_desc)));
        mNecessaryList.add(new NecessaryItemData("5", "百度", "16", "百度助手", "百度助手百度助手百度助手百度助手百度助手",
         getString(R.string
                .necessary_content_desc)));

    }*/
    String[] products = {"全部通知", "未读通知", "@ 我的通知", "标记全部已读",
            "清空已读通知"};

    private void initGP() {
        listPopupWindow = new ListPopupWindow(content);
        listPopupWindow.setHeight(ListPopupWindow.WRAP_CONTENT);
        //listPopupWindow.setAnimationStyle(R.style.Animations_PopDownMenu);
        listPopupWindow.setAdapter(new ArrayAdapter(content,
                R.layout.fragment_1_popup_window, R.id.fragment1_item_tv, products));
        listPopupWindow.setAnchorView(mRadioGroup);
        listPopupWindow.setModal(true);
        listPopupWindow.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color
                .white)));//设置背景色
        listPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mTopBt1.setSelected(false);
            }
        });
        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listPopupWindow.dismiss();
                Log.d(TAG, "item选中:" + position);
            }
        });
        // 设置Action
        mTopBt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setSelected(true);
                if (listPopupWindow.isShowing()) {
                    listPopupWindow.dismiss();
                } else {
                    listPopupWindow.show();
                }
            }
        });
        mTopBt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setSelected(!view.isSelected());
            }
        });
    }


    @Override
    public void onStop() {
        super.onStop();
      /*  if (null != mNecessaryAdapter) {
            for (TimerTask timerTask : timerTasks) {
                timerTask.cancel();
            }
            timerTasks.clear();
        }*/
    }

    @Override
    protected void onFirstUserVisible() {
        //getLikeList();
    }

    protected final static String TAG = Fragment1.class.getSimpleName();

    @Override
    protected void onUserVisible() {
        //getLikeList();
    }


    @Override
    protected void onUserInvisible() {
    }

    @Override
    protected View getLoadView(View view) {
        return null;
    }
}