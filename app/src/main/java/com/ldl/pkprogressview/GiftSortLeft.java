package com.ldl.pkprogressview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;

public class GiftSortLeft extends View {

    private Paint mPaint;

    public GiftSortLeft(Context context) {
        super(context);
    }

    public GiftSortLeft(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GiftSortLeft(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int sc = canvas.saveLayer(0, 0, canvas.getWidth(), canvas.getHeight(), null, Canvas.ALL_SAVE_FLAG);
        drawDst(canvas, mPaint);

        //设置xfermode
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        drawSrc(canvas, mPaint);
        //还原
        mPaint.setXfermode(null);
        canvas.restoreToCount(sc);
    }

    private void drawDst(Canvas canvas, Paint p) {
        //画圆角背景
        p.setColor(Color.parseColor("#E6302A28"));
        RectF rectF = new RectF(0, 0, getWidth(), getHeight());
        canvas.drawRoundRect(rectF, 0, 0, p);
    }

    private void drawSrc(Canvas canvas, Paint p) {

        float[] rightRadiusArray = {0f, 0f, 0, 0, dp2px(16), dp2px(16), 0f, 0f};
        //画中心镂空
        p.setColor(0xFF66AAFF);
        RectF rectF = new RectF(0, 0, getWidth(), getHeight());

        Path path = new Path();
        path.addRoundRect(rectF, rightRadiusArray, Path.Direction.CW);
        canvas.drawPath(path, mPaint);
    }

    public int dp2px(int dpValue) {
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, getResources().getDisplayMetrics());
        return px;
    }
}
