package com.verlif.idea.singledown.ui.dialog.one;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.verlif.idea.singledown.R;
import com.verlif.idea.singledown.ui.dialog.base.CenterDialog;

public class OneDialog extends CenterDialog {

    public OneDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected int layout() {
        return R.layout.dialog_one;
    }

    public void setMessageAndShow(String message) {
        TextView textView = view.findViewById(R.id.dialog_one_text);
        textView.setText(message);
        show();
    }
}
