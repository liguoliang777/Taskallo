package com.android.taskallo.project.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.android.taskallo.R;
import com.android.taskallo.activity.BaseFgActivity;
import com.android.taskallo.core.utils.KeyConstant;

public class CardDetailActivity extends BaseFgActivity implements PopupMenu
        .OnMenuItemClickListener {
    private Button mTop_Left_Finished_BT, mTop_Left_Delete_BT, mTopEditSaveBt;
    private String mCardTitle = "";
    private String mCardId = "";
    private CardDetailActivity context;
    private EditText mCardTitleEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        setContentView(R.layout.activity_card_detail);
        context = this;
        Intent intent = getIntent();
        mCardTitle = intent.getStringExtra(KeyConstant.cardTitle);
        mCardTitle = mCardTitle == null ? "" : mCardTitle;
        mCardId = intent.getStringExtra(KeyConstant.cardId);

        mTop_Left_Finished_BT = (Button) findViewById(R.id.top_left_finish_bt);
        mTop_Left_Delete_BT = (Button) findViewById(R.id.top_left_delete_bt);
        mCardTitleEt = (EditText) findViewById(R.id.card_title_et);
        mCardTitleEt.setText(mCardTitle);
        mCardTitleEt.setSelection(mCardTitle.length());
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
                Toast.makeText(this, "开始游戏", Toast.LENGTH_SHORT).show();
                break;
            case R.id.card_menu_1:
                Toast.makeText(this, "结束游戏", Toast.LENGTH_SHORT).show();
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
