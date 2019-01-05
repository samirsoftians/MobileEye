package com.twtech.fleetviewapp;

import android.os.Environment;
import android.util.Log;
import org.apache.commons.net.smtp.SMTP;

/**
 * Created by twtech on 9/10/17.
 */

public class RegularStampsTransmission implements Runnable {

    android.content.Context Context;
    String SNo, TableName;
    Thread CurrThread;
    String ExceptionStampsDatabase = Environment.getExternalStorageDirectory().getPath() + "/DDExceptionStamps.db";
    String IncidentDataDatabase = Environment.getExternalStorageDirectory().getPath() + "/DDIncidentData.db";
    DatabaseOperation DatabaseOperation;
    String UnitID, Password, SmtpHost, SmtpPort, ToMailId, SiDuration, smtpHost;
    int SleepInterval;
    String sNo, tableName;
    String Tag="RegularStampsTransmission";

    public RegularStampsTransmission(android.content.Context ctx) {

        this.Context = ctx;
        DatabaseOperation = new DatabaseOperation(Context);
        //databaseOperations.insertData(regularLogDatabase, Tag + "called");
        try {
            CanDatabase canDatabase = new CanDatabase(Context);
            canDatabase.openCanDatabase();
            UnitID = canDatabase.getValue("UnitID");
            Password = canDatabase.getValue("SmtpPassword");
            SmtpHost = canDatabase.getValue("SmtpHost");
            //SmtpHost = "103.241.181.36";
            SmtpPort = canDatabase.getValue("SmtpPort");
            ToMailId = canDatabase.getValue("ToMailStamp");
            SiDuration = canDatabase.getValue("SIDuration");
            smtpHost = "103.241.181.36";
            canDatabase.closeCanDatabase();
            SleepInterval = Integer.parseInt(SiDuration);
           // new MyLogger().storeMassage("RegularStampsTransmission Retrieve Data", "UnitId-" + UnitID + ", Password-" + Password + ", SmtpHost-" + SmtpHost + ", SmtpPort-" + SmtpPort + ", ToMailId-" + ToMailId + ", SleepInterval-" + SleepInterval);
            //new DatabaseOperations(mContext).storeRegularLog("1", "RegularStampsTransmission method called Retrieve Data" + "unitId-" + unitID + "password-" + password + "smtpPort-" + smtpPort +  "toMailId-" + toMailId);
            CurrThread = new Thread(this);
            CurrThread.start();
        } catch (Exception e) {
            new MyLogger().storeMassage(Tag+" : Exception while getValue from DDCANDatabase ", "in RegularStampsTransmission");
            //databaseOperations.insertData(exceptionLogDatabase, Tag + "Exception while getValue from DDCANDatabase");
            //new DatabaseOperations(mContext).storeExceptionLog("3", "Exception while getValue from DDCANDatabase");
        }
    }

    @Override
    public void run() {
        while (true) {
           //generateGSStamp();
            Log.e("SmtpHost"," : "+SmtpHost);
            transmitRegularStamps();
           // transmitGSMStamps();
            try {
                CurrThread.sleep(SleepInterval*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

//        while (true) {
//            transmitRegularStamps();
//            //transmitGSMStamps();
//        }
//
//        try {
//            CurrThread.sleep(SleepInterval * 1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

    }

    public void transmitRegularStamps() {
        try {
            String retrieveData = DatabaseOperation.RetrieveRegData();
            if (retrieveData.equals("finished")) {
               // new MyLogger().storeMassage("retrieveRegularStamps", "finished");
                //new DatabaseOperations(mContext).storeRegularLog("1", "No exception log data in current table for Transmission");
              //  Log.e("Thread Sleep-main", "For 2 Min...");
                Thread.sleep(SleepInterval * 1000);
            } else {
               // new MyLogger().storeMassage("retrieveRegularStamps Data", "Successfully");
                //new DatabaseOperations(mContext).storeRegularLog("1", "Retrieve exception log Data from current table Successfully for transmission");
                String[] str = retrieveData.split("%");
                String returnData = str[0];
                SNo = str[1];
                TableName = str[2];
                //new MyLogger().storeMassage("return data" + returnData, "sNo" + sNo + "table name" + tableName);
                    //   Log.e("return data", returnData);
                try {
                    TWsimpleMailSender mTWsimpleMailSender = new TWsimpleMailSender(Context, UnitID, Password, SmtpHost, SmtpPort);
                    boolean flag = mTWsimpleMailSender.sendMail(UnitID, returnData, UnitID, ToMailId);

                    if (flag == true) {
                        DatabaseOperation.UpdateRegStatus(SNo, TableName);
                        new MyLogger().storeMassage(Tag+" : Regular Stamps database Updated", "Successfully !! "+SmtpHost+", "+ToMailId);

                    } else {
                        new MyLogger().storeMassage(Tag+" : Regular Stamps", "not sent"+ SmtpHost);
                      //  new MyLogger().storeMassage(Tag+" : Regular Stamps database ", "not Updated : "+SmtpHost);
                        TWsimpleMailSender mTWsimpleMailSender2 = new TWsimpleMailSender(Context, UnitID, Password, smtpHost, SmtpPort);
                        boolean flag1 = mTWsimpleMailSender2.sendMail(UnitID, returnData, UnitID, ToMailId);

                        if(flag1 == true){
                           new MyLogger().storeMassage(Tag+" : @@@ Stamp sent successfully"," from hardcoded IP @@@@@@@ "+smtpHost);
                           Log.e(Tag+" : stamps sent ","Successfully from hardcoded Id : "+smtpHost);
                        }else{
                            new MyLogger().storeMassage(Tag+" : Stamp not sent "," from hardcoded IP "+ smtpHost);
                            Log.e(Tag+" : stamp not sent "," from hardcoded IP : "+smtpHost);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    new MyLogger().storeMassage(Tag+" : Exception while Transmission Regular Stamps", e.getMessage());
                    // new DatabaseOperations(mContext).storeExceptionLog("3", "Exception while mail send of exception log in TRansmissionMailRunnable method");
                }
                //Thread.sleep(5 * 1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
            new MyLogger().storeMassage(Tag+" : Exception while RegularStampsTransmission run method", e.getMessage());
            //new DatabaseOperations(mContext).storeExceptionLog("3", "Exception in RegularStampsTransmission run method");
        }
    }

   /* public void transmitGSMStamps() {
        try {
            String retrieveData = DatabaseOperation.RetrieveGSMData();

            if (retrieveData.equals("finished")) {
                new MyLogger().storeMassage("retrieveGSMStamps", "finished");
                //new DatabaseOperations(mContext).storeRegularLog("1", "No exception log data in current table for Transmission");
                Log.e("Thread Sleep-main", "For 2 Min...");
                Thread.sleep(SleepInterval * 1000);
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

                    TWsimpleMailSender mTWsimpleMailSender = new TWsimpleMailSender(Context, UnitID, Password, SmtpHost, SmtpPort);
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
    }*/
}
