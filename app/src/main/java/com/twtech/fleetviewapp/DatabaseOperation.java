package com.twtech.fleetviewapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by twtech on 5/2/18.
 */

public class DatabaseOperation {

    Context mContext;
    SQLiteDatabase database;
    String TableName;
   // String databaseName = Environment.getExternalStorageDirectory().getPath() + "/DailyData.db";
    String dailyDataDatabaseName = Environment.getExternalStorageDirectory().getPath() + "/DailyData.db";
    //String userInfodatabaseName = Environment.getExternalStorageDirectory().getPath() + "/UserInfo1.db";
    String uDB=Environment.getExternalStorageDirectory().getPath() + "/UserInfo.db";

    public DatabaseOperation(Context context) {
        this.mContext = context;
    }

    public void insertDailyData(String strRD,String strRA,String strOSduration,String strCRKM, String strCRHR, String strDistance,String strOSCount,String strVehcode,String strNDHR,String strRDurationinHrs,String strND,String strDate, String strNDKM) {

       // new MyLogger().storeMassage("InsertData method", "Called");
        try {
            SQLiteDatabase database = mContext.openOrCreateDatabase(dailyDataDatabaseName, mContext.MODE_PRIVATE, null);
            String sqlQuery1 = "create table if not exists t_dailyData (SrNo INTEGER PRIMARY KEY AUTOINCREMENT, RDCount varchar, RACount varchar, Osduration varchar, CRKM varchar, CRHR varchar, Distance varchar, OSCount varchar, VehCode varchar, NDHR varchar, RDurationinHrs varchar, ND varchar, TheDate Date UNIQUE, NDKM varchar)";
           // new MyLogger().storeMassage("Create table", "Successfully");
            database.execSQL(sqlQuery1);
            database.execSQL("insert into t_dailyData (RDCount,RACount,Osduration,CRKM,CRHR,Distance,OSCount,VehCode,NDHR,RDurationinHrs,ND,TheDate,NDKM) values('" + strRD + "','" + strRA + "','" + strOSduration + "','" + strCRKM + "','" + strCRHR + "','" + strDistance + "', '" + strOSCount + "', '" + strVehcode + "','" + strNDHR + "','" + strRDurationinHrs + "', '" + strND + "', '" + strDate + "', '" + strNDKM + "')");
            //new MyLogger().storeMassage("insert data", "Successfully");
            database.close();
        } catch (Exception e) {
            e.getMessage();
            new MyLogger().storeMassage("Exception while insert data", ""+e.getMessage());
        }
    }

    public void updateDailyData(String lastDT,String strRD,String strRA,String strOSduration,String strCRKM, String strCRHR, String strDistance,String strOSCount,String strVehcode,String strNDHR,String strRDurationinHrs,String strND,String strDate, String strNDKM) {
        Log.e("updateCalled","))");
        File dbF = mContext.getDatabasePath(dailyDataDatabaseName);
            // new MyLogger().storeMassage("InsertData method", "Called");
            Log.e("DB ","Exists");
            try {
                SQLiteDatabase database = mContext.openOrCreateDatabase(dailyDataDatabaseName, mContext.MODE_PRIVATE, null);
                database.execSQL("insert into t_dailyData (RDCount,RACount,Osduration,CRKM,CRHR,Distance,OSCount,VehCode,NDHR,RDurationinHrs,ND,TheDate,NDKM) values('" + strRD + "','" + strRA + "','" + strOSduration + "','" + strCRKM + "','" + strCRHR + "','" + strDistance + "', '" + strOSCount + "', '" + strVehcode + "','" + strNDHR + "','" + strRDurationinHrs + "', '" + strND + "', '" + strDate + "', '" + strNDKM + "')");
                database.execSQL("update t_dailyData set RDCount = '" + strRD + "',RACount = '" + strRA + "',Osduration = '" + strOSduration + "',CRKM = '" + strCRKM + "',CRHR = '" + strCRHR + "',Distance = '" + strDistance + "',OSCount = '" + strOSCount + "',VehCode = '" + strVehcode + "',NDHR = '" + strNDHR + "',RDurationinHrs = '" + strRDurationinHrs + "',ND = '" + strND + "',TheDate = '" + strDate + "',NDKM = '" + strNDKM + "' where TheDate='" + lastDT + "'");
                database.close();
                Log.e("Updated Successfully", "!!!!!");
            } catch (Exception e) {
                e.getMessage();
                new MyLogger().storeMassage("Exception while insert data", "" + e.getMessage());
            }

    }

    public void insertWinners(String user,String rank,String rating,String date) {

         new MyLogger().storeMassage("InsertData method Winners", "Called");
        try {
            SQLiteDatabase database = mContext.openOrCreateDatabase(dailyDataDatabaseName, mContext.MODE_PRIVATE, null);
            String sqlQuery1 = "create table if not exists winners (SrNo INTEGER PRIMARY KEY AUTOINCREMENT, User varchar, Rank varchar, Rating varchar)";
            database.execSQL(sqlQuery1);
            database.execSQL("insert into winners (User,Rank,Rating) values('" + user + "','" + rank + "','" + rating + "')");
            database.close();
        } catch (Exception e) {
            e.getMessage();
            Log.e("Exception while", "  insert data : "+e.getMessage());
        }
    }

    public String retrieveDailyData(String frmDAte, String toDate) {
        String returnData = "finished";
        float   NDHR = 0, NDKM = 0, Distance = 0, RDurationinHrs = 0, CRKM = 0, CRHR=0;
        int RDcount = 0, Osduration = 0, ND = 0, OSCount = 0, TheDate = 0,  VehCode = 0, RACount = 0;

        File dbFile = mContext.getDatabasePath(dailyDataDatabaseName);
        if (dbFile.exists()) {

           // new MyLogger().storeMassage("database exist", "");
            try {
                SQLiteDatabase database = mContext.openOrCreateDatabase(dailyDataDatabaseName, mContext.MODE_PRIVATE, null);
                //String sqlQuery1 = "select * from t_Log where TheDate>='" + frmDAte + "' and TheDate<='" + toDate+"'";
                // String sqlQuery1 = "select * from t_Log where TheDate>= Convert(datetime, "+ frmDAte + ") and TheDate<= Convert(datetime, "+ toDate;
                String query2 = "SELECT * FROM t_dailyData WHERE TheDate BETWEEN '" + frmDAte + "' AND '" + toDate + "'";
                // String sqlQuery1 = "select * from t_Log";

                Cursor cr = database.rawQuery(query2, null);

                cr.moveToFirst();
                while (!cr.isAfterLast()) {

                    //new MyLogger().storeMassage("while loop", "called");

                    int strRDCount = Integer.parseInt(cr.getString(cr.getColumnIndex("RDCount")));
                    RDcount = RDcount + strRDCount;

                    int strRACount = Integer.parseInt(cr.getString(cr.getColumnIndex("RACount")));
                    RACount = RACount + strRACount;

                    int strOsduration = Integer.parseInt(cr.getString(cr.getColumnIndex("Osduration")));
                    Osduration = Osduration + strOsduration;


                    float strCRKM = Float.parseFloat(cr.getString(cr.getColumnIndex("CRKM")));
                    CRKM = CRKM + strCRKM;

                    float strCRHR = Float.parseFloat(cr.getString(cr.getColumnIndex("CRHR")));
                    CRHR = CRHR + strCRHR;

                    float strDistance = Float.parseFloat(cr.getString(cr.getColumnIndex("Distance")));
                    Distance = Distance + strDistance;

                    int strOSCount = Integer.parseInt(cr.getString(cr.getColumnIndex("OSCount")));
                    OSCount = OSCount + strOSCount;
                    //Log.e("OSCount-", String.valueOf(OSCount));

                  /*  int strVehCode = Integer.parseInt(cr.getString(cr.getColumnIndex("VehCode")));
                    VehCode = VehCode + strVehCode;
*/

                    float strNDHR = Float.parseFloat(cr.getString(cr.getColumnIndex("NDHR")));
                    NDHR = NDHR + strNDHR;

                    float strRDurationHrs = Float.parseFloat(cr.getString(cr.getColumnIndex("RDurationinHrs")));
                    RDurationinHrs = RDurationinHrs + strRDurationHrs;

                    int strND = Integer.parseInt(cr.getString(cr.getColumnIndex("ND")));
                    ND = ND + strND;

                    /* int strTheDate = Integer.parseInt(cr.getString(cr.getColumnIndex("TheDate")));
                    TheDate = TheDate + strTheDate;
*/
                    float strNDKM = Float.parseFloat(cr.getString(cr.getColumnIndex("NDKM")));
                    NDKM = NDKM + strNDKM;

                    cr.moveToNext();
                }
                cr.close();

                returnData = RDcount + "%" + RACount + "%" + Osduration + "%" + CRKM + "%" + CRHR + "%" + Distance + "%" + OSCount + "%" + NDHR + "%" + RDurationinHrs + "%" + ND + "%" + NDKM;
                database.close();

            } catch (Exception e) {
                e.getMessage();
               // new MyLogger().storeMassage("Exception while retrieve data", e.getMessage());
            }
        }else {
            returnData = RDcount + "%" + RACount + "%" + Osduration + "%" + CRKM + "%" + CRHR + "%" + Distance + "%" + OSCount + "%" + NDHR + "%" + RDurationinHrs + "%" + ND + "%" + NDKM;
        }

        return returnData;
    }


    public String getYesterdaysData(String dt) {
        String data = "No";
        File db1File = mContext.getDatabasePath(dailyDataDatabaseName);
        String rdCount = "", raCount = "", distance = "", osCount = "",nightData = "";
        if (db1File.exists()) {

            try {
                SQLiteDatabase database = mContext.openOrCreateDatabase(dailyDataDatabaseName, mContext.MODE_PRIVATE, null);
                String query2 = "SELECT * FROM t_dailyData WHERE TheDate='" + dt + "'";
                Cursor cr = database.rawQuery(query2, null);
                cr.moveToFirst();
                while (!cr.isAfterLast()) {
                    rdCount = cr.getString(cr.getColumnIndex("RDCount"));
                    raCount = cr.getString(cr.getColumnIndex("RACount"));
                    distance = cr.getString(cr.getColumnIndex("Distance"));
                    osCount = cr.getString(cr.getColumnIndex("OSCount"));
                    nightData = cr.getString(cr.getColumnIndex("NDKM"));

                    cr.moveToNext();
                }
                data = rdCount + "," + raCount + "," + distance + "," + osCount + "," + nightData;
                Log.e("Yesterdays ","Data : "+data);
                new MyLogger().storeMassage("Yetsterdays Data"," : "+data);
                cr.close();
                database.close();
            } catch (Exception e) {
                Log.e("Exception while ", "gettting Esterdays Data" + e.getMessage());
            }
        }
        return data;
    }


    /*code for getting all user details in DashBoard*/

    public String retrieveDataforDashBoard(String lastDate) {
        String returnData = "finished";
        float   NDHR = 0, NDKM = 0, Distance = 0, RDurationinHrs = 0, CRKM = 0, CRHR=0;
        int RDcount = 0, Osduration = 0, ND = 0, OSCount = 0, TheDate = 0,  VehCode = 0, RACount = 0;

        File dbFile = mContext.getDatabasePath(dailyDataDatabaseName);
        if (dbFile.exists()) {

            // new MyLogger().storeMassage("database exist", "");
            try {
                SQLiteDatabase database = mContext.openOrCreateDatabase(dailyDataDatabaseName, mContext.MODE_PRIVATE, null);
                //String sqlQuery1 = "select * from t_Log where TheDate>='" + frmDAte + "' and TheDate<='" + toDate+"'";
                // String sqlQuery1 = "select * from t_Log where TheDate>= Convert(datetime, "+ frmDAte + ") and TheDate<= Convert(datetime, "+ toDate;
                String query2 = "SELECT * FROM t_dailyData WHERE TheDate ='" + lastDate;
                // String sqlQuery1 = "select * from t_Log";

                Cursor cr = database.rawQuery(query2, null);

                cr.moveToFirst();
                while (!cr.isAfterLast()) {

                    //new MyLogger().storeMassage("while loop", "called");

                    int strRDCount = Integer.parseInt(cr.getString(cr.getColumnIndex("RDCount")));
                    RDcount = RDcount + strRDCount;

                    int strRACount = Integer.parseInt(cr.getString(cr.getColumnIndex("RACount")));
                    RACount = RACount + strRACount;

                    int strOsduration = Integer.parseInt(cr.getString(cr.getColumnIndex("Osduration")));
                    Osduration = Osduration + strOsduration;


                    float strCRKM = Float.parseFloat(cr.getString(cr.getColumnIndex("CRKM")));
                    CRKM = CRKM + strCRKM;

                    float strCRHR = Float.parseFloat(cr.getString(cr.getColumnIndex("CRHR")));
                    CRHR = CRHR + strCRHR;

                    float strDistance = Float.parseFloat(cr.getString(cr.getColumnIndex("Distance")));
                    Distance = Distance + strDistance;

                    int strOSCount = Integer.parseInt(cr.getString(cr.getColumnIndex("OSCount")));
                    OSCount = OSCount + strOSCount;
                    //Log.e("OSCount-", String.valueOf(OSCount));

                  /*  int strVehCode = Integer.parseInt(cr.getString(cr.getColumnIndex("VehCode")));
                    VehCode = VehCode + strVehCode;
*/

                    float strNDHR = Float.parseFloat(cr.getString(cr.getColumnIndex("NDHR")));
                    NDHR = NDHR + strNDHR;

                    float strRDurationHrs = Float.parseFloat(cr.getString(cr.getColumnIndex("RDurationinHrs")));
                    RDurationinHrs = RDurationinHrs + strRDurationHrs;

                    int strND = Integer.parseInt(cr.getString(cr.getColumnIndex("ND")));
                    ND = ND + strND;

                    /* int strTheDate = Integer.parseInt(cr.getString(cr.getColumnIndex("TheDate")));
                    TheDate = TheDate + strTheDate;
*/
                    float strNDKM = Float.parseFloat(cr.getString(cr.getColumnIndex("NDKM")));
                    NDKM = NDKM + strNDKM;

                    cr.moveToNext();
                }
                cr.close();

                returnData = RDcount + "%" + RACount + "%" + Osduration + "%" + CRKM + "%" + CRHR + "%" + Distance + "%" + OSCount + "%" + NDHR + "%" + RDurationinHrs + "%" + ND + "%" + NDKM;
                Log.e("return data",returnData);
                database.close();

            } catch (Exception e) {
                e.getMessage();
                // new MyLogger().storeMassage("Exception while retrieve data", e.getMessage());
            }
        }else {
            returnData = RDcount + "%" + RACount + "%" + Osduration + "%" + CRKM + "%" + CRHR + "%" + Distance + "%" + OSCount + "%" + NDHR + "%" + RDurationinHrs + "%" + ND + "%" + NDKM;
            Log.e("return data else ",returnData);
        }

        return returnData;
    }

                 /*code ends here*/




    public String retrieveUserDetailsData() {

       // new MyLogger().storeMassage("retrieveUserDetailsData method", "Called");
        String returnData = "finished", strUnitId="0", strUserName="0", strEmailId="0", strMobileNo="0", strCompName="0", strAddress="0", strUserOTP="0", strVehCode="0";

        File dbFile = mContext.getDatabasePath(uDB);

        if (dbFile.exists()) {

            //new MyLogger().storeMassage("database exist", "");
            try {
                SQLiteDatabase database = mContext.openOrCreateDatabase(uDB, mContext.MODE_PRIVATE, null);
                String sqlQuery = "select * from otp";
                Cursor cr = database.rawQuery(sqlQuery, null);

                cr.moveToFirst();
                while (!cr.isAfterLast()) {
                   // strUnitId = cr.getString(cr.getColumnIndex("unitID"));
                    strUserName = cr.getString(cr.getColumnIndex("userName"));
                    strEmailId = cr.getString(cr.getColumnIndex("emailId"));
                    strMobileNo = cr.getString(cr.getColumnIndex("mobileNo"));
                   // strCompName = cr.getString(cr.getColumnIndex("compName"));
                    strAddress = cr.getString(cr.getColumnIndex("address"));
                    Log.e("Address",""+strAddress);
                   // strUserOTP = cr.getString(cr.getColumnIndex("userotp"));
                     strVehCode = cr.getString(cr.getColumnIndex("vehCode"));
                    cr.moveToNext();


                }

                cr.close();
                //new MyLogger().storeMassage("User Information from ","Database Retrieved :"+strUserName+" "+strEmailId+" "+strMobileNo+" "+strVehCode);
                returnData = strUserName+"%"+strEmailId+"%"+strMobileNo+"%"+strAddress+"%"+strVehCode;
               // Log.e("Get Data"," : "+returnData);

                //returnData = sb.toString();
            }catch (Exception e){
                new MyLogger().storeMassage("Exception while retrieve user Details", e.getMessage());
            }
        }

        return returnData;
    }

    public void storeIncidentData(String IncidentData) {
        String IncidentDatabaseName = Environment.getExternalStorageDirectory().getPath() + "/DDIncidentData.db";
        try {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
            String date = sdf.format(cal.getTime());
            String tableName = "t_Incident" + date;

            database = mContext.openOrCreateDatabase(IncidentDatabaseName, mContext.MODE_PRIVATE, null);
            String sqlQuery1 = "create table if not exists " + tableName + "(SrNo INTEGER PRIMARY KEY AUTOINCREMENT, DateTime datetime default (datetime('now','localtime')), IncidentData varchar, Status varchar DEFAULT 'Null')";
            database.execSQL(sqlQuery1);
            database.execSQL("insert into " + tableName + "(IncidentData) values('" + IncidentData + "')");
            database.close();

        } catch (Exception e) {
            Log.e("not store Inident data", e.getMessage());
        }
    }

    public void storeRegularStamp(String RegularStamps) {
        String RegularDatabaseName = Environment.getExternalStorageDirectory().getPath() + "/DDRegularStamps.db";
        try {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String date = sdf.format(cal.getTime());
            String tableName = "t_RegularStamps" + date;

            database = mContext.openOrCreateDatabase(RegularDatabaseName, mContext.MODE_PRIVATE, null);
            String sqlQuery1 = "create table if not exists " + tableName + "(SrNo INTEGER PRIMARY KEY AUTOINCREMENT, DateTime datetime default (datetime('now','localtime')), RegularStamps varchar, Status varchar DEFAULT 'Null')";
            database.execSQL(sqlQuery1);
            database.execSQL("insert into " + tableName + "(RegularStamps) values('" + RegularStamps + "')");
            database.close();

        } catch (Exception e) {
            Log.e("not store Regular data", e.getMessage());
        }
    }

    public void storeGSMStamp(String RegularStamps, String location,int lac, int cellId) {
        String RegularDatabaseName = Environment.getExternalStorageDirectory().getPath() + "/GSMStamps.db";
        try {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String date = sdf.format(cal.getTime());
            String tableName = "t_GSMStamps" + date;

            database = mContext.openOrCreateDatabase(RegularDatabaseName, mContext.MODE_PRIVATE, null);
            String sqlQuery1 = "create table if not exists " + tableName + "(SrNo INTEGER PRIMARY KEY AUTOINCREMENT, DateTime datetime default (datetime('now','localtime')), GSMStamps varchar, Location varchar, Lac varchar, CellID varchar, Status varchar DEFAULT 'Null')";
            database.execSQL(sqlQuery1);
            database.execSQL("insert into " + tableName + "(GSMStamps,Location,Lac,CellID) values('" + RegularStamps + "','"+location+"','"+lac+"','"+cellId+"')");
            database.close();
           // Log.e("GSM Stamps inserted "," into DataBase Successfully");
            //new MyLogger().storeMassage("GSM Stamps inserted "," into DataBase Successfully");

        } catch (Exception e) {
            Log.e("not store Regular data", e.getMessage());
            new MyLogger().storeMassage("Exception while GSM  "," insrting GSM Stamps "+e.getMessage());
        }
    }

    public void getLaststamp(String lastRegularstamp) {
        String lastRegularStamp = Environment.getExternalStorageDirectory().getPath() + "/DDRegularStamps.db";
        File dbFile = mContext.getDatabasePath(lastRegularStamp);
        try {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String date = sdf.format(cal.getTime());
            String tableName = "t_RegularStamps" + date;

            database = mContext.openOrCreateDatabase(lastRegularStamp, mContext.MODE_PRIVATE, null);
            String lastStamp = "select * from " + tableName + "(SrNo INTEGER PRIMARY KEY AUTOINCREMENT, DateTime datetime default (datetime('now','localtime')), RegularStamps varchar, Status varchar DEFAULT 'Null')";
            // String lastStamp2=
        } catch (Exception e) {

        }
    }

    public String retrieveStampsData() {
        String RegularStampsData = Environment.getExternalStorageDirectory().getPath() + "/DDRegularStamps.db";
        File stampsFile = mContext.getDatabasePath(RegularStampsData);
        String str2 = "";
        if (stampsFile.exists()) {
            try {
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                String date = sdf.format(cal.getTime());
                String TableName = "t_RegularStamps" + date;

                database = mContext.openOrCreateDatabase(RegularStampsData, Context.MODE_PRIVATE, null);
                String stampsQuery = "select * from " + TableName + "  order by SrNo desc limit 1";
                Cursor stampCR = database.rawQuery(stampsQuery, null);
                stampCR.moveToFirst();
                while (!stampCR.isAfterLast()) {

                    str2 = stampCR.getString(stampCR.getColumnIndex("RegularStamps"));

                    stampCR.moveToNext();

                }

                stampCR.close();

            } catch (Exception e) {
                Log.e("not retrieve stamps", e.getMessage());
                database.close();
            }
        }
        return str2;
    }

    public void storeExceptionData(String ExceptionStamps) {
        String ExceptionDatabaseName = Environment.getExternalStorageDirectory().getPath() + "/DDExceptionStamps.db";

        try {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String date = sdf.format(cal.getTime());
            String tableName = "t_ExceptionStamps" + date;

            database = mContext.openOrCreateDatabase(ExceptionDatabaseName, mContext.MODE_PRIVATE, null);
            String sqlQuery1 = "create table if not exists " + tableName + "(SrNo INTEGER PRIMARY KEY AUTOINCREMENT, DateTime datetime default (datetime('now','localtime')), ExceptionStamps varchar, Status varchar DEFAULT 'Null')";
            database.execSQL(sqlQuery1);
            database.execSQL("insert into " + tableName + "(ExceptionStamps) values('" + ExceptionStamps + "')");
            database.close();

        } catch (Exception e) {
            Log.e("not store Exceptiondata", e.getMessage());
        }

    }

    //==============>Method for retrieve Data from database
    public String RetrieveRegData() {
        //new MyLogger().storeMassage("RetrieveRegData method", "called");

        String RegularStampsDatabase = Environment.getExternalStorageDirectory().getPath() + "/DDRegularStamps.db";

        String returnData = "finished", srNo = "";
        StringBuffer sb, sb1;

        File dbFile = mContext.getDatabasePath(RegularStampsDatabase);
        // new MyLogger().storeMassage("DatabaseName", databaseName);
        //new DatabaseOperations(mContext).storeRegularLog("1", "");

        if (dbFile.exists()) {

            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            //SimpleDateFormat stf = new SimpleDateFormat("HH:mm:ss");
            String date = sdf.format(cal.getTime());
            //String time = stf.format(cal.getTime());
            TableName = " t_RegularStamps" + date;
            // new MyLogger().storeMassage("Table Name", tableName);

            database = mContext.openOrCreateDatabase(RegularStampsDatabase, Context.MODE_PRIVATE, null);

            try {
                String countQuery = "select * from " + TableName + " where Status='Null'";
                Cursor countCR = database.rawQuery(countQuery, null);
                int dataCount = countCR.getCount();
                countCR.close();

                Log.e("dataCount", "" + dataCount);

                if (dataCount == 0) {

                    int i = 1;
                    int totalCount = 0;

                    while (totalCount == 0) {

                        Calendar cal1 = Calendar.getInstance();
                        cal.add(Calendar.DATE, -i);
                        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
                        String date1 = sdf1.format(cal1.getTime());
                        TableName = "t_RegularStamps" + date1;
                       // Log.e("Prev. tableName", TableName);
                        // new MyLogger().storeMassage("Table Name ", tableName);

                        String countQuery1 = "select * from " + TableName + " where Status='Null'";
                        Cursor countCR1 = database.rawQuery(countQuery1, null);
                        totalCount = countCR1.getCount();
                        countCR1.close();

                        //Log.e("totalCount", "" + totalCount);
                        i++;

                        if (totalCount != 0) {

                            String sqlQuery = "select * from " + TableName + " where Status='Null' order by SrNo desc limit 100";
                            Cursor cr = database.rawQuery(sqlQuery, null);

                            sb = new StringBuffer();
                            sb1 = new StringBuffer();

                            cr.moveToFirst();
                            while (!cr.isAfterLast()) {
                                //String str1 = cr.getString(cr.getColumnIndex("DateTime"));
                                String str2 = cr.getString(cr.getColumnIndex("RegularStamps"));
                                sb.append(str2 + "\n");
                                //sb.append(str + "\n" );
                                int i1 = cr.getInt(cr.getColumnIndex("SrNo"));
                                sb1.append("'" + i1 + "',");
                                cr.moveToNext();
                            }

                            cr.close();

                            returnData = sb.toString();
                            srNo = sb1.toString();

                            //new MyLogger().storeMassage("Return Data", returnData);
                            Log.e("Return Data", returnData);
                            returnData = returnData + "%" + srNo + "%" + TableName;
                            Log.e("returnData", returnData + " " + srNo + " " + TableName);
                        }

                    }

                } else {
                    String sqlQuery = "select * from " + TableName + " where Status='Null' order by SrNo desc limit 100";
                    Cursor cr = database.rawQuery(sqlQuery, null);

                    sb = new StringBuffer();
                    sb1 = new StringBuffer();

                    cr.moveToFirst();
                    while (!cr.isAfterLast()) {
                        //String str1 = cr.getString(cr.getColumnIndex("DateTime"));
                        String str2 = cr.getString(cr.getColumnIndex("RegularStamps"));
                        sb.append(str2 + "\n");
                        // sb.append(str + "\n" );
                        int i = cr.getInt(cr.getColumnIndex("SrNo"));
                        sb1.append("'" + i + "',");
                        cr.moveToNext();
                    }

                    cr.close();

                    returnData = sb.toString();
                    srNo = sb1.toString();

                    //new MyLogger().storeMassage("Return Data", returnData);
                    Log.e("Return Data", returnData);
                    returnData = returnData + "%" + srNo + "%" + TableName;
                }

            } catch (Exception e) {
                Log.e("Database Exception", e.getMessage());
               // new MyLogger().storeMassage("Exception while Retriever RegularStamps Database", e.getMessage());
                //new DatabaseOperations(mContext).storeRegularLog("2", "Exception while Retriever Database in retrieveMailData method");
                //new DatabaseOperations(mContext).insertData(exceptionLogDatabase, Tag + "Exception while retrieve data");
            }

            database.close();
        } else {
            new MyLogger().storeMassage("Retriever RegularStamps databaseName not found", RegularStampsDatabase);
            //new DatabaseOperations(mContext).insertData(exceptionLogDatabase, Tag + "Database not found-" + databaseName);
            // new DatabaseOperations(mContext).storeExceptionLog("3", "Exception while Retriever databaseName not found in retrieveMailData method");
        }

        return returnData;
    }

    // Method for update status after read data from database
    public void UpdateRegStatus(String srNo, String tableName) {

        String RegularStampsDatabase = Environment.getExternalStorageDirectory().getPath() + "/DDRegularStamps.db";

        try {
            SQLiteDatabase database = mContext.openOrCreateDatabase(RegularStampsDatabase, Context.MODE_PRIVATE, null);
            database.execSQL("update " + tableName + " set Status=(datetime('now','localtime')) where SrNo in(" + srNo + "'0')");
            database.close();
        } catch (Exception e) {
            new MyLogger().storeMassage("Exception while updateStatus of RegularStamps Database", e.getMessage());
            //new DatabaseOperations(mContext).storeExceptionLog("3", "Exception in updateStatus method");
            //new DatabaseOperations(mContext).insertData(exceptionLogDatabase, Tag + "Exception while updateStatus");
        }

    }

    //==============>Method for retrieve Data from database
    public String RetrieveExData() {

        String ExceptionStampsDatabase = Environment.getExternalStorageDirectory().getPath() + "/DDExceptionStamps.db";


        String returnData = "finished", srNo = "", incidentData = "finished";
        StringBuffer sb, sb1;

        File dbFile = mContext.getDatabasePath(ExceptionStampsDatabase);
        // new MyLogger().storeMassage("DatabaseName", databaseName);
        //new DatabaseOperations(mContext).storeRegularLog("1", "");

        if (dbFile.exists()) {

            Calendar calendar1 = Calendar.getInstance();
            // cal.add(Calendar.DATE, -1);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            //SimpleDateFormat stf = new SimpleDateFormat("HH:mm:ss");
            String date = sdf.format(calendar1.getTime());
            //String time = stf.format(cal.getTime());
            TableName = "t_ExceptionStamps" + date;
            // new MyLogger().storeMassage("Table Name", tableName);

            SQLiteDatabase database = mContext.openOrCreateDatabase(ExceptionStampsDatabase, Context.MODE_PRIVATE, null);

            try {
                String countQuery = "select * from " + TableName + " where Status='Null'";
                Cursor countCR = database.rawQuery(countQuery, null);
                int dataCount = countCR.getCount();
                countCR.close();

                Log.e("dataCount", "" + dataCount);

                if (dataCount == 0) {

                    int i = 1;
                    int totalCount = 0;

                    while (totalCount == 0) {

                        Calendar calendar2 = Calendar.getInstance();
                        calendar2.add(Calendar.DATE, -i);
                        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
                        String date1 = sdf1.format(calendar2.getTime());
                        TableName = "t_ExceptionStamps" + date1;
                       // Log.e("Prev. tableName", TableName);
                        // new MyLogger().storeMassage("Table Name ", tableName);

                        String countQuery1 = "select * from " + TableName + " where Status='Null'";
                        Cursor countCR1 = database.rawQuery(countQuery1, null);
                        totalCount = countCR1.getCount();
                        countCR1.close();

                        i++;

                        if (totalCount != 0) {

                            String sqlQuery = "select * from " + TableName + " where Status='Null' order by SrNo desc limit 150";
                            Cursor cr = database.rawQuery(sqlQuery, null);

                            sb = new StringBuffer();
                            sb1 = new StringBuffer();

                            cr.moveToFirst();
                            while (!cr.isAfterLast()) {
                                //String str1 = cr.getString(cr.getColumnIndex("DateTime"));
                                String str2 = cr.getString(cr.getColumnIndex("ExceptionStamps"));
                                sb.append(str2 + "\n");
                                //sb.append(str + "\n" );
                                int i1 = cr.getInt(cr.getColumnIndex("SrNo"));
                                sb1.append("'" + i1 + "',");
                                cr.moveToNext();
                            }

                            cr.close();

                            returnData = sb.toString();
                            srNo = sb1.toString();

                            //new MyLogger().storeMassage("Return Data", returnData);
                           // Log.e("Return Data", returnData);
                            returnData = returnData + "%" + srNo + "%" + TableName;
                           // Log.e("returnData", returnData + " " + srNo + " " + TableName);
                        }

                    }

                } else {
                    String sqlQuery = "select * from " + TableName + " where Status='Null' order by SrNo desc limit 150";
                    Cursor cr = database.rawQuery(sqlQuery, null);

                    sb = new StringBuffer();
                    sb1 = new StringBuffer();

                    cr.moveToFirst();
                    while (!cr.isAfterLast()) {
                        String str1 = cr.getString(cr.getColumnIndex("DateTime"));
                        String str2 = cr.getString(cr.getColumnIndex("ExceptionStamps"));
                        sb.append(str2 + "\n");
                        // sb.append(str + "\n" );
                        int i = cr.getInt(cr.getColumnIndex("SrNo"));
                        sb1.append("'" + i + "',");
                        cr.moveToNext();
                    }

                    cr.close();

                    returnData = sb.toString();
                    srNo = sb1.toString();

                    //new MyLogger().storeMassage("Return Data", returnData);
                  //  Log.e("Return Data", returnData);
                    returnData = returnData + "%" + srNo + "%" + TableName;
                }

            } catch (Exception e) {
                Log.e("Database Exception", e.getMessage());
              //  new MyLogger().storeMassage("Exception while Retriever ExceptionStamps Database", e.getMessage());
                //new DatabaseOperations(mContext).storeRegularLog("2", "Exception while Retriever Database in retrieveMailData method");
                //new DatabaseOperations(mContext).insertData(exceptionLogDatabase, Tag + "Exception while retrieve data");
            }

            database.close();
        } else {
            new MyLogger().storeMassage("Retriever Exception databaseName not found", ExceptionStampsDatabase);
            //new DatabaseOperations(mContext).insertData(exceptionLogDatabase, Tag + "Database not found-" + databaseName);
            //new DatabaseOperations(mContext).storeExceptionLog("3", "Exception while Retriever databaseName not found in retrieveMailData method");
        }

        return returnData;
    }

    // Method for update status after read data from database
    public void UpdateExStatus(String srNo, String tableName) {

        String ExceptionStampsDatabase = Environment.getExternalStorageDirectory().getPath() + "/DDExceptionStamps.db";

        try {
            SQLiteDatabase database = mContext.openOrCreateDatabase(ExceptionStampsDatabase, Context.MODE_PRIVATE, null);
            database.execSQL("update " + tableName + " set Status=(datetime('now','localtime')) where SrNo in(" + srNo + "'0')");
            database.close();
        } catch (Exception e) {
            new MyLogger().storeMassage("Exception while updateStatus of ExceptionStamps Database", e.getMessage());
            //new DatabaseOperations(mContext).storeExceptionLog("3", "Exception in updateStatus method");
            //new DatabaseOperations(mContext).insertData(exceptionLogDatabase, Tag + "Exception while updateStatus");
        }

    }

    public void UpdateIncidentDataStatus(String databaseName, String srNo, String tableName) {

        try {
            SQLiteDatabase database = mContext.openOrCreateDatabase(databaseName, Context.MODE_PRIVATE, null);
            database.execSQL("update " + tableName + " set Status=(datetime('now','localtime')) where SrNo in(" + srNo + "'0')");
            database.close();
        } catch (Exception e) {
            new MyLogger().storeMassage("Exception while updateStatus of IncidentData Database", e.getMessage());
            //new DatabaseOperations(mContext).storeExceptionLog("3", "Exception in updateStatus method");
            //new DatabaseOperations(mContext).insertData(exceptionLogDatabase, Tag + "Exception while updateStatus");
        }
    }

    public void storeNGStamp(String NGStamps) {
        String CanDatabaseName = Environment.getExternalStorageDirectory().getPath() + "/DDCANDatabase.db";
        try {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String date = sdf.format(cal.getTime());
            String tableName = "t_NGStamps" + date;

            database = mContext.openOrCreateDatabase(CanDatabaseName, mContext.MODE_PRIVATE, null);
            String sqlQuery1 = "create table if not exists " + tableName + "(SrNo INTEGER PRIMARY KEY AUTOINCREMENT, DateTime datetime default (datetime('now','localtime')), NGStamps varchar, Status varchar DEFAULT 'Null')";
            database.execSQL(sqlQuery1);
            database.execSQL("insert into " + tableName + "(NGStamps) values('" + NGStamps + "')");
            database.close();

        } catch (Exception e) {
            Log.e("not store NG data", e.getMessage());
        }
    }

    public String RetrieveNGData() {
       // new MyLogger().storeMassage("RetrieveNGData method", "called");

        String CanDatabaseName = Environment.getExternalStorageDirectory().getPath() + "/DDCANDatabase.db";

        String returnData = "finished", srNo = "";
        StringBuffer sb, sb1;

        File dbFile = mContext.getDatabasePath(CanDatabaseName);
        // new MyLogger().storeMassage("DatabaseName", databaseName);
        //new DatabaseOperations(mContext).storeRegularLog("1", "");

        if (dbFile.exists()) {

            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            //SimpleDateFormat stf = new SimpleDateFormat("HH:mm:ss");
            String date = sdf.format(cal.getTime());
            //String time = stf.format(cal.getTime());
            TableName = " t_NGStamps" + date;
            // new MyLogger().storeMassage("Table Name", tableName);

            database = mContext.openOrCreateDatabase(CanDatabaseName, Context.MODE_PRIVATE, null);

            try {
                String countQuery = "select * from " + TableName + " where Status='Null'";
                Cursor countCR = database.rawQuery(countQuery, null);
                int dataCount = countCR.getCount();
                countCR.close();

                Log.e("dataCount", "" + dataCount);

                if (dataCount == 0) {

                    int i = 1;
                    int totalCount = 0;

                    while (totalCount == 0) {

                        Calendar cal1 = Calendar.getInstance();
                        cal.add(Calendar.DATE, -i);
                        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
                        String date1 = sdf1.format(cal1.getTime());
                        TableName = "t_NGStamps" + date1;
                      //  Log.e("Prev. tableName", TableName);
                        // new MyLogger().storeMassage("Table Name ", tableName);

                        String countQuery1 = "select * from " + TableName + " where Status='Null'";
                        Cursor countCR1 = database.rawQuery(countQuery1, null);
                        totalCount = countCR1.getCount();
                        countCR1.close();

                       // Log.e("totalCount", "" + totalCount);
                        i++;

                        if (totalCount != 0) {

                            String sqlQuery = "select * from " + TableName + " where Status='Null' order by SrNo desc limit 100";
                            Cursor cr = database.rawQuery(sqlQuery, null);

                            sb = new StringBuffer();
                            sb1 = new StringBuffer();

                            cr.moveToFirst();
                            while (!cr.isAfterLast()) {
                                //String str1 = cr.getString(cr.getColumnIndex("DateTime"));
                                String str2 = cr.getString(cr.getColumnIndex("NGStamps"));
                                sb.append(str2 + "\n");
                                //sb.append(str + "\n" );
                                int i1 = cr.getInt(cr.getColumnIndex("SrNo"));
                                sb1.append("'" + i1 + "',");
                                cr.moveToNext();
                            }

                            cr.close();

                            returnData = sb.toString();
                            srNo = sb1.toString();

                            //new MyLogger().storeMassage("Return Data", returnData);
                            //Log.e("Return Data", returnData);
                            returnData = returnData + "%" + srNo + "%" + TableName;
                           // Log.e("returnData", returnData + " " + srNo + " " + TableName);
                        }

                    }

                } else {
                    String sqlQuery = "select * from " + TableName + " where Status='Null' order by SrNo desc limit 100";
                    Cursor cr = database.rawQuery(sqlQuery, null);

                    sb = new StringBuffer();
                    sb1 = new StringBuffer();

                    cr.moveToFirst();
                    while (!cr.isAfterLast()) {
                        //String str1 = cr.getString(cr.getColumnIndex("DateTime"));
                        String str2 = cr.getString(cr.getColumnIndex("NGStamps"));
                        sb.append(str2 + "\n");
                        // sb.append(str + "\n" );
                        int i = cr.getInt(cr.getColumnIndex("SrNo"));
                        sb1.append("'" + i + "',");
                        cr.moveToNext();
                    }

                    cr.close();

                    returnData = sb.toString();
                    srNo = sb1.toString();

                    //new MyLogger().storeMassage("Return Data", returnData);
                  //  Log.e("Return Data", returnData);
                    returnData = returnData + "%" + srNo + "%" + TableName;
                }

            } catch (Exception e) {
                Log.e("Database Exception", e.getMessage());
               // new MyLogger().storeMassage("Exception while Retriever NGStamps Database", e.getMessage());
                //new DatabaseOperations(mContext).storeRegularLog("2", "Exception while Retriever Database in retrieveMailData method");
                //new DatabaseOperations(mContext).insertData(exceptionLogDatabase, Tag + "Exception while retrieve data");
            }

            database.close();
        } else {
            new MyLogger().storeMassage("Retriever NGStamps databaseName not found", CanDatabaseName);
            //new DatabaseOperations(mContext).insertData(exceptionLogDatabase, Tag + "Database not found-" + databaseName);
            // new DatabaseOperations(mContext).storeExceptionLog("3", "Exception while Retriever databaseName not found in retrieveMailData method");
        }

        return returnData;
    }

    public void UpdateNGStatus(String srNo, String tableName) {

        String CanDatabaseName = Environment.getExternalStorageDirectory().getPath() + "/DDCANDatabase.db";

        try {
            SQLiteDatabase database = mContext.openOrCreateDatabase(CanDatabaseName, Context.MODE_PRIVATE, null);
            database.execSQL("update " + tableName + " set Status=(datetime('now','localtime')) where SrNo in(" + srNo + "'0')");
            database.close();
        } catch (Exception e) {
            new MyLogger().storeMassage("Exception while updateStatus of NGStamps Database", e.getMessage());
            //new DatabaseOperations(mContext).storeExceptionLog("3", "Exception in updateStatus method");
            //new DatabaseOperations(mContext).insertData(exceptionLogDatabase, Tag + "Exception while updateStatus");
        }

    }

    //==============>Method for retrieve Data from database
    public String RetrieveGSMData() {
        //new MyLogger().storeMassage("RetrieveGSMData method", "called");

        String RegularStampsDatabase = Environment.getExternalStorageDirectory().getPath() + "/GSMStamps.db";

        String returnData = "finished", srNo = "";
        StringBuffer sb, sb1;

        //File dbFile = mContext.getDatabasePath(RegularStampsDatabase);
        // new MyLogger().storeMassage("DatabaseName", databaseName);
        //new DatabaseOperations(mContext).storeRegularLog("1", "");
        File dbFile=new File(RegularStampsDatabase);

        if (dbFile.exists()) {

            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            //SimpleDateFormat stf = new SimpleDateFormat("HH:mm:ss");
            String date = sdf.format(cal.getTime());
            //String time = stf.format(cal.getTime());
            TableName = " t_GSMStamps" + date;
            // new MyLogger().storeMassage("Table Name", tableName);

            database = mContext.openOrCreateDatabase(RegularStampsDatabase, Context.MODE_PRIVATE, null);

            try {
                String countQuery = "select * from " + TableName + " where Status='Null'";
                Cursor countCR = database.rawQuery(countQuery, null);
                int dataCount = countCR.getCount();
                countCR.close();

                Log.e("dataCount", "" + dataCount);

                if (dataCount == 0) {

                    int i = 1;
                    int totalCount = 0;

                    while (totalCount == 0) {

                        Calendar cal1 = Calendar.getInstance();
                        cal.add(Calendar.DATE, -i);
                        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
                        String date1 = sdf1.format(cal1.getTime());
                        TableName = "t_GSMStamps" + date1;
                        // Log.e("Prev. tableName", TableName);
                        // new MyLogger().storeMassage("Table Name ", tableName);

                        String countQuery1 = "select * from " + TableName + " where Status='Null'";
                        Cursor countCR1 = database.rawQuery(countQuery1, null);
                        totalCount = countCR1.getCount();
                        countCR1.close();

                        //Log.e("totalCount", "" + totalCount);
                        i++;

                        if (totalCount != 0) {

                            String sqlQuery = "select * from " + TableName + " where Status='Null' order by SrNo desc limit 100";
                            Cursor cr = database.rawQuery(sqlQuery, null);

                            sb = new StringBuffer();
                            sb1 = new StringBuffer();

                            cr.moveToFirst();
                            while (!cr.isAfterLast()) {
                                //String str1 = cr.getString(cr.getColumnIndex("DateTime"));
                                String str2 = cr.getString(cr.getColumnIndex("GSMStamps"));
                                sb.append(str2 + "\n");
                                //sb.append(str + "\n" );
                                int i1 = cr.getInt(cr.getColumnIndex("SrNo"));
                                sb1.append("'" + i1 + "',");
                                cr.moveToNext();
                            }

                            cr.close();

                            returnData = sb.toString();
                            srNo = sb1.toString();

                            //new MyLogger().storeMassage("Return Data", returnData);
                           // Log.e("Return Data", returnData);
                            returnData = returnData + "%" + srNo + "%" + TableName;
                           // Log.e("returnData", returnData + " " + srNo + " " + TableName);
                        }

                    }

                } else {
                    String sqlQuery = "select * from " + TableName + " where Status='Null' order by SrNo desc limit 100";
                    Cursor cr = database.rawQuery(sqlQuery, null);

                    sb = new StringBuffer();
                    sb1 = new StringBuffer();

                    cr.moveToFirst();
                    while (!cr.isAfterLast()) {
                        //String str1 = cr.getString(cr.getColumnIndex("DateTime"));
                        String str2 = cr.getString(cr.getColumnIndex("GSMStamps"));
                        sb.append(str2 + "\n");
                        // sb.append(str + "\n" );
                        int i = cr.getInt(cr.getColumnIndex("SrNo"));
                        sb1.append("'" + i + "',");
                        cr.moveToNext();
                    }

                    cr.close();

                    returnData = sb.toString();
                    srNo = sb1.toString();

                    //new MyLogger().storeMassage("Return Data", returnData);
                    Log.e("Return Data", returnData);
                    returnData = returnData + "%" + srNo + "%" + TableName;
                }

            } catch (Exception e) {
                Log.e("Database Exception", e.getMessage());
               // new MyLogger().storeMassage("Exception while Retriever GSMStamps Database", e.getMessage());
                //new DatabaseOperations(mContext).storeRegularLog("2", "Exception while Retriever Database in retrieveMailData method");
                //new DatabaseOperations(mContext).insertData(exceptionLogDatabase, Tag + "Exception while retrieve data");
            }
            database.close();
        } else {
            new MyLogger().storeMassage("Retriever GSMStamps databaseName not found", RegularStampsDatabase);
            //new DatabaseOperations(mContext).insertData(exceptionLogDatabase, Tag + "Database not found-" + databaseName);
            // new DatabaseOperations(mContext).storeExceptionLog("3", "Exception while Retriever databaseName not found in retrieveMailData method");
        }
        return returnData;
    }

    public void UpdateGSMStatus(String srNo, String tableName) {

        String CanDatabaseName = Environment.getExternalStorageDirectory().getPath() + "/GSMStamps.db";

        try {
            SQLiteDatabase database = mContext.openOrCreateDatabase(CanDatabaseName, Context.MODE_PRIVATE, null);
            database.execSQL("update " + tableName + " set Status=(datetime('now','localtime')) where SrNo in(" + srNo + "'0')");
            database.close();
        } catch (Exception e) {
            new MyLogger().storeMassage("Exception while updateStatus of GSMStamps Database", e.getMessage());
            //new DatabaseOperations(mContext).storeExceptionLog("3", "Exception in updateStatus method");
            //new DatabaseOperations(mContext).insertData(exceptionLogDatabase, Tag + "Exception while updateStatus");
        }
    }


    public void userDetail(String uNM, String emaiID, String mNo, String comNm, String address, int getOTP) {
       // new MyLogger().storeMassage("userDetails method", "Called");
        try {
            SQLiteDatabase database = mContext.openOrCreateDatabase(uDB, mContext.MODE_PRIVATE, null);
            String sqlQuery1 = "create table if not exists otp (userName varchar(50),emailId varchar(50),mobileNo integer(20),compName varchar(50),address varchar(50),userotp varchar(10),unitID varchar(10) default null,vehCode varchar(10) default null)";
            database.execSQL(sqlQuery1);
            database.execSQL("insert into otp (userName,emailId,mobileNo,compName,address,userotp) values('" + uNM + "','" + emaiID + "','" + mNo + "','" + comNm + "','" + address + "','" + getOTP + "')");
            database.close();
            Log.e("OTP successfully ", "Inserted ! ! ! !"+getOTP);
           // new MyLogger().storeMassage("insert details", "Successfully");
        } catch (Exception e) {
            Log.e("Exception while OTP ", e.getMessage());
        }
    }

    public void updateUidVehCode(String unitID, String vehCode){
       // new MyLogger().storeMassage("updateUidVehicleCode method called","!!!"+unitID+" "+vehCode);
        try {
            SQLiteDatabase database = mContext.openOrCreateDatabase(uDB, mContext.MODE_PRIVATE, null);
            database.execSQL("update otp set unitID='" + unitID + "', vehCode='"+vehCode+"'");
            database.close();
            //new MyLogger().storeMassage("unitid vehicle code updated  ", "successfully ! ! ! !");
           // Log.e("unitid vehicleupdated  ", "successfully ! ! ! !");
        }catch (Exception e){
            Log.e("Exception while ","Updating OTP");
            new MyLogger().storeMassage("Exception while ","updating uid and vehicle code "+e.getMessage());
        }
    }

    public void updateOTP(String nm, String email, String phone, String address, int otp){
        Log.e("Data to be post",""+nm+", "+email+", "+phone+", "+address+", "+otp);
        try {
            SQLiteDatabase database = mContext.openOrCreateDatabase(uDB, mContext.MODE_PRIVATE, null);
            database.execSQL("update otp set userName='" + nm + "', emailId='" + email + "', mobileNo='" + phone + "', address='"+address+"', userotp='" + otp + "'");
            database.close();
            Log.e("OTP  updated  ", "successfully ! ! ! !");
        }catch (Exception e){
            Log.e("Exception while ","Updating OTP");
        }
    }

    public int readOTP(){
        int lastOTP=0;
        try {
            SQLiteDatabase database = mContext.openOrCreateDatabase(uDB, mContext.MODE_PRIVATE, null);
            Cursor c = database.rawQuery("select * from otp", null);
            c.moveToFirst();
            do {
                lastOTP = c.getInt(5);
                Log.e("otp from DB"," : "+lastOTP);
            } while (c.moveToNext());
            c.close();
            database.close();
            Log.e("OTP Retrieved ","Successfully");
        }catch (Exception e){
            Log.e("Exception while reading","OTP from db : "+e.getMessage());
        }
        return lastOTP;
    }

    public String getLastRecord() {

        String lastRecordAdded=" ";
        File fl = mContext.getDatabasePath(dailyDataDatabaseName);
        if(fl.exists()){
            try {
                SQLiteDatabase dt = mContext.openOrCreateDatabase(dailyDataDatabaseName, mContext.MODE_PRIVATE, null);
                String lastRecordQuery = "select * from t_dailyData order by TheDate DESC limit 1";
                //Cursor cLastData = dt.rawQuery("select * from t_dailyData order by TheDate DESC limit 1", null);
                Cursor cLastData = dt.rawQuery(lastRecordQuery, null);
                cLastData.moveToFirst();
                do{
                    lastRecordAdded = cLastData.getString(cLastData.getColumnIndex("TheDate"));
                }while(cLastData.moveToNext());

                Log.e("Last Record "," : "+lastRecordAdded);
                dt.close();

            } catch (Exception e) {

                Log.e("Exception while ","retrieving last record detail : "+e.getMessage());
                lastRecordAdded = " ";
            }
        }else {
            try {
                SQLiteDatabase dt2 = mContext.openOrCreateDatabase(dailyDataDatabaseName, mContext.MODE_PRIVATE, null);
                String sqlQuery1 = "create table if not exists t_dailyData (SrNo INTEGER PRIMARY KEY AUTOINCREMENT, RDCount varchar, RACount varchar, Osduration varchar, CRKM varchar, CRHR varchar, Distance varchar, OSCount varchar, VehCode varchar, NDHR varchar, RDurationinHrs varchar, ND varchar, TheDate Date UNIQUE, NDKM varchar)";
                // new MyLogger().storeMassage("Create table", "Successfully");
                dt2.execSQL(sqlQuery1);
               // dt2.execSQL("insert into t_dailyData (RDCount,RACount,Osduration,CRKM,CRHR,Distance,OSCount,VehCode,NDHR,RDurationinHrs,ND,TheDate,NDKM) values('" + strRD + "','" + strRA + "','" + strOSduration + "','" + strCRKM + "','" + strCRHR + "','" + strDistance + "', '" + strOSCount + "', '" + strVehcode + "','" + strNDHR + "','" + strRDurationinHrs + "', '" + strND + "', '" + strDate + "', '" + strNDKM + "')");
                //new MyLogger().storeMassage("insert data", "Successfully");
                Log.e("Daily Database ","Created Successfully");
                database.close();
            } catch (Exception e) {
                e.getMessage();
                new MyLogger().storeMassage("Exception while insert data", ""+e.getMessage());
            }
        }

        return lastRecordAdded;
    }

    public void deleteOldRecord() {

        Log.e("deleteOLD Record method", "Called");

        File dbFile2 = mContext.getDatabasePath(dailyDataDatabaseName);

        if (dbFile2.exists()) {

            try{
                SQLiteDatabase db2 = mContext.openOrCreateDatabase(dailyDataDatabaseName, mContext.MODE_PRIVATE, null);
                String deleteQuery = "DELETE FROM t_dailyData WHERE TheDate <= date('now','-30 day')";
                db2.execSQL(deleteQuery);
                db2.close();
                Log.e("Records Deleted ","successfully!!!!");
            }catch (Exception e){
                Log.e("Exception While ","Deleting older records : "+e.getMessage());
                new MyLogger().storeMassage("Exception while ","Deleting Record : "+e.getMessage());
            }
        }
    }

    public void userScore(String score) {
        // new MyLogger().storeMassage("userDetails method", "Called");

        File databaseExistD = mContext.getDatabasePath("scoreUser.db");
        if (databaseExistD.exists()){
            try {
                SQLiteDatabase database = mContext.openOrCreateDatabase("scoreUser.db", mContext.MODE_PRIVATE, null);
                database.execSQL("update UserScore set score='" + score + "'");
                database.close();
                Log.e("score  updated  ", "successfully ! ! ! !");
            }catch (Exception e){
                Log.e("Exception while ","Updating score");
            }
        }else {
            try {
                SQLiteDatabase database = mContext.openOrCreateDatabase("scoreUser.db", mContext.MODE_PRIVATE, null);
                String sqlQuery1 = "create table if not exists UserScore (score varchar(20))";
                database.execSQL(sqlQuery1);
                database.execSQL("insert into UserScore (score) values('" + score + "')");
                database.close();
                Log.e("score successfully ", "Inserted ! ! ! !");
                // new MyLogger().storeMassage("insert details", "Successfully");
            } catch (Exception e) {
                Log.e("Exception while score insertion ", e.getMessage());
            }
        }
    }
}


