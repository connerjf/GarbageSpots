package com.garbagespots.garbagespotsapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ReportActivity extends AppCompatActivity {

    private ValueEventListener stateValueEventListener5;
    private DatabaseReference myRef;
    private String spotTitle = "";
    private String reporterID = "";
    private StringBuilder userInput = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        Button report = findViewById(R.id.button14);
        Intent i = getIntent();
        spotTitle = i.getStringExtra("spot");
        reporterID = i.getStringExtra("reporter");
        myRef = FirebaseDatabase.getInstance().getReference("users");
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((Switch)findViewById(R.id.switch3)).isChecked() || ((Switch)findViewById(R.id.switch4)).isChecked() || ((Switch)findViewById(R.id.switch5)).isChecked() || ((Switch)findViewById(R.id.switch6)).isChecked() || !((EditText) findViewById(R.id.otherReason)).getText().toString().equals("")) {
                    myRef.child(reporterID).addValueEventListener(stateValueEventListener5 = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            userInput.append(snapshot.child("name").getValue().toString()).append(" (Username: ").append(snapshot.child("username").getValue().toString()).append(") is reporting the Garbage Spot titled: ").append(spotTitle).append(". \n \n");
                            userInput.append("The reasons for reporting are: \n");
                            if (((Switch)findViewById(R.id.switch3)).isChecked()) {
                                userInput.append("  -There was never any garbage here. \n");
                            }
                            if (((Switch)findViewById(R.id.switch4)).isChecked()) {
                                userInput.append("  -The picture is not of the garbage. \n");
                            }
                            if (((Switch)findViewById(R.id.switch5)).isChecked()) {
                                userInput.append("  -The user who posted this is spamming. \n");
                            }
                            if (((Switch)findViewById(R.id.switch6)).isChecked()) {
                                userInput.append("  -The description or date are not accurate. \n");
                            }
                            if (!(((EditText)findViewById(R.id.otherReason)).getText().toString().equals(""))) {
                                userInput.append("  -").append(((EditText)findViewById(R.id.otherReason)).getText().toString());
                            }
                            userInput.append("\n \nUserID = ").append(reporterID);
                            Intent i = new Intent(Intent.ACTION_SEND);
                            i.setType("message/rfc822");
                            i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"garbagespotsapp@gmail.com"});
                            i.putExtra(Intent.EXTRA_SUBJECT, "Garbage Spots Reporting");
                            i.putExtra(Intent.EXTRA_TEXT   , userInput.toString());
                            myRef.child(reporterID).removeEventListener(stateValueEventListener5);
                            try {
                                startActivity(Intent.createChooser(i, "Send mail..."));
                            } catch (android.content.ActivityNotFoundException ex) {
                                Toast.makeText(ReportActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            System.out.println("The read failed: " + databaseError.getCode());
                        }
                    });
                } else {
                    Toast.makeText(ReportActivity.this, "Please provide a reason for reporting.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void returnInfoWindow(View v) {
        Intent i = new Intent(ReportActivity.this, InfoWindowActivity.class);
        i.putExtra("title", spotTitle);
        startActivity(i);
    }
}