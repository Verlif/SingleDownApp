package com.verlif.idea.singledown.module.setting;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.verlif.idea.singledown.R;
import com.verlif.idea.singledown.listener.TextChangedEndListener;
import com.verlif.idea.singledown.manager.ServerConnManager;
import com.verlif.idea.singledown.manager.ServerInfoManager;
import com.verlif.idea.singledown.manager.ThumbnailManager;
import com.verlif.idea.singledown.manager.UserManager;
import com.verlif.idea.singledown.module.base.BaseActivity;
import com.verlif.idea.singledown.module.login.LoginActivity;
import com.verlif.idea.singledown.tools.FileTool;
import com.verlif.idea.singledown.ui.dialog.message.MessageDialog;
import com.verlif.idea.singledown.ui.dialog.one.OneDialog;
import com.verlif.idea.singledown.ui.text.NoLinesEditView;

public class SettingActivity extends BaseActivity {

    private TextView loginStatus;
    private TextView serverStatus;

    private ServerConnManager serverConnManager;
    private UserManager userManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        init();
    }

    private void init() {
        ServerInfoManager serverInfoManager = ServerInfoManager.newInstance(this);
        serverConnManager = ServerConnManager.newInstance(serverInfoManager.getRootUrl());
        userManager = UserManager.newInstance(this);

        findViewById(R.id.setting_login).setOnClickListener(v -> startActivity(new Intent(this, LoginActivity.class)));
        loginStatus = findViewById(R.id.setting_login_status);

        NoLinesEditView serverIpEdit = findViewById(R.id.setting_serverIp);
        serverIpEdit.setText(serverInfoManager.getRootUrl());
        serverIpEdit.addTextChangedListener(new TextChangedEndListener() {
            @Override
            public void changed(Editable s) {
                serverStatus.setText("×");
                serverConnManager.setRootUrl(serverIpEdit.getText().toString());
                serverInfoManager.saveRootUrl(serverIpEdit.getText().toString());
            }
        });
        Button serverIpCheckButton = findViewById(R.id.setting_serverIp_check);
        serverStatus = findViewById(R.id.setting_serverIp_status);
        serverIpCheckButton.setOnClickListener(v -> {
            serverIpCheckButton.setEnabled(false);
            serverConnManager.serverInfo(new ServerConnManager.InnerCallBack() {
                @Override
                public void stateOnTrue(String data) {
                    runOnUiThread(() -> {
                        serverIpCheckButton.setEnabled(true);
                        serverStatus.setText("√");
                    });
                }

                @Override
                public void stateOnFalse(String message) {
                    runOnUiThread(() -> {
                        serverIpCheckButton.setEnabled(true);
                        serverStatus.setText("×");
                    });
                }
            });
        });
        serverIpEdit.setEntered(serverIpCheckButton::callOnClick);

        // 设置缓存清理
        findViewById(R.id.setting_clearCache).setOnClickListener(v -> {
            OneDialog wait = new OneDialog(this);
            wait.setMessageAndShow("正在清除, 请稍等...");
            new Thread(() -> {
                ThumbnailManager thumbnailManager = ThumbnailManager.newInstance(serverInfoManager.getRootUrl());
                thumbnailManager.clear();
                runOnUiThread(() -> {
                    wait.cancel();
                    new MessageDialog(this).setMessageAndShow("清除完成!");
                });
            }).start();
        });

        findViewById(R.id.setting_filePath).setOnClickListener(v -> {
            MessageDialog dialog = new MessageDialog(this);
            dialog.setMessageAndShow("当前的下载目录为:\n" + new FileTool().getOrCreateUsedDirectory().getAbsolutePath());
        });

        // 设置软件信息
        findViewById(R.id.setting_appInfo).setOnClickListener(v -> {
            PackageManager manager = getPackageManager();
            String name;
            try {
                PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
                name = info.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                name = "???";
            }
            new MessageDialog(this).setMessageAndShow("当前版本: " + name);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!userManager.getToken().equals("")) {
            loginStatus.setText("√");
        } else loginStatus.setText("×");

        serverConnManager.serverInfo(new ServerConnManager.InnerCallBack() {
            @Override
            public void stateOnTrue(String data) {
                runOnUiThread(() -> {
                    serverStatus.setText("√");
                });
            }

            @Override
            public void stateOnFalse(String message) {
                runOnUiThread(() -> {
                    serverStatus.setText("×");
                });
            }
        });
    }
}
