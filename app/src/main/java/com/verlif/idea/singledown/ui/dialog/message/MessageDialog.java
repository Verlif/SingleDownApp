package com.verlif.idea.singledown.ui.dialog.message;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.verlif.idea.singledown.R;

public class MessageDialog extends Dialog {
    private View view;
    private TextView textView;

    public MessageDialog(@NonNull Context context) {
        super(context, R.style.BottomDialog);
        this.view = LayoutInflater.from(context).inflate(R.layout.dialog_message, null);

        textView = view.findViewById(R.id.dialog_message_text);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(view);
        Window window = this.getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM);
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(params);
        }

        findViewById(R.id.dialog_message_button).setOnClickListener(v1 -> cancel());
    }

    public void setMessage(String message) {
        textView.setText(message);
    }

    public void setMessage(@StringRes int resId) {
        textView.setText(resId);
    }

    public void textLayout(boolean ifLeft) {
        if (ifLeft) {
            textView.setGravity(Gravity.LEFT);
        } else textView.setGravity(Gravity.CENTER);
    }

    public void setMessageAndShow(String message) {
        setMessage(message);
        show();
    }

    public void setMessageAndShow(@StringRes int resId) {
        setMessage(resId);
        show();
    }

    @Override
    public void show() {
        super.show();
        getWindow().setWindowAnimations(R.style.bottomAnimation);
    }
}
