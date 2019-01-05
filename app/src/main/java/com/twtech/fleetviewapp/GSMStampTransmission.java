package com.twtech.fleetviewapp;

import android.content.Context;
import android.util.Log;

import java.util.ConcurrentModificationException;

/**
 * Created by Deepali Shinde on 30/4/18.
 */

public class GSMStampTransmission implements Runnable{

    Thread CurrThread;
    Context mCtx;
    String UnitID, Password, SmtpHost, SmtpPort, ToMailId, SiDuration;
    int SleepInterval;
    String SNo, TableName;
    DatabaseOperation DatabaseOperation;

    public GSMStampTransmission( Context mCtx) {
        this.mCtx = mCtx;

       // new MyLogger().storeMassage("GSMStampsTransmission", "Called");
        //new DatabaseOperations(mContext).storeRegularLog("1", "RegularStampsTransmission method Called");

       DatabaseOperation = new DatabaseOperation(mCtx);
        //databaseOperations.insertData(regularLogDatabase, Tag + "called");

        try {
            CanDatabase canDatabase = new CanDatabase(mCtx);
            canDatabase.openCanDatabase();
            UnitID = canDatabase.getValue("UnitID");
            Password = canDatabase.getValue("SmtpPassword");
            SmtpHost = canDatabase.getValue("SmtpHost");
            SmtpPort = canDatabase.getValue("SmtpPort");
            ToMailId = canDatabase.getValue("ToMailStamp");
           // SiDuration = canDatabase.getValue("SIDuration");
            canDatabase.closeCanDatabase();

           // SleepInterval = Integer.parseInt(SiDuration);

           // new MyLogger().storeMassage("GSMStampsTransmission Retrieve Data", "UnitId-" + UnitID + ", Password-" + Password + ", SmtpHost-" + SmtpHost + ", SmtpPort-" + SmtpPort + ", ToMailId-" + ToMailId + ", SleepInterval-" + SleepInterval);
            //new DatabaseOperations(mContext).storeRegularLog("1", "RegularStampsTransmission method called Retrieve Data" + "unitId-" + unitID + "password-" + password + "smtpPort-" + smtpPort +  "toMailId-" + toMailId);

            CurrThread = new Thread(this);
            CurrThread.start();

        } catch (Exception e) {
           // new MyLogger().storeMassage("Exception while getValue from DDCANDatabase ", "in RegularStampsTransmission");
            //databaseOperations.insertData(exceptionLogDatabase, Tag + "Exception while getValue from DDCANDatabase");
            //new DatabaseOperations(mContext).storeExceptionLog("3", "Exception while getValue from DDCANDatabase");
        }
    }

    @Override
    public void run() {
        while (true) {
            //generateGSStamp();
           // transmitRegularStamps();
             transmitGSMStamps();
            try {
                CurrThread.sleep(60*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public void transmitGSMStamps() {
        try {
            String retrieveData = DatabaseOperation.RetrieveGSMData();

            if (retrieveData.equals("finished")) {
               // new MyLogger().storeMassage("retrieveGSMStamps", "finished");
                //new DatabaseOperations(mContext).storeRegularLog("1", "No exception log data in current table for Transmission");
                Log.e("Thread Sleep-main", "For 2 Min...");
                Thread.sleep(5 * 1000);
            } else {
                new MyLogger().storeMassage("retrieveGSMStamps Data", "Successfully");
                //new DatabaseOperations(mContext).storeRegularLog("1", "Retrieve exception log Data from current table Successfully for transmission");
                String[] str = retrieveData.split("%");
                String returnData = str[0];
                SNo = str[1];
                TableName = str[2];
                //new MyLogger().storeMassage("return data" + returnData, "sNo" + sNo + "table name" + tableName);

                Log.e("return data", returnData);
                try {

                    TWsimpleMailSender mTWsimpleMailSender = new TWsimpleMailSender(mCtx, UnitID, Password, SmtpHost, SmtpPort);
                    new MyLogger().storeMassage("MailSender of GSM Stamps Transmission", "Called.............");
                    // new DatabaseOperations(mContext).storeRegularLog("1", "MailSender method for transmission exception logs Called");
                    boolean flag = mTWsimpleMailSender.sendMail(UnitID, returnData, UnitID, ToMailId);
                    //boolean flag = mTWsimpleMailSender.sendMail(unitID, returnData, unitID, "r_hajare@transworld-compressor.com");
                    // boolean flag = mTWsimpleMailSender.sendMail(UnitID, returnData, UnitID, "s_nirhali@twtech.in");

                    Log.e("Mail send ", "Successfully");
                    if (flag == true) {
                        new MyLogger().storeMassage("GSM Stamps MailSend", "Successfully.................");
                        //new DatabaseOperations(mContext).storeRegularLog("1", "Exception Log MailSend Successfully");
                        DatabaseOperation.UpdateGSMStatus(SNo, TableName);
                        new MyLogger().storeMassage("GSMStamps database Update", "Successfully..................");
                        //new DatabaseOperations(mContext).storeRegularLog("1", "ExceptionLogDatabase Update Successfully");
                    } else {
                        new MyLogger().storeMassage("GSM Stamps MailSend", "not Successfully.................");
                        new MyLogger().storeMassage("GSMStamps database Update", "not Successfully.................");
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    new MyLogger().storeMassage("Exception while Transmission GSM Stamps", e.getMessage());
                    // new DatabaseOperations(mContext).storeExceptionLog("3", "Exception while mail send of exception log in TRansmissionMailRunnable method");
                }

                //Thread.sleep(5 * 1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
            new MyLogger().storeMassage("Exception while GSMStampsTransmission run method", e.getMessage());
            //new DatabaseOperations(mContext).storeExceptionLog("3", "Exception in RegularStampsTransmission run method");
        }
    }
}
