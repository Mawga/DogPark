package com.src.magakim.dogpark.Chats;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.src.magakim.dogpark.R;

public class ReceivedMessage extends RecyclerView.ViewHolder {
    TextView messageText, nameText, timeText;
    ImageView profileImage;

    ReceivedMessage(View itemView) {
        super(itemView);
        messageText = itemView.findViewById(R.id.text_message_body);
        nameText = itemView.findViewById(R.id.text_message_name);
        timeText = itemView.findViewById(R.id.text_message_time);

        profileImage = itemView.findViewById(R.id.image_message_profile);

    }

    void bind(ChatObject obj) {
        messageText.setText(obj.getMessage());
        nameText.setText(obj.getUsername());
        timeText.setText(obj.getTime());
/*
        // Format the stored timestamp into a readable String using method.
        timeText.setText(Utils.formatDateTime(message.getCreatedAt()));
        nameText.setText(message.getSender().getNickname());
*/
        // Insert the profile image from the URL into the ImageView.

    }
}
