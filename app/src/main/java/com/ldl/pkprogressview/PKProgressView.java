package com.ldl.pkprogressview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
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
    private Canvas mCanvas;

    private int[] mAnimRes = {
            R.drawable.pk_light0,
            R.drawable.pk_light1,
            R.drawable.pk_light2,
            R.drawable.pk_light3,
            R.drawable.pk_light4,
            R.drawable.pk_light5,
            R.drawable.pk_light6,
            R.drawable.pk_light7,
            R.drawable.pk_light8,
            R.drawable.pk_light9,
            R.drawable.pk_light10,
            R.drawable.pk_light11,
            R.drawable.pk_light12,
            R.drawable.pk_light13,
            R.drawable.pk_light14,
            R.drawable.pk_light15,
            R.drawable.pk_light16,
            R.drawable.pk_light17,
            R.drawable.pk_light18,
            R.drawable.pk_light19,
            R.drawable.pk_light20,
            R.drawable.pk_light21,
            R.drawable.pk_light22,
            R.drawable.pk_light23,
            R.drawable.pk_light24,
            R.drawable.pk_light25,
            R.drawable.pk_light26,
            R.drawable.pk_light27,
            R.drawable.pk_light28,
            R.drawable.pk_light29,
            R.drawable.pk_light30,
    };
    private int mAnimIndex = 0;

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

        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, 31);
        valueAnimator.setDuration(3100);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatedValue = (int) animation.getAnimatedValue();
                Log.i(TAG, "animatedValue:" + animatedValue);
                drawLight(mCanvas);
            }
        });
        valueAnimator.start();
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
        if (mCanvas == null) {
            mCanvas = canvas;
        }
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


        drawProgress(canvas);
    }

    /**
     * 绘制中间闪烁
     *
     * @param canvas
     */
    private void drawLight(Canvas canvas) {
        if (canvas != null && !canvas.isOpaque()) {
            if (mAnimIndex >= mAnimRes.length) {
                mAnimIndex = 0;
            }
            //1.获取图片
            Bitmap lightBitmap = BitmapFactory.decodeResource(mContext.getResources(), mAnimRes[mAnimIndex++]);


            //2.绘制图片
            float currentDistance = mProgress * mProgressBarWidthWithoutFrame - mRadius;
            int bitmapWidth = lightBitmap.getWidth();
            int bitmapHeight = lightBitmap.getHeight();
            Rect srcRect = new Rect(0, 0, bitmapWidth, bitmapHeight);

            int desWidth = this.dp2px(175);
            int desHeight = this.dp2px(20);
            RectF desRectF = new RectF(currentDistance - desWidth / 2, -desHeight / 2, currentDistance + desWidth / 2, desHeight / 2);

            canvas.drawBitmap(lightBitmap, srcRect, desRectF, mPaint);

            //3.释放图片
            lightBitmap.recycle();
        }
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
     * 绘制进度
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void drawProgress(Canvas canvas) {
        float[] leftRadiusArray = {mRadius, mRadius, 0f, 0f, 0f, 0f, mRadius, mRadius};
        float[] rightRadiusArray = {0f, 0f, mRadius, mRadius, mRadius, mRadius, 0f, 0f};
        boolean userAllRadiousArray = mProgress > (mProgressBarWidthWithoutFrame - mRadius) / mProgressBarWidthWithoutFrame;
        boolean userLeftRadiousArray = mProgress < mRadius / mProgressBarWidthWithoutFrame;

        float currentDistance = mProgress * mProgressBarWidthWithoutFrame - mRadius;

        if (userLeftRadiousArray) {
            //当进度小于左边的半径的时候
            mPaint.setColor(mProgressColor);

            RectF rectF = new RectF(-mRadius, -mRadius, mRadius, mRadius);
            float startAngle = (float) (180 - Math.asin((mRadius + currentDistance) / mRadius) * 180 / Math.PI);
            float sweepAngle = (float) (180 - (90 - Math.asin((mRadius + currentDistance) / mRadius) * 180 / Math.PI) * 2);
            canvas.drawArc(rectF, startAngle, sweepAngle, false, mPaint);
        } else if (userAllRadiousArray) {
            //当进度大于右边的半径
            mPaint.setColor(mProgressColor);
            RectF rectF = new RectF(-mRadius, -mRadius, mProgressBarWidthWithoutFrame - mRadius, mRadius);
            Path path = new Path();
            path.addRoundRect(rectF, mRadius, mRadius, Path.Direction.CW);
            canvas.drawPath(path, mPaint);
            //绘制右边部分半圆
            mPaint.setColor(mProgressBankgroundColor);
            rectF.left = mProgressBarWidthWithoutFrame - mRadius * 3;
            rectF.top = -mRadius;
            rectF.right = mProgressBarWidthWithoutFrame - mRadius;
            rectF.bottom = mRadius;

            float startAngle = (float) (360 - Math.acos((2 * mRadius + currentDistance - mProgressBarWidthWithoutFrame) / mRadius) * 180 / Math.PI);
            float sweepAngle = (float) ((Math.acos((2 * mRadius + currentDistance - mProgressBarWidthWithoutFrame) / mRadius) * 180 / Math.PI) * 2);
            canvas.drawArc(rectF, startAngle, sweepAngle, false, mPaint);
        } else {
            mPaint.setColor(mProgressColor);
            RectF rectF = new RectF(-mRadius, -mRadius, currentDistance, mRadius);
            Path path = new Path();
            path.addRoundRect(rectF, leftRadiusArray, Path.Direction.CW);
            canvas.drawPath(path, mPaint);
        }
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
