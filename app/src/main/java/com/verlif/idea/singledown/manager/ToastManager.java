package com.verlif.idea.singledown.manager;

import android.content.Context;

import com.verlif.idea.singledown.ui.toast.InnerToast;

public class ToastManager {

    private static ToastManager instance;
    private InnerToast innerToast;

    private ToastManager(Context context) {
        innerToast = new InnerToast(context);
    }

    public static ToastManager newInstance(Context context) {
        if (instance == null) {
            synchronized (ToastManager.class) {
                if (instance == null) {
                    instance = new ToastManager(context);
                }
            }
        }
        return instance;
    }

    public void setTextAndShow(CharSequence s) {
        innerToast.setTextAndShow(s);
    }

    public void setTextAndShow(int resId) {
        innerToast.setTextAndShow(resId);
    }
}
