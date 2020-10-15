package com.verlif.idea.singledown.manager;

public class OnlineFileManager {

    private static OnlineFileManager onlineFileManager;

    private OnlineFileManager() {

    }

    public static synchronized OnlineFileManager newInstance() {
        if (onlineFileManager == null) {
            onlineFileManager = new OnlineFileManager();
        }
        return onlineFileManager;
    }


}
