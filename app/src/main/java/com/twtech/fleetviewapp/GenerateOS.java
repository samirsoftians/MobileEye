package com.twtech.fleetviewapp;

import android.content.Context;
import android.util.Log;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Deepali Shinde on 23/4/18.
 */

public class GenerateOS implements Runnable {

    Context mContext;
    Thread currentThread;
    GlobalVariable globalVariable;
    CanDatabase canDatabase;
    DatabaseOperation databaseOperation;
    String stampOS, currentVal, currTime, currSpeed, currDate;
    String OSTime, OSVal, OSLat, OSLatD, OSLong, OSLongD, /*OSSpeed,*/
            OSDirDegree, OSDate;
    // float  /*OSGPspeed,*/ ;
    boolean OSGPRMCFlag = false, OSgenFlag = false;
    float MaxOSspeed = 0;
    int OsSpeedLimit, TimeDiff, currentTime, currentDate, OSGPTime, OSGPDate, OSminCounter = 0;
    Float currentSpeed;
    int OSgenerateTime;
    // float OSminCounter = 0;

    public GenerateOS(Context mContext) {
        this.mContext = mContext;
        canDatabase = new CanDatabase(mContext);
        canDatabase.openCanDatabase();
        OsSpeedLimit = Integer.parseInt(canDatabase.getValue("OsLimit"));
        OSgenerateTime = Integer.parseInt(canDatabase.getValue("OsGenerationTime"));
        canDatabase.closeCanDatabase();
        databaseOperation = new DatabaseOperation(mContext);
        globalVariable = (GlobalVariable) mContext.getApplicationContext();
        currentThread = new Thread(this);
        currentThread.start();
    }

    @Override
    public void run() {
        Log.e("OS Speed Limit", " : " + OsSpeedLimit);
       // new MyLogger().storeMassage("OS Speed Limit", " : " + OsSpeedLimit);
        while (true)
            OSGeneration();
    }

    private void OSGeneration() {
       // Log.e("OSGeneration", " called");
        // new MyLogger().storeMassage("OSGeneration"," called");
        try {
            Thread.sleep(OSgenerateTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
            //new MyLogger().storeMassage("Exception while run thread", e.getMessage());
        }

        try {
            String StringGPRMC = globalVariable.getStringGPRMC();
           // Log.e("GprMC ","String "+StringGPRMC);
           // new MyLogger().storeMassage(" -- GPRMC String ", ": " + StringGPRMC);
            String[] parts = StringGPRMC.split(",");
            currTime = "" + parts[1];
            currentVal = "" + parts[2];
            currSpeed = "" + parts[7];
            currDate = "" + parts[9];
            if (currTime.contains(".")) {
                currTime = currTime.substring(0, 6);
            }

            currentTime = Integer.parseInt(currTime);
            currentDate = Integer.parseInt(currDate);
            currentSpeed = null;

            try {
                float f = Float.parseFloat(currSpeed);
                currentSpeed = (float) (f * 1.852);
            } catch (Exception e) {
               // new MyLogger().storeMassage("unable to find DistanceComputeSpeed", "exception..");
            }

            if (currentSpeed >= OsSpeedLimit) {

                TimeDiff = 0;

                if (OSGPRMCFlag == false) {

                    OSDate = new String();
                    OSTime = new String();
                    OSGPDate = 0;
                    OSGPTime = 0;
                    OSVal = new String();
                    OSLat = new String();
                    OSLatD = new String();
                    OSLong = new String();
                    OSLongD = new String();
                    OSDirDegree = new String();

                    String[] osPart = StringGPRMC.split(",");
                    OSTime = "" + osPart[1];
                    OSVal = "" + parts[2];
                    OSLat = "" + parts[3];
                    OSLatD = "" + parts[4];
                    OSLong = "" + parts[5];
                    OSLongD = "" + parts[6];
                    //OSSpeed = "" + parts[7];
                    OSDirDegree = "" + parts[8];
                    OSDate = "" + parts[9];
                    if (OSTime.contains(".")) {
                        OSTime = OSTime.substring(0, 6);

                    }
                    OSGPTime = Integer.parseInt(OSTime);
                    OSGPDate = Integer.parseInt(OSDate);
                    OSGPRMCFlag = true;

                    //new MyLogger().storeMassage(" -- First OS Detected ", "Details : " + OSDate + ", " + OSTime + ", " + currDate + ", " + currTime);
                }

                if (currentSpeed > MaxOSspeed) {
                    MaxOSspeed = currentSpeed;
                }

                TimeDiff = getTimeDifference(OSDate, OSTime, currDate, currTime);
                //TimeDiff = getTimeDifference(""+OSGPDate,""+OSGPTime,""+currentDate,""+currentTime);
               // new MyLogger().storeMassage("-- OSTimeDifference between ", OSDate + ", " + OSTime + " And " + currDate + ", " + currTime + "IS = " + TimeDiff);
               // new MyLogger().storeMassage("-- Time Differenece", " : " + TimeDiff);
                if (TimeDiff >= 10) {
                    OSgenFlag = true;
                   // new MyLogger().storeMassage(" -- Greater Than ", "10 seconds");

                    if (TimeDiff >= 60) {

                       // new MyLogger().storeMassage(" -- Greater than 60 secongs", "OSminCounter : " + OSminCounter);
                        if ((TimeDiff - OSminCounter) >= 60) {
                            stampOS = "OS," + OSDate + "," + OSTime + "," + OSLat + "," + OSLatD + "," + OSLong + "," + OSLongD + "," + OSDirDegree + "," + MaxOSspeed + "," + TimeDiff + ",0.0," + OSVal + "";
                            databaseOperation.storeExceptionData(stampOS);
                           // new MyLogger().storeMassage("OS stamp 1 ", "" + stampOS);
                            OSminCounter = TimeDiff;
                        }
                    }
                }
            } else if (OSgenFlag == true) {
                stampOS = "OS," + OSDate + "," + OSTime + "," + OSLat + "," + OSLatD + "," + OSLong + "," + OSLongD + "," + OSDirDegree + "," + MaxOSspeed + "," + TimeDiff + ",0.0," + OSVal + "";
               // new MyLogger().storeMassage("OS stamp 2 ", "" + stampOS);
                databaseOperation.storeRegularStamp(stampOS);
                OSgenFlag = false;
                MaxOSspeed = 0;
                OSGPRMCFlag = false;
                OSminCounter = 0;
//              Added by Deepali on 23/4/2018
            } else if (currentSpeed < OsSpeedLimit && OSGPRMCFlag == true) {
               // new MyLogger().storeMassage("OS for below 10 seconds ", "!!!!!!!!!!");
                OSGPRMCFlag = false;
                TimeDiff = 0;
                MaxOSspeed = 0;
            }
            //Till here
        } catch (Exception e) {
            //Log.e("Exception in OS", "" + e);
           // new MyLogger().storeMassage("Exception in OS", e.getMessage());
        }
    }

    public Integer getTimeDifference(String startDate, String startTime, String endDate, String endTime) {

        String time1 = startDate + " " + startTime;
        String time2 = endDate + " " + endTime;

        SimpleDateFormat format = new SimpleDateFormat("ddMMyy HHmmss");
        Date date1 = null;
        Date date2 = null;
        try {
            date1 = format.parse(time1);
            date2 = format.parse(time2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
      //  Log.e("getDateDifference-" + date1, "date2-" + date2);
        // new MyLogger().storeMassage("getDateDifference-"+date1, "date2-"+date2);
        int difference = (int) (date2.getTime() - date1.getTime());
        difference = difference / 1000;
     //   Log.e("getTimeDifference", "" + difference);
        // new MyLogger().storeMassage("getTimeDifference", ""+difference);

        return difference;
    }

}