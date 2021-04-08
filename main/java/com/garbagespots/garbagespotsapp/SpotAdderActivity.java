package com.garbagespots.garbagespotsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.ValueEventListener;

public class SpotAdderActivity extends AppCompatActivity {

    private DatabaseReference myRef;
    private DatabaseReference myRef2;
    private String userID;
    private int ecoScoreValue;
    private String gDesc;
    private String num;
    private LatLng location;
    private boolean repeat = false;
    private ValueEventListener stateValueEvent;
    info information;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spot_adder);
        final SeekBar skBar = findViewById(R.id.seekBar);
        skBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ((TextView)findViewById(R.id.seekBarVal)).setText(String.valueOf(Integer.valueOf(progress)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        Button spotAdder = findViewById(R.id.button4);
        spotAdder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                num = ((EditText)findViewById(R.id.editTextNumber)).getText().toString();
                gDesc = ((EditText)findViewById(R.id.spotDesc)).getText().toString();
                if ((num.length() == 8) && !(gDesc.equals(""))) {
                    if ((num.charAt(0) == '0' || (num.charAt(0) == '1' && (num.charAt(1) == '0' || num.charAt(1) == '1' || num.charAt(1) == '2'))) && (num.charAt(2) == '0' || num.charAt(2) == '1' || num.charAt(2) == '2' || num.charAt(2) == '3') && num.charAt(4) == '2' && num.charAt(5) == '0' && num.charAt(6) == '2' && (num.charAt(7) == '0' || num.charAt(7) == '1' || num.charAt(7) == '2')) {
                        Intent j = getIntent();
                        location = j.getExtras().getParcelable("location");
                        information = new info();
                        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        myRef = database.getReference("garbageSpots");
                        myRef2 = database.getReference("users");
                        myRef2.child(userID).addValueEventListener(stateValueEvent = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                if (!repeat) {
                                    repeat = true;
                                    ecoScoreValue = Integer.parseInt(snapshot.child("ecoScore").getValue().toString());
                                    information.setCred(ecoScoreValue * 10);
                                    information.setDesc(gDesc);
                                    information.setLat(location.latitude);
                                    information.setLong(location.longitude);
                                    information.setUserID(userID);
                                    information.setRating(((SeekBar) findViewById(R.id.seekBar)).getProgress());
                                    information.setDate(num);
                                    myRef.push().setValue(information);
                                    ((SeekBar) findViewById(R.id.seekBar)).setProgress(3);
                                    ((EditText) findViewById(R.id.editTextNumber)).setText("");
                                    ((EditText) findViewById(R.id.spotDesc)).setText("");
                                    Intent i = new Intent(SpotAdderActivity.this, MapsActivity.class);
                                    myRef2.removeEventListener(stateValueEvent);
                                    startActivity(i);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                System.out.println("The read failed: " + databaseError.getCode());
                            }
                        });
                    } else {
                        Toast.makeText(SpotAdderActivity.this, "Enter a valid date.",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SpotAdderActivity.this, "Fill in all forms.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void returnMap(View v) {
        Intent i = new Intent(SpotAdderActivity.this, MapsActivity.class);
        startActivity(i);
    }
}