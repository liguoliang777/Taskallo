package com.android.taskallo.activity.classify;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.taskallo.App;
import com.android.taskallo.R;
import com.android.taskallo.adapter.classify.Fragment0_0_Adapter;
import com.android.taskallo.adapter.classify.Fragment0_1_Adapter;
import com.android.taskallo.base.fragment.BaseSearchFragment;
import com.android.taskallo.bean.JsonResult;
import com.android.taskallo.bean.ProjItemInfo;
import com.android.taskallo.core.net.GsonRequest;
import com.android.taskallo.core.utils.Constant;
import com.android.taskallo.core.utils.KeyConstant;
import com.android.taskallo.core.utils.NetUtil;
import com.android.taskallo.core.utils.UrlConstant;
import com.android.taskallo.util.ToastUtil;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.jzt.hol.android.jkda.sdk.bean.main.DiscoverListBean;
import com.jzt.hol.android.jkda.sdk.bean.main.DiscoverTopBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分类
 * Created by gp on 2017/3/14 0014.
 */
@SuppressLint("WrongConstant")
public class Fragment0 extends BaseSearchFragment {
    private FragmentActivity context;
    private RecyclerView main1Rv;
    private Fragment0_1_Adapter main1_Adapter;
    private List<DiscoverTopBean> mEverydayList = new ArrayList();
    private Fragment0_1_Adapter mTopicsAdapter;
    private RecyclerView mEverydayRv, mDeletedRv, mFinishedRv;
    private RecyclerView mHotRecentRv;
    private Fragment0_0_Adapter mEverydayAdapter;
    private List<DiscoverTopBean> mHotRecentList = new ArrayList();
    private Fragment0_0_Adapter mHotRecentAdapter;
    private List<ProjItemInfo> main1_List = new ArrayList<>();
    private DiscoverListBean.DataBean.DailyNewGamesListBean dailyNewGames;
    private DiscoverListBean.DataBean.WeeklyNewGamesListBean hotGames;
    private LinearLayoutManager linearLayoutManager;
    private TextView mDeletedOpenClosedBt, mFinishedOpenClosedBt;

    public Fragment0() {
        android.util.Log.d(TAG, "DiscoverFragment: ()");
    }

    public static Fragment0 newInstance(String arg) {
        Fragment0 fragment = new Fragment0();
        Bundle bundle = new Bundle();
        bundle.putString("", arg);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_0;
    }

    @Override
    protected void initViewsAndEvents(View view) {
        context = getActivity();
        init1(view);
        init0(view);
        init_2(view);
        init_3(view);

        //getData();
        Log.d(TAG, "请求主界面数据=======initViewsAndEvents: ");
    }

    private void getData() {
        getTypeData(0);
        getTypeData(1);
        getTypeData(2);
        getTypeData(3);
    }

    @Override
    public void onStart() {
        super.onStart();
        getData();
    }

    private void init0(View headView) {
        //如果Listview或者RecycleView显示不全，只有一个itme，请在ScrollView中添加  android:fillViewport="true"
        mEverydayRv = headView.findViewById(R.id.everyday_discover_recyclerview);
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(context, 2);
        mGridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mEverydayRv.setLayoutManager(mGridLayoutManager);
        mEverydayList.add(new DiscoverTopBean());
        mEverydayList.add(new DiscoverTopBean());
        mEverydayList.add(new DiscoverTopBean());
        mEverydayAdapter = new Fragment0_0_Adapter(context, mEverydayList);
        mEverydayRv.setHasFixedSize(true);
        mEverydayRv.setNestedScrollingEnabled(false);
        mEverydayRv.setAdapter(mEverydayAdapter);
    /*    mEverydayRv.addItemDecoration(new RecyclerViewDivider(context,
                R.dimen.main_margin_left_px, R.dimen.main_margin_20px, mEverydayList.size()));*/
    }

    private void init1(View headView) {
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(context, 1);
        mGridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        main1Rv = headView.findViewById(R.id.discover_head_rv_classify);//条目
        main1Rv.setLayoutManager(mGridLayoutManager);
        main1_Adapter = new Fragment0_1_Adapter(context, main1_List);
        main1Rv.setHasFixedSize(true);
        main1Rv.setNestedScrollingEnabled(false);
        main1Rv.setAdapter(main1_Adapter);
    }

    //收起.展开
    private void init_2(View headView) {
        mFinishedRv = headView.findViewById(R.id.rv_subject);
        mFinishedOpenClosedBt = (TextView) headView.findViewById(R.id
                .fragment_0_finished_open_close_bt);
        mFinishedOpenClosedBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mFinishedRv.getVisibility() == View.VISIBLE) {
                    mFinishedRv.setVisibility(View.GONE);
                    mFinishedOpenClosedBt.setText(R.string.fragment_opened);
                } else {
                    mFinishedRv.setVisibility(View.VISIBLE);
                    mFinishedOpenClosedBt.setText(R.string.fragment_closed);
                }
            }
        });
        linearLayoutManager = new LinearLayoutManager(
                context, LinearLayoutManager.VERTICAL, false);
        mFinishedRv.setLayoutManager(linearLayoutManager);

        mTopicsAdapter = new Fragment0_1_Adapter(context, main1_List);
        mFinishedRv.setHasFixedSize(true);
        mFinishedRv.setNestedScrollingEnabled(false);
        mFinishedRv.setAdapter(mTopicsAdapter);

    }

    //回收站
    private void init_3(View headView) {
        mDeletedRv = headView.findViewById(R.id.rv_deleted_board);
        mDeletedOpenClosedBt = (TextView) headView.findViewById(R.id
                .fragment_0_deleted_open_close_bt);
        mDeletedOpenClosedBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDeletedRv.getVisibility() == View.VISIBLE) {
                    mDeletedRv.setVisibility(View.GONE);
                    mDeletedOpenClosedBt.setText(R.string.fragment_opened);
                } else {
                    mDeletedRv.setVisibility(View.VISIBLE);
                    mDeletedOpenClosedBt.setText(R.string.fragment_closed);
                }
            }
        });
        linearLayoutManager = new LinearLayoutManager(
                context, LinearLayoutManager.VERTICAL, false);
        mDeletedRv.setLayoutManager(linearLayoutManager);

        mTopicsAdapter = new Fragment0_1_Adapter(context, main1_List);
        mDeletedRv.setHasFixedSize(true);
        mDeletedRv.setNestedScrollingEnabled(false);
        mDeletedRv.setAdapter(mTopicsAdapter);
    }


    private String TAG = Fragment0.class.getSimpleName();
    private String PAGER = "/1/100";

    //请求数据
    private void getTypeData(final int type) {
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, "网络异常,请检查网络设置");
            return;
        }
        // 0 默认状态，1 已删除，2  收藏，3 已完成',
        String url = Constant.WEB_SITE1 + UrlConstant.URL_PROJECT_HOME + "/" + type + PAGER;

        Response.Listener<JsonResult<List<ProjItemInfo>>> successListener = new Response
                .Listener<JsonResult<List<ProjItemInfo>>>() {
            @Override
            public void onResponse(JsonResult<List<ProjItemInfo>> result) {
                if (result == null) {
                    ToastUtil.show(context, getString(R.string.server_exception));
                    return;
                }
                Log.d(TAG, result.msg + ",请求主界面数据:" + result.data);
                if (result.code == 0 && context != null && result.data != null) {
                    setData(result.data, type);
                }
            }
        };

        Request<JsonResult<List<ProjItemInfo>>> versionRequest = new
                GsonRequest<JsonResult<List<ProjItemInfo>>>(
                        Request.Method.GET, url,
                        successListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                        Log.d(TAG, "网络连接错误！" + volleyError.getMessage());
                    }
                }, new TypeToken<JsonResult<List<ProjItemInfo>>>() {
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

    // 设置数据
    public void setData(List<ProjItemInfo> dataList, int type) {
        Log.d(TAG, "请求主界面数据 type==" + type);
        switch (type) {
            case 0:
                main1_List = dataList;
                main1_Adapter.setList(main1_List);
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onFirstUserVisible() {

    }

    @Override
    protected void onUserVisible() {

    }

    @Override
    protected void onUserInvisible() {

    }

    @Override
    protected View getLoadView(View view) {
        return null;
    }

}
