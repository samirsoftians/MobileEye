package com.twtech.fleetviewapp;

import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyDataActivity extends AppCompatActivity {
    TextView date,time,txtuName;
    EditText odomter,fuel_litres,expense;
    static final int TIME_DIALOG_ID = 999;
    static final int DATE_PICKER_ID = 1111;
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private int second;
    ImageView imageView1;
    private ScrollView rootContent;
    SessionManager sf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_data);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar_layout1);
        View view =getSupportActionBar().getCustomView();
        rootContent=(ScrollView)findViewById(R.id.scroll);
        odomter = (EditText) findViewById(R.id.ed_odometer);
        fuel_litres = (EditText) findViewById(R.id.ed_fuel);
        expense = (EditText) findViewById(R.id.ed_expense);
        date = (TextView) findViewById(R.id.textCurrentDate);
        time = (TextView) findViewById(R.id.textCurrentTime);
        imageView1=(ImageView)findViewById(R.id.screen_share);
        txtuName=(TextView)findViewById(R.id.tv_input_uname);

        sf = new SessionManager(MyDataActivity.this);

        HashMap<String, String> user = sf.getUserDetails();
        String name = user.get(SessionManager.KEY_NAME);

        txtuName.setText(name);
        /*date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                // On button click show datepicker dialog
                showDialog(DATE_PICKER_ID);
            }
        });
        time.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                showDialog(TIME_DIALOG_ID);

            }
        });*/

        date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                // On button click show datepicker dialog
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(MyDataActivity.this,new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        date.setText(year+ "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                        String todat = date.getText().toString();
                        java.text.SimpleDateFormat sdf3 = new java.text.SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                        java.text.SimpleDateFormat sdf4 = new java.text.SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                        String tdate;
                        try {
                            tdate = sdf4.format(sdf3.parse(todat));
                            date.setText(tdate);
                        } catch (Exception e) {


                        }
                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });

        time.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                // Get Current Time
                final Calendar c = Calendar.getInstance();
                hour = c.get(Calendar.HOUR_OF_DAY);
                minute = c.get(Calendar.MINUTE);
                second = c.get(Calendar.SECOND);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(MyDataActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                time.setText(hourOfDay + ":" + minute);
                                String totime = time.getText().toString();
                                java.text.SimpleDateFormat sdf3 = new java.text.SimpleDateFormat("hh:mm aa");
                                String time1;
                                try {
                                    time1 = sdf3.format(totime);
                                    // tdate = sdf4.format(sdf3.parse(totime));
                                    time.setText(time1);
                                } catch (Exception e) {


                                }
                            }
                        }, hour, minute, false);
                timePickerDialog.show();
            }
        });

        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.screen_share:
                        takeScreenshot(ScreenshotType.FULL);
                        break;
                }
            }
        });


        odomter.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(8,2)});
        fuel_litres.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(5,2)});
        expense.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(9,2)});

        final Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);
        second=c.get(Calendar.SECOND);

        // set current time into textview
        time.setText(new StringBuilder().append(pad(hour))
                .append(":").append(pad(minute)).append(":").append(pad(second)));

        // set current date into textview

        Date currentDate = Calendar.getInstance().getTime();
       // Log.e("Current time" , c.toString());
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = df.format(currentDate);
        date.setText(formattedDate);

    }
  /*  @Override
    protected Dialog onCreateDialog(int id)
    {

        switch (id)
        {
            case DATE_PICKER_ID:

                return new DatePickerDialog(this, pickerListener, year, month,day);
            case TIME_DIALOG_ID:
                return new TimePickerDialog(this,
                        timePickerListener, hour, minute,false);


        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        @Override
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {

            year  = selectedYear;
            month = selectedMonth;
            day   = selectedDay;

            // Show selected date
            date.setText(new StringBuilder().append(day)
                    .append("-").append(month+1).append("-").append(year)
                    .append(" "));

            String dat=date.getText().toString();
            SimpleDateFormat sdf3 = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);

            SimpleDateFormat sdf4 = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);

            String tdate;
            try {
                tdate = sdf4.format(sdf3.parse(dat));
                date.setText(tdate);

            }
            catch(Exception e)
            {

            }
        }
    };

    private TimePickerDialog.OnTimeSetListener timePickerListener =
            new TimePickerDialog.OnTimeSetListener()
            {
                public void onTimeSet(TimePicker view, int selectedHour,
                                      int selectedMinute)
                {

                    hour = selectedHour;
                    minute = selectedMinute;
                    time.setText(new StringBuilder().append(pad(hour))
                            .append(":").append(pad(minute)).append(":").append("00"));


                }
            };
*/
    private static String pad(int c)
    {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);

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

    /*  Share Screenshot  */
    private void shareScreenshot(File file) {
        Uri uri = Uri.fromFile(file);//Convert file path into Uri for sharing
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.sharing_text));
        intent.putExtra(Intent.EXTRA_STREAM, uri);//pass uri here
        //startActivity(Intent.createChooser(intent, getString(R.string.share_title)));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i=new Intent(MyDataActivity.this,DashboardActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    public class DecimalDigitsInputFilter implements InputFilter {
        Pattern mPattern;

        public DecimalDigitsInputFilter(int digitsBeforeZero,int digitsAfterZero) {
            mPattern= Pattern.compile("[0-9]{0," + (digitsBeforeZero-1) + "}+((\\.[0-9]{0," + (digitsAfterZero-1) + "})?)||(\\.)?");
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            Matcher matcher=mPattern.matcher(dest);
            if(!matcher.matches())
                return "";
            return null;
        }
    }
}
