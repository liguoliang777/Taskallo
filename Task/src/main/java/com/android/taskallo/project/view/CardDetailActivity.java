package com.android.taskallo.project.view;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
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
    private Button mTop_Left_Finished_BT, mTop_Left_Delete_BT, mTopEditSaveBt;
    private String mListTitle = "";
    private String mCardId = "";
    private CardDetailActivity context;
    private EditText mCardTitleEt,mCardDescEt;
    private TextView mListTitleTv;
    private BoardVOListBean mCardBean;
    private String mBoardId;
    private String mListItemId;
    private String mBoardName;
    private String mBoardDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        setContentView(R.layout.activity_card_detail);
        context = this;
        Bundle bundle = getIntent().getExtras();
        Serializable serializable = bundle.getSerializable(KeyConstant.cardBean);

        if (serializable != null && serializable instanceof BoardVOListBean) {
            mCardBean = (BoardVOListBean)serializable;

            mBoardId = mCardBean.boardId;
            mListItemId = mCardBean.listItemId;
            mBoardName = mCardBean.boardName;
            mBoardDesc = mCardBean.boardDesc==null?"": mCardBean.boardDesc;
        }

        mListTitle = bundle.getString(KeyConstant.listItemName);

        mTop_Left_Finished_BT = (Button) findViewById(R.id.top_left_finish_bt);
        mTop_Left_Delete_BT = (Button) findViewById(R.id.top_left_delete_bt);

        mCardTitleEt = (EditText) findViewById(R.id.card_title_et);
        mCardDescEt = (EditText) findViewById(R.id.card_describe_et);
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


        mTop_Left_Finished_BT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mTopEditSaveBt = (Button) findViewById(R.id.edit_right_save_bt);
        //mTop_Right_SaveTv.setVisibility(View.VISIBLE);

    }

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

/*    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (getCurrentFocus() != null) {
                if (getCurrentFocus().getWindowToken() != null) {
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        }
        return super.onTouchEvent(event);

    }*/
}
