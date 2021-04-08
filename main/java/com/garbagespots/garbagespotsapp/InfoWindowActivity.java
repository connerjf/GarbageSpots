package com.garbagespots.garbagespotsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Map;

public class InfoWindowActivity extends AppCompatActivity {

    private String dateText;
    private String descText;
    private long ratingText;
    private long credText;
    private String posterText;
    private String userID;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private DatabaseReference myRef2;
    private String title = "";
    private ValueEventListener stateValueEventListener;
    private ValueEventListener stateValueEventListener2;
    private ValueEventListener state;
    private HashMap<DatabaseReference, ValueEventListener> hashMap = new HashMap<>();
    private boolean repeat = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_window);
        final Intent i = getIntent();
        title = i.getStringExtra("title");
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference().child("garbageSpots");
        myRef2 = mFirebaseDatabase.getReference().child("users");
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        myRef.child(title).addValueEventListener(stateValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot snapshot3) {
                try {
                    dateText = snapshot3.child("date").getValue().toString();
                    String monthText = "";
                    String month = String.valueOf(dateText.charAt(0)) + String.valueOf(dateText.charAt(1));
                    switch (month) {
                        case "01":
                            monthText = "January";
                            break;
                        case "02":
                            monthText = "February";
                            break;
                        case "03":
                            monthText = "March";
                            break;
                        case "04":
                            monthText = "April";
                            break;
                        case "05":
                            monthText = "May";
                            break;
                        case "06":
                            monthText = "June";
                            break;
                        case "07":
                            monthText = "July";
                            break;
                        case "08":
                            monthText = "August";
                            break;
                        case "09":
                            monthText = "September";
                            break;
                        case "10":
                            monthText = "October";
                            break;
                        case "11":
                            monthText = "November";
                            break;
                        case "12":
                            monthText = "December";
                            break;
                    }
                    dateText = monthText + " " + dateText.substring(2,4) + ", " + dateText.substring(4,8);
                    ((TextView)findViewById(R.id.gSpotDate2)).setText(dateText);
                    descText = (String)(snapshot3.child("desc").getValue());
                    ((TextView)findViewById(R.id.gSpotDesc2)).setText(descText);
                    ratingText = (long)(snapshot3.child("rating").getValue());
                    ((TextView)findViewById(R.id.gSpotRating2)).setText(String.valueOf(ratingText));
                    credText = (long)(snapshot3.child("cred").getValue());
                    ((TextView)findViewById(R.id.gSpotCred2)).setText(String.valueOf(credText));
                    posterText = (String)(snapshot3.child("userID").getValue());
                    myRef2.child(posterText).addValueEventListener(state = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot4) {
                            ((TextView)findViewById(R.id.gSpotPoster2)).setText(snapshot4.child("username").getValue().toString());
                            myRef2.child(posterText).removeEventListener(state);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    hashMap.put(myRef, stateValueEventListener);
                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Button credIncrease = findViewById(R.id.button9);
        credIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myRef2.child(userID).addValueEventListener(stateValueEventListener2 = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (Integer.parseInt(snapshot.child("numCleanedToday").getValue().toString()) <= 5 && !repeat) {
                            int credibilityAdder = Integer.parseInt(snapshot.child("ecoScore").getValue().toString());
                            if (credText + (credibilityAdder * 8) < 950) {
                                long newCredText = credText + (credibilityAdder * 8);
                                myRef.child(title).child("cred").setValue(newCredText);
                                ((TextView) findViewById(R.id.gSpotCred2)).setText(String.valueOf(newCredText));
                                myRef2.child(userID).child("numCleanedToday").setValue(Integer.parseInt(snapshot.child("numCleanedToday").getValue().toString()) + 1);
                                Toast.makeText(InfoWindowActivity.this, "Increased the spot's credibility.", Toast.LENGTH_LONG).show();
                                Intent i = new Intent(InfoWindowActivity.this, MapsActivity.class);
                                myRef2.child(userID).removeEventListener(stateValueEventListener2);
                                startActivity(i);
                            } else if (credText + (credibilityAdder * 10) >= 950 && credText < 950) {
                                long newCredText = 950;
                                myRef.child(title).child("cred").setValue(newCredText);
                                ((TextView) findViewById(R.id.gSpotCred2)).setText(String.valueOf(newCredText));
                                myRef2.child(userID).child("numCleanedToday").setValue(Integer.parseInt(snapshot.child("numCleanedToday").getValue().toString()) + 1);
                                Toast.makeText(InfoWindowActivity.this, "Increased the spot's credibility.", Toast.LENGTH_LONG).show();
                                Intent i = new Intent(InfoWindowActivity.this, MapsActivity.class);
                                myRef2.child(userID).removeEventListener(stateValueEventListener2);
                                startActivity(i);
                            } else if (credText >= 950 && credText < 1000 && credibilityAdder > 95 && credibilityAdder <= 100) {
                                Toast.makeText(InfoWindowActivity.this, "Increased the spot's credibility.",
                                        Toast.LENGTH_LONG).show();
                                long newCredText = credText + 1;
                                myRef.child(title).child("cred").setValue(newCredText);
                                ((TextView) findViewById(R.id.gSpotCred2)).setText(String.valueOf(newCredText));
                                myRef2.child(userID).child("numCleanedToday").setValue(Integer.parseInt(snapshot.child("numCleanedToday").getValue().toString()) + 1);
                                Intent i = new Intent(InfoWindowActivity.this, MapsActivity.class);
                                startActivity(i);
                                myRef2.child(userID).removeEventListener(stateValueEventListener2);
                            } else {
                                Toast.makeText(InfoWindowActivity.this, "You can't add more credibility to this spot.",
                                        Toast.LENGTH_LONG).show();
                                myRef2.child(userID).removeEventListener(stateValueEventListener2);
                            }
                            repeat = true;
                        } else if (!repeat) {
                            Toast.makeText(InfoWindowActivity.this, "You have already reached the maximum number of actions allowed per 6 hours.",
                                    Toast.LENGTH_LONG).show();
                            myRef2.child(userID).removeEventListener(stateValueEventListener2);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println("The read failed: " + databaseError.getCode());
                    }
                });
            }
        });

        Button cleanedUp = findViewById(R.id.button10);
        cleanedUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myRef2.child(userID).addValueEventListener(stateValueEventListener2 = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (Integer.parseInt(snapshot.child("numCleanedToday").getValue().toString()) <= 5) {
                            Intent i = new Intent(InfoWindowActivity.this, CleanedUpActivity.class);
                            i.putExtra("spot", title);
                            hashMap.put(myRef2.child(userID), stateValueEventListener2);
                            startActivity(i);
                        } else {
                            Toast.makeText(InfoWindowActivity.this, "You have already reached the maximum number of actions allowed per 6 hours.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println("The read failed: " + databaseError.getCode());
                    }
                });
            }
        });

        Button addPic = findViewById(R.id.button11);
        addPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(InfoWindowActivity.this, PictureActivity.class);
                i.putExtra("spot", title);
                i.putExtra("reporter", userID);
                startActivity(i);
            }
        });

        Button report = findViewById(R.id.button13);
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(InfoWindowActivity.this, ReportActivity.class);
                i.putExtra("spot", title);
                i.putExtra("reporter", userID);
                startActivity(i);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        for (Map.Entry<DatabaseReference, ValueEventListener> entry : hashMap.entrySet()) {
            DatabaseReference databaseReference = entry.getKey();
            ValueEventListener value = entry.getValue();
            databaseReference.removeEventListener(value);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        for (Map.Entry<DatabaseReference, ValueEventListener> entry : hashMap.entrySet()) {
            DatabaseReference databaseReference = entry.getKey();
            ValueEventListener value = entry.getValue();
            databaseReference.addValueEventListener(value);
        }
    }

    public void returnMap(View v) {
        Intent i = new Intent(InfoWindowActivity.this, MapsActivity.class);
        startActivity(i);
    }


}