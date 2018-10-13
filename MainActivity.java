/*
    Displays main screen with cards for matching
*/
package com.src.magakim.dogpark;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.src.magakim.dogpark.Cards.arrayAdapter;
import com.src.magakim.dogpark.Cards.cards;
import com.src.magakim.dogpark.Matches.MatchesActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private cards card_data[];
    private com.src.magakim.dogpark.Cards.arrayAdapter arrayAdapter;
    private int i;
    private FirebaseAuth mAuth;
    private String currentUId;
    private DatabaseReference userDatabase;
    ListView listView;
    List<cards> rowItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set userDatabase to users in databse
        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        currentUId = mAuth.getCurrentUser().getUid();

        checkUser();

        rowItems = new ArrayList<cards>();
        arrayAdapter = new arrayAdapter(this, R.layout.item, rowItems);

        SwipeFlingAdapterView flingContainer = findViewById(R.id.frame);

        flingContainer.setAdapter(arrayAdapter);

        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                Log.d("LIST", "removed object!");
                rowItems.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

            // Swipe left = no
            @Override
            public void onLeftCardExit(Object dataObject) {
                cards obj = (cards)dataObject;
                String userId = obj.getUserId();
                userDatabase.child(userId).child("Connection").child("Nah").child(currentUId).setValue(true);
                Toast.makeText(MainActivity.this, "Nah!", Toast.LENGTH_SHORT).show();
            }

            // Swipe right = yes
            @Override
            public void onRightCardExit(Object dataObject) {
                cards obj = (cards)dataObject;
                String userId = obj.getUserId();
                userDatabase.child(userId).child("Connection").child("Yeah").child(currentUId).setValue(true);
                // Check if other user also swiped right (yes)
                isMatch(userId);
                Toast.makeText(MainActivity.this, "Yeah!", Toast.LENGTH_SHORT).show();
            }

            // Once cards are empty, no unseen users for current user in database
            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                if (arrayAdapter.getCount() == 0 && rowItems.isEmpty()) {
                    Toast.makeText(MainActivity.this, "No more users :(", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
            }
        });

        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Toast.makeText(MainActivity.this, "click", Toast.LENGTH_SHORT).show();
            }
        });

    }

    // If matched create new chat on chatactivity
    private void isMatch(final String userId) {
        DatabaseReference currentUserConnectionDatabase = userDatabase.child(currentUId).child("Connection").child("Yeah").child(userId);
        currentUserConnectionDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(MainActivity.this, "Congratulations, new match!", Toast.LENGTH_LONG).show();
                    String key = FirebaseDatabase.getInstance().getReference().child("Chat").push().getKey();

                    updateMatches(currentUId);
                    updateMatches(dataSnapshot.getKey());

                    // MatchPos refers to location of user on chat list. 1 on top
                    userDatabase.child(dataSnapshot.getKey()).child("Connection").child("Match").child(currentUId).child("MatchPos").setValue("1");
                    userDatabase.child(currentUId).child("Connection").child("Match").child(dataSnapshot.getKey()).child("MatchPos").setValue("1");

                    userDatabase.child(dataSnapshot.getKey()).child("Connection").child("Match").child(currentUId).child("ChatId").setValue(key);
                    userDatabase.child(currentUId).child("Connection").child("Match").child(dataSnapshot.getKey()).child("ChatId").setValue(key);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    // update MatchPos so users in chat move down the list
    private void updateMatches(String userId) {
        final DatabaseReference UserConnectionDatabase = userDatabase.child(userId).child("Connection").child("Match");
        UserConnectionDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot match : dataSnapshot.getChildren()) {
                        if (match.child("MatchPos").getValue() != null) {
                            String currPos = match.child("MatchPos").getValue().toString();
                            int newPosInt = Integer.parseInt(currPos);
                            newPosInt += 1;
                            String newPos = Integer.toString(newPosInt);
                            DatabaseReference posRef = match.getRef();
                            posRef.child("MatchPos").setValue(newPos);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private String checkId;

    // Find current user information in database
    public void checkUser() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference usersDatabase = userDatabase.child(user.getUid());
        usersDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    checkId = dataSnapshot.getKey();
                    getOtherUser();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    // Get other users in database and if not previously swiped add them to cards to display
    public void getOtherUser() {
        userDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists() &&
                        !dataSnapshot.child("Connection").child("Nah").hasChild(currentUId) &&
                        !dataSnapshot.child("Connection").child("Yeah").hasChild(currentUId) &&
                         !dataSnapshot.getKey().equals(checkId)) {
                    String profileImageUrl = "default";
                    if (!dataSnapshot.child("profileImageUrl").getValue().equals("default")) {
                        profileImageUrl = dataSnapshot.child("profileImageUrl").getValue().toString();
                    }
                    cards item = new cards(dataSnapshot.getKey(),
                            dataSnapshot.child("name").getValue().toString(), profileImageUrl);
                    rowItems.add(item);
                    arrayAdapter.notifyDataSetChanged();
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

    public void logoutUser(View view) {
        mAuth.signOut();
        Intent intent = new Intent(MainActivity.this, LoginOrCreateAccountActivity.class);
        startActivity(intent);
        finish();
        return;
    }

    public void goToSettings(View view) {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
        return;
    }

    public void goToMatches(View view) {
        Intent intent = new Intent(MainActivity.this, MatchesActivity.class);
        startActivity(intent);
        return;
    }
}