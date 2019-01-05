package com.twtech.fleetviewapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class LeaderboardActivity extends AppCompatActivity implements View.OnClickListener {

    TextView textView, textView2, textView3;
    TextView clickHere1;
    ImageView imageView, imageView2, imageView3, share;
    LinearLayout mainLinearLayout;
    FrameLayout frameLayout, frameLayout1;
    LinearLayout rootContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        getSupportActionBar().hide();
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);

        textView = (TextView) findViewById(R.id.today);
        textView2 = (TextView) findViewById(R.id.weekly);
        textView3 = (TextView) findViewById(R.id.podium);
        // clickHere1 = (TextView) findViewById(R.id.register);
        imageView = (ImageView) findViewById(R.id.img_date);
        imageView2 = (ImageView) findViewById(R.id.img_week);
        imageView3 = (ImageView) findViewById(R.id.img_podium);
        share = (ImageView) findViewById(R.id.leader_share);
        rootContent=(LinearLayout)findViewById(R.id.scroll_view);

        frameLayout = (FrameLayout) View.inflate(this, R.layout.fragment_today, null);
        frameLayout1 = (FrameLayout) View.inflate(this, R.layout.fragment_podium, null);
        imageView.setOnClickListener(this);
        imageView3.setOnClickListener(this);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.leader_share:
                        takeScreenshot(ScreenshotType.FULL);
                        break;
                }
            }
        });
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onClick(View v) {
        Fragment fragment = null;
        if (v == findViewById(R.id.img_date)) {
            textView.setTextColor(Color.BLACK);
            textView3.setTextColor(getResources().getColor(R.color.end));
            // textView3.setBackgroundResource(R.color.end);
            fragment = new Today();
        } else if (v == findViewById(R.id.img_podium)) {
            textView3.setTextColor(Color.BLACK);
            textView.setTextColor(getResources().getColor(R.color.end));
            // textView.setBackgroundResource(R.color.end);
            fragment = new Podium();


        }

        switch (v.getId()) {
            case R.id.leader_share:
                takeScreenshot(ScreenshotType.FULL);
                break;
        }
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.output, fragment);
        transaction.commit();

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
        Intent i = new Intent(LeaderboardActivity.this, DashboardActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();

    }
}

