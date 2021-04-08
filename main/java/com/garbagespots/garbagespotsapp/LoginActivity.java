package com.garbagespots.garbagespotsapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private DatabaseReference myRef2;
    private ValueEventListener stateValueEvent;
    private FirebaseUser user;
    private EditText mEmail, mPassword;
    private Button btnSignIn,btnPasswordReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        btnSignIn = findViewById(R.id.email_sign_in_button);
        btnPasswordReset = findViewById(R.id.password_reset_button);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                myRef = FirebaseDatabase.getInstance().getReference().child("users");
                myRef.addValueEventListener(stateValueEvent = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (user != null) {
                            if (snapshot.child(user.getUid()).exists()) {
                                toastMessage("Successfully signed in with: " + user.getEmail());
                                finish();
                                Intent i = new Intent(LoginActivity.this, HomePage.class);
                                myRef.removeEventListener(stateValueEvent);
                                startActivity(i);
                                lastLogin();
                            } else {
                                toastMessage("Account Doesn't Exist.");
                                myRef.removeEventListener(stateValueEvent);
                            }
                        } else {
                            toastMessage("Not signed in.");
                            myRef.removeEventListener(stateValueEvent);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        };

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmail.getText().toString();
                String pass = mPassword.getText().toString();
                if(!email.equals("") && !pass.equals("")){
                    if (pass.length() >= 5) {
                        mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    toastMessage("Incorrect Password");
                                }
                            }
                        });
                    } else {
                        toastMessage("Password must be 5 or more characters");
                    }
                }else{
                    toastMessage("You didn't fill in all the fields.");
                }
            }
        });

        btnPasswordReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEmail = findViewById(R.id.email);
                String emailAddress = mEmail.getEditableText().toString();
                if (!emailAddress.equals("")) {
                    mAuth.sendPasswordResetEmail(emailAddress)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(LoginActivity.this, "Password reset email successfully sent.", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Failed to send password reset email.", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(LoginActivity.this, "To reset your password, please enter your email.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    public void lastLogin() {
        myRef2 = FirebaseDatabase.getInstance().getReference().child("users");
        myRef2.addValueEventListener(stateValueEvent = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userID = user.getUid();
                Long timeDiff = System.currentTimeMillis() - Long.parseLong(snapshot.child(userID).child("lastLogin").getValue().toString());
                if (timeDiff > 300000 && timeDiff <= 21600000) {
                    myRef2.child(userID).child("numCleanedToday").setValue(Integer.parseInt(snapshot.child(userID).child("numCleanedToday").getValue().toString()) - 1);
                }
                if (timeDiff > 21600000) {
                    myRef2.child(userID).child("numCleanedToday").setValue(0L);
                    Long currentEcoScore = Long.parseLong(snapshot.child(userID).child("ecoScore").getValue().toString());
                    if (currentEcoScore >= 10 && timeDiff > 86400000) {
                         currentEcoScore = currentEcoScore - 1;
                        if (currentEcoScore >= 10 && timeDiff > 432000000L) {
                            currentEcoScore = currentEcoScore - 2;
                            if (currentEcoScore >= 10 && timeDiff > 1814400000L) {
                                currentEcoScore = currentEcoScore - 2;
                            }
                        }
                    } else if (currentEcoScore > 0 && timeDiff > 432000000L) {
                        myRef2.child(userID).child("ecoScore").setValue(currentEcoScore - 1L);
                    }
                    myRef2.child(userID).child("ecoScore").setValue(currentEcoScore);
                }
                myRef2.child(userID).child("lastLogin").setValue(System.currentTimeMillis());
                myRef2.removeEventListener(stateValueEvent);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}