package com.twtech.fleetviewapp;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by twtech on 2/2/18.
 */

public class MainService extends Service {

    CanDatabase canDatabase;
    PendingIntent pintent;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public int onStartCommand(Intent intent, int flags, int startId){
       // handleCommand(intent);
        try {
            Calendar caldar = Calendar.getInstance();
            AlarmManager alarm = (AlarmManager) getSystemService(MainService.this.ALARM_SERVICE);
            Intent intent1 = new Intent(MainService.this, ReceiverCall.class);
            pintent = PendingIntent.getBroadcast(MainService.this, 1, intent1, pintent.FLAG_UPDATE_CURRENT);
            alarm.setRepeating(AlarmManager.RTC_WAKEUP, caldar.getTimeInMillis(), 15 * 60 * 1000, pintent);
        }catch (Exception e){
            new MyLogger().storeMassage("MainSErvice : Exception while "," starting Alarm Manager ");
        }
    return START_STICKY;
    }

    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(1,new Notification());
        }
        Log.e("MainService", "Called");
        new MyLogger().storeMassage("MainService", "Called");
        canDatabase = new CanDatabase(getApplicationContext());
        canDatabase.createCanDatabase();
        canDatabase.openCanDatabase();
        //UnitId = canDatabase.getValue("UnitID");
        //ImeiNo = canDatabase.getValue("IMEI");
        canDatabase.closeCanDatabase();
        new CaptureLocation(MainService.this);
        new GenerateStamp(MainService.this);
        new DistanceCompute(MainService.this);
        new GenerateStamps2(MainService.this);
        new GenerateACDC(MainService.this);
        new GenerateOS(MainService.this);
        new AutoDeletionIncidentTable(MainService.this);
        new RegularStampsTransmission(MainService.this);
        //new GSMStampTransmission(MainService.this);
        new ExceptionStampsTransmission(MainService.this);
        new IncidentDataTransmissionNewLogic(MainService.this);
        new NGStampsTransmission(MainService.this);
        new TrackFileNewLogic(MainService.this);
        new DailyDataWebService(MainService.this);
      //  new GetGSLocation(MainService.this);
        new UpdateCanParameter(MainService.this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        new MyLogger().storeMassage("onDestroy"," Called ");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        new MyLogger().storeMassage("onConfigurationChanged"," Called **");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        new MyLogger().storeMassage("onLowMemory"," Called ** ");
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        new MyLogger().storeMassage("onTrimMemory "," Called ** ");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        new MyLogger().storeMassage("onTaskRemoved "," Called ** ");
    }
}
