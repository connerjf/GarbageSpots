package com.garbagespots.garbagespotsapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private String username;
    private Switch importantEmailsSwitch;
    private Switch weeklyEmailsSwitch;
    private String userID;
    private int noInitialWeeklyEmail = 1;
    private int noInitialImportantEmail = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        importantEmailsSwitch = findViewById(R.id.switch1);
        weeklyEmailsSwitch = findViewById(R.id.switch2);
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("users").child(userID);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                username = (String)snapshot.child("username").getValue();
                ((TextView)findViewById(R.id.textView9)).setText(username);
                ((TextView)findViewById(R.id.textView14)).setText(String.valueOf(snapshot.child("name").getValue()));
                ((TextView)findViewById(R.id.textView16)).setText(String.valueOf(snapshot.child("ecoScore").getValue()));
                ((TextView)findViewById(R.id.textView19)).setText(String.valueOf(snapshot.child("numCleanedTotal").getValue()));
                if (snapshot.child("importantEmails").getValue().toString().equals("true")) {
                    importantEmailsSwitch.setChecked(true);
                } else {
                    importantEmailsSwitch.setChecked(false);
                }
                if (snapshot.child("weeklyEmails").getValue().toString().equals("true")) {
                    weeklyEmailsSwitch.setChecked(true);
                } else {
                    weeklyEmailsSwitch.setChecked(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        importantEmailsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (noInitialImportantEmail == 0) {
                    if (isChecked) {
                        String userInput = "User: " + username + " has requested to receive important emails from Garbage Spots App. \n \nUserID = " + userID;
                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("message/rfc822");
                        i.putExtra(Intent.EXTRA_EMAIL, new String[]{"garbagespotsapp@gmail.com"});
                        i.putExtra(Intent.EXTRA_SUBJECT, "Receive Important Emails");
                        i.putExtra(Intent.EXTRA_TEXT, userInput);
                        try {
                            startActivity(Intent.createChooser(i, "Send mail..."));
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(ProfileActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        String userInput = "User: " + username + " has requested to NOT receive important emails from Garbage Spots App. \n \nUserID = " + userID;
                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("message/rfc822");
                        i.putExtra(Intent.EXTRA_EMAIL, new String[]{"garbagespotsapp@gmail.com"});
                        i.putExtra(Intent.EXTRA_SUBJECT, "No longer receive Important Emails");
                        i.putExtra(Intent.EXTRA_TEXT, userInput);
                        try {
                            startActivity(Intent.createChooser(i, "Send mail..."));
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(ProfileActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                noInitialImportantEmail = 0;
            }
        });
        weeklyEmailsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (noInitialWeeklyEmail == 0) {
                    if (isChecked) {
                        String userInput = "User: " + username + " has requested to receive weekly emails from Garbage Spots App. \n \nUserID = " + userID;
                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("message/rfc822");
                        i.putExtra(Intent.EXTRA_EMAIL, new String[]{"garbagespotsapp@gmail.com"});
                        i.putExtra(Intent.EXTRA_SUBJECT, "Receive Weekly Emails");
                        i.putExtra(Intent.EXTRA_TEXT, userInput);
                        try {
                            startActivity(Intent.createChooser(i, "Send mail..."));
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(ProfileActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        String userInput = "User: " + username + " has requested to NOT receive weekly emails from Garbage Spots App. \n \nUserID = " + userID;
                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("message/rfc822");
                        i.putExtra(Intent.EXTRA_EMAIL, new String[]{"garbagespotsapp@gmail.com"});
                        i.putExtra(Intent.EXTRA_SUBJECT, "No longer receive Weekly Emails");
                        i.putExtra(Intent.EXTRA_TEXT, userInput);
                        try {
                            startActivity(Intent.createChooser(i, "Send mail..."));
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(ProfileActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                noInitialWeeklyEmail = 0;
            }
        });
    }

    public void returnHomePage(View v) {
        Intent i = new Intent(ProfileActivity.this, HomePage.class);
        startActivity(i);
    }
}