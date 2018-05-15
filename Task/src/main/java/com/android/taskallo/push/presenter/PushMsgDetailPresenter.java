package com.android.taskallo.push.presenter;

import android.content.Context;
import android.os.Handler;

import com.android.taskallo.push.model.IPushMessageModel;
import com.android.taskallo.push.model.MessageDetail;
import com.android.taskallo.push.model.PushMessageModel;
import com.android.taskallo.push.view.IPushMsgDetailView;

/**
 * 消息详情控制层实现类
 * Created by zeng on 2016/11/25.
 */
public class PushMsgDetailPresenter implements IPushMsgDetailPresenter {

    IPushMessageModel msgModel;
    IPushMsgDetailView msgView;
    Handler handler = new Handler();

    public PushMsgDetailPresenter(Context context, IPushMsgDetailView msgView) {
        this.msgView = msgView;
        this.msgModel = new PushMessageModel(context);
    }

    @Override
    public void showDetail(final int type,final long msgId) {

        /*Observable.just(msgModel.getMsgDetail(type,msgId))
                //指定了被观察者执行的线程环境
                .subscribeOn(Schedulers.newThread())
                //将接下来执行的线程环境指定为io线程
                //.observeOn(Schedulers.io())
                //此处可以使用操作符进行类型转换等其他操作
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MessageDetail>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MessageDetail msg) {
                        msgView.showMsgDetail(msg);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });*/

        new Thread(new Runnable() {
            @Override
            public void run() {
                final MessageDetail d = msgModel.getMsgDetail(type,msgId);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        msgView.showMsgDetail(d);
                    }
                });
            }
        }).start();

    }

    @Override
    public void markHasRead(final int msgType,final long msgId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                msgModel.markMsgHasRead(msgType,msgId);
            }
        }).start();
    }

}
