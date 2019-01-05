package com.twtech.fleetviewapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Deepali Shinde on 26/4/18.
 */

public class MyLoginActivity extends AppCompatActivity {
    EditText edUname,edUpadd;
    Button btnLogin;
    SessionManager sf;
    PendingIntent pintent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        addAutoStartup();

        sf = new SessionManager(MyLoginActivity.this);

        edUname=(EditText)findViewById(R.id.eduname);
        edUpadd=(EditText)findViewById(R.id.edupass);
        //validating email
        if (edUname.getText().toString().trim().equals("")) {
            edUname.setError("Enter registered EmailID/Number");
        }
        else if (!isValidEmail(edUname.getText().toString())){
            edUname.setError("Enter registered EmailID/Number");
        }
        edUpadd=(EditText)findViewById(R.id.edupass);
        if (edUpadd.getText().toString().trim().equals("")) {
            edUpadd.setError("Please enter registered Number");
        }else if (!isValidPassword(edUpadd.getText().toString())){
            edUpadd.setError("Please enter registered Number");
        }

        btnLogin=(Button)findViewById(R.id.btnlogin1);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String unm  = edUname.getText().toString();
                String upass  = edUpadd.getText().toString();
                HashMap<String, String> user = sf.getUserDetails();
                //String name = user.get(SessionManager.KEY_NAME);
                String email = user.get(SessionManager.KEY_EMAIL);
                String phone = user.get(SessionManager.KEY_PHONE);
                if(unm.equals(email) && upass.equals(phone)){
                    // Log.e("Login Successfull","@@@@@ "+name+" "+email+" "+phone+" "+userName+" "+userPass);

                  //  Toast.makeText(getApplicationContext(), "Logged In Successfully!!!!! ", Toast.LENGTH_LONG).show();
                    try {
                        sf.createLoginSession(unm, upass);
                        // Staring MainActivity
                        Intent i = new Intent(getApplicationContext(), DashboardActivity.class);
                        //Toast.makeText(MyLoginActivity.this, "Thanks", Toast.LENGTH_LONG).show();
                        startActivity(i);
                        //finish();
                        //this.finish();
                    }catch (Exception e){
                        Log.e("Exception while ","storing in SharedPrefrence : "+e.getMessage());
                    }

                    Intent intent = new Intent(MyLoginActivity.this, DashboardActivity.class);
                    startActivity(intent);
                    /*Calendar caldar = Calendar.getInstance();
                    AlarmManager alarm = (AlarmManager)getSystemService(MyLoginActivity.this.ALARM_SERVICE);
                    Intent intent1 = new Intent(MyLoginActivity.this, ReceiverCall.class);
                    pintent = PendingIntent.getBroadcast(MyLoginActivity.this, 1, intent1, pintent.FLAG_UPDATE_CURRENT);
                    alarm.setRepeating(AlarmManager.RTC_WAKEUP, caldar.getTimeInMillis(), 2*60*1000, pintent);*/
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(), "Please enter valid details " + sf.isRegistered(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to close this application ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
                    }
                });
    }

    // validating email id
    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // validating password with retype password
    private boolean isValidPassword(String pass) {
        if (pass != null && pass.length() > 1) {
            return true;
        }
        return false;
    }

    private void addAutoStartup() {

        Log.e("AddAutostart UP"," Permission ");

        try {
            Intent intent = new Intent();
            String manufacturer = android.os.Build.MANUFACTURER;
            if ("xiaomi".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
            } else if ("oppo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
            } else if ("Letv".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"));
            } else if ("Honor".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
            } else if ("vivo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
            }
            startActivity(intent);

        } catch (Exception e) {
            Log.e("exc" , String.valueOf(e));
        }
    }

}
