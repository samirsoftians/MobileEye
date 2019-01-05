package com.twtech.fleetviewapp;

import android.content.Context;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by twtech on 22/2/18.
 */

public class GenerateACDC implements Runnable {
    Context mContext;
    Thread currentThread;
    GlobalVariable globalVariable;
    String /*Time,*/Lat, LatD, Long, LongD,DirDgree, Date;
    DatabaseOperation databaseOperation;
    CanDatabase canDatabase;
    String stampAC, stampDC, currentVal, currTime, currSpeed, prevtime, prevSpeed,prevDate;
    float  currentSpeed, previousSpeed,  speedDiffDC, speedDiffAC;
    int acdcLimit, acdcLimit1;
    int currentTime,previousTime,TimeDiff,currentDate,previousDate;
    int acdcGenerationTime;
    String Tag="GenerateACDC";

    public GenerateACDC(Context mContext) {
        this.mContext = mContext;
        canDatabase = new CanDatabase(mContext);
        acdcLimit= Integer.parseInt(canDatabase.getSingleValue("AcDcLimit"));
        acdcGenerationTime= Integer.parseInt(canDatabase.getSingleValue("AcDcGenerationTime"));
        globalVariable = (GlobalVariable) mContext.getApplicationContext();
        databaseOperation = new DatabaseOperation(mContext);
        currentThread = new Thread(this);
        currentThread.start();
    }

    @Override
    public void run() {
        Log.e("acdc Limit"," : "+acdcLimit);
        while (true)
            ACDCGeneration();
    }

    private void ACDCGeneration() {
        try {
            Thread.sleep(acdcGenerationTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            String StringGPRMC = globalVariable.getStringGPRMC();
            String[] parts = StringGPRMC.split(",");
            currTime = "" + parts[1];
            currentVal = "" + parts[2];
            currSpeed = "" + parts[7];
            Lat = "" + parts[3];
            LatD = "" + parts[4];
            Long = "" + parts[5];
            LongD = "" + parts[6];
            DirDgree = "" + parts[8];
            Date = "" + parts[9];
            try {
                if (currTime.contains(".")) {
                    currTime = currTime.substring(0, 6);
                }
            }catch (Exception e){
                new MyLogger().storeMassage(Tag+" : Exception in AC-DC generation",""+e);
            }


            String prevAcDcGPRMC = globalVariable.getPrevAcDcGPRMC();
            // new MyLogger().storeMassage("previousGPRMC",""+prevAcDcGPRMC);
            // new MyLogger().storeMassage("currentGPRMC",""+StringGPRMC);


            if (prevAcDcGPRMC != null) {

                String[] partsV = prevAcDcGPRMC.split(",");
                prevtime = "" + partsV[1];
                prevSpeed = "" + partsV[7];
                prevDate=""+partsV[9];
                if (prevtime.contains(".")) {
                    prevtime=prevtime.substring(0,6);

                }


                currentDate= Integer.parseInt(Date);
                previousDate= Integer.parseInt(prevDate);
                currentTime = Integer.parseInt(currTime);
                previousTime = Integer.parseInt(prevtime);
                currentSpeed = Float.parseFloat(currSpeed);
                previousSpeed = Float.parseFloat(prevSpeed);

                if (currentVal.equals("A") && currentSpeed > 6) {
                   // TimeDiff = currentTime - previousTime;
                    TimeDiff=getTimeDifference(""+previousDate,""+previousTime,""+currentDate,""+currentTime);
                   // new MyLogger().storeMassage("TimeDifference calculated", "" + TimeDiff);
                    if ((TimeDiff <= 3) && (TimeDiff >= 1)) {
                        acdcLimit1 = acdcLimit * TimeDiff;
                        speedDiffDC = previousSpeed - currentSpeed;
                        speedDiffAC = currentSpeed - previousSpeed;
                      //  new MyLogger().storeMassage("TimeDifference*acdcLimit-", "" + acdcLimit1);
                      //  new MyLogger().storeMassage("speedDiffDC is-", "" + speedDiffDC);

                        if (speedDiffDC >= acdcLimit1) {
                            stampDC = "DC," + Date + "," + currTime + "," + Lat + "," + LatD + "," + Long + "," + LongD + "," + DirDgree + "," + currentSpeed + "," + previousSpeed + ",0.0,A";
                            databaseOperation.storeExceptionData(stampDC);
                            new MyLogger().storeMassage(Tag+" : DC stamp ", "" + stampDC);
                        } else {
                          //  new MyLogger().storeMassage("Dc stamp not generate", "");
                        }
                        if (speedDiffAC >= acdcLimit1) {
                            stampAC = "AC," + Date + "," + currTime + "," + Lat + "," + LatD + "," + Long + "," + LongD + "," + DirDgree + "," + currentSpeed + "," + previousSpeed + ",0.0,A";
                            databaseOperation.storeExceptionData(stampAC);
                            new MyLogger().storeMassage(Tag+" : AC stamp ", "" + stampAC);
                        } else {
                            //new MyLogger().storeMassage("AC stamp not generated", "");
                        }
                        //globalVariable.setPrevAcDcGPRMC(prevAcDcGPRMC);
                    }
                }
            }
            if (currentVal.equals("A")) {
                globalVariable.setPrevAcDcGPRMC(StringGPRMC);
            }
        } catch (Exception e) {

           // new MyLogger().storeMassage(Tag+" : StampGeneration Exception", "" + e);
        }
    }
    public Integer getTimeDifference(String startDate, String startTime, String endDate, String endTime) {

        String time1 = startDate+" "+startTime;
        String time2 = endDate+" "+endTime;

        SimpleDateFormat format = new SimpleDateFormat("ddMMyy HHmmss");
        java.util.Date date1 = null;
        java.util.Date date2 = null;
        try {
            date1 = format.parse(time1);
            date2 = format.parse(time2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
       // Log.e("getDateDifference-"+date1, "date2-"+date2);
        int difference = (int) (date2.getTime() - date1.getTime());
        difference = difference/1000;
        //Log.e("getTimeDifference", ""+difference);

        return difference;
    }
}
