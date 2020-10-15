package com.verlif.idea.singledown.model.map;

import androidx.annotation.DrawableRes;

public interface ShowIt {

    /**
     * 获取前景图资源id
     */
    @DrawableRes int getForegroundResId();

    /**
     * 获取显示类型
     */
    String getType();
}
