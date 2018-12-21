package com.koenidv.camtools;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class TimerService extends Service {

    private final static String TAG = "TimerService";
    final static String ACTION = "com.koenidv.camtools.timer";
    final static int NOTIFYID = 42;

    Intent intent = new Intent(ACTION);

    private int time = 10000;
    private String name;
    private boolean running = false;

    CountDownTimer timer = null;

    public TimerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle extras = intent.getExtras();

        int newTime = time;
        if (extras != null) {
            newTime = extras.getInt("time");
            name = extras.getString("name");
        }

        if (newTime != time && running) {
            timer.cancel();
            running = false;
        }
        if (!running) {
            time = newTime;
            startTimer();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    void startTimer() {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent contentIntent = new Intent(this, MainActivity.class);
        PendingIntent contentPending = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), contentIntent, PendingIntent.FLAG_ONE_SHOT);
        Intent cancelIntent = new Intent(this, StopTimerReceiver.class);
        PendingIntent cancelPending = PendingIntent.getBroadcast(this, (int) System.currentTimeMillis(), cancelIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        timer = new CountDownTimer(time, 20) {

            public void onTick(long millisUntilFinished) {
                intent.putExtra("max", time)
                        .putExtra("remaining", millisUntilFinished)
                        .putExtra("name", name);
                sendBroadcast(intent);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), getString(R.string.channel_timer))
                        .setSmallIcon(R.drawable.ic_timer)
                        .setContentTitle(name)
                        .setContentText(String.format(getString(R.string.timer_text_short), (new ModuleManager()).convertMilliseconds(millisUntilFinished)))
                        .setContentIntent(contentPending)
                        .addAction(R.drawable.ic_cancel, getString(R.string.cancel), cancelPending)
                        .setCategory(NotificationCompat.CATEGORY_ALARM)
                        .setPriority(NotificationCompat.PRIORITY_LOW)
                        .setOngoing(true);

                manager.notify(NOTIFYID, builder.build());

            }

            public void onFinish() {
                intent.putExtra("remaining", 0);
                sendBroadcast(intent);
                try {
                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                    r.play();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), getString(R.string.channel_timer_finished))
                        .setSmallIcon(R.drawable.ic_timer)
                        .setContentTitle(name)
                        .setContentText(getString(R.string.timer_finished_short))
                        .setCategory(NotificationCompat.CATEGORY_ALARM)
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

                manager.cancel(NOTIFYID);
                manager.notify(NOTIFYID + 1, builder.build());

                stopSelf();
            }
        };
        timer.start();
        running = true;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if (timer != null) {
            timer.cancel();
        }
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(NOTIFYID);
        Log.i(TAG, "Destroyed timer service");
        super.onDestroy();
    }

    boolean isRunning() {
        return running;
    }
}
