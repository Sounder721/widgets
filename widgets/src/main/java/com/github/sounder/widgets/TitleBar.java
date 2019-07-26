package com.github.sounder.widgets;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

public class TitleBar extends ConstraintLayout {

    private ImageView imgBack;
    private TextView tvBack;
    private TextView tvTitle;
    private ImageView imgFunc;
    private TextView tvFunc;


    public TitleBar(Context context) {
        super(context);
    }

    public TitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
