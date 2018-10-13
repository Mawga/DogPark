package com.src.magakim.dogpark.Chats;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.bumptech.glide.Glide;
import com.src.magakim.dogpark.R;

import java.util.List;


public class ChatAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private String currentUserId, matchId, chatId;

    private Context mContext;
    private List<ChatObject> chatList;

    public ChatAdapter(List<ChatObject> matchesList, Context context) {
        this.mContext = context;
        this.chatList = matchesList;
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {
        if (chatList.get(position).getCurrentUser()) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }


    // Inflates the appropriate layout according to the ViewType.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.sent_message, parent, false);
            return new SentMessage(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.received_message, parent, false);
            return new ReceivedMessage(view);
        }

        return null;
    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChatObject obj = chatList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessage) holder).bind(obj);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                Context holderContext = ((ReceivedMessage) holder).messageText.getContext();
                ((ReceivedMessage) holder).bind(obj);
                Glide.clear(((ReceivedMessage) holder).profileImage);
                switch (obj.getImageURL()) {
                    case "default":
                        Glide.with(holderContext).load(R.drawable.ic_launcher).into(((ReceivedMessage) holder).profileImage);
                        break;
                    default:
                        Glide.with(holderContext).load(obj.getImageURL()).into(((ReceivedMessage) holder).profileImage);
                        break;
                }
        }
    }

}