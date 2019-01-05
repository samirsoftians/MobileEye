package com.twtech.fleetviewapp;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class ReceiverCall extends BroadcastReceiver {

    Context mCtx;

	@Override
	public void onReceive(Context context, Intent intent) {

		this.mCtx=context;
		//Log.i("Service  ","BroadCast Reciever Called!!!!!!!!!!!!!!");
		//new MyLogger().storeMassage("Service  ","BroadCast Reciever Called!!!!!!!!!!!!!!");
		boolean serviceRunningStatus = isServiceRunning(MainService.class);
		//boolean serviceUpdateRunningStatus = isServiceRunning(UpdateRemoteCanParameter.class);
		Log.e("Alarm Woke Up"," !!! ");
		new MyLogger().storeMassage("Alarm Woke ","Up !!!! ");

		Log.e("Service Running "," Status : "+serviceRunningStatus);

		if(serviceRunningStatus==false) {

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
				context.startForegroundService(new Intent(context, MainService.class));
			} else {
				context.startService(new Intent(context, MainService.class));
			}
            //startService(new Intent(DashboardActivity.this, MainService.class));
		}
		//context.startService(new Intent(context, MainService.class));;
	}

	private boolean isServiceRunning(Class<?> serviceClass) {
		try {
			ActivityManager manager = (ActivityManager) mCtx.getSystemService(Context.ACTIVITY_SERVICE);
			for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
				if (serviceClass.getName().equals(service.service.getClassName())) {
					return true;
				}
			}
		}catch (Exception e){
			Log.e("Exception While"," Checking service status");
		}
		return false;
	}
}
