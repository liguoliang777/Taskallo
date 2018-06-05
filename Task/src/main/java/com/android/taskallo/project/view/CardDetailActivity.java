package com.android.taskallo.project.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.android.taskallo.R;
import com.android.taskallo.activity.BaseFgActivity;
import com.android.taskallo.core.utils.KeyConstant;

public class CardDetailActivity extends BaseFgActivity implements PopupMenu
        .OnMenuItemClickListener {
    private Button mTop_Left_Finished_BT, mTop_Left_Delete_BT, mTopEditSaveBt;
    private String mCardTitle, mCardId;
    private CardDetailActivity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        setContentView(R.layout.activity_card_detail);
        context = this;
        Intent intent = getIntent();
        mCardTitle = intent.getStringExtra(KeyConstant.cardTitle);
        mCardId = intent.getStringExtra(KeyConstant.cardId);

        mTop_Left_Finished_BT = (Button) findViewById(R.id.top_left_finish_bt);
        mTop_Left_Delete_BT = (Button) findViewById(R.id.top_left_delete_bt);

        mTop_Left_Finished_BT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mTop_Left_Finished_BT.setText("卡片");

        mTopEditSaveBt = (Button) findViewById(R.id.edit_right_save_bt);
        //mTop_Right_SaveTv.setVisibility(View.VISIBLE);

    }


    public void showCardPopupWindow(View v) {
        //实例化一个弹出式菜单，传入上下文和控件
        PopupMenu popupMenu = new PopupMenu(context, v);
        //根据菜单填充器获得菜单的布局
        popupMenu.getMenuInflater().inflate(R.menu.card_menu, popupMenu.getMenu());
        //设置菜单的点击事件
        popupMenu.setOnMenuItemClickListener(this);
        //显示菜单
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
}
