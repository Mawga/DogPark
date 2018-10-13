/*
    Chat with matched user
*/
package com.src.magakim.dogpark.Chats;

import android.icu.util.Calendar;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.CellIdentityGsm;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.src.magakim.dogpark.Matches.MatchesActivity;
import com.src.magakim.dogpark.Matches.MatchesAdapter;
import com.src.magakim.dogpark.Matches.MatchesObject;
import com.src.magakim.dogpark.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mChatAdapter;
    private RecyclerView.LayoutManager mChatLayoutManager;
    private String currentUserId, matchId, chatId, currentUsername, profileImageUrl, timeSent;
    private EditText mSendText;
    private Button mSendButton;
    private int posLim;
    DatabaseReference mDataBaseUser, mDataBaseChat, mDataBaseUserInfo, userDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        matchId = getIntent().getExtras().getString("matchId");
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDataBaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId).child("Connection").child("Match").child(matchId).child("ChatId");
        mDataBaseChat = FirebaseDatabase.getInstance().getReference().child("Chat");
        mDataBaseUserInfo = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        DateFormat df = new SimpleDateFormat("hh:mm a");
        timeSent = df.format(Calendar.getInstance().getTime());

        FirebaseDatabase.getInstance().getReference().child("Users").child(matchId).child("Connection")
                .child("Match").child(currentUserId).child("ReadStatus").setValue("Yes");

        getChatInfo();
        getChatId();

        mRecyclerView = findViewById(R.id.reyclerview_message_list);
        mChatLayoutManager = new LinearLayoutManager(ChatActivity.this);
        mRecyclerView.setLayoutManager(mChatLayoutManager);
        mChatAdapter = new ChatAdapter(getDataSetChat(), ChatActivity.this);
        mRecyclerView.setAdapter(mChatAdapter);
        mSendText = findViewById(R.id.edittext_chatbox);
        mSendButton = findViewById(R.id.button_chatbox_send);

        // Listener for sending message
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
    }

    private void updateMatches(String userId) {
        final DatabaseReference UserConnectionDatabase = userDatabase.child(userId).child("Connection").child("Match");
        UserConnectionDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot match : dataSnapshot.getChildren()) {
                        if(match.child("MatchPos").getValue() != null) {
                            String currPos = match.child("MatchPos").getValue().toString();
                            int newPosInt = Integer.parseInt(currPos);
                            if (newPosInt < posLim) {
                                newPosInt += 1;
                                String newPos = Integer.toString(newPosInt);
                                DatabaseReference posRef = match.getRef();
                                posRef.child("MatchPos").setValue(newPos);
                            }
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    // Mesage information is uploaded to database 
    private void sendMessage() {
        String sendMessageText = mSendText.getText().toString();
        if (!sendMessageText.isEmpty()) {
            DatabaseReference newMessageDatabase = mDataBaseChat.push();
            Map newMessage = new HashMap();
            newMessage.put("Created By User", currentUserId);
            newMessage.put("Text", sendMessageText);
            newMessage.put("Username", currentUsername);
            newMessage.put("profileImageUrl", profileImageUrl);
            newMessage.put("TimeStamp", timeSent);
            newMessageDatabase.setValue(newMessage);

            mDataBaseUserInfo.child("Connection").child("Match").child(matchId).child("ReadStatus")
                    .setValue("No");
            mDataBaseUserInfo.child("Connection").child("Match").child(matchId).child("LastMessage")
                    .setValue(sendMessageText);

            DatabaseReference currentUserConnectionDatabase = mDataBaseUserInfo
                    .child("Connection").child("Match");
            currentUserConnectionDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        if (dataSnapshot.child(matchId).child("MatchPos").getValue() != null) {
                            String matchPos = dataSnapshot.child(matchId).child("MatchPos").getValue().toString();
                            int matchPosInt = Integer.parseInt(matchPos);
                            if (matchPosInt != 1) {
                                posLim = matchPosInt;
                                updateMatches(currentUserId);
                                userDatabase.child(currentUserId).child("Connection").child("Match")
                                        .child(matchId).child("MatchPos").setValue("1");
                            }
                        }

                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

            DatabaseReference refUpdate = userDatabase.child(matchId)
                    .child("Connection").child("Match");
            refUpdate.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(currentUserId).child("MatchPos").getValue() != null) {
                        String matchPos = dataSnapshot.child(currentUserId).child("MatchPos")
                                .getValue().toString();
                        int matchPosInt = Integer.parseInt(matchPos);
                        if (matchPosInt != 1) {
                            posLim = matchPosInt;
                            updateMatches(matchId);
                            userDatabase.child(matchId).child("Connection").child("Match")
                                    .child(currentUserId).child("MatchPos").setValue("1");
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        mSendText.setText(null);
    }

    // Get username and profile image of current user to display in chat
    private void getChatInfo() {
        userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    currentUsername = dataSnapshot.child(currentUserId).child("name").getValue().toString();
                    profileImageUrl = "default";
                    if (!dataSnapshot.child(currentUserId).child("profileImageUrl").getValue().equals("default")) {
                        profileImageUrl = dataSnapshot.child(currentUserId).child("profileImageUrl").getValue().toString();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    // Opening a chat with a matched user
    private void getChatId() {
        mDataBaseUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    chatId = dataSnapshot.getValue().toString();
                    mDataBaseChat = mDataBaseChat.child(chatId);
                    getChatMessages();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    // get chat messages with matched user displaying messages, timestamps, profile pictures, and usernames
    private void getChatMessages() {
        mDataBaseChat.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()) {
                    String message = null;
                    String username = null;
                    String createdByUser = null;
                    String profileURL = null;
                    String TimeStamp = null;
                    if (dataSnapshot.child("Text").getValue() != null) {
                        message = dataSnapshot.child("Text").getValue().toString();
                    }
                    if (dataSnapshot.child("Created By User").getValue() != null) {
                        createdByUser = dataSnapshot.child("Created By User").getValue().toString();
                    }
                    if (dataSnapshot.child("Username").getValue() != null) {
                        username = dataSnapshot.child("Username").getValue().toString();
                    }
                    if (dataSnapshot.child("profileImageUrl").getValue() != null) {
                        profileURL = dataSnapshot.child("profileImageUrl").getValue().toString();
                    }
                    if (dataSnapshot.child("TimeStamp").getValue() != null) {
                        TimeStamp = dataSnapshot.child("TimeStamp").getValue().toString();
                    }
                    if (message != null && createdByUser != null && username != null && TimeStamp != null) {
                        Boolean currentUserBoolean = false;
                        if (createdByUser.equals(currentUserId)) {
                            currentUserBoolean = true;
                        }
                        ChatObject newMessage;
                        if (profileURL != null) {
                            newMessage = new ChatObject(message, currentUserBoolean, username, profileURL, TimeStamp);
                        } else {
                            newMessage = new ChatObject(message, currentUserBoolean, username, TimeStamp);
                        }
                        resultsChat.add(newMessage);
                        mChatAdapter.notifyDataSetChanged();
                        mRecyclerView.getLayoutManager().scrollToPosition(mChatAdapter.getItemCount()-1);
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private ArrayList<ChatObject> resultsChat = new ArrayList<ChatObject>();
    private List<ChatObject> getDataSetChat() {
        return resultsChat;
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseDatabase.getInstance().getReference().child("Users").child(matchId).child("Connection")
                .child("Match").child(currentUserId).child("ReadStatus").setValue("Yes");
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseDatabase.getInstance().getReference().child("Users").child(matchId).child("Connection")
                .child("Match").child(currentUserId).child("ReadStatus").setValue("Yes");
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseDatabase.getInstance().getReference().child("Users").child(matchId).child("Connection")
                .child("Match").child(currentUserId).child("ReadStatus").setValue("Yes");
    }
}

