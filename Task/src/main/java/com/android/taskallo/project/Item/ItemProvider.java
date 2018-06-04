package com.android.taskallo.project.Item;


import android.support.v7.widget.RecyclerView;

class ItemProvider {

    static final String TAG = "BoardView";

    private long mSelectedId = RecyclerView.NO_ID;
    private boolean isSmall = false;
    private float mFac = 1;
    private static ItemProvider INSTANCE;

    static ItemProvider getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ItemProvider();
        }
        return INSTANCE;
    }

    private ItemProvider() {
    }

    void setSelectedId(long selectedId) {
        mSelectedId = selectedId;
    }

    long getSelectedId() {
        return mSelectedId;
    }

    void setSmall(boolean isSmall) {
        this.isSmall = isSmall;
        if (isSmall) {
            mFac = 0.6f;
        } else {
            mFac = 1f;
        }
    }

    boolean isSmall() {
        return isSmall;
    }

    float getFac() {
        return mFac;
    }
}
