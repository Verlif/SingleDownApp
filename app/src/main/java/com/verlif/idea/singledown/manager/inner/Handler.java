package com.verlif.idea.singledown.manager.inner;

import com.verlif.idea.singledown.manager.HandlerManager;

import java.io.Serializable;

import lombok.Data;

@Data
public abstract class Handler implements Serializable {

    public Handler() {
        owner = getOwnerName();
        tag = owner;
        HandlerManager.newInstance().addHandler(this);
    }

    private String owner;
    private String tag;

    /**
     * 此处的Handler基于新线程运行
     * @param message   接受的信息
     */
    public abstract void handlerMessage(Message message);

    private String getOwnerName() {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        String[] name1 = stack[4].getClassName().split("\\.");
        String[] name2 = name1[name1.length - 1].split("\\$");
        return name2[0];
    }

    /**
     * 开始Handler处理
     */
    public void start() {
        if (owner == null) {
            try {
                throw new Exception("缺失owner");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        HandlerManager.newInstance().addHandler(this);
    }

    /**
     * 使Handler失效，从管理器中移除
     */
    public void finish() {
        HandlerManager.newInstance().removeHandler(this);
    }
}
