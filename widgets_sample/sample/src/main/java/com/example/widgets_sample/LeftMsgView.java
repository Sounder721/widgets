package com.example.widgets_sample;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

public class LeftMsgView extends android.support.v7.widget.AppCompatTextView {

    private Paint mBgPaint;
    private float mRadius;
    private Path mTriangle;
    private RectF mRectF;
    private float dp10, dp20;

    public LeftMsgView(Context context) {
        super(context);
        init();
    }

    public LeftMsgView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LeftMsgView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mBgPaint = new Paint(getPaint());
        mBgPaint.setColor(Color.WHITE);
        mRectF = new RectF();
        dp20 = getResources().getDimension(R.dimen.dp20);
        dp10 = getResources().getDimension(R.dimen.dp10);
        mRadius = dp10;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int w = getMeasuredWidth();
        int h = getMeasuredHeight();
        if (mTriangle == null) {
            mTriangle = new Path();
            mTriangle.moveTo(0, dp20);
            mTriangle.lineTo(dp10, dp20 - dp10);
            mTriangle.lineTo(dp10, dp20 + dp10);
            mTriangle.close();

        }
//        canvas.drawPath(mTriangle, mBgPaint);
        mRectF.set(dp10, 0, w, h);
        mTriangle.addRoundRect(mRectF,mRadius,mRadius, Path.Direction.CCW);
        canvas.drawPath(mTriangle, mBgPaint);
//        canvas.drawRoundRect(mRectF, mRadius, mRadius, mBgPaint);
        super.onDraw(canvas);
    }
}
