package com.garbagespots.garbagespotsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomePage extends AppCompatActivity {

    private static final int ERROR_DIALOG_REQUEST = 9001;
    private ImageView imageView;
    private FirebaseFirestore myStore3;
    private TextView textView;
    private String spotTitle = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        imageView = findViewById(R.id.imageView2);
        textView = findViewById(R.id.textView23);
        myStore3 = FirebaseFirestore.getInstance();
        DocumentReference docIdRef = myStore3.collection("images").document("specialImage");
        docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    final DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        spotTitle = document.getString("spotTitle");
                        if (!spotTitle.equals("")) {
                            DocumentReference docIdRef2 = myStore3.collection("images").document(spotTitle);
                            docIdRef2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task2) {
                                    if (task2.isSuccessful()) {
                                        final DocumentSnapshot document2 = task2.getResult();
                                        String encodedString = document2.getString("picture");
                                        byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
                                        Bitmap bitImage = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                                        imageView.setImageBitmap(bitImage);
                                        textView.setText(new StringBuilder().append("GARBAGE SPOT OF THE DAY\nLocated in ").append(document.getString("locationName")).toString());
                                    } else {
                                        textView.setText("");
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    public boolean isServicesOK() {
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(HomePage.this);
        if(available == ConnectionResult.SUCCESS){
            return true;
        }
        else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(HomePage.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public void goToMap(View v) {
        if (isServicesOK()) {
            Intent i = new Intent(this, InstructionsActivity.class);
            startActivity(i);
        }
    }

    public void seeProfile(View v) {
        Intent i = new Intent(this, ProfileActivity.class);
        startActivity(i);
    }

    public void signOut(View v) {
        FirebaseAuth.getInstance().signOut();
        Intent i = new Intent(this, WelcomeActivity.class);
        startActivity(i);
        finish();
    }

}