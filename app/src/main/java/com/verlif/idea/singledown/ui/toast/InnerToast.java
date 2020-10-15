package com.verlif.idea.singledown.ui.toast;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.verlif.idea.singledown.R;

public class InnerToast extends Toast {

    private TextView textView;

    public InnerToast(Context context) {
        super(context);
        // 获取用于显示的视图
        View view = LayoutInflater.from(context).inflate(R.layout.toast_layout, null);
        // 获取用于加载文本的文字框
        textView = view.findViewById(R.id.toast_text);
        setView(view);
        setDuration(Toast.LENGTH_SHORT);
        setGravity(Gravity.TOP, 0, 0);
    }

    @Override
    public void setText(int resId) {
        textView.setText(resId);
    }

    @Override
    public void setText(CharSequence s) {
        textView.setText(s);
        show();
    }

    public void setTextAndShow(int resId) {
        setText(resId);
        show();
    }

    public void setTextAndShow(CharSequence s) {
        setText(s);
        show();
    }
}
