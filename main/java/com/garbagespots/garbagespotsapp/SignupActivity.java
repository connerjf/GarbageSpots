package com.garbagespots.garbagespotsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();;
    private DatabaseReference myRef2;
    private ValueEventListener stateValueEventListener;
    private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    FirebaseAuth.getInstance().signOut();
                    Toast.makeText(SignupActivity.this, "Signed out of previous account.",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        };

    }

    public void signUpUser(View v) {
        email = ((EditText) findViewById(R.id.newEmail)).getEditableText().toString();
        password = ((EditText) findViewById(R.id.newPassword)).getEditableText().toString();
        if (password.length() < 5 || TextUtils.isEmpty(password)) {
            Toast.makeText(SignupActivity.this, "Must enter a password that is 5 or more characters.",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(SignupActivity.this, "Must enter an email.",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        myRef2 = database.getReference().child("usernames");
        myRef2.child(((EditText)findViewById(R.id.newUsername)).getEditableText().toString()).addListenerForSingleValueEvent(stateValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(SignupActivity.this, "Username already taken.",
                            Toast.LENGTH_SHORT).show();
                    myRef2.child(((EditText)findViewById(R.id.newUsername)).getEditableText().toString()).removeEventListener(stateValueEventListener);
                    return;
                } else {
                    myRef2.child(((EditText)findViewById(R.id.newUsername)).getEditableText().toString()).removeEventListener(stateValueEventListener);
                    createAccount(email, password);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String email = ((EditText) findViewById(R.id.newEmail)).getEditableText().toString();
                            String username = ((EditText) findViewById(R.id.newUsername)).getEditableText().toString();
                            String name = ((EditText) findViewById(R.id.newName)).getEditableText().toString();
                            FirebaseUser user = mAuth.getCurrentUser();
                            String userID = user.getUid();
                            DatabaseReference myRef = database.getReference().child("users").child(userID);
                            myRef.child("numCleanedToday").setValue(0);
                            myRef.child("numCleanedTotal").setValue(0);
                            myRef.child("email").setValue(email);
                            myRef.child("username").setValue(username);
                            myRef.child("name").setValue(name);
                            myRef.child("lastLogin").setValue(System.currentTimeMillis());
                            myRef.child("weeklyEmails").setValue(false);
                            myRef.child("importantEmails").setValue(false);
                            myRef.child("ecoScore").setValue(30L);
                            database.getReference().child("usernames").child(username).setValue(userID);
                            Toast.makeText(SignupActivity.this, "Authentication success.", Toast.LENGTH_SHORT).show();
                            finish();
                            Intent i = new Intent(SignupActivity.this, HomePage.class);
                            startActivity(i);
                        } else {
                            Toast.makeText(SignupActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                });
    }
}