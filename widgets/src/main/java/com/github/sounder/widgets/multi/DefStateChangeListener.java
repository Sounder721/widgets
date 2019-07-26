package com.github.sounder.widgets.multi;

import android.graphics.drawable.Animatable;
import android.view.View;
import android.widget.ImageView;

import com.github.sounder.widgets.R;

public class DefStateChangeListener extends OnStateChangeAdapter {

    Animatable mAnim;

    @Override
    public void onInflateLoadingView(View loadingView) {
        ImageView imageView = loadingView.findViewById(R.id.img_loading);
        if (imageView != null) {
            mAnim = (Animatable) imageView.getDrawable();
        }
    }

    @Override
    public void onLoadingViewStateChanged(boolean show) {
        if (mAnim != null) {
            if (show) mAnim.start();
            else mAnim.stop();
        }
    }
}
