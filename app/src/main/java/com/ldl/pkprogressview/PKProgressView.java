package com.ldl.pkprogressview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.RequiresApi;

/**
 * Created by 12 on 2018/7/23.
 */

public class PKProgressView extends View {
    private static final String TAG = "PKProgressView";

    private static final int PROGRESSBAR_WIDTH = 550;
    /*进度条样式*/
    public static final int SOLID = 1;//实心
    public static final int SOLID_AND_FRAME = 2;//实心加边框
    public static final int HOLLOW = 3;//空心

    /***/
    private int mStartAngle_LeftArc = 90;//左边半圆或弧度的初始角度
    private int mStartAngle_RightArc_One = -90;//右边半圆或弧度上面的那部分的初始角度
    private int mStartAngle_RightArc_Two = 0;//右边半圆或弧度下面的那部分的初始角度


    public void setmProgressBankgroundColor(int mProgressBankgroundColor) {
        this.mProgressBankgroundColor = mProgressBankgroundColor;
    }

    private int mProgressBankgroundColor = Color.parseColor("#FFD94F5A");
    private int mFrameColor = Color.parseColor("#000000");

    public void setmProgressColor(int mProgressColor) {
        this.mProgressColor = mProgressColor;
    }

    private int mProgressColor = Color.parseColor("#FF518CD7");
    private float mProgress;//当前的进度
    private int mProgressBarFrameHeight = 0;
    private int mRectRoundRadius = this.dp2px(3);
    private int mProgressBarBankgroundStyle = SOLID;//默认实心
    private int mProgressBarHeight = this.dp2px(10);//进度条总高度
    private int mProgressBarWidth = PROGRESSBAR_WIDTH;//进度条总长度
    //
    private boolean mHasCoordinate = false;//是否绘制参考坐标系

    /***/
    private Paint mPaint;
    private int mViewWidth, mViewHeight;
    private int mScreenWidth, mScreenHeight;
    private boolean mHasBankground = true;//是否绘制背景
    private float mProgressMaxWidth;//进度最大宽度
    private float mProgressLoadingWidth;//当前进度条宽度

    private float mOneArcProgress;//半圆占用的最大的进度
    private float mRectWidth;//进度条中间矩形的最大宽度
    private int mProgressBarWidthWithoutFrame;
    private int mProgressBarHeightWithoutFrame;

    private float mRadius;//进度条内左右两个半圆的最大半径

    private int mDuration = 5 * 1000;//动画执行时间
    private Context mContext;

    public PKProgressView(Context context) {
        this(context, null);
        init();
    }

    public PKProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init();
    }

    public PKProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        //
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        /*mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setColor(Color.GREEN);*/

        mScreenWidth = getScreenWidth(mContext);
        mScreenHeight = getScreenHeight(mContext);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // mWidth =getMeasuredWidth();
        //mHeight = getMeasuredHeight();
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width = resolveSize(widthSize, widthMeasureSpec);
        int height = resolveSize(heightSize, heightMeasureSpec);


        //
        mViewWidth = width;
        mViewHeight = height > width ? width : height;
        //
        mProgressBarWidth = mViewWidth;
        mProgressBarHeight = mViewHeight;
        setMeasuredDimension(width, mViewHeight);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mHasCoordinate) {
            drawCoordinate(canvas);
            drawCoordinateOnCenter(canvas);
        }
        //
        switch (mProgressBarBankgroundStyle) {
            case SOLID:
                mProgressBarFrameHeight = 0;
//                mProgressBarFrameHeight = this.dp2px(3);
                break;
            case SOLID_AND_FRAME:
                mProgressBarFrameHeight = this.dp2px(1);
                break;
            case HOLLOW:
                //mProgressBarFrameHeight=0;
                break;
        }
        /**
         * 处理笔触的大小
         */
        mProgressBarWidthWithoutFrame = mProgressBarWidth - mProgressBarFrameHeight * 2;//不包含边框的进度条宽
        mProgressBarHeightWithoutFrame = mProgressBarHeight - mProgressBarFrameHeight * 2;//不包含边框的进度条高
        //
        mRadius = mProgressBarHeightWithoutFrame / 2;

        mRectRoundRadius = mProgressBarHeight / 2;

        //
        mRectWidth = mProgressBarWidthWithoutFrame - 2 * mRadius;//矩形的宽度
        mProgressMaxWidth = mProgressBarWidthWithoutFrame;
        mOneArcProgress = mRadius / mProgressBarWidth;//半圆最大的 进度
        if (mHasBankground) {
            drawBankground(canvas);
        }


        mProgressLoadingWidth = mProgressMaxWidth * mProgress;

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mProgressColor);
        //

        //canvas.translate(-(mRadiusMax-mArcLeftWidth),0);//向左偏移半圆剩余的宽  保证左边对齐

//        if (mProgress <= 0) {
//            return;
//        }
//        if (mProgress <= 0.02) {
//            drawLeftArc(canvas);
//
//        } else if (mProgress > 0.02 && mProgress < 0.98) {
//            drawLeftArc(canvas);
//            drawCenterRect(canvas);
//
//        } else {
//            drawLeftArc(canvas);
//            drawCenterRect(canvas);
//            drawRightArc(canvas);
//        }
        // Log.d(TAG, "onDraw: mProgressNow:"+mProgressNow);
        drawProgress(canvas);

    }

    /**
     * 画默认坐标系
     *
     * @param canvas
     */
    private void drawCoordinate(Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(6f);
        canvas.drawLine(0, 0, mViewWidth, 0, mPaint);//X 轴
        canvas.drawLine(0, 0, 0, mViewHeight, mPaint);//y 轴
    }

    /**
     * 画居中坐标系
     *
     * @param canvas
     */
    private void drawCoordinateOnCenter(Canvas canvas) {
        canvas.save();
        canvas.translate(mViewWidth / 2, mViewHeight / 2);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setColor(Color.YELLOW);
        mPaint.setStrokeWidth(6f);
        canvas.drawLine(-mViewWidth / 2, 0, mViewWidth / 2, 0, mPaint);//X 轴
        canvas.drawLine(0, -mViewHeight / 2, 0, mViewHeight / 2, mPaint);//y 轴
        canvas.restore();
    }

    /**
     * 画边框背景
     */
    private void drawBankground(Canvas canvas) {
        //边框背景
        mPaint.setColor(mProgressBankgroundColor);
        mPaint.setStrokeWidth(mProgressBarFrameHeight);

        //移动到第一个半圆圆心
        canvas.translate(mRadius + mProgressBarFrameHeight, mProgressBarHeight / 2);
        switch (mProgressBarBankgroundStyle) {
            case SOLID:
                //进度条实心
                mPaint.setStyle(Paint.Style.FILL);
                float radiusTemp = mRadius + mProgressBarFrameHeight;
                RectF rectF_Center = new RectF(-radiusTemp, -radiusTemp, mProgressBarWidth - radiusTemp, radiusTemp);

                mPaint.setColor(mFrameColor);
                canvas.drawRoundRect(rectF_Center, mRectRoundRadius, mRectRoundRadius, mPaint);

                rectF_Center.inset(mProgressBarFrameHeight, mProgressBarFrameHeight);
                mPaint.setColor(mProgressBankgroundColor);
                canvas.drawRoundRect(rectF_Center, mRectRoundRadius, mRectRoundRadius, mPaint);


                break;
            case SOLID_AND_FRAME:
                //进度条实心加边框
                mPaint.setStyle(Paint.Style.FILL);//FILL_AND_STROKE画时候  笔触右半边会和内容重合 差一半笔触!!!
                radiusTemp = mRadius + mProgressBarFrameHeight;
                rectF_Center = new RectF(-radiusTemp, -radiusTemp, mProgressBarWidth - radiusTemp, radiusTemp);
                mPaint.setColor(mFrameColor);
                canvas.drawRoundRect(rectF_Center, mRectRoundRadius, mRectRoundRadius, mPaint);

                rectF_Center.inset(mRectRoundRadius / 2, mRectRoundRadius / 2);
                mPaint.setColor(mProgressBankgroundColor);
                canvas.drawRoundRect(rectF_Center, mRectRoundRadius, mRectRoundRadius, mPaint);
                break;
            case HOLLOW:
                //进度条空心
                mPaint.setStyle(Paint.Style.STROKE);//STROKE画时候  笔触右半边会和内容重合 差一半笔触!!!
                //
                //画 左边半圆环
                float newRadius = mRadius + mProgressBarFrameHeight / 2;
                RectF rectF_Left_Right = new RectF(-newRadius, -newRadius, newRadius, newRadius);
                canvas.drawArc(rectF_Left_Right, mStartAngle_LeftArc, 180, false, mPaint);
                canvas.save();
                canvas.translate(mRectWidth, 0);
                //画 右边半圆环
                canvas.drawArc(rectF_Left_Right, -mStartAngle_LeftArc, 180, false, mPaint);
                canvas.restore();
                //画 两条平行线
                canvas.drawLine(0, -newRadius, mRectWidth, -newRadius, mPaint);
                canvas.drawLine(0, newRadius, mRectWidth, newRadius, mPaint);
                break;
        }
    }

    /**
     * 画半圆左侧的任意部分
     */
    private void drawLeftArc(Canvas canvas) {

        float[] radiusArray = {mRadius, mRadius, 0f, 0f, 0f, 0f, mRadius, mRadius};
        mPaint.setColor(mProgressColor);
        RectF rectF = new RectF(-mRadius, -mRadius, 0, mRadius);
        Path path = new Path();
        path.addRoundRect(rectF, radiusArray, Path.Direction.CW);
//        canvas.drawRoundRect(rectF, mRectRoundRadius, mRectRoundRadius, mPaint);
        canvas.drawPath(path, mPaint);
    }

    /**
     * 画中间矩形部分
     */
    private void drawCenterRect(Canvas canvas) {
        float rectAndLeftArcMaxWidth = mProgressMaxWidth - mRadius;//所有进度条减去右边 就是左边和矩形

        float progressBarWidthNowTemp = mProgressLoadingWidth < rectAndLeftArcMaxWidth ? mProgressLoadingWidth : rectAndLeftArcMaxWidth;
        float rectWidth = progressBarWidthNowTemp - mRadius;//当前进度条减去左边半圆

        rectWidth = rectWidth < rectAndLeftArcMaxWidth ? rectWidth : rectAndLeftArcMaxWidth;


        Path path1 = new Path();
        path1.moveTo(0, mRadius);
        path1.lineTo(0, -mRadius);
//        //中间带斜边
//        path1.lineTo(rectWidth + 10, -mRadius);
//        path1.lineTo(rectWidth - 10, mRadius);
        //中间没有斜边，竖直方向
        path1.lineTo(rectWidth, -mRadius);
        path1.lineTo(rectWidth, mRadius);
        path1.close();
        canvas.drawPath(path1, mPaint);

    }

    /**
     * 画半圆右侧的任意部分  分2个圆弧  1个三角形  demo图 见https://code.aliyun.com/hi31588535/outside_chain/raw/master/blog_custom_view_show_pic2.png
     */
    private void drawRightArc(Canvas canvas) {

        float radiusTemp = mRadius + mProgressBarFrameHeight;
        RectF rectF_Center = new RectF(-radiusTemp, -radiusTemp, mProgressBarWidth - radiusTemp, radiusTemp);

//        mPaint.setColor(mFrameColor);
//        canvas.drawRoundRect(rectF_Center, mRectRoundRadius, mRectRoundRadius, mPaint);

        rectF_Center.inset(mRadius / 2, mRadius / 2);
        mPaint.setColor(mProgressColor);
        canvas.drawRoundRect(rectF_Center, mRadius, mRadius, mPaint);

    }


    /**
     * 绘制进度
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void drawProgress(Canvas canvas) {
        float[] leftRadiusArray = {mRadius, mRadius, 0f, 0f, 0f, 0f, mRadius, mRadius};
        float[] rightRadiusArray = {0f, 0f, mRadius, mRadius, mRadius, mRadius, 0f, 0f};


        boolean userAllRadiousArray = mProgress > (mProgressBarWidthWithoutFrame - mRadius) / mProgressBarWidthWithoutFrame;
        float right = mProgress * mProgressBarWidthWithoutFrame - mRadius;

        //第一种方法
        if (userAllRadiousArray) {
            //当进度大于右边的半径的时候，更换底部颜色
            mPaint.setColor(mProgressColor);
            canvas.drawRoundRect(new RectF(-mRadius, -mRadius, mProgressBarWidthWithoutFrame - mRadius, mRadius), mRadius, mRadius, mPaint);

            mPaint.setColor(mProgressBankgroundColor);
            RectF rightRectF = new RectF(right, -mRadius, mProgressBarWidthWithoutFrame - mRadius, mRadius);
            Path rightPath = new Path();
            rightPath.addRoundRect(rightRectF, rightRadiusArray, Path.Direction.CW);
            canvas.drawPath(rightPath, mPaint);

        } else {
            mPaint.setColor(mProgressColor);
            RectF rectF = new RectF(-mRadius, -mRadius, right, mRadius);
            Path path = new Path();
            path.offset(mRadius, 0);
            path.addRoundRect(rectF, leftRadiusArray, Path.Direction.CW);
            path.offset(mRadius/2.5f, 0, path);
//            path.addRoundRect(-mRadius, -mRadius, right, mRadius, leftRadiusArray, Path.Direction.CW);
//            canvas.drawRoundRect(rectF, mRectRoundRadius, mRectRoundRadius, mPaint);
            canvas.drawPath(path, mPaint);
        }


//        if (userAllRadiousArray) {
//            mPaint.setXfermode(null);
//            mPaint.setColor(Color.MAGENTA);
//            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
//            RectF rightRect = new RectF(mProgressBarWidthWithoutFrame - 2 * mRadius, -mRadius, mProgressBarWidthWithoutFrame - mRadius, mRadius);
//            Path rightPath = new Path();
//            rightPath.addRoundRect(rightRect, rightRadiusArray, Path.Direction.CW);
//            canvas.drawPath(rightPath, mPaint);
//        }
//        mPaint.setXfermode(null);
    }


    public void setProgress(float progress) {
        mProgress = progress;
        invalidate();
    }

    public void setProgressBarBankgroundStyle(int progressBarBankgroundStyle) {
        mProgressBarBankgroundStyle = progressBarBankgroundStyle;
        invalidate();
    }

    public void setProgressColor(int progressColor) {
        mProgressColor = progressColor;
        invalidate();
    }

    public void setProgressBankgroundColor(int progressBankgroundColor) {
        mProgressBankgroundColor = progressBankgroundColor;
        invalidate();
    }

    public void setProgressBarFrameHeight(int progressBarFrameHeight) {
        mProgressBarFrameHeight = progressBarFrameHeight;
        invalidate();
    }

    //
    public int dp2px(int dpValue) {
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, getResources().getDisplayMetrics());
        return px;
    }

    //获取屏幕的宽度
    public static int getScreenWidth(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        float density = displayMetrics.density;
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        return width;
    }

    //获取屏幕的高度
    public static int getScreenHeight(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        float density = displayMetrics.density;
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        return height;
    }
}
