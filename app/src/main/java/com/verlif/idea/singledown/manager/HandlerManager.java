package com.verlif.idea.singledown.manager;

import android.text.TextUtils;

import com.verlif.idea.singledown.manager.inner.Handler;
import com.verlif.idea.singledown.manager.inner.Message;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class HandlerManager {

    private static HandlerManager instance;

    private final Hashtable<String, Handler> handlerHashMap;
    private final List<Message> messageList;

    private HandlerManager() {
        handlerHashMap = new Hashtable<>();
        messageList = new ArrayList<>();
    }

    public static HandlerManager newInstance() {
        if (instance == null) {
            synchronized (HandlerManager.class) {
                if (instance == null) {
                    instance = new HandlerManager();
                }
            }
        }
        return instance;
    }

    public void addHandler(Handler handler) {
        handlerHashMap.put(handler.getOwner(), handler);
    }

    public void removeHandler(Handler handler) {
        handlerHashMap.remove(handler.getTag());
    }

    /**
     * 将Message交由相关的Handler处理。
     *
     * @param message 需要处理的Handler
     */
    public void receiveMessage(Message message) {
        new Thread(() -> {
            String tag = message.getTag();
            if (!TextUtils.isEmpty(tag)) {
                for (Handler handler : handlerHashMap.values()) {
                    if (handler.getTag().equals(tag)) {
                        handler.handlerMessage(message);
                    }
                }
            } else {
                for (Handler handler : handlerHashMap.values()) {
                    handler.handlerMessage(message);
                }
            }
        }).start();
    }

}
