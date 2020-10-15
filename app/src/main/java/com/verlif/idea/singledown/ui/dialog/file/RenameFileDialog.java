package com.verlif.idea.singledown.ui.dialog.file;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.verlif.idea.singledown.R;
import com.verlif.idea.singledown.ui.dialog.base.BottomDialog;
import com.verlif.idea.singledown.ui.text.NoLinesEditView;

public abstract class RenameFileDialog extends BottomDialog {

    private NoLinesEditView nowNameEdit;
    private String oldName;

    public RenameFileDialog(@NonNull Context context, String oldName) {
        super(context);
        this.oldName = oldName;
    }

    @Override
    protected int layout() {
        return R.layout.dialog_rename;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        nowNameEdit = view.findViewById(R.id.dialogRename_name);
        Button sureButton = view.findViewById(R.id.dialogRename_sure);
        nowNameEdit.setText(oldName);
        nowNameEdit.setEntered(sureButton::callOnClick);
        sureButton.setOnClickListener(v -> {
            if (nowNameEdit.length() > 0) {
                nowName(nowNameEdit.getText().toString());
            }
        });
    }

    public abstract void nowName(String nowName);
}
