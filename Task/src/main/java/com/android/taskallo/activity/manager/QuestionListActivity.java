package com.android.taskallo.activity.manager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.taskallo.R;
import com.android.taskallo.activity.BaseFgActivity;
import com.android.taskallo.adapter.QuestionListAdapter;
import com.jzt.hol.android.jkda.sdk.bean.admin.AdminGameUpdateBody;
import com.jzt.hol.android.jkda.sdk.bean.admin.QuestionListBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 问题列表
 * Created by gp on 2017/4/13 0013.
 */

public class QuestionListActivity extends BaseFgActivity {

    private LinearLayout ll_back;
    private TextView tv_title;
    private ListView listView;
    String title;
    int type;
    QuestionListAdapter adapter;
    List<QuestionListBean.DataBean> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_list_activity);
        init();
    }

    private void init() {
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        listView = (ListView) findViewById(R.id.listView);
        title = getIntent().getStringExtra("title");
        type = getIntent().getIntExtra("type", 0);
        tv_title.setText(title);
        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QuestionListActivity.this.finish();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent();
                i.setClass(QuestionListActivity.this, QuestionDetailActivity.class);
                i.putExtra("url", list.get(position).getUrl());
                i.putExtra("id", list.get(position).getId());
                startActivity(i);
            }
        });
        runService();
    }

    private void runService() {
        AdminGameUpdateBody body = new AdminGameUpdateBody();
        body.setType(type);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter = null;
        list = null;
    }
}
