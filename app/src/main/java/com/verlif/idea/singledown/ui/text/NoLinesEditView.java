package com.verlif.idea.singledown.ui.text;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class NoLinesEditView extends androidx.appcompat.widget.AppCompatEditText {

    private Entered entered;

    public NoLinesEditView(Context context) {
        super(context);
        setListener();
    }

    public NoLinesEditView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setListener();
    }

    public NoLinesEditView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setListener();
    }

    public void setEntered(Entered entered) {
        this.entered = entered;
    }

    private void setListener() {
        setOnKeyListener((view1, i, keyEvent) -> {
            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                if (entered != null) {
                    entered.enter();
                }
                return true;
            }
            return false;
        });
    }

    public interface Entered {
        void enter();
    }
}
