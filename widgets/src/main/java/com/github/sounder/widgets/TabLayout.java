package com.github.sounder.widgets;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TabLayout extends HorizontalScrollView implements ViewPager.OnPageChangeListener,
        ViewPager.OnAdapterChangeListener {

    public static final String TAG = "TabLayout";
    /**
     * 指示器默认滚动时间
     * The duration of the animation of the indicator
     */
    public static final int DURATION_INDICATOR = 300;
    /**
     * Tab可滑动布局
     * Scrollable layout
     */
    public static final int FLAG_SCROLL = 0;
    /**
     * 固定布局
     * Fixed Layout
     */
    public static final int FLAG_FIXED = 1;
    /**
     * 指示器宽度和所在tab的宽度保持一致
     * Indicator width is same as the selected child view
     */
    public static final int MATCH_VIEW = 0;
    /**
     * 指示器宽度跟所在tab的文本宽度保持一致
     * Indicator width is same as the content width of the
     * selected child view
     */
    public static final int MATCH_CONTENT = 1;
    /**
     * 指示器固定宽度，设置该值时需要指定{@link #mIndicatorWidth}
     * Indicator width is a fixed value
     */
    public static final int FIXED = 2;
    private Context mContext;

    private LinearLayout mSlidingIndicator;
    private ImageView mIndicatorView;

    /**
     * tab之间的间距
     * Margin of two child view
     */
    private int mItemSpace;
    /**
     * tab本身的水平padding
     */
    private int mItemPadding;
    private int mScrollFlag = FLAG_SCROLL;
    private int mDefTextStyleAttr;
    private Drawable mIndicatorDrawable;
    private int mIndicatorHeight = 4;
    private int mIndicatorMode = MATCH_CONTENT;
    private int mIndicatorWidth;
    private int mIndicatorLeft;
    private int mIndicatorRight;

    private int mSelectPosition;
    private AnimatorSet mMoveAnimatorSet;

    private ViewPager mViewPager;
    private OnTabChangeListener mTabChangeListener;
    private OnTabClickListener mTabClickListener;

    public TabLayout(Context context) {
        super(context);
        initView(context);
    }

    public TabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs, defStyleAttr);
        initView(context);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (mIndicatorView != null) {
            mIndicatorView.layout(mIndicatorLeft,
                    mIndicatorView.getTop(),
                    mIndicatorRight,
                    mIndicatorView.getBottom());
        }
    }

    private void initAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        final TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TabLayout, defStyleAttr, 0);
        int n = ta.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = ta.getIndex(i);
            if (R.styleable.TabLayout_tabTextStyle == attr) {
                mDefTextStyleAttr = ta.getResourceId(attr, android.R.attr.textAppearanceMedium);
            } else if (R.styleable.TabLayout_tabIndicatorColor == attr) {
                if (mIndicatorDrawable == null) {
                    int color = ta.getColor(attr, Color.BLACK);
                    mIndicatorDrawable = new ColorDrawable(color);
                }
            } else if (R.styleable.TabLayout_tabIndicatorHeight == attr) {
                mIndicatorHeight = ta.getDimensionPixelSize(attr, 0);
            } else if (R.styleable.TabLayout_tabItemPadding == attr) {
                mItemPadding = ta.getDimensionPixelOffset(attr, 0);
            } else if (R.styleable.TabLayout_tabSpace == attr) {
                mItemSpace = ta.getDimensionPixelOffset(attr, 0);
            } else if (R.styleable.TabLayout_tabScrollFlag == attr) {
                mScrollFlag = ta.getInt(attr, FLAG_SCROLL);
            } else if (R.styleable.TabLayout_tabIndicatorMode == attr) {
                mIndicatorMode = ta.getInt(attr, MATCH_CONTENT);
            } else if (R.styleable.TabLayout_tabIndicatorDrawable == attr) {
                mIndicatorDrawable = ta.getDrawable(attr);
            } else if (R.styleable.TabLayout_tabIndicatorWidth == attr) {
                mIndicatorWidth = ta.getDimensionPixelOffset(attr, 10);
            }
        }
        ta.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    private void initView(Context context) {
        mContext = context;
        FrameLayout root = new FrameLayout(context);
        mSlidingIndicator = new LinearLayout(context);
        mSlidingIndicator.setOrientation(LinearLayout.HORIZONTAL);
        LayoutParams p1;

        int width = getResources().getDisplayMetrics().widthPixels;
        if (mScrollFlag == FLAG_FIXED) {
            p1 = new LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT);
        } else {
            p1 = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        root.setLayoutParams(p1);

        LayoutParams p2;
        if (mScrollFlag == FLAG_FIXED) {
            p2 = new LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT);
        } else {
            p2 = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        mSlidingIndicator.setLayoutParams(p2);
        root.addView(mSlidingIndicator);

        mIndicatorView = new ImageView(context);
        LayoutParams p3 = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, mIndicatorHeight);
        p3.gravity = Gravity.BOTTOM;
        mIndicatorView.setLayoutParams(p3);
        mIndicatorView.setBackground(mIndicatorDrawable);
        root.addView(mIndicatorView);

        addView(root);
        if (isInEditMode()) {
            addTab("Tab1");
            addTab("Tab2");
            addTab("Tab3");
            mIndicatorView.getLayoutParams().width = 50;
            setSelectTab(0);
        }
    }

    public void addTab(CharSequence text) {
        addTab(text, mSlidingIndicator.getChildCount());
    }

    public void addTab(CharSequence text, final int position) {
        final TextView textView = new TextView(mContext, null, 0, mDefTextStyleAttr);
        textView.setGravity(Gravity.CENTER);
        textView.setText(text);

        LinearLayout.LayoutParams params;
        if (mScrollFlag == FLAG_FIXED) {
            params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
            params.weight = 1f;
        } else {
            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        if (mSlidingIndicator.getChildCount() != 0) {
            params.leftMargin = mItemSpace;
        }
        textView.setLayoutParams(params);
        textView.setPadding(mItemPadding, 0, mItemPadding, 0);
        textView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = (int) v.getTag();
                int childCount = mSlidingIndicator.getChildCount();
                int index = pos;
                if (pos == childCount) {
                    index = pos - 1;
                }
                if (mTabClickListener != null && mTabClickListener.onTabClick(((TextView) v), pos)) {
                    return;
                }
                for (int i = 0; i < childCount; i++) {
                    View view = mSlidingIndicator.getChildAt(i);
                    if (view.getTag().equals(pos)) {
                        view.setSelected(true);
                    } else {
                        view.setSelected(false);
                    }
                }
                if (mViewPager != null) {
                    mViewPager.setCurrentItem(index);
                } else {
                    setSelectTab(index, true);
                }
            }
        });
        textView.setTag(position);
        mSlidingIndicator.addView(textView, position);
    }

    private void scrollView2Center(int position) {
        View v = mSlidingIndicator.getChildAt(position);
        int scrollX = getScrollX();
        int toLeft = v.getLeft() - scrollX;
        int toRight = getMeasuredWidth() - v.getRight() + scrollX;
        smoothScrollBy((toLeft - toRight) / 2, 0);
        for (int i = 0; i < mSlidingIndicator.getChildCount(); i++) {
            View _v = mSlidingIndicator.getChildAt(i);
            _v.setSelected(_v == v);
        }
    }

    private int getViewContentOffset(int position) {
        if (position < 0) return 0;
        TextView view = (TextView) mSlidingIndicator.getChildAt(position);
        int width = view.getMeasuredWidth();
        float textWidth = view.getPaint().measureText(view.getText().toString());
        return (int) ((width - textWidth) / 2);
    }

    private int getViewContentOffset(View v) {
        TextView view = ((TextView) v);
        int width = view.getMeasuredWidth();
        float textWidth = view.getPaint().measureText(view.getText().toString());
        return (int) ((width - textWidth) / 2);
    }

    public void setSelectTab(int position, boolean anim) {
        if (position == mSelectPosition) return;
        scrollView2Center(position);
        int[] now = getLeftAndRight(position);
        if (!anim) {
            FrameLayout.LayoutParams params = (LayoutParams) mIndicatorView.getLayoutParams();
            if (mIndicatorMode == MATCH_CONTENT) {
                int offsetNow = getViewContentOffset(position);
                setIndicatorPos(now[0] + offsetNow, now[1] - offsetNow);
            } else if (mIndicatorMode == MATCH_VIEW) {
                setIndicatorPos(now[0], now[1]);
            } else {
                int dif = (now[1] - now[0] - mIndicatorWidth) / 2;
                setIndicatorPos(now[0] + dif, now[1] - dif);
            }
            params.width = mIndicatorView.getRight() - mIndicatorView.getLeft();
            params.leftMargin = mIndicatorView.getLeft();
            mIndicatorView.postInvalidate();
        } else {
            if (mMoveAnimatorSet == null) {
                mMoveAnimatorSet = new AnimatorSet();
            }
            if (mMoveAnimatorSet.isRunning()) {
                mMoveAnimatorSet.cancel();
            }
            ObjectAnimator animLeft;
            ObjectAnimator animRight;
            if (mIndicatorMode == MATCH_CONTENT) {
                int offsetNow = getViewContentOffset(position);
                animLeft = ObjectAnimator.ofInt(mIndicatorView, "left",
                        mIndicatorView.getLeft(), now[0] + mItemPadding + offsetNow);
                animRight = ObjectAnimator.ofInt(mIndicatorView, "right",
                        mIndicatorView.getRight(), now[1] - mItemPadding - offsetNow);
            } else if (mIndicatorMode == MATCH_VIEW) {
                animLeft = ObjectAnimator.ofInt(mIndicatorView, "left",
                        mIndicatorView.getLeft(), now[0]);
                animRight = ObjectAnimator.ofInt(mIndicatorView, "right",
                        mIndicatorView.getRight(), now[1]);
            } else {
                int dif = (now[1] - now[0] - mIndicatorWidth) / 2;
                animLeft = ObjectAnimator.ofInt(mIndicatorView, "left",
                        mIndicatorView.getLeft(), now[0] + dif);
                animRight = ObjectAnimator.ofInt(mIndicatorView, "right",
                        mIndicatorView.getRight(), now[1] - dif);
            }
            mMoveAnimatorSet.setDuration(DURATION_INDICATOR)
                    .playTogether(animLeft, animRight);
            mMoveAnimatorSet.start();
        }
        notify(position);
        mSelectPosition = position;
    }

    public void setSelectTab(int position) {
        setSelectTab(position, false);
    }

    /**
     * 获取指定位置view的left&right
     *
     * @return left & right as a array
     */
    public int[] getLeftAndRight(int position) {
        if (position > mSlidingIndicator.getChildCount() - 1 || position < 0) {
            return new int[]{0, 0};
        }
        View view = mSlidingIndicator.getChildAt(position);
        return new int[]{view.getLeft(), view.getRight()};
    }

    public void setupWithViewPager(ViewPager viewPager) {
        if (mViewPager != null) {
            mViewPager.removeOnPageChangeListener(this);
            mViewPager.removeOnAdapterChangeListener(this);
        }
        PagerAdapter adapter = viewPager.getAdapter();
        refreshTabs(adapter);
        mViewPager = viewPager;
        mViewPager.addOnPageChangeListener(this);
        mViewPager.addOnAdapterChangeListener(this);
    }

    private void removeAllTabs() {
        mSlidingIndicator.removeAllViews();
        mIndicatorView.getLayoutParams().width = 0;
        mIndicatorView.invalidate();
    }

    private void refreshTabs(PagerAdapter adapter) {
        if (adapter == null) {
            throw new NullPointerException("no adapter found in viewPager");
        }
        removeAllTabs();
        int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            addTab(adapter.getPageTitle(i));
        }
        mSelectPosition = -1;
        setSelectTab(0);
    }

    private int contentOffsetLeft = 0;
    private int contentOffsetRight = 0;

    @Override
    public void onPageScrolled(int i, float v, int i1) {
        View left = mSlidingIndicator.getChildAt(i);
        int offsetLeft;
        int offsetRight;
        if (v > 0) {
            View right = mSlidingIndicator.getChildAt(i + 1);
            if (mScrollFlag == FLAG_FIXED && mIndicatorMode == MATCH_CONTENT) {
                if (contentOffsetLeft == 0 || contentOffsetRight == 0) {
                    contentOffsetLeft = getViewContentOffset(left);
                    contentOffsetRight = getViewContentOffset(right);
                }
                offsetLeft = (int) ((right.getLeft() + contentOffsetRight - left.getLeft() - contentOffsetLeft) * v) + contentOffsetLeft;
                offsetRight = (int) ((right.getRight() - contentOffsetRight - left.getRight() + contentOffsetLeft) * v) - contentOffsetLeft;
            } else {
                offsetLeft = (int) ((right.getLeft() - left.getLeft()) * v);
                offsetRight = (int) ((right.getRight() - left.getRight()) * v);
            }
            int l = left.getLeft() + offsetLeft;
            int r = left.getRight() + offsetRight;
            if (mIndicatorMode == FIXED) {
                int dif = (r - l - mIndicatorWidth) / 2;
                l += dif;
                r -= dif;
            }
            setIndicatorPos(l, r);
            mIndicatorView.invalidate();
        } else {
            if (mScrollFlag == FLAG_FIXED && mIndicatorMode == MATCH_CONTENT) {
                contentOffsetLeft = getViewContentOffset(left);
                offsetLeft = left.getLeft() + contentOffsetLeft;
                offsetRight = left.getRight() - contentOffsetLeft;
            } else {
                offsetLeft = left.getLeft();
                offsetRight = left.getRight();
            }
            contentOffsetLeft = contentOffsetRight = 0;
            if (mIndicatorMode == FIXED) {
                int dif = (offsetRight - offsetLeft - mIndicatorWidth) / 2;
                offsetLeft += dif;
                offsetRight -= dif;
            }
            setIndicatorPos(offsetLeft, offsetRight);
            mIndicatorView.invalidate();
            scrollView2Center(i);
        }
    }

    private void setIndicatorPos(int left, int right) {
        mIndicatorLeft = left;
        mIndicatorRight = right;
        mIndicatorView.setLeft(left);
        mIndicatorView.setRight(right);
    }

    public void setOnTabChangeListener(OnTabChangeListener listener) {
        this.mTabChangeListener = listener;
    }

    public void setOnTabClickListener(OnTabClickListener listener) {
        this.mTabClickListener = listener;
    }

    @Override
    public void onPageSelected(int i) {
        notify(i);
        mSelectPosition = i;
    }

    private void notify(int position) {
        if (mTabChangeListener != null) {
            mTabChangeListener.onTabSelect((TextView) mSlidingIndicator.getChildAt(position), position);
            if (mSelectPosition >= 0)
                mTabChangeListener.onTabUnSelect((TextView) mSlidingIndicator.getChildAt(mSelectPosition), mSelectPosition);
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {
    }

    @Override
    public void onAdapterChanged(@NonNull ViewPager viewPager, @Nullable PagerAdapter pagerAdapter, @Nullable PagerAdapter pagerAdapter1) {
        refreshTabs(pagerAdapter1);
    }

    @Override
    protected void onDetachedFromWindow() {
        if (mMoveAnimatorSet != null && mMoveAnimatorSet.isRunning()) {
            mMoveAnimatorSet.cancel();
            mMoveAnimatorSet = null;
        }
        super.onDetachedFromWindow();
    }

    /**
     * 切换Tab触发
     * 在设置ViewPager时，先设置该监听更好
     */
    public interface OnTabChangeListener {
        void onTabSelect(TextView view, int position);

        void onTabUnSelect(TextView view, int position);
    }

    public interface OnTabClickListener {
        /**
         * 可通过此方法拦截tab的点击事件,
         *
         * @return true 事件被上层拦截
         */
        boolean onTabClick(TextView view, int position);
    }
}
