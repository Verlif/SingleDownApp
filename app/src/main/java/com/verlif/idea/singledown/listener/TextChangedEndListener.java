package com.verlif.idea.singledown.listener;

import android.text.Editable;
import android.text.TextWatcher;

public abstract class TextChangedEndListener implements TextWatcher {

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        changed(s);
    }

    public abstract void changed(Editable s);
}
