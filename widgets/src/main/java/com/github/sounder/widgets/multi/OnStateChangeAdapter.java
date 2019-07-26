package com.github.sounder.widgets.multi;

import android.view.View;

public abstract class OnStateChangeAdapter implements OnStateChangeListener {
    @Override
    public void onInflateLoadingView(View loadingView) {

    }

    @Override
    public void onInflateErrorView(View errorView) {

    }

    @Override
    public void onInflateNetworkView(View networkView) {

    }

    @Override
    public void onInflateEmptyView(View emptyView) {

    }

    @Override
    public void onContentViewStateChanged(boolean show) {

    }

    @Override
    public void onLoadingViewStateChanged(boolean show) {

    }

    @Override
    public void onEmptyViewStateChanged(boolean show) {

    }

    @Override
    public void onErrorViewStateChanged(boolean show) {

    }

    @Override
    public void onNetworkViewStateChanged(boolean show) {

    }
}
