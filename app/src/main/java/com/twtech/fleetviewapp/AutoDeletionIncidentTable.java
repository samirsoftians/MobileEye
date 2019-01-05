package com.twtech.fleetviewapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

/**
 * Created by twteh on 3/11/17.
 */

public class AutoDeletionIncidentTable implements Runnable {

    android.content.Context Context;
    String RegularStampsDatabase = Environment.getExternalStorageDirectory().getPath() + "/DDRegularStamp.db";
    String ExceptionStampsDatabase = Environment.getExternalStorageDirectory().getPath() + "/DDExceptionStamp.db";
    String IncidentDataDatabase = Environment.getExternalStorageDirectory().getPath() + "/DDIncidentData.db";
    String Tag="AutoDeletionincidentTable";

    SQLiteDatabase database;
    String TableName;
    StringBuffer sb;
    String TableNameData;
    String IncidentTableLimit, IncTableDeleteSleep;
    int incidentTableLimit, sleepInterval;

    Thread CurrThread;

    public AutoDeletionIncidentTable(android.content.Context context) {
        this.Context = context;

        //new DatabaseOperations(mContext).storeRegularLog("1", "AutoDeletionIncidentTable method from AutoCretionDeletionTable called");
      //  new MyLogger().storeMassage(Tag , " Called");

        CanDatabase canDatabase = new CanDatabase(Context);
        canDatabase.openCanDatabase();

        IncidentTableLimit = canDatabase.getValue("IncidentTableLimit");
        IncTableDeleteSleep = canDatabase.getValue("IncTableDeleteSleep");

        canDatabase.closeCanDatabase();

        incidentTableLimit = Integer.parseInt(IncidentTableLimit);
        sleepInterval = Integer.parseInt(IncTableDeleteSleep);

        //new MyLogger().storeMassage("Retrieve values from CAN Database IncidentTableLimit-" + incidentTableLimit + ", SleepInterval-" + sleepInterval, "");

        CurrThread = new Thread(this);
        CurrThread.start();

    }

    @Override
    public void run() {

        while (true)
            try {
               // new MyLogger().storeMassage(Tag+"tTable ", "Run method **** ");
                //new DatabaseOperations(mContext).storeRegularLog("1", "AutoDeletionIncidentTable Run method ");

                TableDelete(IncidentDataDatabase);
                Thread.sleep(sleepInterval * 60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                new MyLogger().storeMassage(Tag, " Exception while calling Run Method ");
                //new DatabaseOperations(mContext).insertData(exceptionLogDatabase, Tag + "Exception while calling Run Method");
                // new DatabaseOperations(mContext).storeExceptionLog("3", "Exception while AutoDeletionIncidentTable run method");

            }

    }

    public void TableDelete(String databaseName) {

      //  new MyLogger().storeMassage(Tag+" : TableDalete method", "Called");

        database = Context.openOrCreateDatabase(databaseName, Context.MODE_PRIVATE, null);

        String countQuery = "SELECT name FROM sqlite_master WHERE type='table' AND name!='android_metadata' AND name!='sqlite_sequence'";
        Cursor cursor1 = database.rawQuery(countQuery, null);

        sb = new StringBuffer();

        cursor1.moveToFirst();
        while (!cursor1.isAfterLast()) {
            String str = cursor1.getString(cursor1.getColumnIndex("name"));
            sb.append(str + ",");
            cursor1.moveToNext();
        }

        TableNameData = sb.toString();
        String[] tableNameArr = TableNameData.split(",");

       // new MyLogger().storeMassage(Tag+" Table Count", String.valueOf(tableNameArr.length));

        cursor1.close();

        try {
            for (int i = 0; i < tableNameArr.length - incidentTableLimit; i++) {
                String strTableName = tableNameArr[i];
              //  new MyLogger().storeMassage("Table Name", strTableName);

                String sqlQuery1 = "DROP TABLE IF EXISTS " + strTableName;
                database.execSQL(sqlQuery1);
                new MyLogger().storeMassage(Tag+" Table Deleted ",  strTableName);
            }

        } catch (Exception e) {
            new MyLogger().storeMassage(Tag+" : Exception while", "Table deletion");
        }

        database.close();
    }

}
