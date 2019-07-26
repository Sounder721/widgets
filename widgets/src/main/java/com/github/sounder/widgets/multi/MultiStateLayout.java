package com.github.sounder.widgets.multi;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.github.sounder.widgets.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;

public class MultiStateLayout extends FrameLayout {

    public static final int STATE_CONTENT = 0;
    public static final int STATE_LOADING = 1;
    public static final int STATE_ERROR = 2;
    public static final int STATE_EMPTY = 3;
    public static final int STATE_NETWORK = 4;
    public static final int STATE_PENDING_LOADING = 5;
    private static final long DURATION_REMOVE = 250L;
    private static final int WHAT_LOADING = 12;
    public static long DELAY_DEF = 150;

    public static int sErrorLayoutRes = R.layout.layout_def_none;
    public static int sEmptyLayoutRes = R.layout.layout_def_none;
    public static int sLoadingLayoutRes = R.layout.layout_def_none;
    public static int sNetwordLayoutRes = R.layout.layout_def_none;

    private View mErrorView;
    private View mContentView;
    private View mLoadingView;
    private View mEmptyView;
    private View mNetworkView;
    private LayoutInflater mInflater;

    private int mErrorLayoutRes;
    private int mEmptyLayoutRes;
    private int mLoadingLayoutRes;
    private int mNetworkLayoutRes;

    private int mState;
    private int mStateInXml;
    /**
     * 当前是否延迟加载loading
     */
    private boolean mPendLoading;

    private CharSequence mErrorStr;
    private CharSequence mEmptyStr;

    @SuppressLint("HandlerLeak")
    private Handler mPendHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == WHAT_LOADING && mPendLoading) {
                showLoading();
            }
        }
    };

    private OnStateChangeListener mStateChangeListener;

    public MultiStateLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttr(context, attrs, 0);
    }

    public MultiStateLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int childCount = getChildCount();
        if (childCount > 1) {
            throw new IllegalArgumentException("child count can't greater than 1");
        } else if (childCount == 1) {
            mContentView = getChildAt(0);
        } else {
            mContentView = null;
        }
        setState(mStateInXml);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        removeCurChild();
        mPendHandler.removeCallbacksAndMessages(null);
        super.onDetachedFromWindow();
    }

    private void initAttr(Context context, AttributeSet attrs, int defStyleAttr) {
        final TypedArray array = context.obtainStyledAttributes(
                attrs,
                R.styleable.MultiStateLayout,
                defStyleAttr,
                R.style.defStyleMultiStateLayout
        );
        mErrorLayoutRes = array.getResourceId(R.styleable.MultiStateLayout_errorLayout, sErrorLayoutRes);
        mEmptyLayoutRes = array.getResourceId(R.styleable.MultiStateLayout_emptyLayout, sEmptyLayoutRes);
        mLoadingLayoutRes = array.getResourceId(R.styleable.MultiStateLayout_loadLayout, sLoadingLayoutRes);
        mNetworkLayoutRes = array.getResourceId(R.styleable.MultiStateLayout_networkLayout, sNetwordLayoutRes);
        mStateInXml = array.getInt(R.styleable.MultiStateLayout_viewState, STATE_CONTENT);
        array.recycle();
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        return new SavedState(superState, mState);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        setState(savedState.state);
        super.onRestoreInstanceState(savedState.getSuperState());
    }

    @State
    public int getState() {
        return mState;
    }

    public void setState(@State int state) {
        if (mState == state) return;
        update(state);
    }

    public void showLoadingDelay() {
        showLoadingDelay(DELAY_DEF);
    }

    public void showLoadingDelay(long delay) {
        if (delay <= 0) {
            showLoading();
        } else {
            mState = STATE_PENDING_LOADING;
            mPendLoading = true;
            mPendHandler.sendEmptyMessageDelayed(WHAT_LOADING, delay);
        }
    }

    public void showLoading() {
        setState(STATE_LOADING);
    }

    public void showContent() {
        setState(STATE_CONTENT);
    }

    public void showError() {
        setState(STATE_ERROR);
    }

    public void showError(CharSequence errorStr) {
        this.mErrorStr = errorStr;
        showError();
    }

    public void showEmpty() {
        setState(STATE_EMPTY);
    }

    public void showEmpty(CharSequence emptyStr) {
        this.mEmptyStr = emptyStr;
        showEmpty();
    }

    public void showNetwork() {
        setState(STATE_NETWORK);
    }

    private void removeCurChild() {
        switch (mState) {
            case STATE_CONTENT:
                ensureStateChangeListener();
                mStateChangeListener.onContentViewStateChanged(false);
                break;
            case STATE_EMPTY:
                ensureStateChangeListener();
                mStateChangeListener.onEmptyViewStateChanged(false);
                removeChildView(mEmptyView);
                break;
            case STATE_ERROR:
                ensureStateChangeListener();
                mStateChangeListener.onErrorViewStateChanged(false);
                removeChildView(mErrorView);
                break;
            case STATE_LOADING:
                ensureStateChangeListener();
                mStateChangeListener.onLoadingViewStateChanged(false);
                removeChildView(mLoadingView);
                break;
            case STATE_NETWORK:
                ensureStateChangeListener();
                mStateChangeListener.onNetworkViewStateChanged(false);
                removeChildView(mNetworkView);
                break;
            case STATE_PENDING_LOADING:
                mPendLoading = false;
                break;
        }
    }

    private void update(@State int state) {
        removeCurChild();
        mState = state;
        switch (mState) {
            case STATE_CONTENT:
                if (mContentView != null) {
                    ensureStateChangeListener();
                    mStateChangeListener.onContentViewStateChanged(true);
                }
                break;
            case STATE_EMPTY:
                ensureEmptyView();
                addChildView(mEmptyView);
                ensureStateChangeListener();
                if (mEmptyStr != null && mEmptyView instanceof TextView) {
                    ((TextView) mEmptyView).setText(mEmptyStr);
                }
                mStateChangeListener.onEmptyViewStateChanged(true);
                break;
            case STATE_ERROR:
                ensureErrorView();
                addChildView(mErrorView);
                ensureStateChangeListener();
                if (mErrorStr != null && mErrorView instanceof TextView) {
                    ((TextView) mErrorView).setText(mErrorStr);
                }
                mStateChangeListener.onErrorViewStateChanged(true);
                break;
            case STATE_LOADING:
                ensureLoadingView();
                addChildView(mLoadingView);
                ensureStateChangeListener();
                mStateChangeListener.onLoadingViewStateChanged(true);
                break;
            case STATE_NETWORK:
                ensureNetworkView();
                addChildView(mNetworkView);
                ensureStateChangeListener();
                mStateChangeListener.onNetworkViewStateChanged(true);
                break;
        }
    }

    private void removeChildView(View view) {
        if (view != null && view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
//        if (view != null) {
//            if (view.getParent() != null) {
//                view.setAlpha(1);
//                view.animate().alpha(0).setDuration(DURATION_REMOVE)
//                        .setListener(new RemoveAnimationListener(view))
//                        .start();
//            }
//        }
    }

    private void addChildView(View view) {
        if (view != null && view.getParent() == null) {
            addView(view);
        }
    }

    private void ensureInflater() {
        if (mInflater == null)
            mInflater = LayoutInflater.from(getContext());
    }

    private void ensureEmptyView() {
        if (mEmptyView != null) return;
        ensureInflater();
        mEmptyView = mInflater.inflate(mEmptyLayoutRes, this, false);
        ensureStateChangeListener();
        mStateChangeListener.onInflateEmptyView(mEmptyView);
    }

    private void ensureErrorView() {
        if (mErrorView != null) return;
        ensureInflater();
        mErrorView = mInflater.inflate(mErrorLayoutRes, this, false);
        ensureStateChangeListener();
        mStateChangeListener.onInflateErrorView(mErrorView);
    }

    private void ensureLoadingView() {
        if (mLoadingView != null) return;
        ensureInflater();
        mLoadingView = mInflater.inflate(mLoadingLayoutRes, this, false);
        ensureStateChangeListener();
        mStateChangeListener.onInflateLoadingView(mLoadingView);
    }

    private void ensureNetworkView() {
        if (mNetworkView != null) return;
        ensureInflater();
        mNetworkView = mInflater.inflate(mNetworkLayoutRes, this, false);
        ensureStateChangeListener();
        mStateChangeListener.onInflateNetworkView(mNetworkView);
    }

    private void ensureStateChangeListener() {
        if (mStateChangeListener == null) {
            mStateChangeListener = new DefStateChangeListener();
        }
    }

    public void setOnStateChangeListener(OnStateChangeListener listener) {
        this.mStateChangeListener = listener;
    }

    private static class SavedState extends BaseSavedState {
        int state;

        private SavedState(Parcelable superState, int state) {
            super(superState);
            this.state = state;
        }

        private SavedState(Parcel in) {
            super(in);
            state = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(state);
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {

            @Override
            public SavedState createFromParcel(Parcel source) {
                return new SavedState(source);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    @IntDef({STATE_CONTENT, STATE_EMPTY, STATE_ERROR, STATE_LOADING, STATE_NETWORK})
    @Retention(RetentionPolicy.SOURCE)
    private @interface State {
    }

    private static class RemoveAnimationListener extends AnimatorListenerAdapter {

        private WeakReference<View> mView;

        private RemoveAnimationListener(View view) {
            mView = new WeakReference<>(view);
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            View view = mView.get();
            if (view != null && view.getParent() != null) {
                ((ViewGroup) view.getParent()).removeView(view);
                view.setAlpha(1f);
            }
        }
    }

    private static abstract class AnimatorListenerAdapter implements Animator.AnimatorListener {

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {

        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }
}
