package com.android.taskallo.project.view;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.android.taskallo.R;
import com.android.taskallo.activity.BaseFgActivity;

public class CardDetailActivity extends BaseFgActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        setContentView(R.layout.activity_card_detail);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.less) {
            return true;
        } else if (item.getItemId() == R.id.restore) {
            return false;
        }
        return super.onOptionsItemSelected(item);
    }
}
