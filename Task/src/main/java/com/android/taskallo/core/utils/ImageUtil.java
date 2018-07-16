package com.android.taskallo.core.utils;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.PopupWindow;

import com.android.taskallo.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

/**
 * 获取图片的Base64字符串
 * Created by zeng on 2016/6/20.
 */
public class ImageUtil {

    private static float mRatio;
    private static int mScreenWidth;
    private static int mScreenHeight;
    private static float scale;

    // 根据路径获得图片并压缩，返回bitmap用于显示
    public static Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 480, 800);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(filePath, options);
    }

    //计算图片的缩放值
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int
            reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    public static String getImageStr(String filePath) {
        Bitmap bm = getSmallBitmap(filePath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        //1.5M的压缩后在100Kb以内，测试得值,压缩后的大小=94486字节,压缩后的大小=74473字节
        //这里的JPEG 如果换成PNG，那么压缩的就有600kB这样
        bm.compress(Bitmap.CompressFormat.JPEG, 40, baos);
        byte[] b = baos.toByteArray();
        Log.d("d", "压缩后的大小=" + b.length);
        return Base64.encodeToString(b, Base64.DEFAULT);

    }

    public static int getScreenWidth(Activity activity) {
        if (mScreenWidth == 0) {
            initialScreenParams(activity);
        }
        return mScreenWidth;
    }

    public static int getScreenHeight(Activity activity) {
        if (mScreenHeight == 0) {
            initialScreenParams(activity);
        }
        return mScreenHeight;
    }

    public static void initialScreenParams(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels - statusBarHeight;
        mRatio = mScreenWidth / (float) mScreenHeight;
        scale = activity.getResources().getDisplayMetrics().density;
    }

    public static int getStatusBarHeight(Activity activity) {
        int result = 0;
        Resources resources = activity.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId);
        }
        return result;
    }

    //设置GridView高度
    public static void setGridViewHeight(GridView gridView) {
        // 获取listview的adapter
        ListAdapter listAdapter = gridView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int verticalSpacing = gridView.getVerticalSpacing();
        // 固定列宽，有多少列
        int col = 2;// gridView.getNumColumns();
        int totalHeight = 0;
        // i每次加2，相当于listAdapter.getCount()小于等于2时 循环一次，计算一次item的高度，
        // listAdapter.getCount()小于等于4时计算两次高度相加
        int count = listAdapter.getCount();
        for (int i = 0; i < count; i += col) {
            // 获取listview的每一个item
            View listItem = listAdapter.getView(i, null, gridView);
            listItem.measure(0, 0);
            // 获取item的高度和
            int measuredHeight = listItem.getMeasuredHeight() + verticalSpacing;
            totalHeight += measuredHeight;
        }
        if (count >= 2) {
            totalHeight = totalHeight - verticalSpacing;
        }
        // 获取listview的布局参数
        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        // 设置高度
        params.height = totalHeight;
        // 设置margin
        //((ViewGroup.MarginLayoutParams) params).setMargins(10, 10, 10, 10);
        // 设置参数
        gridView.setLayoutParams(params);
    }

    /**
     * @param
     * @param attachOnView  显示在这个View的下方
     * @param popView       被显示在PopupWindow上的View
     * @param popShowHeight 被显示在PopupWindow上的View的高度，可以传默认值defaultBotom
     * @param popShowWidth  被显示在PopupWindow上的View的宽度，一般是传attachOnView的getWidth()
     * @return PopupWindow
     */
    public static PopupWindow showPopupWindow(Activity context, View attachOnView, View popView,
                                              final int popShowHeight, final
    int popShowWidth) {
        final int defaultBotom = -60;//距离底部
        if (popView != null && popView.getParent() != null) {
            ((ViewGroup) popView.getParent()).removeAllViews();
        }

        PopupWindow popupWindow = null;
        Rect frame = new Rect();
        context.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);

        int location[] = new int[2];
        int x, y;
        int popHeight = 0, popWidth = 0;

        attachOnView.getLocationOnScreen(location);
        x = location[0];
        y = location[1];

        int h = attachOnView.getHeight();
        int screenHeight = getScreenHeight(context);

        if (popShowHeight == defaultBotom) {
            popHeight = screenHeight / 6;
            popHeight = Math.abs(screenHeight - (h + y)) - popHeight;
        } else if (popHeight == ViewGroup.LayoutParams.WRAP_CONTENT) {
            popHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
        } else {
            popHeight = popShowHeight;
        }

        if (popShowWidth == ViewGroup.LayoutParams.WRAP_CONTENT) {
            popWidth = attachOnView.getWidth();
        } else {
            popWidth = popShowWidth;
        }

        popupWindow = new PopupWindow(popView, popWidth, popHeight, true);

        //这行代码时用来让PopupWindow点击区域之外消失的，这个应该是PopupWindow的一个bug。
        //但是利用这个bug可以做到点击区域外消失
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setAnimationStyle(R.style.anim_rank_list_popup);
        popupWindow.showAtLocation(attachOnView, Gravity.NO_GRAVITY, x, h + y);
        popupWindow.update();
        return popupWindow;
    }

    public static Drawable loadImageFromNetwork(String imageUrl) {
        Drawable drawable = null;
        try {
            // 可以在这里通过第二个参数(文件名)来判断，是否本地有此图片
            drawable = Drawable.createFromStream(new URL(imageUrl).openStream(), null);
        } catch (IOException e) {
            android.util.Log.d("skythinking", e.getMessage());
        }
        if (drawable == null) {
            //Log.d("skythinking", "null drawable");
        } else {
            //Log.d("skythinking", "not null drawable");
        }

        return drawable;
    }

    public static  boolean isImageSuffix(String url) {
        return url.contains(".png?")
                || url.contains(".PNG?")
                || url.contains(".jpg?")
                || url.contains(".JPG?")
                || url.contains(".jpeg?")
                || url.contains(".JPEG?");
    }
}
