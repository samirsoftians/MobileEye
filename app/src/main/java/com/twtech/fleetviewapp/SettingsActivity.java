package com.twtech.fleetviewapp;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
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

public class SettingsActivity extends AppCompatActivity {

    SQLiteDatabase db;
    TextView edtMobile, edtEmail, tv_unitId, tv_vehCode;
    TextView edFTUNAMe, edFTPASS, edCompCode;
    String uDB = Environment.getExternalStorageDirectory().getPath() + "/UserInfo.db";
    String Tag = "SettingsActivity";
    TextView fv_url;
    CheckBox checkB;
    EditText edNewComp;
    RequestQueue requestQueue;
    String emailId, mobile, compCode, vehCode;
    String getComp;

    private TextView edtPassword;
    private AppCompatCheckBox checkbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        fv_url = findViewById(R.id.tv_fvurl);
        fv_url.setPaintFlags(fv_url.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        mobile = getData("mobileNo");
        compCode = getData("compName");
        // e("Mobile No "," : "+mobile);
        Log.e("CompName", ": " + "," + compCode + ",");
        emailId = getData("emailId");
        // Log.e("Email ID "," : "+emailId);
        String data = getUIdandVehCode();
        Log.e("data!!!", data);
        String[] str = data.split(",");
        String unitId = str[0];
        vehCode = str[1];

        edtMobile = findViewById(R.id.ed_mobno);
        edtEmail = findViewById(R.id.ed_email);
        tv_unitId = findViewById(R.id.ed_unitid);
        tv_vehCode = findViewById(R.id.ed_vehcode);
        edFTUNAMe = findViewById(R.id.ed_fv_username);
        edFTPASS = findViewById(R.id.edtPassword);
        edCompCode = findViewById(R.id.ed_CompanyCode);
        checkB = (CheckBox) findViewById(R.id.checkbox2);
        edNewComp = (EditText) findViewById(R.id.getcompcode);

        edtMobile.setText(mobile);
        edtEmail.setText(emailId);
        tv_unitId.setText(unitId);
        tv_vehCode.setText(vehCode);
        edFTUNAMe.setText(emailId);
        edFTPASS.setText(mobile);
        if (!(compCode.equals(""))&&!(compCode.equals("0"))) {
            //Log.e("")
            edCompCode.setText(compCode);
            checkB.setVisibility(View.GONE);
        }

        edCompCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCompCodeDialog();
            }
        });

        checkbox = (AppCompatCheckBox) findViewById(R.id.checkbox);
        edtPassword = (TextView) findViewById(R.id.edtPassword);

        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (!isChecked) {
                    // show password
                    edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    // hide password
                    edtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });

        checkB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    // show password
                    showCompCodeDialog();
                } else {
                    // hide password
                }
            }
        });
        getUIdandVehCode();
    }

    public void showCompCodeDialog() {
        // flag=false;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        Dialog dialog = new Dialog(SettingsActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custum_dialog_addcompany, null);
        dialogBuilder.setView(dialogView);
        final EditText edt = (EditText) dialogView.findViewById(R.id.getcompcode);
        Button btnVarify;

        btnVarify = (Button) dialogView.findViewById(R.id.btnAddCompany);

        btnVarify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getComp = edt.getText().toString();
                Log.e("New Comp Code", ": " + getComp);
                try {
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                            Request.Method.GET,
                            "http://103.241.181.36:8080/VehSummary/rest/UpdateDetails?Username=" + emailId + "&VehCode=" + vehCode + "&Parameter=CompanyCode&Value=" + getComp + "&format=json",
                            //"http://103.241.181.36:8080/VehSummary/rest/UserRegistration?Username=" + userName + "&EmailId=" + emailID + "&MobileNo=" + mobileNo + "&Address=" + address + "&CompanyCode=" + compCode + "&imeiNo=" + imeiNo + "&OTP=" + OTP + "&format=json",
                            // "http://192.168.2.124:6060/FWebservice/rest/UserRegistration?Username=" + userName + "&EmailId=" + emailID + "&MobileNo=" + mobileNo + "&Address=" + address + "&CompanyCode=" + compCode + "&imeiNo=" + imeiNo + "&OTP=" + OTP + "&format=json",
                            new JSONObject(),
                            new Response.Listener<JSONObject>() {
                                public void onResponse(JSONObject response) {

                                    Toast.makeText(getApplicationContext(), "Company Code Added", Toast.LENGTH_SHORT).show();

                                    try {
                                        SQLiteDatabase database = openOrCreateDatabase(uDB, MODE_PRIVATE, null);
                                        database.execSQL("update otp set compName='" + getComp + "'");
                                        database.close();

                                        //new MyLogger().storeMassage("unitid vehicle code updated  ", "successfully ! ! ! !");
                                        Log.e("Company code updated  ", "successfully ! ! ! !");
                                    } catch (Exception e) {
                                        Log.e("Exception while ", "Updating Company code");
                                        new MyLogger().storeMassage("Exception while ", "updating company code " + e.getMessage());
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                           // Toast.makeText(SettingsActivity.this, "Error", Toast.LENGTH_LONG).show();
                            // Log.e("Error"," : ");
                            try {
                                SQLiteDatabase database = openOrCreateDatabase(uDB, MODE_PRIVATE, null);
                                database.execSQL("update otp set compName='" + getComp + "'");
                                database.close();
                                finish();
                                Toast.makeText(SettingsActivity.this, "Company code Added Successfully!!!!!", Toast.LENGTH_LONG).show();
                                Log.e("Company code updated  ", "successfully ! ! ! !");
                            } catch (Exception e) {
                                Log.e("Exception while ", "Updating Company code");
                                new MyLogger().storeMassage("Exception while ", "updating company code " + e.getMessage());
                            }
                        }
                    });
                    requestQueue.add(jsonObjectRequest);
                    //  showChangeLangDialog();
                } catch (Exception e) {
                    Log.e("Exception while ", "Sending data to server : " + e.getMessage());
                    Toast.makeText(SettingsActivity.this, "Error ,Try Later", Toast.LENGTH_LONG).show();
                }
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    public String getData(String input) {

        String result = "";
        try {
            SQLiteDatabase database = openOrCreateDatabase(uDB, MODE_PRIVATE, null);
            Cursor c = database.rawQuery("select " + input + " from otp", null);
            c.moveToFirst();
            do {
                result = c.getString(c.getColumnIndex(input));
                // String str2 = cr.getString(cr.getColumnIndex("GSMStamps"));
            } while (c.moveToNext());
            c.close();
            database.close();
            // Log.e("Data EmailId/Mobile","Successfully");
        } catch (Exception e) {
            Log.e(Tag + " : Exception", " : " + e.getMessage());
        }
        return result;
    }

    public String getUIdandVehCode() {
        String unitId = null;
        String vehCode = null;
        try {
            SQLiteDatabase database = openOrCreateDatabase(uDB, MODE_PRIVATE, null);
            Cursor c = database.rawQuery("select * from otp", null);
            c.moveToFirst();
            do {
                unitId = c.getString(c.getColumnIndex("unitID"));
                Log.e("unitID", unitId);
                vehCode = c.getString(c.getColumnIndex("vehCode"));
                Log.e("vehicle Code", vehCode);
            } while (c.moveToNext());
            c.close();
            database.close();
            //Log.e("OTP Retrieved ","Successfully");
        } catch (Exception e) {
            Log.e("Exception while reading", "OTP from db : " + e.getMessage());
        }
        String returnData = unitId + "," + vehCode;
        Log.e("return", returnData);
        return returnData;
    }
}