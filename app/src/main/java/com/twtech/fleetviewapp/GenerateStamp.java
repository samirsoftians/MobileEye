package com.twtech.fleetviewapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.nfc.Tag;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by twtech on 3/2/18.
 */

public class GenerateStamp implements Runnable {

    Context mContext;
    Thread CurrentThread;
    GlobalVariable globalVariable;
    String Time, Val, Lat, LatD, Long, LongD, Speed, DirDgree, Date;
    DatabaseOperation databaseOperation;
    CanDatabase canDatabase;
    int GenerateStamptime;
    String SIStamp;
    String lastStamp;
    int cellID, lac, mcc, mnc;
    GsmCellLocation location;
    String locString;
    String Tag="GenerateStamp";

    public GenerateStamp(Context mContext) {
        this.mContext = mContext;

        databaseOperation = new DatabaseOperation(mContext);
        canDatabase = new CanDatabase(mContext);
        globalVariable = (GlobalVariable) mContext.getApplicationContext();
        canDatabase.openCanDatabase();
        GenerateStamptime = Integer.parseInt(canDatabase.getValue("GenerateStamptime"));
        // new MyLogger().storeMassage("GnerateStamp time is-",""+GenerateStamptime);
        canDatabase.closeCanDatabase();
        CurrentThread = new Thread(this);
        CurrentThread.start();
    }

    @Override
    public void run() {

        while (true)
            init();

    }

    private void init() {

        try {
            CurrentThread.sleep(GenerateStamptime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {

            TelephonyManager tel = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            String networkOperator = tel.getNetworkOperator();
            if (!TextUtils.isEmpty(networkOperator)) {
                mcc = Integer.parseInt(networkOperator.substring(0, 3));
                mnc = Integer.parseInt(networkOperator.substring(3));
            }

            ///*****************************************

            TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

        try {
            location = (GsmCellLocation) tm.getCellLocation();
            cellID = location.getCid();
            lac = location.getLac();
            locString = location.toString();
           // Log.e("mcc : " + mcc, "mnc : " + mnc + "lac : " + lac + ", CellId : " + cellID);
           // new MyLogger().storeMassage(Tag+"  mcc : " + mcc, "mnc : " + mnc + "lac : " + lac + ", CellId : " + cellID);
        }catch (Exception e){
            //new MyLogger().storeMassage(Tag+" Exception while"," getting cell is information "+e.getMessage());
        }

        try {
            String StringGPRMC = globalVariable.getStringGPRMC();
           // Log.e("GPRMC String "," : "+StringGPRMC);
           // new MyLogger().storeMassage("GPRMC String"," : "+StringGPRMC);

            String[] parts = StringGPRMC.split(",");
            Time = "" + parts[1];
            Val = "" + parts[2];
            Lat = "" + parts[3];
            LatD = "" + parts[4];
            Long = "" + parts[5];
            LongD = "" + parts[6];
            Speed = "" + parts[7];
            DirDgree = "" + parts[8];
            Date = "" + parts[9];

            if (Time.contains(".")) {
                Time = Time.substring(0, 6);
            }

            Float FloatCurrentSpeed = Float.valueOf(0);
            try {
                float f = Float.parseFloat(Speed);
                FloatCurrentSpeed = (float) (f * 1.852);
            } catch (Exception e) {
               // Log.e("unable to find SIspeed", "exception.." + e.getMessage());
            }

            String distance = new CanDatabase(mContext).getSingleValue("UnitDistance");

            if(FloatCurrentSpeed>6.0) {

                SIStamp = "SI," + Date + "," + Time + "," + Lat + "," + LatD + "," + Long + "," + LongD + "," + DirDgree + "," + FloatCurrentSpeed + "," + distance + ",0.0," + Val + "" + "$" + ",C1," + mcc + "," + mnc + "," + cellID + "," + lac;
                //SIStamp = "SI," + Date + "," + Time + "," + Lat + "," + LatD + "," + Long + "," + LongD + "," + DirDgree + "," + FloatCurrentSpeed + "," + distance + ",0.0," + Val + "";
                new MyLogger().storeMassage(Tag+" : SI Stamp ", "" + SIStamp);
                Log.e("SI Stamp ", " : " + SIStamp);
                databaseOperation.storeRegularStamp(SIStamp);
                databaseOperation.storeGSMStamp(SIStamp, locString, lac, cellID);
                try {
                    if (globalVariable == null) {
                        Calendar caldar = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy");
                        SimpleDateFormat stf = new SimpleDateFormat("HHmmss");
                        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                        stf.setTimeZone(TimeZone.getTimeZone("GMT"));
                        String date1 = sdf.format(caldar.getTime());
                        String time1 = stf.format(caldar.getTime());
                        lastStamp = databaseOperation.retrieveStampsData();
                    }
                } catch (Exception e) {

                }
            }
        }catch (Exception e){
            Log.e("Exception in SI stamp",""+e);
           // new MyLogger().storeMassage(Tag, " Exception- "+e.getMessage());
        }
        }catch (Exception e){
            Log.e("Exception ","No sim");
        }
    }
}

