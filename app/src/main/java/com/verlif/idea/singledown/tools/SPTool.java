package com.verlif.idea.singledown.tools;

import android.content.Context;
import android.content.SharedPreferences;

public class SPTool {

    private SharedPreferences sp;

    public SPTool(Context context, String spName) {
        sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
    }

    /**
     * 保存int数据
     *
     * @param key   数据key
     * @param value 数据本体
     */
    public void saveValue(String key, Object value) {
        final SharedPreferences.Editor editor = sp.edit();
        if (value != null) {
            switch (value.getClass().getSimpleName()) {
                case "int":
                case "Integer": {
                    editor.putInt(key, (int) value);
                    break;
                }
                case "String": {
                    editor.putString(key, (String) value);
                    break;
                }
                case "float":
                case "Float": {
                    editor.putFloat(key, (Float) value);
                    break;
                }
                case "boolean":
                case "Boolean": {
                    editor.putBoolean(key, (Boolean) value);
                    break;
                }
                default:
                    throw new IllegalStateException("Unexpected value: " + value.getClass().getSimpleName());
            }
            editor.apply();
        }
    }

    public int loadInt(String key, int defaultValue) {
        return sp.getInt(key, defaultValue);
    }

    public boolean loadBoolean(String key, boolean defaultValue) {
        return sp.getBoolean(key, defaultValue);
    }

    public float loadFloat(String key, float defaultValue) {
        return sp.getFloat(key, defaultValue);
    }

    public String loadString(String key, String defaultValue) {
        return sp.getString(key, defaultValue);
    }
}
