package com.verlif.idea.singledown.ui.dialog.confirm;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.verlif.idea.singledown.R;
import com.verlif.idea.singledown.ui.dialog.base.BottomDialog;

import lombok.Builder;
import lombok.Data;

public class ConfirmDialog extends BottomDialog {

    private BuildItem buildItem;

    public ConfirmDialog(@NonNull Context context) {
        super(context);
    }

    public ConfirmDialog(@NonNull Context context, BuildItem buildItem) {
        super(context);
        setBuildItem(buildItem);
    }

    public void setBuildItem(BuildItem buildItem) {
        this.buildItem = buildItem;
    }

    @Override
    protected int layout() {
        return R.layout.dialog_confirm;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (buildItem != null) {
            TextView left = findViewById(R.id.dialog_confirm_left);
            TextView right = findViewById(R.id.dialog_confirm_right);
            TextView message = findViewById(R.id.dialog_confirm_message);

            left.setText(buildItem.leftButtonMsg);
            right.setText(buildItem.rightButtonMsg);
            message.setText(buildItem.message);

            left.setOnClickListener(buildItem.leftClickedLis);
            right.setOnClickListener(buildItem.rightClickedLis);
        }
    }

    @Data
    @Builder
    public static class BuildItem {
        private String message;
        private String leftButtonMsg;
        private String rightButtonMsg;
        private View.OnClickListener leftClickedLis;
        private View.OnClickListener rightClickedLis;
    }
}
