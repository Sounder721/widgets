package com.example.widgets_sample;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MyFragment extends Fragment {

    private TextView mTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTextView = view.findViewById(R.id.text);
        text();
    }


    private void text() {
        Context context = getContext();
        SpannableString ss = new SpannableString("安居老师对符合你垃圾卡士大夫；偶来潍坊");
        ss.setSpan(new BackgroundSpan(
                ContextCompat.getDrawable(context, R.drawable.bg_circle),
                Color.YELLOW,
                mTextView.getTextSize() * 0.5f,
                25
        ), 0, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTextView.setText(ss);
    }
}
