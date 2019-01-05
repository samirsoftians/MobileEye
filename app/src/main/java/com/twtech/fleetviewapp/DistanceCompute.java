package com.twtech.fleetviewapp;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import java.text.DecimalFormat;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;

/**
 * Created by twtech on 5/2/18.
 */

public class DistanceCompute implements Runnable {

    Context mContext;
    Thread CurrentThread;
    CanDatabase canDatabase;
    GlobalVariable globalVariable;
    double currLatitude, currLongitude, prevLatitude, prevLongitude;
    String currentLat, currentLong, currentVal, currentSpeed, prevLat, prevLong;
    Float SpeedFloat, distanceKM, CanDistFloat;
    int DistanceComputetime;
    String Tag = "DistanceCompute";

    public DistanceCompute(Context mContext) {
        this.mContext = mContext;
        canDatabase = new CanDatabase(mContext);
        DistanceComputetime = Integer.parseInt(canDatabase.getSingleValue("DistanceComputetime"));
        // new MyLogger().storeMassage("DistanceCompute time is",""+DistanceComputetime);
        globalVariable = (GlobalVariable) mContext.getApplicationContext();
        CurrentThread = new Thread(this);
        CurrentThread.start();

    }

    @Override
    public void run() {

        /*try {
            //set Runnable priority to max and keep running in background
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            CurrentThread.setPriority(THREAD_PRIORITY_BACKGROUND);
            CurrentThread.setPriority(Thread.MAX_PRIORITY);
            new MyLogger().storeMassage("Priority set ","successfully");
        }catch (Exception e){
            new MyLogger().storeMassage(Tag+" : Exception while Setting "," thread Priority : "+e.getMessage());
        }*/

        try {
            while (true)
                getDistance();
        } catch (Exception e) {
            new MyLogger().storeMassage(Tag + " Exception while", " calling thread " + e.getMessage());
            Log.e(Tag + " Exception while", " calling thread " + e.getMessage());
        }
    }

    private void getDistance() {

        try {
            CurrentThread.sleep(DistanceComputetime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            String StringGPRMC = globalVariable.getStringGPRMC();
            String[] parts = StringGPRMC.split(",");
            currentVal = "" + parts[2];
            currentLat = "" + parts[3];
            currentLong = "" + parts[5];
            currentSpeed = "" + parts[7];
            String canDistance = canDatabase.getSingleValue("UnitDistance");
            // Float CanDistFloat= Float.valueOf(canDistance);
            CanDistFloat = Float.parseFloat(canDistance);
            String PrevGPRMC = globalVariable.getPrevGPRMC();

            if (PrevGPRMC != null) {

                String[] partsV = PrevGPRMC.split(",");
                prevLat = "" + partsV[3];
                prevLong = "" + partsV[5];

                prevLatitude = Double.parseDouble(prevLat);
                prevLongitude = Double.parseDouble(prevLong);
                currLatitude = Double.parseDouble("" + currentLat);
                currLongitude = Double.parseDouble("" + currentLong);
                prevLatitude = revConvertToStandard(prevLatitude);
                prevLongitude = revConvertToStandard(prevLongitude);
                currLatitude = revConvertToStandard(currLatitude);
                currLongitude = revConvertToStandard(currLongitude);

                // new MyLogger().storeMassage("Source Lat : "+prevLatitude,"Long : "+prevLongitude);
                // new MyLogger().storeMassage("Destin Lat : "+currLatitude,"Long : "+currLongitude);
                //Log.e("Longitude After "," Conversion"+ prevLatitude);
                // new MyLogger().storeMassage("Previous GPRMC captured","");
                // new MyLogger().storeMassage("prevLatitude-", +prevLatitude + "prevLongitude-" + prevLongitude);
                // new MyLogger().storeMassage("currentLatitude", +currLatitude + "currentLongitude-" + currLongitude);
                SpeedFloat = Float.parseFloat(currentSpeed);

                if (currentVal.equals("A") && SpeedFloat > 6) {

                     new MyLogger().storeMassage("prevLatitude-", +prevLatitude + "prevLongitude-" + prevLongitude);
                     new MyLogger().storeMassage("currentLatitude", +currLatitude + "currentLongitude-" + currLongitude);

                    Location srcLocation2 = new Location("");
                    Location destLocation2 = new Location("");
                    destLocation2.setLatitude(currLatitude);
                    destLocation2.setLongitude(currLongitude);
                    srcLocation2.setLatitude(prevLatitude);
                    srcLocation2.setLongitude(prevLongitude);

                    double Distance = srcLocation2.distanceTo(destLocation2);//in meters
                    Distance = Distance / 1000;// convert it to kilometer
                    double distance2 = Distance + CanDistFloat;
                    distanceKM = (float) distance2;
                    new MyLogger().storeMassage("Distance Computed ", ": " + distanceKM);
                    canDatabase.storeSingleValue("UnitDistance", "" + distanceKM);

                    globalVariable.setPrevGPRMC(StringGPRMC);
                }
            } else if (currentVal.equals("A")) {
                globalVariable.setPrevGPRMC(StringGPRMC);
            }

        } catch (Exception e) {
            Log.e(Tag + " : Exception ", "" + e.getMessage());
            //new MyLogger().storeMassage(Tag + " : Exception ", "" + e.getMessage());
        }
    }

    private double revConvertToStandard(double x) {
        float convertedVal;
        double v = x / 100;
        long v1 = (long) (v);
        float v2 = (float) (v - v1);
        float v3 = (v2 * 100) / 60;
        convertedVal = v1 + v3;
        DecimalFormat decimalFormat = new DecimalFormat(".000000");
        double d = Double.parseDouble(decimalFormat.format(convertedVal));
        return d;
    }
}
