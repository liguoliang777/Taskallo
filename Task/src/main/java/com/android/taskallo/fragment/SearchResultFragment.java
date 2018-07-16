package com.android.taskallo.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.android.taskallo.R;
import com.android.taskallo.adapter.SearchOtherAdapter;
import com.android.taskallo.adapter.SearchVideoAdapter;
import com.android.taskallo.project.view.ProjListActivity;
import com.android.taskallo.video.view.VideoDetailActivity;
import com.jzt.hol.android.jkda.sdk.bean.search.RequestSearchBean;
import com.jzt.hol.android.jkda.sdk.bean.search.SearchGameVideoBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 显示搜索结果的页面
 * Created by zeng on 2016/6/7.
 */
public class SearchResultFragment extends Fragment {

    public static final String TAG = SearchResultFragment.class.getSimpleName();

    private Context context;
    private List<SearchGameVideoBean.DataBean.HotSearchGameListBean> searchGameList = new ArrayList<>();
    private List<SearchGameVideoBean.DataBean.HotSearchVideoListBean> searchVideotList = new ArrayList<>();
    private GridView gridView_history;
    private GridView gridView_game;
    private GridView gridView_video;
    private SearchOtherAdapter adapter;
    private SearchVideoAdapter videoAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_result, null);
        gridView_history = (GridView) view.findViewById(R.id.gridView_history);
        gridView_game = (GridView) view.findViewById(R.id.gridView_game);
        gridView_video = (GridView) view.findViewById(R.id.gridView_video);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        gridView_history.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        gridView_game.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ProjListActivity.class);
                intent.putExtra("id", searchGameList.get(position).getId());
                startActivity(intent);
            }
        });
        gridView_video.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), VideoDetailActivity.class);
                intent.putExtra("id", searchVideotList.get(position).getId());
                startActivity(intent);
            }
        });
        getResultList();
    }

    private void getResultList() {
        RequestSearchBean bean = new RequestSearchBean();
        bean.setGameTypeId(75);
        bean.setVideoTypeId(76);
    }
}
