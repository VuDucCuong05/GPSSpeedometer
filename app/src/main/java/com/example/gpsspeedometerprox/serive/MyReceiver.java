package com.example.gpsspeedometerprox.serive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int actionClose = intent.getIntExtra("action_close",0);
        Intent intentService = new Intent(context,LocationService.class);
        intentService.putExtra("action_close_serive",actionClose);
        context.startService(intentService);
    }
}
