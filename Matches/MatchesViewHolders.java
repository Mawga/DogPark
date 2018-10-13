package com.src.magakim.dogpark.Matches;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.src.magakim.dogpark.Chats.ChatActivity;
import com.src.magakim.dogpark.R;

public class MatchesViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView mMatchId, mMatchName, mUnreadMessage;
    public ImageView mMatchImage;
    public MatchesViewHolders(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        mMatchId = itemView.findViewById(R.id.matchId);
        mMatchName = itemView.findViewById(R.id.matchName);
        mMatchImage = itemView.findViewById(R.id.matchImage);
        mUnreadMessage = itemView.findViewById(R.id.unreadMessage);
    }
    @Override
    public void onClick(View view) {
        Intent intent = new Intent(view.getContext(), ChatActivity.class);
        Bundle b = new Bundle();
        b.putString("matchId", mMatchId.getText().toString());
        intent.putExtras(b);
        view.getContext().startActivity(intent);
    }
}
