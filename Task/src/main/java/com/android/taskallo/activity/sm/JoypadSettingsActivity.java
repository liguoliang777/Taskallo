package com.android.taskallo.activity.sm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.taskallo.R;
import com.android.taskallo.activity.BaseFgActivity;

/**
 * App设置页面
 * Created by zeng on 2016/12/7.
 */
public class JoypadSettingsActivity extends BaseFgActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        this.setContentView(R.layout.activity_joypad_settings);

        findViewById(R.id.left_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView centerTv = (TextView) findViewById(R.id.center_tv);
        centerTv.setText("游戏手柄设置");

    }

    //游戏手柄支持列表
    public void onSupportedJoypadListClick(View view) {
        Intent intent0 = new Intent(this, SupportedJoypadListActivity.class);
        startActivity(intent0);
    }

    //手柄适配申请
    public void onJoypadPairApplyClick(View view) {
        startActivity(new Intent(this, JoypadAdaptationApplyActivity.class));
    }
}
