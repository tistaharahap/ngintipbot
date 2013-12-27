package com.bango.ngintipbot;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class IntipbotService extends Service {
	
	private boolean isRunning = false;

	private final IBinder mBinder = new LocalBinder();

    private static final long ALARM_INTERVAL = 60*60*1000;

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.isRunning = true;

        Utils.getLocation(this);
        startAlarm();

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        this.isRunning = false;
    }

    private void startAlarm() {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmBroadcastReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), ALARM_INTERVAL, pi);
    }

    public class LocalBinder extends Binder {
        IntipbotService getService() {
            return IntipbotService.this;
        }
    }

    public boolean isRunning() {
        return this.isRunning;
    }

}
