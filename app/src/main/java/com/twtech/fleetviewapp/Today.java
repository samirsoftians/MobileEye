package com.twtech.fleetviewapp;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Today extends Fragment {

    View view;
    Context mContext;
    TextView ranker0NM;
    RequestQueue requestQueue;
    GlobalVariable gbll;
    private RecyclerView mRecyclerView;
    List<String> rank;
    List<String> winnerName;
    List<String> ratings;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_today, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mContext = getActivity().getApplicationContext();
        requestQueue = Volley.newRequestQueue(mContext);
        gbll = (GlobalVariable)mContext;
        rank=new ArrayList<>();
        winnerName=new ArrayList<>();
        ratings=new ArrayList<>();

        String winner = gbll.getWinners();
        Log.e("Winners ",": "+winner);
        // new downloadWinnersList(mContext).downloadWinners();
        ranker0NM = (TextView) view.findViewById(R.id.first_rank_name);

        if(winner!=null) {
            try {
                String drivers[] = winner.split("_");
                for (int i = 0; i < drivers.length; i++) {

                    String details[] = drivers[i].split(",");
                    // String nm = details[0];
              /*  boolean b = stringContainsNumber(nm);
                if(b==true){
                    str = str.substring(0, str.length() - 1);
                }else {
                }
                }*/
                    rank.add(details[0]);
                    winnerName.add(details[1]);
                    ratings.add(details[2]);
                    Log.e("driverDetails", String.valueOf(drivers[i].split(",")));

                }
            } catch (Exception e) {
                Log.e("Exception while ", "Splitting " + e.getMessage());
            }

            CustomLeaderAdapter customAdapter = new CustomLeaderAdapter(mContext, rank, winnerName, ratings);

            mRecyclerView.setAdapter(customAdapter); // set the Adapter to RecyclerView
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
            mRecyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView
            Log.e("ss", "setting adapter");
        }
        return view;
    }

    public boolean stringContainsNumber( String s )
    {
        Pattern p = Pattern.compile( "[0-9]" );
        Matcher m = p.matcher( s );

        return m.find();
    }

    private Date yesterday() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }

    private String getYesterdayDateString() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(yesterday());
    }

}
