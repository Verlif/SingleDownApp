package com.verlif.idea.singledown.util;

import android.util.Log;

import java.util.Date;

public class PrintUtil {

    public static void println(Object text) {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        StringBuilder sb = new StringBuilder();
        sb
                .append(new Date())
                .append(" - Thread-")
                .append(Thread.currentThread().getId())
                .append(" - ");
        if (stack.length > 4) {
            String[] temp;
            for (int i = 4; i > 2; i--) {
                temp = stack[i].getClassName().split("\\.");
                sb.append(temp[temp.length - 1]).append("(").append(stack[i].getMethodName()).append(").");
            }
        }
        sb.append(" -\n\t\t").append(text);
        Log.d("TAG", sb.toString());
    }
}
