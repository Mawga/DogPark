package com.src.magakim.dogpark.Matches;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.src.magakim.dogpark.R;

import java.util.List;

public class MatchesAdapter extends RecyclerView.Adapter<MatchesViewHolders> {
    private List<MatchesObject> matchesList;
    private Context context;
    public MatchesAdapter(List<MatchesObject> matchesList, Context context) {
        this.matchesList = matchesList;
        this.context = context;
    }

    @Override
    public MatchesViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_matches, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        MatchesViewHolders rcv = new MatchesViewHolders((layoutView));
        return rcv;
    }

    @Override
    public void onBindViewHolder(MatchesViewHolders holder, int position) {
        holder.mMatchId.setText(matchesList.get(position).getUserId());
        holder.mMatchName.setText(matchesList.get(position).getName());

        Glide.clear(holder.mMatchImage);
        if (!matchesList.get(position).getProfileImageUrl().equals("default")) {
            Glide.with(context).load(matchesList.get(position).getProfileImageUrl()).into(holder.mMatchImage);
        } else {
            Glide.with(context).load(R.drawable.ic_launcher).into(holder.mMatchImage);
        }
        holder.mUnreadMessage.setText("");
        if (!matchesList.get(position).getUnreadMessage().equals("")) {
            holder.mUnreadMessage.setText(matchesList.get(position).getUnreadMessage());
            holder.mUnreadMessage.setTextColor(Color.parseColor("#000000"));
            holder.mUnreadMessage.setTypeface(null, Typeface.BOLD);
        }
    }

    @Override
    public int getItemCount() {
        return matchesList.size();
    }
}
