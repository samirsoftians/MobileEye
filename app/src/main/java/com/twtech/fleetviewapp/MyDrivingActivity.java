package com.twtech.fleetviewapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class MyDrivingActivity extends AppCompatActivity implements View.OnClickListener {

    TextView txtfrmDate, txttoDate, txtmyStatistics, txtKM, txtHR, txtNightDriving, txtNDKM, txtNDHR, txtFatiguedDriving, txtFDKM, txtFDHR, txtMyBehaviour, txtBreakingCount, txtBrakingCount, txtAC, txtACount, txtSpeeding, txtSpeedCount, txtTotalSpeedHR, txtTotalSpeedKM,txtUserName;
    Button btnSubmit;
    String databaseName = Environment.getExternalStorageDirectory().getPath() + "/DragonDroid.db";

    private ScrollView rootContent;
    ImageView imageView;

    private int year;
    private int month;
    private int day;
    private int toyear;
    private int tomonth;
    private int today;
    static final int DATE_PICKER_ID = 1111;
    static final int DATE_PICKER_ToID = 1;
    SessionManager sf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_driving);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar_layout1);
        View view = getSupportActionBar().getCustomView();

        rootContent=(ScrollView)findViewById(R.id.root_content);
        imageView = findViewById(R.id.screen_share);
        btnSubmit = findViewById(R.id.myDrivingSubmitbtn);
        txtfrmDate = findViewById(R.id.fromdateId);
        txttoDate = findViewById(R.id.toDateId);
        txtmyStatistics = findViewById(R.id.mystattxt);
        txtKM = findViewById(R.id.kmtxt);
        txtHR = findViewById(R.id.hrtxt);
        txtNightDriving = findViewById(R.id.ntdrivingtxt);
        txtNDHR = findViewById(R.id.ndhrtxt);
        txtNDKM = findViewById(R.id.ndkmtxt);
        // txtFatiguedDriving = findViewById(R.id.txtFatiguedDriving);
        txtFDKM = findViewById(R.id.ftkmtxt);
        txtFDHR = findViewById(R.id.fthrtxt);
        txtTotalSpeedKM = findViewById(R.id.totalDrivingkm);
        txtTotalSpeedHR = findViewById(R.id.totalDrivinghr);
        txtMyBehaviour = findViewById(R.id.mybehavetxt);
        //txtBreakingCount = findViewById(R.id.brkcttxt);
        txtBrakingCount = findViewById(R.id.brkcttxt);
        // txtAC = findViewById(R.id.);
        txtACount = findViewById(R.id.acccttxt);
        // txtSpeeding = findViewById(R.id.txtSpeeding);
        txtSpeedCount = findViewById(R.id.spdtxt);
        txtUserName=(TextView)findViewById(R.id.tv_username);

        sf = new SessionManager(MyDrivingActivity.this);

        HashMap<String, String> user = sf.getUserDetails();
        String name = user.get(SessionManager.KEY_NAME);

        txtUserName.setText(name);

         /*show current date in TextView*/
        Date c = Calendar.getInstance().getTime();
       // Log.e("Current time", c.toString());
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c);

        txtfrmDate.setText(formattedDate);
        txttoDate.setText(formattedDate);

        //drilled down for ND,CD,TD,OS,RA,RD
        txtNDHR.setPaintFlags(txtNDHR.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        txtNDKM.setPaintFlags(txtNDKM.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        txtFDKM.setPaintFlags(txtFDKM.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        txtFDHR.setPaintFlags(txtFDHR.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        txtTotalSpeedKM.setPaintFlags(txtTotalSpeedKM.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        txtTotalSpeedHR.setPaintFlags(txtTotalSpeedHR.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        txtBrakingCount.setPaintFlags(txtBrakingCount.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        txtACount.setPaintFlags(txtACount.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        txtSpeedCount.setPaintFlags(txtSpeedCount.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.screen_share:
                        takeScreenshot(ScreenshotType.FULL);
                        break;
                }
            }
        });

       /* txtfrmDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_PICKER_ID);
            }
        });
        txttoDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_PICKER_ToID);
            }
        });
*/
        txtfrmDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(MyDrivingActivity.this,new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        txtfrmDate.setText(year+ "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                        String todat = txtfrmDate.getText().toString();
                        java.text.SimpleDateFormat sdf3 = new java.text.SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                        java.text.SimpleDateFormat sdf4 = new java.text.SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                        String tdate;
                        try {
                            tdate = sdf4.format(sdf3.parse(todat));
                            txtfrmDate.setText(tdate);
                        } catch (Exception e) {


                        }
                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });

        txttoDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                toyear = c.get(Calendar.YEAR);
                tomonth = c.get(Calendar.MONTH);
                today= c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(MyDrivingActivity.this,new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        txttoDate.setText(year+ "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                        String todat = txttoDate.getText().toString();
                        java.text.SimpleDateFormat sdf3 = new java.text.SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                        java.text.SimpleDateFormat sdf4 = new java.text.SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                        String tdate;
                        try {
                            tdate = sdf4.format(sdf3.parse(todat));
                            txttoDate.setText(tdate);
                        } catch (Exception e) {

                        }
                    }
                },toyear,tomonth,today);
                datePickerDialog.show();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String frmDAte = txtfrmDate.getText().toString();
                String toDate = txttoDate.getText().toString();

                Log.e("fromdate : "+frmDAte,"toDate : "+toDate);

                float NDHR = 0, NDKM = 0, Distance = 0, RDurationinHrs = 0, CRKM = 0, CRHR = 0;
                int RDcount = 0, Osduration = 0, ND = 0, OSCount = 0, TheDate = 0, VehCode = 0, RACount = 0;

                // new MyLogger().storeMassage("frmDate-", frmDAte);
               // new MyLogger().storeMassage("toDate-", toDate);
                String returnData = new DatabaseOperation(MyDrivingActivity.this).retrieveDailyData(frmDAte, toDate);
              //  Log.e("Return Data",": "+returnData);

                if(returnData.equals("finished")){
                    returnData = RDcount + "%" + RACount + "%" + Osduration + "%" + CRKM + "%" + CRHR + "%" + Distance + "%" + OSCount + "%" + NDHR + "%" + RDurationinHrs + "%" + ND + "%" + NDKM;
                }
               // new MyLogger().storeMassage("Retrun data-", returnData);

                String[] str = returnData.split("%");
                String rdCount = str[0];
                String raCount = str[1];
                String osDuration = str[2];
                String crKM= str[3];
                String crhr = str[4];
                String distance = str[5];
                String osCount = str[6];
                String ndhr = str[7];
                String rDurationHrs = str[8];
                String nd = str[9];
                String ndkm = str[10];

                Log.e("rd "+rdCount,", ra "+raCount+", osDur "+osDuration+", crKM "+crKM+", crHR "+crhr+", Dist "+Distance+", osCont "+osCount+", ndHr "+ndhr+", rDur "+rDurationHrs+", nd "+nd+", ndkm "+ndkm);

                //Log.e("Val 1 : "+ndhr,"Val 2 : "+rDurationHrs+" , Val 3 : "+crhr);

                ndhr = convertToTime(ndhr);
                rDurationHrs = convertToTime(rDurationHrs);
                crhr = convertToTime(crhr);

               // Log.e("Val 1 : "+ndhr,"Val 2 : "+rDurationHrs+" , Val 3 : "+crhr);
               // new MyLogger().storeMassage("Return Data", rdCount +", "+ raCount +", "+ osDuration +", "+ crKM+", "+ crhr +", "+ distance +", "+ osCount +", "+ ndhr +", "+ rDurationHrs+", "+ndkm);

                txtNDKM.setText(ndkm);
                txtNDHR.setText(ndhr);
                txtACount.setText(raCount);
                txtBrakingCount.setText(rdCount);
                txtSpeedCount.setText(osCount);
                txtTotalSpeedKM.setText(distance);
                txtTotalSpeedHR.setText(rDurationHrs);
                txtFDKM.setText(crKM);
                txtFDHR.setText(crhr);

            }
        });
    }

    /*  Method which will take screenshot on Basis of Screenshot Type ENUM  */
    private void takeScreenshot(ScreenshotType screenshotType) {
        Bitmap b = null;
        switch (screenshotType) {
            case FULL:
                //If Screenshot type is FULL take full page screenshot i.e our root content.
                b = ScreenshotUtils.getScreenShot(rootContent);
                break;
            case CUSTOM:
                b = ScreenshotUtils.getScreenShot(rootContent);
                break;
        }

        //If bitmap is not null
        if (b != null) {
            //   showScreenShotImage(b);//show bitmap over imageview

            File saveFile = ScreenshotUtils.getMainDirectoryName(this);//get the path to save screenshot
            File file = ScreenshotUtils.store(b, "screenshot" + screenshotType + ".jpg", saveFile);//save the screenshot to selected path
            shareScreenshot(file);//finally share screenshot
        } else
            //If bitmap is null show toast message
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
    }

    private String convertToTime(String time){
        String getTime ="";
       // Log.e("Time ",": "+time);
        //float no1 = (float) 777.80;
        float no1 = Float.parseFloat(time);
      //  Log.e("After Float",": "+no1);
        DecimalFormat df = new DecimalFormat("####.00");
        // double con = doubledf.format(no);
        double d = Double.parseDouble(df.format(no1));
      //  Log.e("con",": "+d);
        String no =String.valueOf(d);
        String n[]=no.split("\\.");
        int i=Integer.parseInt(n[1]);
        int a =i/60;
        int b =i%60;
        Log.e("a : "+a,"b : "+b);
        int c= Integer.parseInt(n[0])+a;
     //   Log.e("House ",": "+c+"."+b);
        getTime = c+"."+b;
        return getTime;
    }

    /*  Share Screenshot  */
    private void shareScreenshot(File file) {
        Uri uri = Uri.fromFile(file);//Convert file path into Uri for sharing
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
       // intent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.sharing_text));
        intent.putExtra(Intent.EXTRA_STREAM, uri);//pass uri here
        startActivity(Intent.createChooser(intent, getString(R.string.share_title)));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i=new Intent(MyDrivingActivity.this,DashboardActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.screen_share:
                takeScreenshot(ScreenshotType.FULL);
                break;
        }
    }
}