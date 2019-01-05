package com.twtech.fleetviewapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.os.Environment;
import android.util.Log;

/**
 * Created by twtech on 11/4/18.
 */

public class TrackFileNewLogic implements Runnable {

    Context mContext;
    String unitID, Password, SmtpHost, ToMailId, SmtpPort, getTrackFileData;
    SQLiteDatabase database;
    Thread TrackfileThread;
    String sendInterval;
    String Tag = "TrackFileSend";
    int sleepInterval;
    String canDatabase = Environment.getExternalStorageDirectory().getPath() + "/DDCANDatabase.db";


    public TrackFileNewLogic(Context mContext) {
        this.mContext = mContext;
        try {
            CanDatabase canDatabase = new CanDatabase(mContext);
            canDatabase.openCanDatabase();
            unitID = canDatabase.getValue("UnitID");
            Password = canDatabase.getValue("SmtpPassword");
            SmtpHost = canDatabase.getValue("SmtpHost");
            SmtpPort = canDatabase.getValue("SmtpPort");
            ToMailId = canDatabase.getValue("trackFileToMailID");
            sendInterval = canDatabase.getValue("TrackFileSendInterval");
            canDatabase.closeCanDatabase();
            sleepInterval = Integer.parseInt(sendInterval);

            TrackfileThread = new Thread(this);
            TrackfileThread.start();
           // Log.e("Send Interval", " : " + sendInterval);
           // new MyLogger().storeMassage(Tag + " Can Values ", unitID + "Password : " + Password + " SmtpHost : " + SmtpHost + " ToMailId : " + ToMailId + "Send Interval : " + sleepInterval);
        } catch (Exception e) {
            new MyLogger().storeMassage("Exception while ", "retrieving unit id from CAN : " + e.getMessage());
        }
    }

    @Override
    public void run() {
      //  new MyLogger().storeMassage(Tag, " Thread Called ");
        while (true) {

           // statusCheck();

            try {

                TWsimpleMailSender mTWsimpleMailSender = new TWsimpleMailSender(mContext, unitID, Password, SmtpHost, SmtpPort);
              //  new MyLogger().storeMassage("MailSender ", "Called.............");
                //boolean flag = mTWsimpleMailSender.sendMail(unitID, "Hello", unitID, "s_nirhali@twtech.in", canDatabase, canDatabase);
                boolean flag = mTWsimpleMailSender.sendMail(unitID, "", unitID, ToMailId, canDatabase);
               // new MyLogger().storeMassage("MailSend ", "Successfully.................");

               // Thread.sleep(sleepInterval * 1000);

            } catch (Exception e) {
                e.printStackTrace();
                new MyLogger().storeMassage("Exception in Transmission of trackfile", e.getMessage());
            }

            try {
               TrackfileThread.sleep(sleepInterval * 1000);
                //TrackfileThread.sleep(15*60 * 1000);
            } catch (Exception e) {
                new MyLogger().storeMassage(Tag + " Exception ", "while thread sleep : " + e.getMessage());
            }
        }
    }

    public void statusCheck() {
        final LocationManager manager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
    }

    private void buildAlertMessageNoGps() {
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mContext);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        mContext.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();
    }

}
