package com.twtech.fleetviewapp;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.twtech.fleetviewapp.prefs.MePrefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    ImageView cv_myDriving, cv_myData, cv_leaderBoard;
    TextView userProfile, yesterday_date;
    LinearLayout clk1, clk2, clk3, clk4;
    File file = null;
    public static final Integer GALLERY_PICTURE = 1515;
    public static final Integer RESULT_CROP = 4544;
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    private CircularImageView imageViewAddPic;
    private String selectedImagePath = null;
    SessionManager sf;
    CanDatabase canDatabase;
    DrawerLayout drawer;
    TextView ra, rd, totalkm, overspeed,nightDriving;
    String date,date2;

    RequestQueue requestQueue;
    String winnerEmail,winnerNumer,winnerVehC,fromDate;
    StringBuffer sb1;
    String winnerList="";
    GlobalVariable gbl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        statusCheck();

        gbl= (GlobalVariable)DashboardActivity.this.getApplicationContext();
        requestQueue = Volley.newRequestQueue(DashboardActivity.this);

        boolean serviceRunningStatus = isServiceRunning(MainService.class);
        // boolean serviceUpdateRunningStatus = isServiceRunning(UpdateRemoteCanParameter.class);

      //  enableAutostart();

      //  addAutoStartup();
Log.e("SEvice RunningStatus "," DashBoard : "+serviceRunningStatus);
        if (serviceRunningStatus == false) {
           // Log.e("Service ", "is not Running");
            startService(new Intent(DashboardActivity.this, MainService.class));
        }

        sf = new SessionManager(DashboardActivity.this);

        canDatabase = new CanDatabase(DashboardActivity.this);

        canDatabase.createCanDatabase();
        HashMap<String, String> user = sf.getUserDetails();
        String name = user.get(SessionManager.KEY_NAME);
        ra = (TextView) findViewById(R.id.total_ra);
        rd = (TextView) findViewById(R.id.total_rd);
        totalkm = (TextView) findViewById(R.id.tv_totalkm);
        overspeed = (TextView) findViewById(R.id.total_os);
        nightDriving = (TextView) findViewById(R.id.ntdrivval);

        // imageView=(ImageView)findViewById(R.id.fab2);
        cv_myDriving = (ImageView) findViewById(R.id.cv_driving);
        cv_myData = (ImageView) findViewById(R.id.cv_data);
        cv_leaderBoard = (ImageView) findViewById(R.id.cv_leader);
        cv_myDriving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, MyDrivingActivity.class));
            }
        });
        cv_myData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, MyDataActivity.class));
            }
        });
        cv_leaderBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, LeaderboardActivity.class));
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();
        clk1 = (LinearLayout) findViewById(R.id.click1);
        clk2 = (LinearLayout) findViewById(R.id.click2);
        clk3 = (LinearLayout) findViewById(R.id.click3);
        clk4 = (LinearLayout) findViewById(R.id.click4);
        HashMap<String, String> profile = sf.getUserDetails();
        String userName = profile.get(SessionManager.KEY_NAME);
        userProfile = (TextView) findViewById(R.id.userProfile);
        userProfile.setText(name);
        LinearLayout navigationView = (LinearLayout) findViewById(R.id.drawer);
        toolbar.setNavigationIcon(R.drawable.drawer);
        initImageView();
        date = getYesterdayDateforView();
        date2 = getYesterdayDateString();

        Log.e("date yesterdays", " : " + date);

        yesterday_date = (TextView) findViewById(R.id.date);
        yesterday_date.setText(date);
        DatabaseOperation db = new DatabaseOperation(DashboardActivity.this);
        String details = db.getYesterdaysData(date2);
       // Log.e("details ", ": " + details);

        /*try {
            String myScore = gbl.getWinners();
            Log.e("myScore",myScore);
            String score[] = myScore.split(" ");
            int scoreLen = myScore.length();
            String myRating[] = score[scoreLen - 1].split(",");
            String getRating = myRating[2];
            Log.e("My Rating ", ": " + getRating);
        }catch (Exception e){
            Log.e("Exception "," : "+e.getMessage());
        }*/

        try {
            if (!details.equals("No")) {
                String detailsString[] = details.split(",");
                rd.setText(detailsString[0]);
                ra.setText(detailsString[1]);
                totalkm.setText(detailsString[2]);
                overspeed.setText(detailsString[3]);
                nightDriving.setText(detailsString[4]);
                Log.e("inside try", String.valueOf(detailsString));
            }
        }catch (Exception e){
            Log.e("Exception",e.getMessage());
        }

        String userDetails = new DatabaseOperation(DashboardActivity.this).retrieveUserDetailsData();
        new MyLogger().storeMassage("Today " + " : Retrun data -", userDetails);
        String data[] = userDetails.split("%");
       // String dt = getYesterdayDateString();
        String winner = downloadWinners(data[1],data[2],data[4]);

        Log.e("winner ",": "+winner);
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

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }
    }

    private void buildAlertMessageNoGps() {
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_dash) {
            // Handle the camera action
            Intent i = new Intent(DashboardActivity.this, DashboardActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(DashboardActivity.this, SettingsActivity.class));
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /*code added by shivankchi*/

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void ClickNavigation(View view) {
        /*Fragment fragment = null;
        Class fragmentClass =  FragmentA.class;
*/
        switch (view.getId()) {
            case R.id.click1:
                Intent i = new Intent(DashboardActivity.this, DashboardActivity.class);
                startActivity(i);
                clk1.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_clickb, null));
                clk2.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_click, null));
                clk3.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_click, null));
                clk4.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_click, null));
                break;
            case R.id.click2:
                startActivity(new Intent(DashboardActivity.this, SettingsActivity.class));
                clk1.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_click, null));
                clk2.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_clickb, null));
                clk3.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_click, null));
                clk4.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_click, null));
                break;
            case R.id.click3:
                clk1.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_click, null));
                clk2.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_click, null));
                clk3.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_clickb, null));
                clk4.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_click, null));
                break;
            case R.id.click4:
                clk1.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_click, null));
                clk2.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_click, null));
                clk3.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_click, null));
                clk4.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_clickb, null));
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    //take a photo from camera

    public void initImageView() {
        //  LinearLayout navigationView = (LinearLayout) findViewById(R.id.nav_view);
        //  View header = navigationView.getHeaderView(0);
        imageViewAddPic = (CircularImageView) findViewById(R.id.profile_image);
        if (MePrefs.getKeyImageName(getApplicationContext()).equals("none")) {
            imageViewAddPic.setImageResource(R.drawable.layer_list_add_pic);
        } else {
            File imgFile = new File(MePrefs.getKeyImageName(getApplicationContext()));
            if (imgFile.exists()) {
                Bitmap profilePic = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                int nh = (int) (profilePic.getHeight() * (512.0 / profilePic.getWidth()));
                Bitmap scaled = Bitmap.createScaledBitmap(profilePic, 512, nh, true);
                imageViewAddPic.setImageBitmap(scaled);
            } else {
                imageViewAddPic.setImageResource(R.drawable.layer_list_add_pic);
            }
        }
        imageViewAddPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pictureActionIntent = null;
                pictureActionIntent = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pictureActionIntent, GALLERY_PICTURE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*if (requestCode == REQUEST_RESOLVE_ERROR) {
            resolvingError = false;
            if (resultCode == RESULT_OK) {
                if (!googleApiClient.isConnecting() &&
                        !googleApiClient.isConnected()) {
                    googleApiClient.connect();
                }
            }
        }*/
        if (requestCode == GALLERY_PICTURE) {
            if (resultCode == RESULT_OK) {
                if (data.getData() != null) {
                    Uri selectedImageUri = data.getData();
                    if (selectedImageUri != null) {
                        selectedImagePath = (getPath(selectedImageUri) != null ? getPath(selectedImageUri) : "");
                        MePrefs.saveImage(getApplicationContext(), selectedImagePath);
                        Log.i("@Transworld", "Image Path : " + selectedImagePath);
                        cropImage(selectedImagePath);
                    }
                }
            }
        }
        if (requestCode == RESULT_CROP) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle extras = data.getExtras();
                Bitmap selectedBitmap = extras.getParcelable("data");
                // Set The Bitmap Data To ImageView
                Bitmap profilePic = BitmapFactory.decodeFile(selectedImagePath);
                int nh = (int) (profilePic.getHeight() * (512.0 / profilePic.getWidth()));
                Bitmap scaled = Bitmap.createScaledBitmap(profilePic, 512, nh, true);
                imageViewAddPic.setImageBitmap(scaled);
                imageViewAddPic.setScaleType(ImageView.ScaleType.FIT_XY);
            }
        }
    }

    private void cropImage(String selectedImagePath) {
        try {
            Intent cropImageIntent = new Intent("com.android.camera.action.CROP");
            File file = new File(selectedImagePath);
            Uri contentUri = Uri.fromFile(file);
            cropImageIntent.setDataAndType(contentUri, "image/*");
            cropImageIntent.putExtra("crop", "true");     // set crop properties
            // indicate aspect of desired crop
            cropImageIntent.putExtra("aspectX", 1);
            cropImageIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropImageIntent.putExtra("outputX", 100);
            cropImageIntent.putExtra("outputY", 100);
            cropImageIntent.putExtra("return-data", true);    // retrieve data on return
            startActivityForResult(cropImageIntent, RESULT_CROP);
        } catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private String getPath(Uri uri) {
        Cursor cursor = null;
        try {
            String[] projection = {MediaStore.Images.Media.DATA};
            cursor = getContentResolver().query(uri, projection, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
    //to get yesterday date
    private Date yesterday() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }

    private String getYesterdayDateforView() {
        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        return dateFormat.format(yesterday());
    }

    private String getYesterdayDateString() {
        DateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat2.format(yesterday());
    }

    public String downloadWinners(String emailID,String mobileNumber,String vehicleCode){

        winnerList = "No";
        Log.e("email:"+emailID,"mobile : "+mobileNumber+"vehicleCode :"+vehicleCode);
        winnerEmail = emailID;
        winnerNumer = mobileNumber;
        winnerVehC = vehicleCode;
        sb1 = new StringBuffer();

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        cal.add(Calendar.DATE, -7);
        fromDate = sdf.format(cal.getTime());
        Log.e("Yesterdays Date"," : "+fromDate);

        Log.e("Winner Details "," : "+winnerEmail+", "+winnerNumer+", "+winnerVehC+", "+fromDate);
        try {

            Log.e("URL"," : "+"http://103.241.181.36:8080/VehSummary/rest/LeaderBoard?Username="+winnerEmail+"&Password="+winnerNumer+"&VehCode="+winnerVehC+"&LastRec="+fromDate+"&format=json");
            final JsonArrayRequest jsonarrayRequest = new JsonArrayRequest(
                    Request.Method.GET,
                    //"http://103.241.181.36:8080/VehSummary/rest/VehicleDetails?Username=ubaidullahkhan@bddhalla.com&Password=1gjoQspE&VehCode=11437&LastRec=20&format=json\n",
                    "http://103.241.181.36:8080/VehSummary/rest/LeaderBoard?Username="+winnerEmail+"&Password="+winnerNumer+"&VehCode="+winnerVehC+"&LastRec="+fromDate+"&format=json",
                    //"http://103.241.181.36:8080/VehSummary/rest/VehicleDetails?Username="+"ubaidullahkhan@bddhalla.com"+"&Password="+"1gjoQspE"+"&VehCode="+"11437"+"&LastRec=20&format=json\n",
                    new JSONArray(),
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            Log.e(" : Url -","http://103.241.181.36:8080/VehSummary/rest/LeaderBoard?Username="+winnerEmail+"&Password="+winnerNumer+"&VehCode="+winnerVehC+"&LastRec="+fromDate+"&format=json");
                            try{
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject objectjsn = response.getJSONObject(i);
                                    String user = objectjsn.getString("User");
                                    String rank= objectjsn.getString("Rank");
                                    String rating = objectjsn.getString("Rating");
                                    String list = user+","+rank+","+rating;

                                    sb1.append(list+"_");
                                    Log.e("Response  : ", "" + user + "," + rank + ", " + rating);
                                  //  new MyLogger().storeMassage("data from webservice-", user + "," + rank + ", " + rating);
                                }
                                winnerList = sb1.toString();
                              //  Log.e("winner List",": "+winnerList);
                                gbl.setWinners(winnerList);
                            } catch (JSONException e) {
                                Log.e("Exception ", " : " + e.getMessage());
                                new MyLogger().storeMassage("DashBoard Activity :Exception ", " : " + e.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Error while ","Downloading winnerList");
                            new MyLogger().storeMassage("DownloadWinnerList Error", "While fetching web swervice for daily data");
                        }
                    }
            );
            requestQueue.add(jsonarrayRequest);
        }catch (Exception e){
            Log.e("Exception "," : "+e.getMessage());
        }
        return winnerList;
    }


    private void addAutoStartup() {

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
            }

            List<ResolveInfo>
                    list = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if  (list.size() > 0) {
                startActivity(intent);
            }

        } catch (Exception e) {
            Log.e("exc" , String.valueOf(e));
        }
    }

    /*private void addAutoStartup() {

        try {
            Intent intent = new Intent();
            String manufacturer = android.os.Build.MANUFACTURER;
            if ("xiaomi".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
            } else if ("oppo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
            } else if ("vivo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
            } else if ("Letv".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"));
            } else if ("Honor".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
            }

            List<ResolveInfo>
                    list = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if  (list.size() > 0) {
                startActivity(intent);
            }

        } catch (Exception e) {
            Log.e("exc" , String.valueOf(e));
        }
    }*/
}
