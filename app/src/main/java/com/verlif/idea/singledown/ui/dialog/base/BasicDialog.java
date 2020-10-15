package com.verlif.idea.singledown.ui.dialog.base;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;

public abstract class BasicDialog extends Dialog {

    protected View view;
    protected Window window;

    public BasicDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.view = LayoutInflater.from(context).inflate(layout(), null);
    }

    protected abstract int layout();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        window = getWindow();
    }
}
