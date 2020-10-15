package com.verlif.idea.singledown.manager.inner;

import com.verlif.idea.singledown.manager.HandlerManager;

import java.io.Serializable;

import lombok.Getter;

public class Message implements Serializable {

    /**
     * 消息标签
     */
    @Getter
    private String tag;
    /**
     * 消息类型
     */
    public int what;
    public int arg1;
    public int arg2;
    /**
     * 消息附加实例
     */
    public Object obj;

    public Message() {
    }

    public Message(int what) {
        this.what = what;
    }

    public Message(Class<?> target) {
        target(target);
    }

    /**
     * 消息接收类
     *
     * @param target 期望此消息的接受对象类
     */
    public void target(Class<?> target) {
        this.tag = target.getSimpleName();
    }

    public void send() {
        HandlerManager.newInstance().receiveMessage(this);
    }

    public void send(Class<?> target) {
        target(target);
        HandlerManager.newInstance().receiveMessage(this);
    }
}
