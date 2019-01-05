package com.twtech.fleetviewapp;

import android.Manifest;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.security.SecureRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.security.auth.login.LoginException;

/**
 * Created by Deepali Shinde on 26/4/18.
 */

public class RegisterActivity extends AppCompatActivity {
    boolean flag;
    CheckBox checkBox;
    EditText editText;
    Button register;
    EditText edUname, edLastName, editemail, edmobile, edaddress, edcomp;
    RequestQueue requestQueue;
    String imeiNo, userName,lastName, emailID, mobileNo, address, nmNew, emailNew, phoneNew, addressNew;
    String compCode = "";
    int OTP;
    TelephonyManager telephonyManager;
    CanDatabase canDatabase;
    String uDB = Environment.getExternalStorageDirectory().getPath() + "/UserInfo.db";
    SessionManager session;
    int validation=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        canDatabase = new CanDatabase(RegisterActivity.this);
        session = new SessionManager(getApplicationContext());
        canDatabase.createCanDatabase();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        imeiNo = new String();
        try {
            telephonyManager = (TelephonyManager) this.getSystemService(getApplicationContext().TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling/media/twtech/6D33-6396/login
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            imeiNo = telephonyManager.getDeviceId();
            //Log.e("IMEI ", "Number : " + imeiNo);
        } catch (Exception e) {

        }
        OTP = new Integer(0);
        compCode = new String(" ");
        userName = new String();
        lastName = new String();
        emailID = new String();
        mobileNo = new String();
        address = new String();

        edUname = (EditText) findViewById(R.id.userEdit);
        edLastName = (EditText) findViewById(R.id.sirnameEdit);
        editemail = (EditText) findViewById(R.id.emailEdit);
        edmobile = (EditText) findViewById(R.id.mobileEdit);
        edaddress = (EditText) findViewById(R.id.addressEdit);
        checkBox = (CheckBox) findViewById(R.id.cb1);
        editText = (EditText) findViewById(R.id.comp_code);
        register = (Button) findViewById(R.id.login_title);

        File fileNm = new File(uDB);
        if (fileNm.exists()) {
            //Log.e("User Database Exists", "REgister Activity");

            Bundle extras = getIntent().getExtras();
            if (extras != null) {

                nmNew = extras.getString("name");
                emailNew = extras.getString("email");
                phoneNew = extras.getString("phone");
                addressNew = extras.getString("address");

               // Log.e("New Values ", "" + nmNew + ", " + emailID + ", " + phoneNew + ", " + addressNew);

                edUname.setText(nmNew);
                editemail.setText(emailNew);
                edmobile.setText(phoneNew);
                edaddress.setText(addressNew);
            }
        }

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar_layout);
        View view = getSupportActionBar().getCustomView();
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (checkBox.isChecked()) {
                    editText.setVisibility(View.VISIBLE);
                } else
                    editText.setVisibility(View.GONE);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 validation = 0;
                 registerValidation();
                 if (validation == 0) {

                OTP = generateRandomNumber();
                Log.e("OTP : ", " " + OTP);
                userName = edUname.getText().toString();
                lastName = edLastName.getText().toString();
                emailID = editemail.getText().toString();
                mobileNo = edmobile.getText().toString();
                address = edaddress.getText().toString();

                try {
                    compCode = editText.getText().toString();
                    Log.e("Comp Code ", ":" + compCode);
                } catch (Exception e) {

                }

                String toastOTP = String.valueOf(OTP);
            //    Toast.makeText(RegisterActivity.this, toastOTP, Toast.LENGTH_LONG).show();
                new MyLogger().storeMassage("Values", " : " + userName + " " + lastName + " " + emailID + " " + mobileNo + " " + address + " " + compCode + " " + OTP + " " + imeiNo);

                Log.e("Values", " : " + userName + " " + lastName + " " + emailID + " " + mobileNo + " " + address + " " + compCode + " " + OTP + " " + imeiNo);
                try {
                    File databaseExist = getApplicationContext().getDatabasePath(uDB);
                    if (databaseExist.exists()) {
                        new DatabaseOperation(getApplicationContext()).updateOTP(userName, emailID, mobileNo, address, OTP);
                    } else {
                        new DatabaseOperation(getApplicationContext()).userDetail(userName, emailID, mobileNo, compCode, address, OTP);
                    }
                    Log.e("Register url","http://103.241.181.36:8080/VehSummary/rest/UserRegistration?FirstName="+userName+"&LastName="+lastName+"&Username="+userName+lastName+"&EmailId="+emailID+"&MobileNo="+mobileNo+"&Address="+address+"&CompanyCode="+compCode+"&imeiNo="+imeiNo+"&OTP="+OTP+"&format=json");
                    try {
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                                Request.Method.GET,
                                "http://103.241.181.36:8080/VehSummary/rest/UserRegistration?FirstName="+userName+"&LastName="+lastName+"&Username="+userName+lastName+"&EmailId="+emailID+"&MobileNo="+mobileNo+"&Address="+address+"&CompanyCode="+compCode+"&imeiNo="+imeiNo+"&OTP="+OTP+"&format=json",
                                //"http://103.241.181.36:8080/VehSummary/rest/UserRegistration?Username=" + userName + "&EmailId=" + emailID + "&MobileNo=" + mobileNo + "&Address=" + address + "&CompanyCode=" + compCode + "&imeiNo=" + imeiNo + "&OTP=" + OTP + "&format=json",
                                // "http://192.168.2.124:6060/FWebservice/rest/UserRegistration?Username=" + userName + "&EmailId=" + emailID + "&MobileNo=" + mobileNo + "&Address=" + address + "&CompanyCode=" + compCode + "&imeiNo=" + imeiNo + "&OTP=" + OTP + "&format=json",
                                new JSONObject(),
                                new Response.Listener<JSONObject>() {
                                    public void onResponse(JSONObject response) {
                                        Toast.makeText(getApplicationContext(), "Data posted", Toast.LENGTH_SHORT).show();
                                        Log.e("Exception while 1", "Sending data to server : ");
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                //  Toast.makeText(RegisterActivity.this, "Error", Toast.LENGTH_LONG).show();
                                Log.e("Exception while 2", "Sending data to server : ");
                               // new MyLogger().storeMassage("");
                            }
                        });
                        requestQueue.add(jsonObjectRequest);
                       // showChangeLangDialog();
                        showMessage();
                    } catch (Exception e) {
                        Log.e("Exception while ", "Sending data to server : " + e.getMessage());
                        Toast.makeText(RegisterActivity.this, "Oops....Registration Failed Try Later.", Toast.LENGTH_LONG).show();
                    }

                    //showChangeLangDialog();
                } catch (Exception e) {
                    Log.e("Exception ", "While Inserting data  : " + e.getMessage());
                    Toast.makeText(RegisterActivity.this, "Oops....Registration Failed Try Later.", Toast.LENGTH_LONG).show();

                }
               // showChangeLangDialog();
                 }
                else{
                    Toast.makeText(RegisterActivity.this, "Please enter details in all fields!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private BroadcastReceiver reciever =new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("otp")) {
                final String message = intent.getStringExtra("message");
                //your_edittext.setText(message);
                Log.e("SMS ",": "+","+message+",");
                Log.e("OTP ",","+OTP+",");
                String otp1 =String.valueOf(OTP);

                if(message.equals(otp1)){
                    GetJSONData3();
                    Log.e("Successfully ","Varified");
                    Toast.makeText(RegisterActivity.this, "Successfully Varified @@@@.", Toast.LENGTH_LONG).show();
                }
            }
        }
    };

    public int generateRandomNumber() {
        int randomNumber;
        int range = 9;  // to generate a single number with this range, by default its 0..9
        int length = 4; // by default length is 4
        SecureRandom secureRandom = new SecureRandom();
        String s = "";
        for (int i = 0; i < length; i++) {
            int number = secureRandom.nextInt(range);
            if (number == 0 && i == 0) { // to prevent the Zero to be the first number as then it will reduce the length of generated pin to three or even more if the second or third number came as zeros
                i = -1;
                continue;
            }
            s = s + number;
        }
        randomNumber = Integer.parseInt(s);
        return randomNumber;
    }

    public void showChangeLangDialog() {
        // flag=false;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final Dialog dialog = new Dialog(RegisterActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custum_dialog, null);
        dialogBuilder.setView(dialogView);
        final EditText edt = (EditText) dialogView.findViewById(R.id.edit1);
        Button btnVarify;
        btnVarify = (Button) dialogView.findViewById(R.id.btnSubmit);
        btnVarify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int getOTPFromUser = Integer.parseInt(edt.getText().toString());
                int getOTPFromDB = new DatabaseOperation(getApplicationContext()).readOTP();

                Log.e("OTP from you "+getOTPFromUser,"OTP from DB "+getOTPFromDB);
                //if(getOTPFromUser=){
                if (getOTPFromUser == getOTPFromDB) {
                   // Toast.makeText(RegisterActivity.this, "Successfully Verified.........", Toast.LENGTH_LONG).show();
                    // clearText();
                    Log.e("flag 0", " : " + flag);

                    GetJSONData3();

                } else {
                    Toast.makeText(RegisterActivity.this, "Please enter Valid OTP........", Toast.LENGTH_LONG).show();
                }
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
        // finish();
    }

    public void showMessage() {

        Log.e("Entered in ShowMessage"," ***");

        Log.e("email id :+"+emailID," Mobile : "+mobileNo);

        // flag=false;
        AlertDialog.Builder dialogBuilder1 = new AlertDialog.Builder(this);
        final Dialog dialog = new Dialog(RegisterActivity.this);
        LayoutInflater inflater1 = this.getLayoutInflater();
        final View dialogView1 = inflater1.inflate(R.layout.message, null);
        dialogBuilder1.setView(dialogView1);
        TextView txt = (TextView)dialogView1.findViewById(R.id.txtMssage);
        Log.e("textview "," : "+txt);
        txt.setText(emailID+" is your UserName and "+mobileNo+ " is password.\n use these details for login");
        Button btnok;
        btnok =(Button)dialogView1.findViewById(R.id.btnOK);
        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChangeLangDialog();
            }
        });

        AlertDialog b1 = dialogBuilder1.create();
        b1.show();
        // finish();
    }

    public void GetJSONData3() {
        // flag = false;
        String Data;
       // Log.e("load Data ", "MEthod Called");

        try {
            final JsonArrayRequest jsonarrayRequest = new JsonArrayRequest(
                    Request.Method.GET,
                    "http://103.241.181.36:8080/VehSummary/rest/UserAuthitication?Verification=OK&imeiNo=" + imeiNo + "&format=json",
                    new JSONArray(),
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                for (int i = 0; i < response.length(); i++) {
                                    Log.e("URL 2","http://103.241.181.36:8080/VehSummary/rest/UserAuthitication?Verification=OK&imeiNo=" + imeiNo + "&format=json");
                                    JSONObject objectjsn = response.getJSONObject(i);
                                    String strUnitId = objectjsn.getString("unitid");
                                    String strVehCode = objectjsn.getString("VehicleCode");
                                    Log.e("Unit ID & code Response", "" + strUnitId + "," + strVehCode);
                                    new MyLogger().storeMassage("Response  : ", "" + strUnitId + "," + strVehCode);

                                    new DatabaseOperation(RegisterActivity.this).updateUidVehCode(strUnitId, strVehCode);
                                    canDatabase.openCanDatabase();
                                    canDatabase.storeValue("UnitID", strUnitId);
                                    canDatabase.closeCanDatabase();

                                    try {
                                        session.createRegisterSession(userName, emailID, mobileNo);
                                        // Staring MainActivity
                                        Intent intent = new Intent(getApplicationContext(), MyLoginActivity.class);
                                      //  Toast.makeText(RegisterActivity.this, "Thanks", Toast.LENGTH_LONG).show();
                                        startActivity(intent);
                                        finish();

                                    } catch (Exception e) {
                                        Log.e("Exception while ", "storing in SharedPrefrence : " + e.getMessage());
                                    }

                                    Intent intent = new Intent(RegisterActivity.this, MyLoginActivity.class);
                                    startActivity(intent);

                                }
                            } catch (JSONException e) {
                                new MyLogger().storeMassage("Register Activity : Exception while updating userDetails ", e.getMessage());
                                Log.e("Exception 3453 ", " : " + e.getMessage());
                                Toast.makeText(RegisterActivity.this, "check internet Connection", Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(RegisterActivity.this, "Server not reachable....Try Later", Toast.LENGTH_SHORT).show();
                            Log.e("Exception ", "while url hitting" + error);//NoConnectionError
                        }
                    }
            );
            requestQueue.add(jsonarrayRequest);

        } catch (Exception e) {
            Log.e("Exception ", " : " + e.getMessage());
            Toast.makeText(RegisterActivity.this, "Internet connection error or server not reachable", Toast.LENGTH_SHORT).show();
        }

        //return flag;
    }

    public void registerValidation(){
        if (edUname.getText().toString().trim().equals("")) {
            edUname.setError("Enter Username");
            validation++;
        }
        //validating email
        else if (editemail.getText().toString().trim().equals("")) {
            editemail.setError("Enter Email");
            validation++;
        }
        //validating mobile

        else if (edmobile.getText().toString().trim().equals("")) {
            edmobile.setError("Enter Mobile No");
            validation++;
        }
        //validating address
        else if (edaddress.getText().toString().trim().equals("")) {
            edaddress.setError("Enter Address");
            validation++;
        }
        else if (editText.getText().toString().trim().equals("")){
            editText.setError("Enter company code");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
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

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciever);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(reciever, new IntentFilter("otp"));
    }
}

