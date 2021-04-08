package com.garbagespots.garbagespotsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class CleanedUpActivity extends AppCompatActivity {

    private String title;
    private String enteredDate;
    private Boolean dateIsAfter = false;
    private DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("garbageSpots");
    private DatabaseReference myRef2;
    private String userID;
    private DocumentReference docRef;
    private DocumentReference docRef2;
    private int origPostersScore;
    private String origPoster = "";
    private long cuRating = 222L;
    private long ecoScore = 222L;
    private long numCleanTotalCurrent = -1L;
    private long numCleanTodayCurrent = -1L;
    private ValueEventListener stateValueEventListener;
    private ValueEventListener stateValueEventListener2;
    private ValueEventListener stateValueEventListener3;
    private HashMap<DatabaseReference, ValueEventListener> hashMap = new HashMap<>();
    boolean repeat = true;
    boolean repeatfirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cleaned_up);
        Intent x = getIntent();
        title = x.getStringExtra("spot");
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public void onClick (View v) {
        if (((CheckBox)findViewById(R.id.checkBox)).isChecked()) {
            ((CheckBox) findViewById(R.id.checkBox)).setChecked(false);
            dateIsAfter = false;
            myRef = FirebaseDatabase.getInstance().getReference().child("garbageSpots");
            myRef2 = FirebaseDatabase.getInstance().getReference().child("users");
            enteredDate = ((EditText)findViewById(R.id.editTextNumber2)).getText().toString();
            myRef.child(title).addValueEventListener(stateValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (enteredDate.length() == 8 && repeatfirst) {
                        repeatfirst = false;
                        String postedDate = snapshot.child("date").getValue().toString();
                        if (Integer.parseInt(String.valueOf(enteredDate.charAt(7))) - 1 == Integer.parseInt(String.valueOf(postedDate.charAt(7)))) {
                            dateIsAfter = true;
                        } else if (enteredDate.charAt(7) >= postedDate.charAt(7) && Integer.parseInt(enteredDate.substring(0, 2)) > Integer.parseInt(postedDate.substring(0, 2))) {
                            dateIsAfter = true;
                        } else if (enteredDate.charAt(7) >= postedDate.charAt(7) && Integer.parseInt(enteredDate.substring(0, 2)) >= Integer.parseInt(postedDate.substring(0, 2)) && Integer.parseInt(enteredDate.substring(2, 4)) > Integer.parseInt(postedDate.substring(2, 4))) {
                            dateIsAfter = true;
                        }
                        if ((enteredDate.charAt(0) == '0' || (enteredDate.charAt(0) == '1' && (enteredDate.charAt(1) == '0' || enteredDate.charAt(1) == '1' || enteredDate.charAt(1) == '2'))) && (enteredDate.charAt(2) == '0' || enteredDate.charAt(2) == '1' || enteredDate.charAt(2) == '2' || enteredDate.charAt(2) == '3') && enteredDate.charAt(4) == '2' && enteredDate.charAt(5) == '0' && enteredDate.charAt(6) == '2' && (enteredDate.charAt(7) == '0' || enteredDate.charAt(7) == '1' || enteredDate.charAt(7) == '2') && dateIsAfter) {
                            if (snapshot.exists() && snapshot.child("userID").getValue() != null) {
                                origPoster = snapshot.child("userID").getValue().toString();
                                cuRating = Long.parseLong(snapshot.child("rating").getValue().toString());
                                myRef2.addValueEventListener(stateValueEventListener2 = new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot snapshot2) {
                                        if (cuRating != 222L && repeat) {
                                            ecoScore = Long.parseLong(snapshot2.child(userID).child("ecoScore").getValue().toString()) + cuRating;
                                            if (ecoScore < 90) {
                                                myRef2.child(userID).child("ecoScore").setValue(ecoScore);
                                            }
                                            if (!(origPoster.equals(userID))) {
                                                origPostersScore = Integer.parseInt(snapshot2.child(origPoster).child("ecoScore").getValue().toString());
                                                if (origPostersScore + 5 < 90) {
                                                    myRef2.child(origPoster).child("ecoScore").setValue(origPostersScore + 5);
                                                } else if (origPostersScore + 5 < 95 && ecoScore > 70) {
                                                    myRef2.child(origPoster).child("ecoScore").setValue(origPostersScore + 3);
                                                } else if (origPostersScore + 5 <= 100 && ecoScore > 80) {
                                                    myRef2.child(origPoster).child("ecoScore").setValue(95L);
                                                }
                                                myRef2.child(origPoster).child("numCleanedTotal").setValue(Long.parseLong(snapshot2.child(origPoster).child("numCleanedTotal").getValue().toString()) + 1L);
                                            }
                                            numCleanTotalCurrent = Long.parseLong(snapshot2.child(userID).child("numCleanedTotal").getValue().toString());
                                            myRef2.child(userID).child("numCleanedTotal").setValue(numCleanTotalCurrent + 1);
                                            numCleanTodayCurrent = Long.parseLong(snapshot2.child(userID).child("numCleanedToday").getValue().toString());
                                            myRef2.child(userID).child("numCleanedToday").setValue(numCleanTodayCurrent + 1);
                                            Toast.makeText(CleanedUpActivity.this, "Successfully removed this garbage spot.",
                                                    Toast.LENGTH_SHORT).show();
                                            ((EditText) findViewById(R.id.editTextNumber2)).setText("");
                                            Intent y = new Intent(CleanedUpActivity.this, MapsActivity.class);
                                            hashMap.put(myRef.child(title), stateValueEventListener);
                                            hashMap.put(myRef2, stateValueEventListener2);
                                            myRef.child(title).removeValue();
                                            docRef = FirebaseFirestore.getInstance().collection("images").document(title);
                                            docRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task3) {
                                                    if (task3.isSuccessful()) {
                                                        docRef2 = FirebaseFirestore.getInstance().collection("images").document("specialImage");
                                                        docRef2.update("spotTitle", "").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {

                                                            }
                                                        });
                                                        docRef2.update("locationName", "").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {

                                                            }
                                                        });
                                                    }
                                                }
                                            });
                                            repeat = false;
                                            startActivity(y);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        } else {
                            Toast.makeText(CleanedUpActivity.this, "Enter a valid date. Your Eco Score has been reduced by 5%.",
                                    Toast.LENGTH_SHORT).show();
                            myRef2.child(userID).addValueEventListener(stateValueEventListener3 = new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {
                                    int ecoScoreVal = Integer.parseInt(snapshot.child("ecoScore").getValue().toString());
                                    myRef2.child(userID).child("ecoScore").setValue(ecoScoreVal - Math.round(ecoScoreVal * 0.05));
                                    ((EditText)findViewById(R.id.editTextNumber2)).setText("");
                                    Intent i = new Intent(CleanedUpActivity.this, MapsActivity.class);
                                    i.putExtra("title", title);
                                    hashMap.put(myRef2.child(userID), stateValueEventListener3);
                                    startActivity(i);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    System.out.println("The read failed: " + databaseError.getCode());
                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {

                }
            });
        } else {
            Toast.makeText(CleanedUpActivity.this, "Check 'I am sure'.",
                    Toast.LENGTH_SHORT).show();
        }

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

    public void returnInfoWindow(View v) {
        Intent i = new Intent(CleanedUpActivity.this, InfoWindowActivity.class);
        i.putExtra("title", title);
        startActivity(i);
    }

}