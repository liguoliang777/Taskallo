package com.android.taskallo.activity.classify;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.android.taskallo.R;
import com.android.taskallo.adapter.classify.Fragment0_0_Adapter;
import com.android.taskallo.adapter.classify.Fragment0_1_Adapter;
import com.android.taskallo.base.fragment.BaseSearchFragment;
import com.android.taskallo.bean.ClassifyTopBean;
import com.android.taskallo.bean.PageAction;
import com.jzt.hol.android.jkda.sdk.bean.main.DiscoverListBean;
import com.jzt.hol.android.jkda.sdk.bean.main.DiscoverTopBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 分类
 * Created by gp on 2017/3/14 0014.
 */
@SuppressLint("WrongConstant")
public class Fragment0 extends BaseSearchFragment {
    private FragmentActivity context;
    private RecyclerView mClassifyAllRv;
    private Fragment0_1_Adapter categroyTopAdapter;
    private List<DiscoverTopBean> mEverydayList = new ArrayList();
    private Fragment0_1_Adapter mTopicsAdapter;
    private RecyclerView mEverydayRv, mDeletedRv, mFinishedRv;
    private RecyclerView mHotRecentRv;
    private Fragment0_0_Adapter mEverydayAdapter;
    private List<DiscoverTopBean> mHotRecentList = new ArrayList();
    private Fragment0_0_Adapter mHotRecentAdapter;
    private List<ClassifyTopBean> categroyAllList = new ArrayList<>();
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
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {//3
        super.onActivityCreated(savedInstanceState);

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
        mClassifyAllRv = headView.findViewById(R.id.discover_head_rv_classify);//条目
        mClassifyAllRv.setLayoutManager(mGridLayoutManager);
        categroyAllList.add(new ClassifyTopBean("web界面开发", 101, ""));
        categroyAllList.add(new ClassifyTopBean("办公采购清单", 103, ""));
        categroyAllList.add(new ClassifyTopBean("APP新版界面开发", 153, ""));
        categroyAllList.add(new ClassifyTopBean("周一会议提要", 104, ""));
        categroyAllList.add(new ClassifyTopBean("周一会议提要", 104, ""));
        categroyAllList.add(new ClassifyTopBean("周一会议提要", 104, ""));
        categroyTopAdapter = new Fragment0_1_Adapter(context, categroyAllList);
        mClassifyAllRv.setHasFixedSize(true);
        mClassifyAllRv.setNestedScrollingEnabled(false);
        mClassifyAllRv.setAdapter(categroyTopAdapter);
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

        mTopicsAdapter = new Fragment0_1_Adapter(context, categroyAllList);
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

        mTopicsAdapter = new Fragment0_1_Adapter(context, categroyAllList);
        mDeletedRv.setHasFixedSize(true);
        mDeletedRv.setNestedScrollingEnabled(false);
        mDeletedRv.setAdapter(mTopicsAdapter);
    }
    private final static String TAG = Fragment0.class.getSimpleName();

    // 设置数据
    public void setData(DiscoverListBean result) {
        DiscoverListBean.DataBean data = result.getData();
        if (data == null || context == null) {
            return;
        }
        //----------------------- 每日最新 ------------------------------
        dailyNewGames = data.getDailyNewGamesList();
        if (null != dailyNewGames) {
            mEverydayList = dailyNewGames.getList();
            //每日新发现
            mEverydayAdapter.setList(mEverydayList);
            mEverydayRv.setAdapter(mEverydayAdapter);
        }

        //近期最热
        hotGames = data.getWeeklyNewGamesList();
        if (null != hotGames) {
            mHotRecentList = hotGames.getList();
            //每日新发现
            mHotRecentAdapter.setList(mHotRecentList);
            mHotRecentRv.setAdapter(mHotRecentAdapter);
        }
    }

    private PageAction pageAction;
    public static int PAGE_SIZE = 8;


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
