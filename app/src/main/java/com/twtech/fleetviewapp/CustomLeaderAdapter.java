package com.twtech.fleetviewapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.List;

/**
 * Created by twtech on 13/8/18.
 */

public class CustomLeaderAdapter extends RecyclerView.Adapter<CustomLeaderAdapter.MyViewHolder> {

    List<String> rank;
    List<String> winnerName;
    List<String> ratings;
    Context context;

    public CustomLeaderAdapter(Context context, List<String> rank, List<String> winnerName, List<String> ratings) {
        this.context = context;
        this.rank = rank;
        this.winnerName = winnerName;
        this.ratings = ratings;
        Log.e("inside Constructor","Constructor called");
        Log.e("list1", String.valueOf(rank));
        Log.e("list2", String.valueOf(winnerName));
        Log.e("list3", String.valueOf(ratings));
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
// infalte the item Layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.winner_drivers, parent, false);
// set the view's size, margins, paddings and layout parameters
        MyViewHolder vh = new MyViewHolder(v); // pass the view to View Holder
        return vh;
    }

    public void onBindViewHolder(MyViewHolder holder, final int position) {
// set the data in items
        holder.name.setText(rank.get(position));
        holder.rank.setText(winnerName.get(position));
        Log.e("onBindView","Method called");
        holder.ratings.setText(ratings.get(position));
    }

    @Override
    public int getItemCount() {
        return winnerName.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        // init the item view's
        TextView rank;
        TextView name, ratings;

        public MyViewHolder(final View itemView) {
            super(itemView);

        // get the reference of item view's
            Log.e("MYViewHolder","inside class");
            rank = (TextView) itemView.findViewById(R.id.first);
            name = (TextView) itemView.findViewById(R.id.first_rank_name);
            ratings = (TextView) itemView.findViewById(R.id.first_rank_ratings);
            itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        Animation anim = AnimationUtils.loadAnimation(context, R.anim.scale_in_tv);
                        itemView.startAnimation(anim);
                        anim.setFillAfter(true);
                    } else {
                        // run scale animation and make it smaller
                        Animation anim = AnimationUtils.loadAnimation(context, R.anim.scale_out_tv);
                        itemView.startAnimation(anim);
                        anim.setFillAfter(true);
                    }
                }
            });
        }
    }
}