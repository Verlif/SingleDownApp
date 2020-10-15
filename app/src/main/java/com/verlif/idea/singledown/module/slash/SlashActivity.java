package com.verlif.idea.singledown.module.slash;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.verlif.idea.singledown.R;
import com.verlif.idea.singledown.module.base.BaseActivity;
import com.verlif.idea.singledown.module.main.MainActivity;
import com.verlif.idea.singledown.tools.FileTool;
import com.verlif.idea.singledown.ui.dialog.confirm.ConfirmDialog;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class SlashActivity extends BaseActivity {

    private FileTool fileTool;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slash);
        fileTool = new FileTool();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (fileTool.getPermission(this)) {
            toMain();
        } else {

        }
    }

    private void toMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FileTool.CODE_PERMISSION) {
            if (grantResults[0] == PERMISSION_GRANTED) {
                toMain();
            } else {
                ConfirmDialog dialog = new ConfirmDialog(this);
                dialog.setBuildItem(
                        ConfirmDialog.BuildItem.builder()
                                .message("需要存储权限以上传与下载")
                                .leftButtonMsg("给予权限")
                                .leftClickedLis(v -> fileTool.getPermission(this))
                                .rightButtonMsg("算了")
                                .rightClickedLis(v -> finish())
                                .build()
                );
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
        }
    }
}
