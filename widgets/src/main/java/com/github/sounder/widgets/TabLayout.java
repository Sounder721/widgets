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
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TabLayout extends HorizontalScrollView implements ViewPager.OnPageChangeListener,
        ViewPager.OnAdapterChangeListener {

    public static final String TAG = "TabLayout";
    public static final int DURATION_INDICATOR = 300;
    /**
     * 可滑动布局
     */
    public static final int FLAG_SCROLL = 0;
    /**
     * 固定布局
     */
    public static final int FLAG_FIXED = 1;
    /**
     * Indicator width is same as the selected child view
     */
    public static final int MATCH_VIEW = 0;
    /**
     * Indicator width is same as the content width of the
     * selected child view
     */
    public static final int MATCH_CONTENT = 1;
    /**
     * Indicator width is a fixed value
     */
    public static final int FIXED = 2;
    private Context mContext;

    private LinearLayout mSlidingIndicator;
    private ImageView mIndicatorView;

    private int mItemSpace;
    private int mItemPadding;
    private int mScrollFlag = FLAG_SCROLL;
    private int mDefTextStyleAttr;
    private Drawable mIndicatorDrawable;
    private int mIndicatorHeight = 4;
    private int mIndicatorWidth = MATCH_CONTENT;
    private int mIndicatorActualW;

    private int mSelectPosition;
    private AnimatorSet mMoveAnimatorSet;

    private ViewPager mViewPager;
    private OnTabChangeListener mTabChangeListener;

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
            } else if (R.styleable.TabLayout_tabIndicatorWidth == attr) {
                mIndicatorWidth = ta.getInt(attr, MATCH_CONTENT);
            } else if (R.styleable.TabLayout_tabIndicatorDrawable == attr) {
                mIndicatorDrawable = ta.getDrawable(attr);
            } else if (R.styleable.TabLayout_tabIndicatorWidthInDp == attr) {
                mIndicatorActualW = ta.getDimensionPixelOffset(attr, 10);
            }
        }
        ta.recycle();
    }

    private void initView(Context context) {
        mContext = context;
        FrameLayout root = new FrameLayout(context);
        mSlidingIndicator = new LinearLayout(context);
        mSlidingIndicator.setOrientation(LinearLayout.HORIZONTAL);
        LayoutParams p1;
        if (mScrollFlag == FLAG_FIXED) {
            p1 = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        } else {
            p1 = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        root.setLayoutParams(p1);

        LayoutParams p2;
        if (mScrollFlag == FLAG_FIXED) {
            p2 = new LayoutParams(getResources().getDisplayMetrics().widthPixels, ViewGroup.LayoutParams.MATCH_PARENT);
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

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        if (mSlidingIndicator.getChildCount() != 0) {
            params.leftMargin = mItemSpace;
        }
        if (mScrollFlag == FLAG_FIXED) {
            mSlidingIndicator.setWeightSum(mSlidingIndicator.getChildCount() + 1);
            params.weight = 1;
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

        if (mSlidingIndicator.getChildCount() == 1) {
            textView.setSelected(true);
            textView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mSelectPosition = -1;
                    setSelectTab(0);
                    textView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
        }
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
            if (mIndicatorWidth == MATCH_CONTENT) {
                int offsetNow = getViewContentOffset(position);
                mIndicatorView.setLeft(now[0] + offsetNow);
                mIndicatorView.setRight(now[1] - offsetNow);
            } else if (mIndicatorWidth == MATCH_VIEW) {
                mIndicatorView.setLeft(now[0]);
                mIndicatorView.setRight(now[1]);
            } else {
                int dif = (now[1] - now[0] - mIndicatorActualW) / 2;
                mIndicatorView.setLeft(now[0] + dif);
                mIndicatorView.setRight(now[1] - dif);
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
            if (mIndicatorWidth == MATCH_CONTENT) {
                int offsetNow = getViewContentOffset(position);
                animLeft = ObjectAnimator.ofInt(mIndicatorView, "left",
                        mIndicatorView.getLeft(), now[0] + mItemPadding + offsetNow);
                animRight = ObjectAnimator.ofInt(mIndicatorView, "right",
                        mIndicatorView.getRight(), now[1] - mItemPadding - offsetNow);
            } else if (mIndicatorWidth == MATCH_VIEW) {
                animLeft = ObjectAnimator.ofInt(mIndicatorView, "left",
                        mIndicatorView.getLeft(), now[0]);
                animRight = ObjectAnimator.ofInt(mIndicatorView, "right",
                        mIndicatorView.getRight(), now[1]);
            } else {
                int dif = (now[1] - now[0] - mIndicatorActualW) / 2;
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
        for (int i = 0; i < adapter.getCount(); i++) {
            addTab(adapter.getPageTitle(i));
        }
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
            if (mScrollFlag == FLAG_FIXED && mIndicatorWidth == MATCH_CONTENT) {
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
            mIndicatorView.setLeft(left.getLeft() + offsetLeft);
            mIndicatorView.setRight(left.getRight() + offsetRight);
            mIndicatorView.invalidate();
        } else {
            if (mScrollFlag == FLAG_FIXED && mIndicatorWidth == MATCH_CONTENT) {
                contentOffsetLeft = getViewContentOffset(left);
                offsetLeft = left.getLeft() + contentOffsetLeft;
                offsetRight = left.getRight() - contentOffsetLeft;
            } else {
                offsetLeft = left.getLeft();
                offsetRight = left.getRight();
            }
            contentOffsetLeft = contentOffsetRight = 0;
            mIndicatorView.setLeft(offsetLeft);
            mIndicatorView.setRight(offsetRight);
            mIndicatorView.invalidate();
            scrollView2Center(i);
        }
    }

    public void setOnTabChangeListener(OnTabChangeListener listener) {
        this.mTabChangeListener = listener;
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

    public interface OnTabChangeListener {
        void onTabSelect(TextView view, int position);

        void onTabUnSelect(TextView view, int position);
    }
}
