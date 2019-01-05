package com.twtech.fleetviewapp;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by twteh on 11/4/18.
 */

public class DailyDataWebService implements Runnable {

    Context context;
    String unitId, userName, emailId, mobileNo, compName, address, userOTP, vehCode;
    String lastRecDailyData;
    int dailyDownloadInterval = 6;
    int lastRec;
    RequestQueue requestQueue;
    Thread currThread;
    String strCRHR = "0";
    String Tag = "DailyDataWebService -";
    String lastRecordDate;

    public DailyDataWebService(Context context) {
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);

        // new MyLogger().storeMassage(Tag, " Called");
        try {
            CanDatabase canDatabase = new CanDatabase(context);
            canDatabase.openCanDatabase();
            lastRecDailyData = canDatabase.getValue("lastRecDailyData");
            dailyDownloadInterval = Integer.parseInt(canDatabase.getValue("DailyDataDownloadInterval"));
            canDatabase.closeCanDatabase();
            lastRec = Integer.parseInt(lastRecDailyData);
        } catch (Exception e) {
            new MyLogger().storeMassage(Tag + " : Exception while get Can value", "");
        }
        currThread = new Thread(this);
        currThread.start();
    }

    @Override
    public void run() {

        while (true)
            try {
                // new MyLogger().storeMassage("run method", "Called");
                loadDailyData();
                currThread.sleep(dailyDownloadInterval * 60 * 60 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                new MyLogger().storeMassage(Tag + " : Exception while thread sleep", "");
            }
    }

    public void loadDailyData() {

        lastRecordDate = new String();

        try {
            String userDetails = new DatabaseOperation(context).retrieveUserDetailsData();
            new MyLogger().storeMassage(Tag + " : Retrun data -", userDetails);
            String[] str = userDetails.split("%");
            userName = str[0];
            emailId = str[1];
            mobileNo = str[2];
            address = str[3];
            vehCode = str[4];

            new MyLogger().storeMassage(Tag + " : Retrun Data UnitId-", unitId + "emailID-" + emailId + "mobileNo-" + mobileNo + "compName-" + compName + "address-" + address + "otp-" + userOTP + "vehCode-" + vehCode);
            // Log.e("retrun Data UnitId-",unitId+"emailID-"+emailId+"mobileNo-"+mobileNo+"compName-"+compName+"address-"+address+"otp-"+userOTP+"vehCode-"+vehCode);

        } catch (Exception e) {
            new MyLogger().storeMassage(Tag + " : Exception while retrieve data", e.getMessage());
        }

        lastRecordDate = new DatabaseOperation(context).getLastRecord();

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        cal.add(Calendar.DATE, -1);
        final String dateCurrent = sdf.format(cal.getTime());

        if (lastRecordDate.equals(null) || lastRecordDate.equals(" ")) {
            Log.e("Yesterdays Date", " : " + dateCurrent);
            lastRecordDate = dateCurrent;
            Log.e("Current Date : ", lastRecordDate);
        }

        if(lastRecordDate.equals(dateCurrent)){
            cal.add(Calendar.DATE,-1);
            final String dttt = sdf.format(cal.getTime());

            Log.e("old Date",": "+dateCurrent);
            try {
                Log.e(Tag + " : Url DailyData : ", "http://103.241.181.36:8080/VehSummary/rest/VehicleDetails?Username=" + emailId + "&Password=" + mobileNo + "&VehCode=" + vehCode + "&LastRec=" + dttt + "&format=json\n");
                 new MyLogger().storeMassage(Tag+" : Url-","http://103.241.181.36:8080/VehSummary/rest/VehicleDetails?Username="+emailId+"&Password="+mobileNo+"&VehCode="+vehCode+"&LastRec="+dttt+"&format=json\n");
                final JsonArrayRequest jsonarrayRequest = new JsonArrayRequest(
                        Request.Method.GET,
                        //"http://103.241.181.36:8080/VehSummary/rest/VehicleDetails?Username=ubaidullahkhan@bddhalla.com&Password=1gjoQspE&VehCode=11437&LastRec=20&format=json\n",
                        "http://103.241.181.36:8080/VehSummary/rest/VehicleDetails?Username=" + emailId + "&Password=" + mobileNo + "&VehCode=" + vehCode + "&LastRec="+dttt+"&format=json\n",
                        //"http://103.241.181.36:8080/VehSummary/rest/VehicleDetails?Username="+"ubaidullahkhan@bddhalla.com"+"&Password="+"1gjoQspE"+"&VehCode="+"11437"+"&LastRec=20&format=json\n",
                        new JSONArray(),
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                // Log.e("Got Response ","from web service...........");
                                // new MyLogger().storeMassage("got response from web service", ":");
                                new MyLogger().storeMassage(Tag + " : Url -", "http://103.241.181.36:8080/VehSummary/rest/VehicleDetails?Username=" + emailId + "&Password=" + mobileNo + "&VehCode=" + vehCode + "&LastRec=" + dttt + "&format=json\n");
                                try {
                                    for (int i = 0; i < response.length(); i++) {
                                        JSONObject objectjsn = response.getJSONObject(i);
                                        String strRD = objectjsn.getString("RDCount");
                                        String strRA = objectjsn.getString("RACount");
                                        String strOSduration = objectjsn.getString("Osduration");
                                        String strCRKM = objectjsn.getString("CRCount");
                                        //String strCR= objectjsn.getString("CRhr");
                                        String strDistance = objectjsn.getString("Distance");
                                        String strOSCount = objectjsn.getString("OSCount");
                                        String strVehcode = objectjsn.getString("VehCode");
                                        String strNDHR = objectjsn.getString("NDHR");
                                        String strRDurationinHrs = objectjsn.getString("RDurationinHrs");
                                        String strND = objectjsn.getString("ND");
                                        String strDate = objectjsn.getString("TheDate");
                                        String strNDKM = objectjsn.getString("NDKM");
                                        Log.e("Response  : ", "" + strRD + "," + strRA + ", " + strOSduration + ", " + strCRKM + ", " + strDistance + ", " + strOSCount + ", " + strVehcode + ", " + strNDHR + ", " + strRDurationinHrs + ", " + strND + ", " + strDate);
                                        //new MyLogger().storeMassage("data from webservice-", strRD + "," + strRA + ", " + strOSduration + ", " + strCRKM + ", " + strDistance + ", " + strOSCount+", "+strVehcode+", "+strNDHR+", "+strRDurationinHrs+", "+strND+", "+strDate);
                                        new DatabaseOperation(context).updateDailyData(lastRecordDate,strRD, strRA, strOSduration, strCRKM, strCRHR, strDistance, strOSCount, strVehcode, strNDHR, strRDurationinHrs, strND, strDate, strNDKM);
                                    }
                                    // new MyLogger().storeMassage(Tag+ " : insert daily data into DailyData.db database", "Successfully");
                                } catch (JSONException e) {
                                    Log.e("Exception ", " : " + e.getMessage());
                                    new MyLogger().storeMassage(Tag + " : Exception while fetch data", e.getMessage());
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                new MyLogger().storeMassage(Tag + " Error", "While fetching web swervice for daily data");
                            }
                        }
                );
                requestQueue.add(jsonarrayRequest);

            } catch (Exception e) {
                Log.e("Exception ", " : " + e.getMessage());
            }
        }else {
            Log.e("Last DailyDataSummery", " Date : " + lastRecordDate);
            try {
                Log.e(Tag + " : Url DailyData : ", "http://103.241.181.36:8080/VehSummary/rest/VehicleDetails?Username=" + emailId + "&Password=" + mobileNo + "&VehCode=" + vehCode + "&LastRec=" + lastRecordDate + "&format=json\n");
                 new MyLogger().storeMassage(Tag+" : Url-","http://103.241.181.36:8080/VehSummary/rest/VehicleDetails?Username="+emailId+"&Password="+mobileNo+"&VehCode="+vehCode+"&LastRec="+lastRecordDate+"&format=json\n");
                final JsonArrayRequest jsonarrayRequest = new JsonArrayRequest(
                        Request.Method.GET,
                        //"http://103.241.181.36:8080/VehSummary/rest/VehicleDetails?Username=ubaidullahkhan@bddhalla.com&Password=1gjoQspE&VehCode=11437&LastRec=20&format=json\n",
                        "http://103.241.181.36:8080/VehSummary/rest/VehicleDetails?Username=" + emailId + "&Password=" + mobileNo + "&VehCode=" + vehCode + "&LastRec=" + lastRecordDate + "&format=json\n",
                        //"http://103.241.181.36:8080/VehSummary/rest/VehicleDetails?Username="+"ubaidullahkhan@bddhalla.com"+"&Password="+"1gjoQspE"+"&VehCode="+"11437"+"&LastRec=20&format=json\n",
                        new JSONArray(),
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                // Log.e("Got Response ","from web service...........");
                                // new MyLogger().storeMassage("got response from web service", ":");
                                new MyLogger().storeMassage(Tag + " : Url -", "http://103.241.181.36:8080/VehSummary/rest/VehicleDetails?Username=" + emailId + "&Password=" + mobileNo + "&VehCode=" + vehCode + "&LastRec=" + lastRecordDate + "&format=json\n");
                                try {
                                    for (int i = 0; i < response.length(); i++) {
                                        JSONObject objectjsn = response.getJSONObject(i);
                                        String strRD = objectjsn.getString("RDCount");
                                        String strRA = objectjsn.getString("RACount");
                                        String strOSduration = objectjsn.getString("Osduration");
                                        String strCRKM = objectjsn.getString("CRCount");
                                        //String strCR= objectjsn.getString("CRhr");
                                        String strDistance = objectjsn.getString("Distance");
                                        String strOSCount = objectjsn.getString("OSCount");
                                        String strVehcode = objectjsn.getString("VehCode");
                                        String strNDHR = objectjsn.getString("NDHR");
                                        String strRDurationinHrs = objectjsn.getString("RDurationinHrs");
                                        String strND = objectjsn.getString("ND");
                                        String strDate = objectjsn.getString("TheDate");
                                        String strNDKM = objectjsn.getString("NDKM");
                                        Log.e("Response  : ", "" + strRD + "," + strRA + ", " + strOSduration + ", " + strCRKM + ", " + strDistance + ", " + strOSCount + ", " + strVehcode + ", " + strNDHR + ", " + strRDurationinHrs + ", " + strND + ", " + strDate);
                                        //new MyLogger().storeMassage("data from webservice-", strRD + "," + strRA + ", " + strOSduration + ", " + strCRKM + ", " + strDistance + ", " + strOSCount+", "+strVehcode+", "+strNDHR+", "+strRDurationinHrs+", "+strND+", "+strDate);
                                        new DatabaseOperation(context).insertDailyData(strRD, strRA, strOSduration, strCRKM, strCRHR, strDistance, strOSCount, strVehcode, strNDHR, strRDurationinHrs, strND, strDate, strNDKM);
                                    }
                                    // new MyLogger().storeMassage(Tag+ " : insert daily data into DailyData.db database", "Successfully");
                                } catch (JSONException e) {
                                    Log.e("Exception ", " : " + e.getMessage());
                                    new MyLogger().storeMassage(Tag + " : Exception while fetch data", e.getMessage());
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                new MyLogger().storeMassage(Tag + " Error", "While fetching web swervice for daily data");
                            }
                        }
                );
                requestQueue.add(jsonarrayRequest);
                new DatabaseOperation(context).deleteOldRecord();
            } catch (Exception e) {
                Log.e("Exception ", " : " + e.getMessage());
            }
        }
    }
}