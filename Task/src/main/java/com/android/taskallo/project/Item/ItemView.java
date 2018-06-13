package com.android.taskallo.project.Item;


import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;

import com.android.taskallo.R;
import com.android.taskallo.bean.BoardVOListBean;
import com.android.taskallo.bean.ListItemVOListBean;
import com.android.taskallo.project.view.ProjListActivity;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ItemView extends FrameLayout {
    private final String TAG = ItemProvider.TAG;
    private static final int ACTION_BIND = 0;
    private static final int ACTION_UPDATE = 1;
    private View mMirrorView;
    private View mCurrentSelectedLayout;
    private RecyclerView mContentView;
    private RecyclerView mCurrentSelectedRecyclerView;
    private GestureDetectorCompat mGestureDetector;
    private RecyclerView.ViewHolder mRecyclerViewViewHolder;
    private float mInitialTouchX, mInitialTouchY;
    private MotionEvent mCurrentTouchEvent, mLastMoveEvent, mLongPressEvent;
    private boolean mCanMove = true;
    private int mTitleHeight;
    private int mInsertPosition = -1;
    private int mTouchSlop;
    private int mScaledTouchSlop;
    private Callback mCallback;
    private int mRootViewHeight;
    private ItemAdapter adapter;

    public ItemView(@NonNull Context context) {
        this(context, null);
    }

    public ItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ItemView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        //noinspection deprecation
        mTouchSlop = ViewConfiguration.getTouchSlop();
        mScaledTouchSlop = viewConfiguration.getScaledTouchSlop();
        if (mGestureDetector == null) {
            mGestureDetector = new GestureDetectorCompat(getContext(), new
                    BoardViewGestureDetector());
        }
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    List<ListItemVOListBean> itemList = new ArrayList();

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mContentView = new RecyclerView(getContext());
        mContentView.setLayoutParams(generateDefaultLayoutParams());
        mContentView.setLayoutManager(new ItemLayoutManager(getContext(), LinearLayoutManager
                .HORIZONTAL, false));
        mContentView.setHasFixedSize(true);
        addView(mContentView);
        adapter = new ItemAdapter(getContext(), itemList);
        mContentView.setAdapter(adapter);
        mContentView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (mGestureDetector == null) {
                    return;
                }
                if (newState != RecyclerView.SCROLL_STATE_IDLE) {
                    mGestureDetector.setIsLongpressEnabled(false);
                } else {
                    mGestureDetector.setIsLongpressEnabled(true);
                }

            }
        });
        ItemTouchHelper helper = new ItemTouchHelper(new ItemViewTouchCallback(adapter));
        helper.attachToRecyclerView(mContentView);
        LinearSnapHelper linearSnapHelper = new LinearSnapHelper();
        linearSnapHelper.attachToRecyclerView(mContentView);
        mMirrorView = new View(getContext());
        mMirrorView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, ViewGroup
                .LayoutParams.WRAP_CONTENT));
        mMirrorView.setVisibility(GONE);
        mMirrorView.setAlpha(.8f);
        addView(mMirrorView);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (landUpInlineRecyclerView(ev)) { //小item长按
            mGestureDetector.onTouchEvent(ev);
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                recoverSelected();
                break;
        }
        return mRecyclerViewViewHolder != null || super.onInterceptTouchEvent(ev);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (landUpInlineRecyclerView(event)) {
            mGestureDetector.onTouchEvent(event);
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (mRecyclerViewViewHolder != null) {
                    mCurrentTouchEvent = MotionEvent.obtain(event);
                    if (ItemProvider.getInstance().isSmall()) {
                        mMirrorView.setTranslationX(event.getX() - mLongPressEvent.getX() +
                                mInitialTouchX);
                        mMirrorView.setTranslationY(event.getY() - mLongPressEvent.getY() +
                                mInitialTouchY);
                    } else {
                        mMirrorView.setTranslationX(event.getX() - mInitialTouchX);
                        mMirrorView.setTranslationY(event.getY() - mInitialTouchY);
                    }
                    if (mLastMoveEvent != null && (Math.abs(mLastMoveEvent.getY() - event.getY())
                            > mTouchSlop
                            || Math.abs(mLastMoveEvent.getX() - event.getX()) > mTouchSlop)) {
                        mLastMoveEvent = MotionEvent.obtain(event);
                        updateAdapterIfNecessary(event);
                        //判断是需要进行横向欢动还是纵向滑动
                    }
                    selectScroll();
                    mContentView.invalidate();

                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                recoverSelected();
                break;
        }
        return mRecyclerViewViewHolder != null || super.onTouchEvent(event);
    }

    /**
     * 选择一个滑动方向
     */
    private void selectScroll() {
        /*
         *  1.找到落点所在的RecyclerView
         */
        RecyclerView mCurrentRecyclerView = findRecyclerView(mCurrentTouchEvent);
        if (mCurrentRecyclerView == null) {
            mContentView.removeCallbacks(mScrollRunnable);
            return;
        }
        //单个列表(RecyclerView)
        mCurrentRecyclerView = mContentView.getChildAt(0).findViewById(R.id.item_recycler_view);
        int mCurrentRecyclerViewTop = mCurrentRecyclerView.getTop();
        int mCurrentRecyclerViewBottom = mCurrentRecyclerView.getBottom();

        int distanceTop = Math.abs((int) (mTitleHeight + mCurrentRecyclerViewTop
                - mCurrentTouchEvent.getY()));
        int distanceBottom = Math.abs((int) (mTitleHeight + mCurrentRecyclerViewBottom
                - mCurrentTouchEvent.getY()));
        int distanceLeft = (int) mCurrentTouchEvent.getX();
        int distanceRight = (int) (getWindowWidth() / ItemProvider.getInstance().getFac()
                - mCurrentTouchEvent.getX());

        if ((distanceTop < distanceLeft && distanceTop < distanceRight)
                || (distanceBottom < distanceLeft && distanceBottom < distanceRight)) {
            mCurrentSelectedRecyclerView.removeCallbacks(mInlineScrollRunnable);
            mInlineScrollRunnable.run();
        } else {
            mContentView.removeCallbacks(mScrollRunnable);
            mScrollRunnable.run();
        }
    }

    private void updateAdapterIfNecessary(MotionEvent event) {
        if (mRecyclerViewViewHolder == null) return;
        //分为两种情况，1 在mSelected所在的RecyclerView与当前点所能查找到的的RecyclerView是同一个
        RecyclerView targetRecycler = findRecyclerView(event);
        if (targetRecycler == mCurrentSelectedRecyclerView) {
            // 同一个RecyclerView
            View child = findRecyclerViewChild(event);
            if (child == null) {
                return;
            }
            LinearLayoutManager layoutManager = (LinearLayoutManager) mCurrentSelectedRecyclerView
                    .getLayoutManager();
            RecyclerView.ViewHolder target = mCurrentSelectedRecyclerView.getChildViewHolder(child);
            if (target != mRecyclerViewViewHolder && !mCurrentSelectedRecyclerView.isAnimating()
                    && mCanMove) {
                if (mCallback != null) {
                    int toPos = target.getAdapterPosition();
                    int position = mRecyclerViewViewHolder.getAdapterPosition();
                    if (position == -1) {
                        ItemItemAdapter adapter = (ItemItemAdapter) mCurrentSelectedRecyclerView
                                .getAdapter();
                        position = adapter.getPositionFromId();
                    }
                    if (position == -1) return;
                    mCallback.onMoved(mCurrentSelectedRecyclerView, position,
                            toPos);
                    /*
                     * 保持RecyclerView不发生移动
                     */
                    ((android.support.v7.widget.helper.ItemTouchHelper.ViewDropHandler)
                            layoutManager)
                            .prepareForDrop(mRecyclerViewViewHolder.itemView, target.itemView,
                                    (int) event.getX(), (int) event.getY());
                    return;
                }
            }
        }

        // 跨RecyclerView
        if (targetRecycler != null && targetRecycler != mCurrentSelectedRecyclerView) {
            BoardVOListBean data = new BoardVOListBean();
            if (mCallback != null) {
                int pos = ((ItemItemAdapter) mCurrentSelectedRecyclerView.getAdapter())
                        .getPositionFromId();
                data = mCallback.onRemoved(mCurrentSelectedRecyclerView, pos);
            }
            mCurrentSelectedRecyclerView = targetRecycler;
            //找到落点所在的位置
            View view = findRecyclerViewChild(event);
            int position = 0;
            RecyclerView.ViewHolder tmpHolder = null;
            if (view != null) {
                tmpHolder = mCurrentSelectedRecyclerView.getChildViewHolder(view);
                position = tmpHolder.getAdapterPosition();
            }
            if (mCallback != null) {
                mCallback.onInserted(mCurrentSelectedRecyclerView, position, data);
                if (position == 0) {
                    Log.e(TAG, "updateAdapterIfNecessary: " + "Event没有落在View上，这种情况是错误的");
                }
            }
            mInsertPosition = position;
            //有时候会出现，删除动画。加上这句，能稍微改善下
            mRecyclerViewViewHolder.itemView.setAlpha(0);
            final RecyclerView.ViewHolder tmpViewHolder = mCurrentSelectedRecyclerView
                    .findViewHolderForAdapterPosition(position);
            if (tmpViewHolder == null || tmpViewHolder.getAdapterPosition() != -1) {
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updateSelectedByInsert();
                    }
                }, 16);
                mCanMove = false;
            } else {
                mRecyclerViewViewHolder = tmpHolder;
                select(mRecyclerViewViewHolder, ACTION_UPDATE);
            }
        }
    }

    final Runnable mScrollRunnable = new Runnable() {
        @Override
        public void run() {
            if (mRecyclerViewViewHolder != null && scrollIfNecessary()) {
                if (mRecyclerViewViewHolder != null) {
                    updateAdapterIfNecessary(mCurrentTouchEvent);
                }
                mContentView.removeCallbacks(mScrollRunnable);
                ViewCompat.postOnAnimation(mContentView, this);
            }
        }
    };

    /**
     * 滑动外层的RecyclerView
     */
    private boolean scrollIfNecessary() {
        int direction = getWindowWidth() - mCurrentTouchEvent.getX() > getWindowWidth() / 2 ? -1
                : 1;
        if (!mContentView.canScrollHorizontally(direction)) {
            return false;
        }
        //边缘检测
        if (mContentView.getLeft() + 50 > mCurrentTouchEvent.getX()) {
            //在左
            mContentView.smoothScrollBy(-mScaledTouchSlop * 5, 0);//缩放触摸溢出
            return true;
        } else if (mContentView.getRight() - 50 < mCurrentTouchEvent.getX()) {
            //在右
            mContentView.smoothScrollBy(mScaledTouchSlop * 5, 0);
            return true;
        }
        return false;
    }

    final Runnable mInlineScrollRunnable = new Runnable() {
        @Override
        public void run() {
            if (mRecyclerViewViewHolder != null && scrollInlineRVIfNecessary()) {
                if (mRecyclerViewViewHolder != null) {
                    updateAdapterIfNecessary(mCurrentTouchEvent);
                }
                mCurrentSelectedRecyclerView.removeCallbacks(mInlineScrollRunnable);
                ViewCompat.postOnAnimation(mCurrentSelectedRecyclerView, this);
            }
        }
    };

    /**
     * 滑动内嵌的RecyclerView
     */
    private boolean scrollInlineRVIfNecessary() {
        int direction = mCurrentSelectedRecyclerView.getBottom() - (mCurrentTouchEvent.getY() -
                mTitleHeight)
                > mCurrentTouchEvent.getY() - mTitleHeight - mCurrentSelectedRecyclerView.getTop
                () ? -1 : 1;
        if (!mCurrentSelectedRecyclerView.canScrollVertically(direction)) return false;
        //边缘检测
        if (mCurrentSelectedRecyclerView.getTop() + mTitleHeight > mCurrentTouchEvent.getY()) {
            mCurrentSelectedRecyclerView.smoothScrollBy(0, -mScaledTouchSlop * 5);
            return true;
        } else if (mCurrentSelectedRecyclerView.getBottom() - 50 < mCurrentTouchEvent.getY()) {
            mCurrentSelectedRecyclerView.smoothScrollBy(0, mScaledTouchSlop * 5);
            return true;
        }
        return false;
    }

    private void recoverSelected() {
        mLastMoveEvent = null;
        mCanMove = true;
        if (mMirrorView != null && mRecyclerViewViewHolder != null) {
            //x方向的偏移量
            float diffX = mCurrentSelectedLayout.getLeft() + mRecyclerViewViewHolder.itemView
                    .getLeft() +
                    getScrollX()
                    - (mMirrorView.getLeft() + mMirrorView.getTranslationX());
            //y方向的偏移量
            float diffY = mRecyclerViewViewHolder.itemView.getTop() +
                    mCurrentSelectedRecyclerView.getTop()
                    - mMirrorView.getTranslationY();
            mMirrorView.animate().cancel();
            mMirrorView.animate()
                    .setDuration(100)
                    .rotation(0)
                    .translationXBy(diffX)
                    .translationYBy(diffY)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            // no op
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mMirrorView.setVisibility(GONE);
                            mRecyclerViewViewHolder.itemView.setAlpha(1);
                            mRecyclerViewViewHolder.itemView.setVisibility(VISIBLE);
                            int posi = mRecyclerViewViewHolder.getAdapterPosition();
                            if (posi == -1) {
                                // 这里是ViewHolder丢失位置信息的处理办法
                                ItemItemAdapter adapter = (ItemItemAdapter)
                                        mCurrentSelectedRecyclerView
                                                .getAdapter();
                                int position = adapter.getPositionFromId();
                                RecyclerView.ViewHolder tmpViewHolder = mCurrentSelectedRecyclerView
                                        .findViewHolderForAdapterPosition(position);
                                if (tmpViewHolder != null) {
                                    tmpViewHolder.itemView.setVisibility(VISIBLE);
                                } else {
                                    mCurrentSelectedRecyclerView.getAdapter().notifyItemChanged
                                            (position);
                                }
                            } else {
                                mCurrentSelectedRecyclerView.getAdapter().notifyItemChanged(posi);
                            }
                            ItemProvider.getInstance().setSelectedId(RecyclerView.NO_ID + "");
                            mRecyclerViewViewHolder = null;
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                            // no op
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                            // no op
                        }
                    })
                    .start();
        }
    }

    private void select(RecyclerView.ViewHolder selected, int actionState) {
        if (selected != null && selected != mRecyclerViewViewHolder) {
            if (mRecyclerViewViewHolder != null) {
                mRecyclerViewViewHolder.itemView.setVisibility(VISIBLE);
            }
            mRecyclerViewViewHolder = selected;
            int position = mRecyclerViewViewHolder.getAdapterPosition();
            ItemItemAdapter adapter = (ItemItemAdapter) mCurrentSelectedRecyclerView.getAdapter();
            String id = adapter.getIdByPosition(position);
            ItemProvider.getInstance().setSelectedId(id);
            if (actionState == ACTION_BIND) {
                onBindSelected(mRecyclerViewViewHolder.itemView);
            }
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private void onBindSelected(View selectedView) {
        //先设置偏移量
        int x = mCurrentSelectedLayout.getLeft() + selectedView.getLeft();
        int y = mCurrentSelectedLayout.getTop() + selectedView.getTop() + mTitleHeight;
        ViewGroup.LayoutParams params = mMirrorView.getLayoutParams();
        params.width = selectedView.getWidth();
        params.height = selectedView.getHeight();
        mMirrorView.setLayoutParams(params);
        mMirrorView.setTranslationX(x);
        mMirrorView.setTranslationY(y);
        Bitmap b = Bitmap.createBitmap(selectedView.getWidth(), selectedView.getHeight(),
                Bitmap.Config.RGB_565);
        Bitmap bitmap = drawRadius(b);
        Canvas canvas = new Canvas(bitmap);
        selectedView.draw(canvas);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mMirrorView.setBackground(new BitmapDrawable(selectedView.getResources(), bitmap));
        } else {
            //noinspection deprecation
            mMirrorView.setBackgroundDrawable(new BitmapDrawable(selectedView.getResources(),
                    bitmap));
        }
        mMirrorView.setVisibility(VISIBLE);
        mMirrorView.setRotation(3f);//小item的旋转角度
        mMirrorView.setAlpha(1f);
        mRecyclerViewViewHolder.itemView.setAlpha(0);
    }

    public Bitmap drawRadius(Bitmap bitmap) {
        try {
            Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                    bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(),
                    bitmap.getHeight());
            final RectF rectF = new RectF(new Rect(0, 0, bitmap.getWidth(),
                    bitmap.getHeight()));
            final float roundPx = 14;
            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(Color.BLACK);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

            final Rect src = new Rect(0, 0, bitmap.getWidth(),
                    bitmap.getHeight());

            canvas.drawBitmap(bitmap, src, rect, paint);
            return output;
        } catch (Exception e) {
            return bitmap;
        }
    }

    private boolean landUpInlineRecyclerView(MotionEvent event) {//最终到达,互相关联的rv里
        float x = event.getX();
        float y = event.getY();
        View view = mContentView.findChildViewUnder(x, y);
        if (view == null) {
            view = findChildViewUnderWithInsets(mContentView, x, y);//找到插入的子条目
        }
        if (view == null) {
            return false;
        }
        View recyclerView = view.findViewById(R.id.item_recycler_view); //单个列表(RecyclerView)
        if (recyclerView == null) {
            return false;
        }
        return y > recyclerView.getTop() && y < recyclerView.getBottom();
    }

    /**
     * 找到当前触摸点所在的RecyclerView
     */
    private RecyclerView findRecyclerView(MotionEvent event) {
        View child = mContentView.findChildViewUnder(event.getX(), event.getY());
        if (child == null) {
            return null;
        }
        RecyclerView viewById = child.findViewById(R.id.item_recycler_view);
        if (viewById == null) {
            return null;
        }
        return viewById; //单个列表(RecyclerView)
    }

    private View findRecyclerViewChild(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        View child = mContentView.findChildViewUnder(x, y);
        mCurrentSelectedLayout = child;
        mCurrentSelectedRecyclerView = child.findViewById(R.id.item_recycler_view); //单个列表
        if (mCurrentSelectedRecyclerView == null) {
            return null;
        }
        // (RecyclerView)
        int titleHeight = getCurrentColumnTitleHeight(event);
        View view = mCurrentSelectedRecyclerView.findChildViewUnder(x - child.getLeft(),
                y - titleHeight);
        if (view == null) {
            view = findChildViewUnderWithInsets(mCurrentSelectedRecyclerView, x -
                            mCurrentSelectedLayout.getLeft(),
                    y - mCurrentSelectedLayout.getTop());
        }
        return view;
    }

    //当前列表RV的title高度
    private int getCurrentColumnTitleHeight(MotionEvent event) {
        if (mTitleHeight == 0) {
            View child = mContentView.findChildViewUnder(event.getX(), event.getY());
            mTitleHeight = child.findViewById(R.id.proj_list_item_title).getHeight();
        }
        return mTitleHeight;
    }

    private void updateSelectedByInsert() {//更新被选中的,
        RecyclerView.ViewHolder viewHolder = mCurrentSelectedRecyclerView
                .findViewHolderForAdapterPosition(mInsertPosition);
        if (viewHolder != null) {
            if (viewHolder.getAdapterPosition() != -1) {
                select(viewHolder, ACTION_UPDATE);
                mCanMove = true;
            } else {
                Log.e(TAG, "updateSelectedByInsert: error " + viewHolder.getAdapterPosition());
            }
        }
    }

    private int mWindowHeight;

    @SuppressWarnings("unused")
    private int getWindowHeight() {
        if (mWindowHeight == 0) {
            mWindowHeight = getContext().getResources().getDisplayMetrics().heightPixels;
        }
        return mWindowHeight;
    }

    private int mWindowWidth;

    private int getWindowWidth() {
        if (mWindowWidth == 0) {
            mWindowWidth = getContext().getResources().getDisplayMetrics().widthPixels;
        }
        return mWindowWidth;
    }

    public void scale(boolean lessen) {
        if (lessen) {
            //缩小
            ItemProvider.getInstance().setSmall(true);
        } else {
            //还原
            ItemProvider.getInstance().setSmall(false);
        }
        float scale = ItemProvider.getInstance().getFac();
        View rootView = (View) getParent();
        if (lessen && mRootViewHeight == 0) {
            mRootViewHeight = rootView.getHeight();
        }
        rootView.getLayoutParams().width = (int) (getWindowWidth() * (1 / scale));
        rootView.getLayoutParams().height = (int) (mRootViewHeight * (1 / scale));
        setScaleX(scale);
        setScaleY(scale);
        setPivotX(0f);
        setPivotY(0f);
        requestLayout();
        for (int i = 0; i < mContentView.getChildCount(); i++) {
            ViewGroup child = (ViewGroup) mContentView.getChildAt(i);
            Log.e(TAG, "scale: " + child.getChildAt(1).getHeight() + ";;;----->" + lessen);
            child.requestLayout();
        }
    }

    public void setContext(ProjListActivity mainActivity, String projectId) {
        adapter.setContext(mainActivity, projectId);
    }

    public void setData(List<ListItemVOListBean> listItemVOList) {
        adapter.setList(listItemVOList);
    }

    private class BoardViewGestureDetector extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            if (mContentView.getScrollState() != RecyclerView.SCROLL_STATE_IDLE
                    || mRecyclerViewViewHolder != null) {
                return;
            }
            mLastMoveEvent = e;
            RecyclerView childRecyclerView = findRecyclerView(e);
            if (childRecyclerView == null) return;
            View recyclerViewChild = findRecyclerViewChild(e);
            if (recyclerViewChild == null) {
                recyclerViewChild = findChildViewUnderWithInsets(childRecyclerView, e.getX(), e
                        .getY());
            }
            if (recyclerViewChild == null) return;
            select(childRecyclerView.getChildViewHolder(recyclerViewChild), 0);
            if (mRecyclerViewViewHolder != null) {
                //左右边界情况
                Rect rect = new Rect();
                mRecyclerViewViewHolder.itemView.getGlobalVisibleRect(rect);
                int width = (int) (mRecyclerViewViewHolder.itemView.getWidth() * ItemProvider
                        .getInstance().getFac());
                if (Math.abs(rect.left - rect.right) < width) {
                    if (rect.right + width > getWindowWidth()) {
                        //右侧
                        mInitialTouchX = e.getRawX() - rect.left;
                    } else {
                        mInitialTouchX = width - (rect.right - e.getRawX());
                    }
                    mInitialTouchY = e.getRawY() - rect.top;
                } else {
                    mInitialTouchX = e.getRawX() - rect.left;
                    mInitialTouchY = e.getRawY() - rect.top;
                }
            } else {
                mInitialTouchX = e.getX();
                mInitialTouchY = e.getY();
            }
            //缩小状态下，上面的方案不行，采用下面的方案
            if (ItemProvider.getInstance().isSmall()) {
                mLongPressEvent = MotionEvent.obtain(e);
                mInitialTouchX = mMirrorView.getTranslationX();
                mInitialTouchY = mMirrorView.getTranslationY();
            }
        }

    }


    private Method getItemDecorInsetsForChildMethod;

    {
        try {
            getItemDecorInsetsForChildMethod = RecyclerView.class.getDeclaredMethod
                    ("getItemDecorInsetsForChild", View.class);
            getItemDecorInsetsForChildMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    /**
     * 加上ItemDecoration的位置
     */
    private View findChildViewUnderWithInsets(RecyclerView recyclerView, float x, float y) {
        final int count = recyclerView.getChildCount();
        Rect rect = new Rect(0, 0, 0, 0);
        for (int i = count - 1; i >= 0; i--) {
            final View child = recyclerView.getChildAt(i);
            final float translationX = child.getTranslationX();
            final float translationY = child.getTranslationY();
            try {
                rect = (Rect) getItemDecorInsetsForChildMethod.invoke(recyclerView, child);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (x >= child.getLeft() + translationX - rect.left - 30
                    && x <= child.getRight() + translationX + rect.right + 30
                    && y >= child.getTop() + translationY - rect.top
                    && y <= child.getBottom() + translationY + rect.bottom) {
                return child;
            }
        }
        return null;
    }

    public interface Callback {
        void onMoved(RecyclerView recyclerView, int from, int to);

        BoardVOListBean onRemoved(RecyclerView recyclerView, int position);

        void onInserted(RecyclerView recyclerView, int position, BoardVOListBean data);
    }
}
