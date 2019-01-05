package com.twtech.fleetviewapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Deepali shinde on 25/4/18.
 */

public class GetGSLocation implements Runnable {
    Context mCtx;
    GsmCellLocation location;
    int cellID, lac, mcc, mnc;
    Thread thisThread;
    String Tag="GetGSLocation";
    String locString;
    String lastData,lastDatanew;
    DatabaseOperation databaseOperation;
    String url = "https://api.mylnikov.org/geolocation/cell?v=1.1&data=open&mcc=405&mnc=864&lac=135&cellid=1302545";
    public GetGSLocation(Context mCtx) {
        this.mCtx = mCtx;
        thisThread = new Thread(this);
        thisThread.start();
        databaseOperation = new DatabaseOperation(mCtx);
    }

    @Override
    public void run() {
        while (true) {
            generateGSStamp();
        }
    }

    public void generateGSStamp() {
       // Log.e("generateGSStamps ", "method Called");
        try {
            thisThread.sleep(1 * 60 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        TelephonyManager tel = (TelephonyManager) mCtx.getSystemService(Context.TELEPHONY_SERVICE);
        String networkOperator = tel.getNetworkOperator();
        if (!TextUtils.isEmpty(networkOperator)) {
            mcc = Integer.parseInt(networkOperator.substring(0, 3));
            mnc = Integer.parseInt(networkOperator.substring(3));
        }

        ///*****************************************

        TelephonyManager tm = (TelephonyManager) mCtx.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(mCtx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mCtx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

try {
    location = (GsmCellLocation) tm.getCellLocation();
    cellID = location.getCid();
    lac = location.getLac();
    locString = location.toString();
   // Log.e("mcc : " + mcc, "mnc : " + mnc + "lac : " + lac + ", CellId : " + cellID);
    //new MyLogger().storeMassage("mcc : " + mcc, "mnc : " + mnc + "lac : " + lac + ", CellId : " + cellID);


    //loadData();

    lastData = databaseOperation.retrieveStampsData();
    lastDatanew = lastData.substring(lastData.indexOf(",") + 1);
   // new MyLogger().storeMassage("lastData", "" + lastDatanew);

    //SI,070518,061259,1831.900609,N,07356.234223,E,,0.0,165.07803,0.0,A$,C1,405,864,135,71302545

    String gsmStamp="SI,"+lastDatanew+"$"+",C,"+mcc+","+mnc+","+cellID+","+lac;
  //  new MyLogger().storeMassage("GSM-SI"," : "+gsmStamp);
    Log.e("GSM-SI"," : "+gsmStamp);
    new DatabaseOperation(mCtx).storeGSMStamp(gsmStamp, locString, lac, cellID);

}catch (Exception e){
           // new MyLogger().storeMassage("Exception while"," getting cell is information "+e.getMessage());
}
    }

    public void loadData() {

        //new MyLogger().storeMassage("GSM loadData ","method Called@@@@@@@@");

      //  Log.e("GSM Data ",location+", "+lac+", "+cellID);
        String gsmUrl="https://api.mylnikov.org/geolocation/cell?v=1.1&data=open&mcc=" + mcc + "&mnc=" + mnc + "&lac=" + cellID + "&cellid=" + lac;
      //  new MyLogger().storeMassage("GSM url",""+gsmUrl);

       /* Calendar caldar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy");
        SimpleDateFormat stf = new SimpleDateFormat("HHmmss");
        *//*sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        stf.setTimeZone(TimeZone.getTimeZone("GMT"));*//*
        final String date1 = sdf.format(caldar.getTime());
        final String time1 = stf.format(caldar.getTime());*/
        try {

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(


                    Request.Method.GET,
                    "https://api.mylnikov.org/geolocation/cell?v=1.1&data=open&mcc=" + mcc + "&mnc=" + mnc + "&lac=" + cellID + "&cellid=" + lac,
                    new JSONObject(),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONObject jsonObject = response.getJSONObject("data");
                                String Lat = jsonObject.getString("lat");
                                String Long = jsonObject.getString("lon");

                               // Log.e("Before Conver Lat  : " + Lat, " Long : " + Long);
                               // new MyLogger().storeMassage("Before Conver Lat  : " + Lat, " Long : " + Long);

                                Date localTime = new Date();
                                //Date localTime = Da"";
                                DateFormat converter = new SimpleDateFormat("ddMMyy,HHmmss");
                                converter.setTimeZone(TimeZone.getTimeZone("GMT"));
                                // System.out.println("local time : " + localTime);;
                                System.out.println("time in GMT : " + converter.format(localTime));
                              //  Log.e("Time, ", converter.format(localTime));
                                String datetimeGMT = converter.format(localTime);
                                String parts[] = datetimeGMT.split(",");

                                double latVal = Double.parseDouble(Lat);
                                double longVal = Double.parseDouble(Long);

                                String lat11 = convertToStandard(latVal);
                                String long11 = convertToStandard(longVal);

                               // Log.e("Latitude : " + lat11, "Longitude : " + long11);

                                //Toast.makeText(mCtx, "Lat and Long-" + latiTude + " " + longiTude, Toast.LENGTH_LONG).show();
                                //GSM,230418,114250,1903.09983,N,7252.68216,E,0.0,0,0,0.0,G
                                //String gsStamp = "GSM," + parts[0] + "," + parts[1] + "," + latiTude + ", ," + longiTude+", , , , "+", ,G";
                                String gsStamp = "GSM1," + parts[0] + "," + parts[1] + "," + lat11 + ", ," + long11 + ", ,0.0,0,0,0.0,G";
                                //GSM,230418,114250,1903.09983,N,7252.68216,E,0.0,0,0,0.0,G
                                //Log.e("GSM stamp ", gsStamp);
                                new DatabaseOperation(mCtx).storeGSMStamp(gsStamp, locString, lac, cellID);
                               // new MyLogger().storeMassage("GS Stamp ", "" + gsStamp);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                new MyLogger().storeMassage("GSM Stamp Generation Exception : ", e.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    }
            );
            RequestQueue requestQueue = Volley.newRequestQueue(mCtx);
            requestQueue.add(jsonObjectRequest);

        }catch (Exception e){
            new MyLogger().storeMassage("Exception while GSM ","Location get"+e.getMessage());
        }
    }

    public String convertToStandard(double x){

        long iPart;
        iPart = (long) x;
        String convertto4digit1 = "";
        double convertto4digit = ((iPart * 100) + ((x - iPart) * 60));
        DecimalFormat df = new DecimalFormat("####.0000");
        convertto4digit1 = df.format(convertto4digit);

        return convertto4digit1;
    }

    private double revConvertToStandard(double x) {
        float convertedVal;
        double v = x / 100;
        long v1 = (long) (v);
        float v2 = (float) (v - v1);
        float v3 = (v2 * 100) / 60;
        convertedVal = v1 + v3;
        DecimalFormat decimalFormat = new DecimalFormat(".000000");
        double d = Double.parseDouble(decimalFormat.format(convertedVal));
        return d;
    }

    /*int ISTtoGMT(char *time_string)
    {
        int i_hr=0,i_Mn=0,i_Sec=0,gmt_time=0;
        int iG_hr=0,iG_Mn=0,iG_Sec=0;
        int total_sec=0;
        int len=0;
        //char time_string[10]={"110000"};
        //  char time_string[10]={'\0'};
        char HR[5]={'\0'};
        char MN[5]={'\0'};
        char SEC[5]={'\0'};
        char dummy_time[10]={'\0'};
        char timetest[5]={'\0'};

        //OUT_D1EBUG(textBuf,"ISTtoGMT time_string =%s,length=%d\r\n",time_string,Ql_strlen(time_string));
        HR[0]=time_string[0];
        HR[1]=time_string[1];
        HR[2]='\0';

        MN[0]=time_string[2];
        MN[1]=time_string[3];
        MN[2]='\0';

        SEC[0]=time_string[4];
        SEC[1]=time_string[5];
        SEC[2]='\0';

        i_hr = Ql_atoi(HR);
        i_Mn = Ql_atoi(MN);
        i_Sec = Ql_atoi(SEC);

        total_sec=((i_hr*3600)+(i_Mn*60)+(i_Sec));

        total_sec=total_sec-19800;

        if(total_sec < 0)
        {
            total_sec=total_sec+86400;
            iG_hr=total_sec/3600;
            total_sec=total_sec%3600;
            iG_Mn=total_sec/60;
            iG_Sec=total_sec%60;
        }
        else
        {
            //total_sec=total_sec+86400;
            iG_hr=total_sec/3600;
            total_sec=total_sec%3600;
            iG_Mn=total_sec/60;
            iG_Sec=total_sec%60;
        }

        Ql_itoa(timetest,iG_hr,10);
        len = strlen(timetest);

        if(len==1)
            strcat(dummy_time,"0");
        strcat(dummy_time,timetest);

        Ql_itoa(timetest,iG_Mn,10);
        len = strlen(timetest);

        if(len==1)
            strcat(dummy_time,"0");
        strcat(dummy_time,timetest);

        Ql_itoa(timetest,iG_Sec,10);
        len = strlen(timetest);

        if(len==1)
            strcat(dummy_time,"0");
        strcat(dummy_time,timetest);
        strcat(dummy_time,"\0");
        //OUT_D1EBUG(textBuf,"\ndummy_time=%s",dummy_time);
        gmt_time = Ql_atoi(dummy_time);
        //OUT_D1EBUG(textBuf,"\ngmt_time=%d",gmt_time);
        return gmt_time;
    }*/
}
