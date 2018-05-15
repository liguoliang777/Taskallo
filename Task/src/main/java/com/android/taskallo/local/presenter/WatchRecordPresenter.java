package com.android.taskallo.local.presenter;

import android.content.Context;
import android.os.Handler;

import java.util.List;

import com.android.taskallo.local.model.IWatchRecordModel;
import com.android.taskallo.local.model.WatchRecord;
import com.android.taskallo.local.model.WatchRecordGroup;
import com.android.taskallo.local.model.WatchRecordModel;
import com.android.taskallo.local.view.IWatchRecordView;

/**
 * 本地管理，视频观看记录控制层实现类
 * Created by zeng on 2016/10/11.
 */
public class WatchRecordPresenter implements IWatchRecordPresenter {

    private IWatchRecordModel watchRecordModel;
    private IWatchRecordView watchRecordView;
    private Handler handler = new Handler();

    public WatchRecordPresenter(Context context, IWatchRecordView view){
        this.watchRecordView = view;
        watchRecordModel = new WatchRecordModel(context);
    }

    /**
     * 显示观看记录
     */
    @Override
    public void showWatchRecord(final String token, final String userCode) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                final WatchRecordGroup group = watchRecordModel.queryWatchRecord(token,userCode);
                if(group != null){

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            watchRecordView.showWatchRecord(group.getWeekPlayRecord(),group.getEarlierPlayRecord());
                        }
                    });
                }
            }
        }).start();

    }

    @Override
    public void deleteRecord(final String token, final String userCode, List<WatchRecord> deleteData) {
        watchRecordModel.deleteRecord(token,userCode, deleteData);
    }
}
