package com.verlif.idea.singledown.module.login;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.verlif.idea.singledown.R;
import com.verlif.idea.singledown.listener.TextChangedEndListener;
import com.verlif.idea.singledown.manager.ServerInfoManager;
import com.verlif.idea.singledown.manager.UserManager;
import com.verlif.idea.singledown.module.base.BaseActivity;
import com.verlif.idea.singledown.module.setting.SettingActivity;
import com.verlif.idea.singledown.ui.dialog.message.MessageDialog;
import com.verlif.idea.singledown.ui.text.NoLinesEditView;
import com.verlif.idea.singledown.manager.ServerConnManager;

import pub.devrel.easypermissions.EasyPermissions;

import static android.content.pm.PackageManager.PERMISSION_DENIED;

public class LoginActivity extends BaseActivity {

    private NoLinesEditView userNameEdit;
    private NoLinesEditView passwordEdit;
    private Button submit;

    private ServerConnManager serverConnManager;
    private UserManager userManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acticity_login);

        init();
        setListener();
    }

    private void init() {
        userNameEdit = findViewById(R.id.login_userName);
        passwordEdit = findViewById(R.id.login_password);
        submit = findViewById(R.id.login_submit);

        serverConnManager = ServerConnManager.newInstance(
                ServerInfoManager.newInstance(this).getRootUrl()
        );
        userManager = UserManager.newInstance(this);
    }

    private void setListener() {
        userNameEdit.addTextChangedListener(new TextChangedEndListener() {
            @Override
            public void changed(Editable s) {
                submit.setEnabled(s.length() > 0);
            }
        });
        userNameEdit.setEntered(() -> passwordEdit.requestFocus());
        passwordEdit.addTextChangedListener(new TextChangedEndListener() {
            @Override
            public void changed(Editable s) {
                submit.setEnabled(s.length() > 0);
            }
        });
        passwordEdit.setEntered(() -> submit.callOnClick());
        submit.setOnClickListener(v -> {
            submit.setEnabled(false);
            serverConnManager.Login(userNameEdit.getText().toString(), passwordEdit.getText().toString(), new ServerConnManager.InnerCallBack() {
                @Override
                public void stateOnTrue(String data) {
                    userManager.saveToken(JSON.parseObject(data).getString("token"));
                    runOnUiThread(() -> {
                        toastManager.setTextAndShow("登录成功");
                        submit.setEnabled(true);
                    });
                    finish();
                }

                @Override
                public void stateOnFalse(String message) {
                    runOnUiThread(() -> {
                        new MessageDialog(LoginActivity.this).setMessageAndShow("登录失败: " + message);
                        submit.setEnabled(true);
                    });
                }
            });
            if (!EasyPermissions.hasPermissions(this, Manifest.permission.INTERNET)) {
                EasyPermissions.requestPermissions(this, "需要网络权限", 1, Manifest.permission.CAMERA);
                new MessageDialog(this).setMessageAndShow("需要网络权限");
                submit.setEnabled(true);
            }
        });
        findViewById(R.id.login_setting).setOnClickListener(v -> startActivity(new Intent(this, SettingActivity.class)));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        if (grantResults.length == 0 || grantResults[0] == PERMISSION_DENIED) {
            startActivity(new Intent(Settings.ACTION_APPLICATION_SETTINGS));
        }
    }

}
