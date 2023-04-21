package com.example.message.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.widget.RemoteViews;

import com.example.message.MainActivity;
import com.example.message.R;


public class Notification {

    // 发送简单的通知消息（包括消息标题和消息内容）
    public static void sendSimpleNotify(Service context) {
        String CHANNEL_ONE_ID = "15445555";
        String CHANNEL_ONE_NAME = "18652366";
        NotificationChannel notificationChannel;
        //进行8.0的判断
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(CHANNEL_ONE_ID,
                    CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setSound(null, null);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(android.app.Notification.VISIBILITY_PUBLIC);
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.createNotificationChannel(notificationChannel);
            }
        }
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification_layout);
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        android.app.Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notification = new android.app.Notification.Builder(context, CHANNEL_ONE_ID).setChannelId(CHANNEL_ONE_ID)
                    .setTicker("Nature")
                    .setCustomContentView(remoteViews)
                    .setSmallIcon(R.drawable.t)
                    .setContentIntent(pendingIntent)
                    .setOnlyAlertOnce(true)
                    .build();
        }
        if (notification != null) {
            notification.flags |= android.app.Notification.FLAG_NO_CLEAR;
        }
        context.startForeground(89455, notification);
    }
}
