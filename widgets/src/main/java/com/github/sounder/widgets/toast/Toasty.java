package com.github.sounder.widgets.toast;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Toast 属于系统级window，所以可使用Application的Context
 */
public class Toasty {
    private Toast mToast;

    private static class SingleTonHolder {
        private static Toasty toast = new Toasty();
    }

    public static void show(CharSequence msg) {
        getToast().mToast.setText(msg);
        SingleTonHolder.toast.mToast.show();
    }

    public static void init(Context appContext) {
        getToast().mToast = new Toast(appContext);
        getToast().mToast.setGravity(Gravity.CENTER, 0, 0);
    }

    private static Toasty getToast() {
        return SingleTonHolder.toast;
    }
}
