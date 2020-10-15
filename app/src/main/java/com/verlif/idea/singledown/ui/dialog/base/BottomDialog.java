package com.verlif.idea.singledown.ui.dialog.base;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.verlif.idea.singledown.R;

import java.util.Objects;

public abstract class BottomDialog extends BasicDialog {

    public BottomDialog(@NonNull Context context) {
        super(context, R.style.BottomDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(view);
        if (window != null) {
            window.setGravity(Gravity.BOTTOM);
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(params);
        }
    }

    @Override
    public void show() {
        super.show();
        Objects.requireNonNull(getWindow()).setWindowAnimations(R.style.bottomAnimation);
    }
}
