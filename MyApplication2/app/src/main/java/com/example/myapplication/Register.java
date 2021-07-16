package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Register extends AppCompatActivity {
    String sEmail, sPass;
    TextInputLayout email, password;
    FirebaseAuth auth;
    DatabaseReference ref;
    MaterialButton register_btn;
    TextView forgotpw;
    int source;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        email = findViewById(R.id.registername);
        password = findViewById(R.id.registerpw);
        register_btn = findViewById(R.id.registerbtn);
        forgotpw = findViewById(R.id.forgotpw);
        auth = FirebaseAuth.getInstance();

        ref = FirebaseDatabase.getInstance("https://taskrabbits-1621680681859-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Users");

        source = getIntent().getIntExtra("source", 55);
        if (source == 1) {
            register_btn.setText("Login");
        } else if (source == 0) {
            forgotpw.setVisibility(View.GONE);
            email.setHelperText("Enter valid email address for verification");
            password.setHelperText("Min. 6 characters");
        }
        email.getEditText().requestFocus();

        //handle login and register
        findViewById(R.id.registerbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sEmail = email.getEditText().getText().toString().trim();
                sPass = password.getEditText().getText().toString().trim();
                if (source == 0 && errorChecks(sEmail, sPass)) {
                    registerUser(sEmail, sPass);
                } else if (source == 1 && errorChecks(sEmail, sPass)) { login(sEmail, sPass); }
            }
        });

        findViewById(R.id.forgotpw).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sEmail = email.getEditText().getText().toString().trim();
                if (sEmail.isEmpty()) {
                    email.setError("*Required");
                    email.getEditText().requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(sEmail).matches()) {
                    email.setError("*Invalid email address");
                    email.getEditText().requestFocus();
                } else { email.setErrorEnabled(false); }

                if (sEmail.length() > 0 && Patterns.EMAIL_ADDRESS.matcher(sEmail).matches()) {
                    //reset password only sent to email/pw acc, excl google acc
                    auth.fetchSignInMethodsForEmail(sEmail).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<SignInMethodQueryResult> task) {
                            auth.fetchSignInMethodsForEmail(sEmail).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<SignInMethodQueryResult> task) {
                                    if (!task.getResult().getSignInMethods().contains("google.com")) {
                                        auth.sendPasswordResetEmail(sEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(Register.this, "Check your email for password reset", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    startActivity(new Intent(Register.this, LoginActivity.class));
                                                    Toast.makeText(Register.this, "You have previously logged in via Google", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    private void registerUser(String sEmail, String sPass) {
        auth.createUserWithEmailAndPassword(sEmail, sPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Please check your email to verify your account", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Register.this, CreateProfile.class));
                            } else { Toast.makeText(getApplicationContext(), "Unable to send verification email", Toast.LENGTH_SHORT).show(); }
                        }
                    });
                } else {
                    String n = task.getException().toString().toLowerCase();
                    if (n.contains("already in use")) {
                        startActivity(new Intent(Register.this, LoginActivity.class));
                        Toast.makeText(getApplicationContext(), "Account registered, please login", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }
        });
    }

    private void login(String sEmail, String sPass) {
            auth.signInWithEmailAndPassword(sEmail, sPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        if (auth.getCurrentUser().isEmailVerified()) {
                            startActivity(new Intent(Register.this, MainActivity.class));
                            Toast.makeText(Register.this, "Welcome back", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(Register.this, "Please verify your email address", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        String n = task.getException().toString().toLowerCase();
                        if (n.contains("no user")) {
                            startActivity(new Intent(Register.this, LoginActivity.class));
                            Toast.makeText(Register.this, "Please register first", Toast.LENGTH_SHORT).show();
                            finish();
                        } else if (n.contains("blocked")) {
                            Toast.makeText(Register.this, "Account disabled due to many failed attempts", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Register.this, LoginActivity.class));
                            finish();
                        } else {
                            auth.fetchSignInMethodsForEmail(sEmail).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<SignInMethodQueryResult> task) {
                                    if (task.getResult().getSignInMethods().contains("google.com")) {
                                        startActivity(new Intent(Register.this, LoginActivity.class));
                                        Toast.makeText(Register.this, "You previously logged in via Google", Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else {
                                        Toast.makeText(Register.this, "Wrong password, try again!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                }
            });
    }

    private boolean errorChecks(String sEmail, String sPass) {
        if (sEmail.isEmpty()) {
            email.setError("*Required");
            email.getEditText().requestFocus();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(sEmail).matches()) {
            email.setError("*Invalid email address");
            email.getEditText().requestFocus();
        } else { email.setErrorEnabled(false); password.getEditText().requestFocus(); }

        if (sPass.length() == 0) {
            password.setError("*Required");
            password.getEditText().requestFocus();
        } else if (sPass.length() > 0 && sPass.length() < 6) {
            password.setError("*Password is too short! Min. 6 characters");
            password.getEditText().requestFocus();
        } else { password.setErrorEnabled(false); }
        return (sPass.length() > 5 && sEmail.length() > 0 && Patterns.EMAIL_ADDRESS.matcher(sEmail).matches());
    }
}