/*
    Displays a list of matched users to chat with. Most recent activity user on top
*/
package com.src.magakim.dogpark.Matches;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.src.magakim.dogpark.Chats.ChatObject;
import com.src.magakim.dogpark.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MatchesActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mMatchesAdapter;
    private RecyclerView.LayoutManager mMatchesLayoutManager;
    private String currentUserID;
    private int currPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches);

        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mRecyclerView = findViewById(R.id.recyclerview);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        mMatchesLayoutManager = new LinearLayoutManager(MatchesActivity.this);
        mRecyclerView.setLayoutManager(mMatchesLayoutManager);
        mMatchesAdapter = new MatchesAdapter(getDataSetMatches(), MatchesActivity.this);
        mRecyclerView.setAdapter(mMatchesAdapter);
    }

    // Find matched users positions to display for current user
    private void getUserPos() {
        DatabaseReference matchDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("Connection").child("Match");
        matchDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot match : dataSnapshot.getChildren()) {
                        FetchUserPos(match.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void FetchUserPos(String key) {
        DatabaseReference userDataBase = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(currentUserID).child("Connection").child("Match").child(key).child("MatchPos");
        userDataBase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userPos = "";
                    int userPosInt;
                    if (dataSnapshot.getValue() != null) {
                        userPos = dataSnapshot.getValue().toString();
                    }
                    userPosInt = Integer.parseInt(userPos);
                    userPosInt -= 1;
                    objPos.add(userPosInt);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getUserMatchId() {
        DatabaseReference matchDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("Connection").child("Match");
        matchDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot match : dataSnapshot.getChildren()) {
                        FetchMatchInformation(match.getKey());
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    // Get matched user information such as unread message and timestamp, profile image, and username to display
    private void FetchMatchInformation(String key) {
        DatabaseReference userDataBase = FirebaseDatabase.getInstance().getReference().child("Users").child(key);
        userDataBase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userId = dataSnapshot.getKey();
                    String name = "";
                    String profileImageUrl = "";
                    String unreadmessage = "";

                    if (dataSnapshot.child("name").getValue() != null) {
                        name = dataSnapshot.child("name").getValue().toString();
                    }
                    if (dataSnapshot.child("profileImageUrl").getValue() != null) {
                        profileImageUrl = dataSnapshot.child("profileImageUrl").getValue().toString();
                    }
                    if (dataSnapshot.child("Connection").child("Match").child(currentUserID)
                            .child("LastMessage").getValue() != null &&
                            dataSnapshot.child("Connection").child("Match").child(currentUserID)
                                    .child("ReadStatus").getValue() != null) {
                        if (!dataSnapshot.child("Connection").child("Match").child(currentUserID)
                                .child("ReadStatus").getValue().equals("Yes")) {
                            unreadmessage = dataSnapshot.child("Connection").child("Match").child(currentUserID)
                                    .child("LastMessage").getValue().toString();
                        }

                    }

                    MatchesObject object = new MatchesObject(userId, name, profileImageUrl, unreadmessage);
                    object.setPos(objPos.get(currPos));
                    preResultsMatches.add(object);
                    if (resultsMatches.isEmpty()) {
                        resultsMatches.add(preResultsMatches.get(currPos));
                    } else {
                        int addPos = -1;
                        for (int i = 0; i < resultsMatches.size(); i++) {
                            if (preResultsMatches.get(currPos).getPos() <= resultsMatches.get(i).getPos()) {
                                addPos = i;
                                break;
                            }
                        }
                        if (addPos == -1) {
                            resultsMatches.add(preResultsMatches.get(currPos));
                        } else {
                            resultsMatches.add(addPos, preResultsMatches.get(currPos));
                        }
                    }
                    currPos++;
                    mMatchesAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private ArrayList<Integer> objPos = new ArrayList<Integer>();
    private ArrayList<MatchesObject> resultsMatches = new ArrayList<MatchesObject>();
    private ArrayList<MatchesObject> preResultsMatches = new ArrayList<MatchesObject>();
    private List<MatchesObject> getDataSetMatches() {
        return resultsMatches;
    }

    @Override
    protected void onResume() {
        super.onResume();
        currPos = 0;
        getUserPos();
        getUserMatchId();

    }

    @Override
    protected void onPause() {
        super.onPause();
        objPos.clear();
        resultsMatches.clear();
        preResultsMatches.clear();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
