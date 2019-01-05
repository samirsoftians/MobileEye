package com.twtech.fleetviewapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by twtech on 3/11/17.
 */

public class CanDatabase {

    Context mContext;
    SQLiteDatabase database;
    String databaseName = Environment.getExternalStorageDirectory().getPath() + "/DDCANDatabase.db";
  // String databaseName = "DDCANDatabase.db";
    String tableName = "t_CANValues";
    String tableName2="t_NGStamps";
    // String regularLogDatabase = Environment.getExternalStorageDirectory().getPath() + "/RegularLogDatabase.db";
    //String exceptionLogDatabase = Environment.getExternalStorageDirectory().getPath() + "/ExceptionLogDatabase.db";
    String Tag = "DragonDroid CanDatabase- ";

    public CanDatabase(Context mContext) {
        this.mContext = mContext;
    }

    public void openCanDatabase() {
        database = mContext.openOrCreateDatabase(databaseName, mContext.MODE_PRIVATE, null);
        //new DatabaseOperations(mContext).insertData(regularLogDatabase, Tag+"Open Database");
    }

    public void closeCanDatabase() {
        database.close();
        //new DatabaseOperations(mContext).insertData(regularLogDatabase, Tag+"Close Database");
    }

    public void storeValue(String parameter, String value) {

        try {

            String sqlQuery1 = "create table if not exists " + tableName + "(SrNo INTEGER PRIMARY KEY AUTOINCREMENT,Parameter varchar, Value varchar, InsertTime datetime default (datetime('now','localtime')), ModifyTime datetime)";
            database.execSQL(sqlQuery1);

            String countQuery1 = "select * from " + tableName + " where Parameter='" + parameter + "'";
            Cursor countCR1 = database.rawQuery(countQuery1, null);
            int totalCount = countCR1.getCount();
            countCR1.close();

           // Log.e("totalCount", "" + totalCount);

            if (totalCount == 0) {
                database.execSQL("insert into " + tableName + "(Parameter,Value) values('" + parameter + "','" + value + "')");
               // Log.e("Insert", "called");
            } else {
                database.execSQL("update " + tableName + " set Value='" + value + "' where Parameter ='" + parameter + "'");
                database.execSQL("update " + tableName + " set ModifyTime=(datetime('now','localtime')) where Parameter ='" + parameter + "'");
               // Log.e("Update", "called");
            }

            //new DatabaseOperations(mContext).insertData(regularLogDatabase, Tag+"Store value-"+value+" for "+parameter);
            Log.e("CAN Updated ","Successfully");
        } catch (Exception e) {
            Log.e("storeValue", e.getMessage());
            new MyLogger().storeMassage("Exception While Updating CanParameter",parameter+", "+value);
           // new CanDatabase(mContext).insertData(exceptionLogDatabase, Tag+"Exception while Store value-"+value+" for "+parameter);
        }
    }

    public String getValue(String parameter) {

        String res = "";
        try {
            Cursor c = database.rawQuery("select Value from " + tableName + " where Parameter='" + parameter + "'", null);
            c.moveToFirst();
            do {
                res = c.getString(0);
            } while (c.moveToNext());

            c.close();
            Log.e(parameter, res);
            //new DatabaseOperations(mContext).insertData(regularLogDatabase, Tag+"Retrieve Value-"+res+" for "+parameter);

        } catch (Exception e) {
            Log.e("retriveValue", e.getMessage());
            //new DatabaseOperations(mContext).insertData(exceptionLogDatabase, Tag+"Exception while Retrieve Value-"+res+" for "+parameter);
        }

        return res;
    }

    public void storeSingleValue(String parameter, String value) {

        try {
            database = mContext.openOrCreateDatabase(databaseName, mContext.MODE_PRIVATE, null);
            String sqlQuery1 = "create table if not exists " + tableName + "(SrNo INTEGER PRIMARY KEY AUTOINCREMENT,Parameter varchar, Value varchar, InsertTime datetime, ModifyTime datetime)";
            database.execSQL(sqlQuery1);

            String countQuery1 = "select * from " + tableName + " where Parameter='" + parameter + "'";
            Cursor countCR1 = database.rawQuery(countQuery1, null);
            int totalCount = countCR1.getCount();
            countCR1.close();

           // Log.e("totalCount", "" + totalCount);

            if (totalCount == 0) {
                database.execSQL("insert into " + tableName + "(Parameter,Value,InsertTime,ModifyTime) values('" + parameter + "','" + value + "',(datetime('now','localtime')),(datetime('now','localtime')))");
               // Log.e("Insert", "called");
            } else {
                database.execSQL("update " + tableName + " set Value='" + value + "' where Parameter ='" + parameter + "'");
                database.execSQL("update " + tableName + " set ModifyTime=(datetime('now','localtime')) where Parameter ='" + parameter + "'");
               // Log.e("Update", "called");
            }
            database.close();

            //new DatabaseOperations(mContext).insertData(regularLogDatabase, Tag+"Store value-"+value+" for "+parameter);

        } catch (Exception e) {
            Log.e("databseOperations", e.getMessage());
            //new DatabaseOperations(mContext).insertData(exceptionLogDatabase, Tag+"Exception while Store value-"+value+" for "+parameter);
        }
    }

    public String getSingleValue(String parameter) {

        String res = "";
        try {
            database = mContext.openOrCreateDatabase(databaseName, mContext.MODE_PRIVATE, null);
            Cursor c = database.rawQuery("select Value from " + tableName + " where Parameter='" + parameter + "'", null);

            c.moveToFirst();
            do {
                res = c.getString(0);
            } while (c.moveToNext());

            c.close();
            database.close();
            Log.e(parameter, res);
            //new DatabaseOperations(mContext).insertData(regularLogDatabase, Tag+"Retrieve Value-"+res+" for "+parameter);

        } catch (Exception e) {
           // Log.e("retriveValue", e.getMessage());
            //new DatabaseOperations(mContext).insertData(exceptionLogDatabase, Tag+"Exception while Retrieve Value-"+res+" for "+parameter);
        }

        return res;
    }

    public String getAllCANValue() {
        StringBuffer sbCAN;
        sbCAN=new StringBuffer();
        String paraCan,valCan,strCAN,returnString = null;
        String databaseName = Environment.getExternalStorageDirectory().getPath() + "/DDCANDatabase.db";
        String tableName = "t_CANValues";
        try {
            database = mContext.openOrCreateDatabase(databaseName, mContext.MODE_PRIVATE, null);
            Cursor c = database.rawQuery("select * from "+tableName+"", null);
            c.moveToFirst();
            do {
                paraCan = c.getString(1);
                valCan = c.getString(2);
                strCAN=paraCan+":"+valCan+",";
                sbCAN.append(strCAN);
               // Log.e("Can Record : ",strCAN);
                // new MyLogger().storeMassage("Can Record : ",strCAN);
            } while (c.moveToNext());
            c.close();
            database.close();
            returnString=sbCAN.toString();
        } catch (Exception e) {
            Log.e("retriveValue", e.getMessage());
            new MyLogger().storeMassage("CanDatabase : Exception ","In retrieving Can Values : "+e.getMessage());
            //new DatabaseOperations(mContext).insertData(exceptionLogDatabase, Tag+"Exception while Retrieve Value-"+res+" for "+parameter);
        }
        return returnString;
    }

    public void createCanDatabase() {
        File databaseFile = new File(databaseName);
        if (!databaseFile.exists()) {
            openCanDatabase();
            try {
                storeValue("UnitID", "1234");
                storeValue("GpsType", "Internal");
                //storeValue("GpsType", "External");
                storeValue("SmtpPassword", "transworld");
               // storeValue("SmtpHost", "103.241.181.36");
                storeValue("SmtpHost", "a.mobileeye.in");
                storeValue("SmtpPort", "2525");
                storeValue("ToMailStamp", "g1@a.mobileeye.in");
               // storeValue("ToMailStamp", "d_shinde@transworld-compressor.com");
                storeValue("ToMailIdExLog", "sgexceptions@a.mobileeye.in");
                storeValue("ToMailIdIncident", "avlincident@twphd.in");
                storeValue("UnitIdUrl", "http://103.241.181.36:8080/DeviceIMEITracker/test?deviceId=");
                storeValue("FtpHost", "sgdota.mobileeye.in");
                storeValue("FtpPort", "21");
                storeValue("FtpUserName", "cameraimages");
                storeValue("FtpPassword", "Cimages@123");
                storeValue("FtpPath", "/Android/");
                storeValue("SIInterval", "60");
                storeValue("SIDuration", "120");
                storeValue("AcDcLimit", "15");
                storeValue("RedZoneOs", "30");
                storeValue("YellowZoneOs", "40");
                storeValue("GreenZoneOs", "65");
                storeValue("NormalOSLimit", "50");
                storeValue("IncidentLimit", "3");
                storeValue("StampsDBLength", "100000");
                storeValue("IncidentDBLength", "1800");
                storeValue("StampsMailLength", "128");
                storeValue("IncidentMailLength", "170");
                storeValue("POP3HostCommand", "pop.mobile-eye.in");
                storeValue("POP3PortCommand", "995");
                storeValue("POP3UserNameCommand", "@mobile-eye.in");
                storeValue("POP3PasswordCommand", "transworld@123");
                storeValue("POP3HostRoute", "pop.mobile-eye.in");
                storeValue("POP3PortRoute", "995");
                storeValue("POP3UserNameRoute", "jrmroute@mobile-eye.in");
                storeValue("POP3PasswordRoute", "jrmroute@123");
                storeValue("DragonDroidCodeVersion", "TWP5MODDV1.4_Beta_Test15_17May2018");
                storeValue("DeviceModelNumber", "");
                storeValue("IMEI", "");
                storeValue("SimNumber", "");
                storeValue("AndroidOSVersion", "");
                storeValue("ProtocolType", "");
                storeValue("GPRMCString", "");
                storeValue("UnitDistance", "0.00");
                storeValue("IncidentDuration", "10");
                storeValue("LocUpdMinTime", "1000");
                storeValue("LocUpdMinDist", "10");
                storeValue("GenerateStamptime", "60000");
                storeValue("otherStampGenerationTime", "120000");
                storeValue("DistanceComputetime", "10000");
                storeValue("IncidentTableLimit", "10");
                storeValue("IncTableDeleteSleep", "45");
                storeValue("AcDcLimit", "15");
                storeValue("AcDcGenerationTime","1000");
                storeValue("OsLimit", "50");
                storeValue("OsGenerationTime", "1000");
                storeValue("POP3Subject", "CAN_UPDATE");
                storeValue("CanUpdateSleepTime", "900");
                storeValue("TrackFileSendInterval", "900");
                storeValue("DailyDataWebURL","http://103.241.181.36:8080/VehSummary/rest/VehicleDetails?Username=ubaidullahkhan@bddhalla.com&Password=1gjoQspE&VehCode=11437&LastRec=20&format=json\n");
                storeValue("DailyDataDownloadInterval","4");
                storeValue("lastRecDailyData","20");
                storeValue("trackFileToMailID","g1@a.mobileeye.in");
                //new DatabaseOperations(mContext).insertData(regularLogDatabase, Tag+"Created");
            } catch (Exception e) {
                Log.e("createCanDatabase", "Exception- " + e.getMessage());
                //new DatabaseOperations(mContext).insertData(exceptionLogDatabase, Tag+"Exception while createCanDatabase");
                createCanDatabase();
            }
            closeCanDatabase();
        }
    }
}
