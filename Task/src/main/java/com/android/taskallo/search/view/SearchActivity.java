package com.android.taskallo.search.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.taskallo.R;
import com.android.taskallo.activity.BaseFgActivity;
import com.android.taskallo.adapter.LvSearchAdapter;
import com.android.taskallo.adapter.SearchAdapter;
import com.android.taskallo.adapter.SearchOtherAdapter;
import com.android.taskallo.adapter.SearchVideoAdapter;
import com.android.taskallo.bean.SearchHistoryBean;
import com.android.taskallo.core.db.DatabaseManager;
import com.android.taskallo.core.utils.KeyConstant;
import com.android.taskallo.core.utils.TextUtil;
import com.android.taskallo.project.view.ProjListActivity;
import com.android.taskallo.view.LoadStateView;
import com.jzt.hol.android.jkda.sdk.bean.search.RequestSearchBean;
import com.jzt.hol.android.jkda.sdk.bean.search.SearchBean;
import com.jzt.hol.android.jkda.sdk.bean.search.SearchGameVideoBean;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;



/**
 * 搜索界面
 * Created by gp on 2016/5/12.
 */
public class SearchActivity extends BaseFgActivity implements View.OnClickListener {

    public static final int READ_EXTERNAL_STORAGE = 24;
    public static final int WRITE_EXTERNAL_STORAGE = 25;
    public static final String TAG = SearchActivity.class.getSimpleName();
    public int GAMETYPE_ID = 36;
    public int VIDEOTYPE_ID = 37;
    private LoadStateView loadStateView;
    //private TextView tv_search;
    private EditText et_search;
    private String searchName;
    private ImageView input_clear_bt;
    private ListView resultListView;

    private LinearLayout ll_show;
    private LinearLayout layout_history;
    private TextView tv_clear;
    List<SearchGameVideoBean.DataBean.HotSearchGameListBean> searchGameList = new ArrayList<>();
    List<SearchGameVideoBean.DataBean.HotSearchVideoListBean> searchVideotList = new ArrayList<>();
    private GridView gridView_history;
    private GridView gridView_game;
    private SearchOtherAdapter gameAdapter;
    private SearchVideoAdapter videoAdapter;

    private List<SearchHistoryBean> historyList;
    private List<SearchBean.DataBean> searchList = new ArrayList<>();
    private DatabaseManager dbManager;
    LvSearchAdapter historyAdapter;
    SearchAdapter searchAdapter;
    private String tvSearch;
    private SearchActivity content;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_search);
        initStatusBar();
        content = this;
        dbManager = DatabaseManager.getInstance(this);
        resultListView = (ListView) findViewById(R.id.listView);
        loadStateView = (LoadStateView) findViewById(R.id.loadStateView);
        //tv_search = (TextView) findViewById(R.id.search_bt);
        et_search = (EditText) findViewById(R.id.et_search);
       /* InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(et_search, 0);*/
        input_clear_bt = (ImageView) findViewById(R.id.but_fork);

        ll_show = (LinearLayout) findViewById(R.id.ll_show);
        layout_history = (LinearLayout) findViewById(R.id.ll_history);
        tv_clear = (TextView) findViewById(R.id.tv_clear);
        gridView_history = (GridView) findViewById(R.id.gridView_history);
        gridView_game = (GridView) findViewById(R.id.gridView_game);

        loadStateView.setReLoadListener(this);
        //tv_search.setOnClickListener(this);
        tv_clear.setOnClickListener(this);

        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    doSearch(false);
                } else {
                    ll_show.setVisibility(View.VISIBLE);
                    resultListView.setVisibility(View.GONE);
                    loadStateView.setVisibility(View.GONE);
                    queryDBHistory(); //查询本地数据库列表
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String searchText = et_search.getText().toString();
                if (!TextUtil.isEmpty(searchText)) {
                    input_clear_bt.setVisibility(View.VISIBLE);
                } else {
                    input_clear_bt.setVisibility(View.GONE);
                }
            }
        });

        //编辑完之后点击软键盘上的回车键才会触发
        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

                    String searchNameStr = et_search.getText().toString().trim();
                    if (searchNameStr != null && searchNameStr.length() > 0) {
                        searchName = searchNameStr;
                        // 先隐藏键盘
                        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                                .hideSoftInputFromWindow(SearchActivity.this.getCurrentFocus()
                                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                        loadStateView.setVisibility(View.VISIBLE);
                        loadStateView.setState(LoadStateView.STATE_ING);
                        doSearch(true);
                    } else {
                        et_search.setText("");
                        Toast.makeText(SearchActivity.this, "请输入搜索内容", Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            }
        });
        input_clear_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_search.setText("");
                ll_show.setVisibility(View.VISIBLE);
                resultListView.setVisibility(View.GONE);
                loadStateView.setVisibility(View.GONE);
            }
        });

        gridView_history.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String historyTitle = historyList.get(position).getTitle();
                et_search.setText(historyTitle);
                et_search.setSelection(historyTitle.length());
                searchName = historyTitle;
                doSearch(false);
            }
        });
        gridView_game.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SearchActivity.this, ProjListActivity.class);
                SearchGameVideoBean.DataBean.HotSearchGameListBean hotSearchGameListBean = searchGameList.get(position);
                String advName = hotSearchGameListBean.getAdvName().trim();
                dbManager.addSearchHistory(advName);
                intent.putExtra(KeyConstant.id, hotSearchGameListBean.getGameId());
                startActivity(intent);
            }
        });

        resultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SearchBean.DataBean dataBean = searchList.get(position);
                if (dataBean.getType() == 1) {
                    String keywordsStr = dataBean.getKeywords();
                    dbManager.addSearchHistory(keywordsStr);
                    Intent intent = new Intent(SearchActivity.this, ProjListActivity.class);
                    intent.putExtra(KeyConstant.id, dataBean.getTypeId());
                    startActivity(intent);
                } else {
                 /*   Intent intent = new Intent(SearchActivity.this, VideoDetailActivity.class);
                    intent.putExtra(KeyConstant.id, dataBean.getTypeId());
                    startActivity(intent);*/
                }
            }
        });

//        FragmentManager fragmentManager = SearchActivity.this.getFragmentManager();
//        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        Fragment fragment = new SearchResultFragment();
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("result", "");
//        fragment.setArguments(bundle);
//        transaction.replace(content_wrapper, fragment);
//        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//        transaction.commit();
        gameAdapter = new SearchOtherAdapter(content, searchGameList);
        gridView_game.setAdapter(gameAdapter);
        getResultList(); //请求热搜游戏，视频
    }

    private void doSearch(boolean b) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        queryDBHistory();
    }

    //查询本地搜索历史
    public void queryDBHistory() {
        try {
            historyList = dbManager.queryAllSearchHistory();
            if (historyList.size() == 0) {
                layout_history.setVisibility(View.GONE);
            } else {
                historyAdapter = new LvSearchAdapter(this, historyList);
                gridView_history.setAdapter(historyAdapter);
                layout_history.setVisibility(View.VISIBLE);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    //请求热搜游戏
    private void getResultList() {
        RequestSearchBean bean = new RequestSearchBean();
        bean.setGameTypeId(GAMETYPE_ID);
        bean.setVideoTypeId(VIDEOTYPE_ID);

    }

//    @Override
//    public void startActivity(Intent intent) {
//        super.startActivity(intent);
//        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.slide_out_right); //搜索页面退出动画
    }

    //搜索

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
          /*  case R.id.search_bt:
                Log.d(TAG, "点击搜索: ");
                String searchStr = et_search.getText().toString().trim();
                if (StringUtil.isEmpty(searchStr)) {
                    et_search.setText("");
                    ToastUtil.show(content, "请输入搜索内容");
                } else {
                    searchName = searchStr;
                    // 先隐藏键盘
                    ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(SearchActivity.this.getCurrentFocus()
                                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                    loadStateView.setVisibility(View.VISIBLE);
                    loadStateView.setState(LoadStateView.STATE_ING);
                    doSearch(true);
                }
                break;*/
            case R.id.tv_clear:
                dbManager.deleteAllSearchHistory(); //清除搜索历史
                layout_history.setVisibility(View.GONE);
                break;
        }
    }

    //通过id删除
    public void deleteItemhistory(String title) {
        dbManager.deleteSearchHistoryById(title);
        queryDBHistory();
    }

    public void onSeachBackClick(View view) {
        finish();
    }
}
