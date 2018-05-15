package com.android.taskallo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import cn.ngame.store.R;
import com.android.taskallo.core.utils.CommonUtil;
import com.android.taskallo.core.utils.TextUtil;

/**
 * 自定义圆环进度条
 * Created by zeng on 2016/8/15.
 */
public class RoundProgressBar extends View {

    public static final String TAG = RoundProgressBar.class.getSimpleName();

    /**
     * 画笔对象的引用
     */
    private Paint paint;

    /**
     * 圆环的颜色
     */
    private int roundColor;

    /**
     * 圆环进度的颜色
     */
    private int roundProgressColor;

    /**
     * 中间进度百分比的字符串的颜色
     */
    private int textColor;

    /**
     * 中间进度百分比的字符串的字体
     */
    private float textSize;

    /**
     * 圆环的宽度
     */
    private float roundWidth;

    /**
     * 最大进度
     */
    private int max;

    /**
     * 当前进度
     */
    private int progress;
    /**
     * 是否显示中间的进度
     */
    private boolean textIsDisplayable;

    /**
     * 进度的风格，实心或者空心
     */
    private int style;

    public static final int STROKE = 0;
    public static final int FILL = 1;

    private Context context;

    private String state = "";
    private String stateDetail = "";

    public RoundProgressBar(Context context) {
        this(context,null);
    }

    public RoundProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RoundProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        paint = new Paint();

        TypedArray mTypedArray = context.obtainStyledAttributes(attrs,R.styleable.RoundProgressBar);

        //获取自定义属性和默认值
        roundColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundColor, getResources().getColor(R.color.font_gray));
        roundProgressColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundProgressColor, getResources().getColor(R.color.mainColor));
        textColor = mTypedArray.getColor(R.styleable.RoundProgressBar_textColor, getResources().getColor(R.color.font_default));
        textSize = mTypedArray.getDimension(R.styleable.RoundProgressBar_textSize, 15);
        roundWidth = mTypedArray.getDimension(R.styleable.RoundProgressBar_roundWidth, 5);
        max = mTypedArray.getInteger(R.styleable.RoundProgressBar_max, 100);
        textIsDisplayable = mTypedArray.getBoolean(R.styleable.RoundProgressBar_textIsDisplayable, true);
        style = mTypedArray.getInt(R.styleable.RoundProgressBar_style, 0);

        mTypedArray.recycle();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        /** 画最外层的大圆环 */
        int centre = getWidth()/2; //获取圆心的x坐标
        int radius = (int) (centre - roundWidth/2); //圆环的半径
        paint.setColor(roundColor); //设置圆环的颜色
        paint.setStyle(Paint.Style.STROKE); //设置空心
        paint.setStrokeWidth(roundWidth); //设置圆环的宽度
        paint.setAntiAlias(true);  //消除锯齿
        canvas.drawCircle(centre, centre, radius, paint); //画出圆环

        //Log.d(TAG,"----------------->>> "+centre);

        /** 画圆环中文字 */
        int percent = (int)(((float)progress / (float)max) * 100);  //中间的进度百分比，先转换成float在进行除法运算，不然都为0
        //float textWidth = paint.measureText(percent + "%");   //测量字体宽度，我们需要根据字体的宽度设置在圆环中间
        if(textIsDisplayable && percent != 0 && style == STROKE){


            paint.setStrokeWidth(0);
            paint.setTextAlign(Paint.Align.CENTER);

            int height;
            if(!TextUtil.isEmpty(stateDetail)){
                height = centre;

                //绘制进度详情
                paint.setColor(context.getResources().getColor(R.color.font_gray));
                paint.setTextSize(CommonUtil.dip2px(context,12));
                paint.setTypeface(Typeface.DEFAULT); //设置字体
                canvas.drawText(stateDetail, centre, centre+CommonUtil.dip2px(context,20), paint);
            }else {
                height = centre + CommonUtil.dip2px(context,9);
            }

            //绘制主体进度状态
            paint.setColor(textColor);
            paint.setTextSize(textSize);
            paint.setTypeface(Typeface.DEFAULT_BOLD); //设置字体
            canvas.drawText(state, centre, height, paint); //画出进度百分比

            //canvas.drawText(percent + "%", centre - textWidth / 2, centre + textSize/2, paint); //画出进度百分比
        }


        /**
         * 画圆弧 ，画圆环的进度
         */

        //设置进度是实心还是空心
        paint.setStrokeWidth(roundWidth); //设置圆环的宽度
        paint.setColor(roundProgressColor);  //设置进度的颜色
        RectF oval = new RectF(centre - radius, centre - radius, centre
                + radius, centre + radius);  //用于定义的圆弧的形状和大小的界限

        switch (style) {
            case STROKE:{
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawArc(oval, 0, 360 * progress / max, false, paint);  //根据进度画圆弧
                break;
            }
            case FILL:{
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                if(progress !=0)
                    canvas.drawArc(oval, 0, 360 * progress / max, true, paint);  //根据进度画圆弧
                break;
            }
        }
    }

    public synchronized int getMax() {
        return max;
    }

    /**
     * 设置进度的最大值
     * @param max 进度的最大值
     */
    public synchronized void setMax(int max) {
        if(max < 0){
            throw new IllegalArgumentException("max not less than 0");
        }
        this.max = max;
    }

    /**
     * 获取进度.需要同步
     * @return 当前进度
     */
    public synchronized int getProgress() {
        return progress;
    }

    /**
     * 设置进度，此为线程安全控件，由于考虑多线的问题，需要同步
     * 刷新界面调用postInvalidate()能在非UI线程刷新
     * @param progress 当前进度
     */
    public synchronized void setProgress(int progress) {
        if(progress < 0){
            throw new IllegalArgumentException("progress not less than 0");
        }
        if(progress > max){
            progress = max;
        }
        if(progress <= max){
            this.progress = progress;
            postInvalidate();
        }

    }

    public int getCricleColor() {
        return roundColor;
    }

    public void setCricleColor(int cricleColor) {
        this.roundColor = cricleColor;
    }

    public int getCricleProgressColor() {
        return roundProgressColor;
    }

    public void setCricleProgressColor(int cricleProgressColor) {
        this.roundProgressColor = cricleProgressColor;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public float getRoundWidth() {
        return roundWidth;
    }

    public void setRoundWidth(float roundWidth) {
        this.roundWidth = roundWidth;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public void setState(String state) {
        this.state = state;
        invalidate();
    }

    public void setStateDetail(String stateDetail) {
        this.stateDetail = stateDetail;
        invalidate();
    }
}
