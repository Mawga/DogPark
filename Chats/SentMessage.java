package com.src.magakim.dogpark.Chats;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.src.magakim.dogpark.R;

public class SentMessage extends RecyclerView.ViewHolder {
    TextView messageText, timeText;

    SentMessage(View itemView) {
        super(itemView);
        messageText = itemView.findViewById(R.id.text_message_body);
        timeText = itemView.findViewById(R.id.text_message_time);
    }

    void bind(ChatObject obj) {
        messageText.setText(obj.getMessage());
        timeText.setText(obj.getTime());
/*
        // Format the stored timestamp into a readable String using method.
        timeText.setText(Utils.formatDateTime(message.getCreatedAt()));

        // Insert the profile image from the URL into the ImageView.
        Utils.displayRoundImageFromUrl(mContext, message.getSender().getProfileUrl(), profileImage);
        */
    }
}