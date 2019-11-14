package com.github.sounder.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 显示静态文本
 */
public class Label extends View {

    public static final int NORMAL = 0;
    public static final int FAKE_BOLD = 1;
    public static final int BOLD = 2;
    public static final int ITALIC = 4;
    public static final int FAKE_BOLD_ITALIC = 8;
    public static final int BOLD_ITALIC = 16;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({NORMAL, FAKE_BOLD, BOLD, ITALIC, FAKE_BOLD_ITALIC, BOLD_ITALIC})
    private @interface Style {
    }

    private TextPaint mPaint;
    private Layout mLayout;
    private CharSequence mText;

    private float mSpacingMulti;
    @Style
    private int mStyle;

    public Label(Context context) {
        super(context);
        init();
    }

    public Label(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Label(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        final TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.Label, defStyleAttr, 0);
        mSpacingMulti = ta.getFloat(R.styleable.Label_spacingMulti, mSpacingMulti);
        mText = ta.getText(R.styleable.Label_text);
        mPaint.setColor(ta.getColor(R.styleable.Label_textColor, ContextCompat.getColor(context, R.color.colorAccent)));
        mPaint.setTextSize(ta.getDimension(R.styleable.Label_textSize, 14 * getResources().getDisplayMetrics().density));
        mStyle = ta.getInt(R.styleable.Label_textStyle, NORMAL);
        updateStyle();
        ta.recycle();
    }

    private void init() {
        mPaint = new TextPaint();
        mPaint.setAntiAlias(true);
        if (isInEditMode()) {
            mText = "Label";
        }
        mSpacingMulti = 1.0f;
    }

    private void updateStyle() {
        switch (mStyle) {
            case NORMAL:
                mPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
                mPaint.setFakeBoldText(false);
                break;
            case FAKE_BOLD:
                mPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
                mPaint.setFakeBoldText(true);
                break;
            case BOLD:
                mPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                mPaint.setFakeBoldText(false);
                break;
            case ITALIC:
                mPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
                mPaint.setFakeBoldText(false);
                break;
            case FAKE_BOLD_ITALIC:
                mPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
                mPaint.setFakeBoldText(true);
                break;
            case BOLD_ITALIC:
                mPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD_ITALIC));
                mPaint.setFakeBoldText(false);
                break;
        }
    }

    public void setStyle(int style) {
        mStyle = style;
        updateStyle();
        invalidate();
    }

    @Style
    public int getStyle() {
        return mStyle;
    }

    public CharSequence getText() {
        return mText;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.translate(getPaddingLeft(), getPaddingTop());
        mLayout.draw(canvas);
        canvas.restore();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = measureWidth(widthMeasureSpec);
        ensureLayout(width);
        int height = measureHeight(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    private int measureWidth(int widthMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int width;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            if (mText != null) {
                int w = (int) getMaxLineWidth() + getPaddingLeft() + getPaddingRight();
                if (widthMode == MeasureSpec.AT_MOST) {
                    width = Math.min(widthSize, w);
                } else {
                    width = w;
                }
            } else {
                width = 0;
            }
        }

        return width;
    }

    private int measureHeight(int heightMeasureSpec) {
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int height;
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            return mLayout.getHeight() + getPaddingBottom() + getPaddingTop();
        }
        return height;
    }

    public void setText(CharSequence text) {
        mText = text;
        requestLayout();
        invalidate();
    }

    public void setTextColor(@ColorInt int color) {
        mPaint.setColor(color);
        invalidate();
    }

    public void setTextColorRes(@ColorRes int colorRes) {
        mPaint.setColor(ContextCompat.getColor(getContext(), colorRes));
        invalidate();
    }

    public void setTextSize(float textSize) {
        mPaint.setTextSize(textSize);
        invalidate();
    }

    public void setTextSizeInSp(int sp) {
        setTextSize(sp * getResources().getDisplayMetrics().density);
    }

    private void ensureLayout(int width) {
        if (mText == null) {
            mText = "";
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mLayout = StaticLayout.Builder.obtain(
                    mText, 0, mText.length(), mPaint, width).build();
        } else {
            mLayout = new StaticLayout(mText, mPaint, width, Layout.Alignment.ALIGN_NORMAL, mSpacingMulti, 0, false);
        }
    }

    /**
     * 如果当前文本有换行，返回最大行宽
     */
    private float getMaxLineWidth() {
        if (mText == null) return 0f;
        String[] lines = mText.toString().split("[\n\r]+");
        float max = 0f;
        for (String s : lines) {
            max = Math.max(mPaint.measureText(s), max);
        }
        return max;
    }
}
