package com.verlif.idea.singledown.manager;

import android.content.Context;

import com.verlif.idea.singledown.tools.SPTool;

public class UserManager {

    private static UserManager instance;

    private static final String SP_NAME = "user";
    private static final String VALUE_TOKEN = "token";

    private static SPTool spTool;

    private UserManager(Context context) {
        spTool = new SPTool(context, SP_NAME);
    }

    public static synchronized UserManager newInstance(Context context) {
        if (instance == null) {
            instance = new UserManager(context);
        }
        return instance;
    }

    public String getToken() {
        return spTool.loadString(VALUE_TOKEN, "");
    }

    public static String token() {
        return spTool.loadString(VALUE_TOKEN, "");
    }

    public void saveToken(String token) {
        spTool.saveValue(VALUE_TOKEN, token);
    }

}
