package com.twtech.fleetviewapp;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by twtech on 6/2/18.
 */

public class BootComplete extends BroadcastReceiver {

    DatabaseOperation databaseOperation;

    String stampON, stampOF;

    Context mCtx;

    public BootComplete(Context mCtx) {
        this.mCtx = mCtx;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        new MyLogger().storeMassage("BootComplete", "Mobile Turn ON");
        databaseOperation = new DatabaseOperation(context);
        String lastData = databaseOperation.retrieveStampsData();
        lastData = lastData.substring(lastData.indexOf(",") + 1);
        stampOF = "OF," + lastData;
        // databaseOperation.storeRegularStamp(stampOF);
        // new MyLogger().storeMassage("stampOF",stampOF);

        Calendar caldar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy");
        SimpleDateFormat stf = new SimpleDateFormat("HHmmss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        stf.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date1 = sdf.format(caldar.getTime());
        String time1 = stf.format(caldar.getTime());

        /*lastData=lastData.substring(lastData.indexOf(",")+1 +lastData.indexOf(",")+1);
        lastData=lastData.substring(0, lastData.lastIndexOf(","));
        stampON="ON,"+date1+","+time1+","+lastData+",V";*/
        //new MyLogger().storeMassage("stampON",stampON);

        stampON = "ON," + date1 + "," + time1 + ",0.0,0,0.0,0,0.0,0,0.0,0.0,V";

        String finalOnOfstr = stampOF + "\n" + stampON;
        new MyLogger().storeMassage("finalOnOfstr", finalOnOfstr);

        databaseOperation.storeExceptionData(finalOnOfstr);

        /*if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {

            addAutoStartup();
            Intent i = new Intent(context, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);

            try {
                Intent intent2 = new Intent();
                String manufacturer = android.os.Build.MANUFACTURER;
                if ("xiaomi".equalsIgnoreCase(manufacturer)) {
                    intent2.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
                } else if ("oppo".equalsIgnoreCase(manufacturer)) {
                    intent2.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
                } else if ("Letv".equalsIgnoreCase(manufacturer)) {
                    intent2.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"));
                } else if ("Honor".equalsIgnoreCase(manufacturer)) {
                    intent2.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
                }
                mCtx.startActivity(intent2);

            } catch (Exception e) {
                Log.e("exc" , String.valueOf(e));
            }
        }*/
    }

}
