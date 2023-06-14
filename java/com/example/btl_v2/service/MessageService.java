package com.example.btl_v2.service;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.btl_v2.R;
import com.example.btl_v2.adapter.MyNotification;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Date;

public class MessageService extends FirebaseMessagingService {
    
    //lang nghe sự kiện một token mới được tạo và cập nhật
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d("FCM","Token"+token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        // lay nguon cua tin nhan
        if (message.getNotification() != null) {
            NotificationCompat.Builder builder=new NotificationCompat.Builder(
                    getApplicationContext(), MyNotification.CHANNEL_ID)
                    .setContentTitle(message.getNotification().getTitle())
                    .setContentText(message.getNotification().getBody()).setSmallIcon(R.drawable.icon_notification)
                    .setDefaults(NotificationCompat.DEFAULT_SOUND)
                    .setCategory(NotificationCompat.CATEGORY_ALARM).setAutoCancel(true);
            System.out.println( "Message Notification Body: " + message.getNotification().getBody());
            NotificationManagerCompat managerCompat=NotificationManagerCompat.from(getApplicationContext());
            managerCompat.notify(getNotification(),builder.build());
        }
    }
    private int getNotification(){
        return (int)new Date().getTime();
    }
}
