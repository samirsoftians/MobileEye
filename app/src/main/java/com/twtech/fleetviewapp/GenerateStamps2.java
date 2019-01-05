package com.twtech.fleetviewapp;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Created by twtech on 13/2/18.
 */

public class GenerateStamps2 implements Runnable {

    Thread thread;
    Context mContext;
    String lastData;
    String lastDatanew;
    DatabaseOperation databaseOperation;
    CanDatabase canDatabase;
    int otherStampGenerationTime;
    int simState;
    TelephonyManager telMgr;
    String Tag = "GenerateStamp2";

    //boolean internet_status;

    public GenerateStamps2(Context mContext) {
        this.mContext = mContext;
        canDatabase = new CanDatabase(mContext);
        canDatabase.openCanDatabase();
        otherStampGenerationTime = Integer.parseInt(canDatabase.getSingleValue("otherStampGenerationTime"));
        canDatabase.closeCanDatabase();
        databaseOperation = new DatabaseOperation(mContext);
        thread = new Thread(this);
        thread.start();
        // boolean getSimStatus=isSimAvailable();
    }

    @Override
    public void run() {
        while (true)
            StampCreation();

    }

    public void StampCreation() {
        // Log.e("Generate Stamp 2 ","Called %%%%%%%%%%%%%%%%%%");

        try {
            Thread.sleep(otherStampGenerationTime);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        telMgr = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        simState = telMgr.getSimState();

        try {
            lastData = databaseOperation.retrieveStampsData();
            lastDatanew = lastData.substring(lastData.indexOf(",") + 1);
            //  new MyLogger().storeMassage(Tag+" : LastData ", "" + lastDatanew);
            //To check No GPS connection
            LocationManager manager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
            boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (statusOfGPS == true) {
            } else {
                //sb.append("NG" + lastDatanew + "\n");
                databaseOperation.storeNGStamp("NG," + lastDatanew);
                // new MyLogger().storeMassage(Tag+" : NG Stamp ", "" + "NG" + lastDatanew);
            }
        } catch (Exception e) {
            Log.e(Tag + " : Exception in NG", "");
        }

        //To check No Internet connection
        /*try {
            ConnectivityManager conMgr = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            {
                NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
                if (netInfo == null) {
                    databaseOperation.storeRegularStamp("NGPRS," + lastDatanew);
                    //sb.append("NGPRS" + lastDatanew + "\n");
                } else {
                    // Log.e("Internet working ", "................");
                }
            }
        } catch (Exception e) {
            Log.e(Tag + " : Exception 1", "");
        }

        // Added by deepali shinde on 16 May 2018 // Generate NGSM stamp
        try {
            if (Build.VERSION.SDK_INT <= 22) {
                boolean result = isSimAvailable();
                // Log.e("Result 1 ", ": " + result);
                //  new MyLogger().storeMassage(Tag+" : NGSM"," "+lastDatanew);
                if (result == false) {
                    databaseOperation.storeRegularStamp("NGSM," + lastDatanew);
                }
            } else if (Build.VERSION.SDK_INT > 22) {
                // Do some stuff
                SubscriptionManager sManager = (SubscriptionManager) mContext.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
                SubscriptionInfo infoSim1 = sManager.getActiveSubscriptionInfoForSimSlotIndex(0);
                SubscriptionInfo infoSim2 = sManager.getActiveSubscriptionInfoForSimSlotIndex(1);
                Log.e("Sim Details :", infoSim1.toString());
                //  new MyLogger().storeMassage(Tag + " info Sim ", "Data" + infoSim1.toString());

                if (infoSim1.toString() != null) {
                    try {
                        if ((infoSim1.toString().contains("status 0") || infoSim1.toString().contains("No service")) || (infoSim2.toString().contains("status 0")) || infoSim2.toString().contains("No service")) {
                            // Log.e("Sim Card deactivated", " : ");
                            // Log.e("Sim 1", "Details : " + infoSim1.toString());
                            // Log.e("Sim 2", "Details : " + infoSim2.toString());

                            databaseOperation.storeRegularStamp("NGSM," + lastDatanew);
                        }
                    } catch (Exception e) {
                        Log.e("Exception ", "NGSM " + e.getMessage());
                        new MyLogger().storeMassage("Exception in if loop ", "to check infosim1");
                    }
                }
            } else {
                // Do some stuff
            }
        } catch (Exception e) {
            new MyLogger().storeMassage("Exception while performing ", "NGSM stamp" + e.getMessage());
        }*/
        //Till here
    }

    public boolean isSimAvailable() {
        boolean result = false;
        TelephonyManager telMgr = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        int simState = telMgr.getSimState();
        switch (simState) {
            case TelephonyManager.SIM_STATE_ABSENT:
                // do something
                break;
            case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                // do something
                break;
            case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                // do something
                break;
            case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                // do something
                break;
            case TelephonyManager.SIM_STATE_READY:
                result = true;
                // do something
                // Log.e("Present","||||||||||||");
                break;
            case TelephonyManager.SIM_STATE_UNKNOWN:
                // do something
                break;
        }
        return result;
    }
}



