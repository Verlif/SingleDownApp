package com.verlif.idea.singledown.ui.dialog.base;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.verlif.idea.singledown.R;

public abstract class CenterDialog extends BasicDialog {

    public CenterDialog(@NonNull Context context) {
        super(context, R.style.defaultDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(view);
        if (window != null) {
            window.setGravity(Gravity.CENTER);
            WindowManager.LayoutParams params = window.getAttributes();;
            DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
            params.width = (int) (displayMetrics.widthPixels * 0.86);
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(params);
        }
    }

}
