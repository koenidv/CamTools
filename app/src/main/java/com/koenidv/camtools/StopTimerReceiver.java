package com.koenidv.camtools;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StopTimerReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent timerIntent = new Intent(context, TimerService.class);
        context.stopService(timerIntent);
    }
}
