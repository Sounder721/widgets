package com.example.widgets_sample;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.style.ReplacementSpan;

public class BackgroundSpan extends ReplacementSpan {

    private Drawable mBackgroundDrawable;
    private int mTextColor;
    private float mTextSize;
    private int mHorizontalMargin;

    public BackgroundSpan(Drawable drawable, int colorInt, float textSize, int horizontalMargin) {
        this.mBackgroundDrawable = drawable;
        this.mTextColor = colorInt;
        this.mTextSize = textSize;
        this.mHorizontalMargin = horizontalMargin;
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        return (int) (paint.measureText(text, start, end) + mHorizontalMargin * 2);
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        mBackgroundDrawable.setBounds(((int) x), top, (int) x + getSize(paint, text, start, end, paint.getFontMetricsInt()), bottom);
        mBackgroundDrawable.draw(canvas);
        paint.setTextSize(mTextSize);
        paint.setColor(mTextColor);
        paint.setTextAlign(Paint.Align.CENTER);
        Rect rect = mBackgroundDrawable.getBounds();
        int _x = (rect.right + rect.left) / 2;
        Paint.FontMetrics fm = paint.getFontMetrics();
        float baseline = (rect.bottom + rect.top) / 2 - (fm.bottom - fm.top) / 2 - fm.top;
        canvas.drawText(text, start, end, _x, baseline, paint);
    }
}
