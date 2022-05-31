package com.example.gpsspeedometerprox.app;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class MyAppication extends Application {
    public static final String CHANNEL_ID = "channel_service_example";
    @Override
    public void onCreate() {
        super.onCreate();
        createChannelNotification();
    }


    private void createChannelNotification() {
        // android 8>
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Channel Service Example",
                    NotificationManager.IMPORTANCE_DEFAULT);
            // tắt âm thanh nitifiacation
            channel.setSound(null,null);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if(manager != null){
                manager.createNotificationChannel(channel);
            }

        }
    }


}
