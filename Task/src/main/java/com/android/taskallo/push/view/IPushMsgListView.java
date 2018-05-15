package com.android.taskallo.push.view;

import java.util.List;

import com.android.taskallo.push.model.PushMessage;

/**
 * 显示层接口
 * Created by zeng on 2016/11/24.
 */
public interface IPushMsgListView {

    /**
     * 显示消息列表
     * @param msgList 消息集合
     */
    void showMsgList(List<PushMessage> msgList);

}
