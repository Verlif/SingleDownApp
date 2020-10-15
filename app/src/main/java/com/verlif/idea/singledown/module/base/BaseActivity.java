package com.verlif.idea.singledown.module.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.verlif.idea.singledown.manager.ToastManager;

public class BaseActivity extends AppCompatActivity {

    protected ToastManager toastManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toastManager = ToastManager.newInstance(this);
        // 去除标题栏
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    protected ToastManager getToastManager() {
        return toastManager;
    }
}
