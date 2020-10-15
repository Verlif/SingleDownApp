package com.verlif.idea.singledown.ui.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.verlif.idea.singledown.R;
import com.verlif.idea.singledown.module.base.BaseActivity;

public class NotificationBuilder {

    private static final String CHANNEL_ID = "single_down";
    private static final String CHANNEL_NAME_DOWNLOAD = "download";
    private static final String CHANNEL_NAME_UPLOAD = "download";

    private static Notification notification;
    private NotificationManager manager;

    public NotificationBuilder(Context context) {

        //获取NotificationManager实例
        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent = PendingIntent.getActivity(
                context, 0, new Intent(context, BaseActivity.class), 0);
        //实例化NotificationCompat.Builder并设置相关属性
        NotificationCompat.Builder builder;
        // 按照Android版本分类设置通知样式
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME_DOWNLOAD, NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
            builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        } else {
            builder = new NotificationCompat.Builder(context);
        }
        builder.setSmallIcon(R.mipmap.icon)
                .setContentTitle("content")
                .setContentText("text")
                .setContentIntent(contentIntent);
        notification = builder.build();
        notification.flags = Notification.FLAG_ONGOING_EVENT;
    }

    public void showNotification() {
        manager.notify(1, notification);
    }

    public void cancel() {
        manager.cancel(1);
    }
}
