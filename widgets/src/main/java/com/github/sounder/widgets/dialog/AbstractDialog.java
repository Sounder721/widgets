package com.github.sounder.widgets.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.FloatRange;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

public abstract class AbstractDialog extends DialogFragment {

    protected Context mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, 0);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getLayoutRes(),container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //override this method
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.gravity = getGravity();
            params.width = getWidth();
            window.setAttributes(params);
            window.setDimAmount(getDimAmount());
            window.setWindowAnimations(getAnimation());
            window.setBackgroundDrawable(null);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = new Dialog(mContext, getTheme());
        dialog.setCanceledOnTouchOutside(isCanceledOnTouchOutside());
        return dialog;
    }

    public void startActivity(Class<? extends Activity> clz, Bundle bundle) {
        Intent intent = new Intent(getContext(), clz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    public void show(FragmentManager manager) {
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, getClass().getSimpleName());
        ft.commitAllowingStateLoss();
    }

    @Override
    public void dismiss() {
        dismissAllowingStateLoss();
    }

    protected boolean isCanceledOnTouchOutside() {
        return false;
    }

    protected abstract int getGravity();

    protected abstract int getWidth();

    @FloatRange(from = 0f, to = 1f)
    protected abstract float getDimAmount();

    @StyleRes
    protected abstract int getAnimation();

    @LayoutRes
    protected abstract int getLayoutRes();

}
