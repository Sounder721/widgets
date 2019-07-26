package com.github.sounder.widgets.multi;

import android.view.View;

public interface OnStateChangeListener {

    void onInflateLoadingView(View loadingView);

    void onInflateErrorView(View errorView);

    void onInflateEmptyView(View emptyView);

    void onInflateNetworkView(View networkView);

    void onContentViewStateChanged(boolean show);

    void onLoadingViewStateChanged(boolean show);

    void onEmptyViewStateChanged(boolean show);

    void onErrorViewStateChanged(boolean show);

    void onNetworkViewStateChanged(boolean show);
}
