package com.android.taskallo.gamehub.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.taskallo.R;
import com.android.taskallo.activity.BaseFgActivity;
import com.android.taskallo.adapter.ChooseGameAloneAdapter;
import com.android.taskallo.adapter.ChooseGameNetworkAdapter;
import com.android.taskallo.adapter.ChooseGameOtherAdapter;
import com.android.taskallo.util.StringUtil;
import com.jzt.hol.android.jkda.sdk.bean.gamehub.GameListBean;
import com.jzt.hol.android.jkda.sdk.bean.gamehub.PostMsgBodyBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 选择游戏
 * Created by gaopan on 2017/3/1 0001.
 */

public class ChooseGameActivity extends BaseFgActivity {

    private LinearLayout ll_back;
    private TextView tv_title;
    private GridView gridView_network, gridView_alone, gridView_other;
    private LinearLayout ll_addGame;
    ChooseGameNetworkAdapter networkAdapter;
    ChooseGameAloneAdapter aloneAdapter;
    ChooseGameOtherAdapter otherAdapter;
    List<GameListBean.DataBean.NetworkGameListBean> networkList = new ArrayList<>();
    List<GameListBean.DataBean.StandAloneGameListBean> aloneList = new ArrayList<>();
    List<GameListBean.DataBean.OtherGameListBean> otherList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_game_activity);
        init();
    }

    private void init() {
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("选择游戏");
        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseGameActivity.this.finish();
            }
        });
        gridView_network = (GridView) findViewById(R.id.gridView_network);
        gridView_alone = (GridView) findViewById(R.id.gridView_alone);
        gridView_other = (GridView) findViewById(R.id.gridView_other);
        ll_addGame = (LinearLayout) findViewById(R.id.ll_addGame);
        ll_addGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChooseGameActivity.this, SubmitGameActivity.class));
            }
        });
        gridView_network.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                isAddGame(networkList.get(position).getGameName());
            }
        });
        gridView_alone.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                isAddGame(aloneList.get(position).getGameName());
            }
        });
        gridView_other.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                isAddGame(otherList.get(position).getGameName());
            }
        });
        runService();
    }

    private void runService() {
        PostMsgBodyBean bodyBean = new PostMsgBodyBean();
    }

    //游戏是否添加
    private void isAddGame(final String gameName) {
        PostMsgBodyBean bodyBean = new PostMsgBodyBean();
        bodyBean.setVoteGame(gameName);
    }

    private void setData(GameListBean.DataBean result) {
        networkList.addAll(result.getNetworkGameList());
        if (networkAdapter == null) {
            networkAdapter = new ChooseGameNetworkAdapter(this, networkList);
            gridView_network.setAdapter(networkAdapter);
        } else {
            networkAdapter.setList(networkList);
        }
        StringUtil.setGridViewHeightBasedOnChildren(gridView_network, 2);

        aloneList.addAll(result.getStandAloneGameList());
        if (aloneAdapter == null) {
            aloneAdapter = new ChooseGameAloneAdapter(this, aloneList);
            gridView_alone.setAdapter(aloneAdapter);
        } else {
            aloneAdapter.setList(aloneList);
        }
        StringUtil.setGridViewHeightBasedOnChildren(gridView_alone, 2);

        otherList.addAll(result.getOtherGameList());
        if (otherAdapter == null) {
            otherAdapter = new ChooseGameOtherAdapter(this, otherList);
            gridView_other.setAdapter(otherAdapter);
        } else {
            otherAdapter.setList(otherList);
        }
        StringUtil.setGridViewHeightBasedOnChildren(gridView_other, 2);
    }
}
