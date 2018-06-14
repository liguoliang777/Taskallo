package com.android.taskallo.project.view;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.taskallo.R;
import com.android.taskallo.activity.BaseFgActivity;
import com.android.taskallo.bean.BoardVOListBean;
import com.android.taskallo.core.utils.KeyConstant;

import java.io.Serializable;

public class CardDetailActivity extends BaseFgActivity implements PopupMenu
        .OnMenuItemClickListener {
    private Button mTopFinishedBT, mCancelBT, mTopEditSaveBt;
    private String mListTitle = "";
    private String mCardId = "";
    private CardDetailActivity context;
    private EditText mCardTitleEt, mCardTalkEt, mCardDescEt;
    private TextView mListTitleTv;
    private BoardVOListBean mCardBean;
    private String mBoardId;
    private String mListItemId;
    private String mBoardName;
    private String mBoardDesc;
    private int TYPE_TITLE = 1;
    private int TYPE_DESC = 2;
    private int TYPE_TALK = 3;
    private int EDITING_TYPE = 0;
    private String mEdit3TypeContentStr = "";

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
            mListItemId = mCardBean.listItemId;
            mBoardName = mCardBean.boardName;
            mBoardDesc = mCardBean.boardDesc == null ? "" : mCardBean.boardDesc;
        }

        mListTitle = bundle.getString(KeyConstant.listItemName);

        mTopFinishedBT = (Button) findViewById(R.id.top_left_finish_bt);
        mCancelBT = (Button) findViewById(R.id.top_left_delete_bt);

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
                    EDITING_TYPE = id == R.id.card_title_et ?
                            TYPE_TITLE : id == R.id.card_describe_et ? TYPE_DESC : TYPE_TALK;
                } else {
                    EditText inputEt = (EditText) view;
                    mEdit3TypeContentStr = inputEt.getText().toString();
                }

            }
        };
        mCardTitleEt.setOnFocusChangeListener(onFocusChangeListener);//标题
        mCardDescEt.setOnFocusChangeListener(onFocusChangeListener);//描述
        mCardTalkEt.setOnFocusChangeListener(onFocusChangeListener);//讨论输入框

    }

    private void initCancelOkVisibility(boolean b) {
        mTopEditSaveBt.setVisibility(b ? View.VISIBLE : View.GONE);
        mCancelBT.setVisibility(b ? View.VISIBLE : View.GONE);
    }

    //取消
    View.OnClickListener onBtClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            switch (id) {
                case R.id.edit_right_save_bt:
                    closeInputMethod();
                    //保存

                    break;
                case R.id.top_left_delete_bt:
                    closeInputMethod();
                    break;
                case R.id.top_left_finish_bt:
                    finish();
                    break;

            }
        }
    };


    //顶部弹窗
    public void showCardPopupWindow(View v) {
        PopupMenu popupMenu = new PopupMenu(context, v);
        popupMenu.getMenuInflater().inflate(R.menu.card_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.show();

    }

    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.card_menu_0:
                Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
                break;
            case R.id.card_menu_1:
                Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
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

}
