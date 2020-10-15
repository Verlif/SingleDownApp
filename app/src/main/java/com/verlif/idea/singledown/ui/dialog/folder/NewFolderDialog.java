package com.verlif.idea.singledown.ui.dialog.folder;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.verlif.idea.singledown.R;
import com.verlif.idea.singledown.ui.dialog.base.BottomDialog;
import com.verlif.idea.singledown.ui.text.NoLinesEditView;

public abstract class NewFolderDialog extends BottomDialog {

    public NewFolderDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        NoLinesEditView editView = view.findViewById(R.id.dialog_folder_name);
        Button sureButton = view.findViewById(R.id.dialog_folder_sure);
        editView.setEntered(sureButton::callOnClick);
        sureButton.setOnClickListener(v -> {
            String s = editView.getText().toString();
            if (s.length() > 0) {
                newFolder(s);
            }
        });
    }

    @Override
    protected int layout() {
        return R.layout.dialog_folder;
    }

    public abstract void newFolder(String folderName);
}
