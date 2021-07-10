package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class Register extends AppCompatActivity {

    String sEmail, sPass;
    TextInputLayout email, password;
    FirebaseAuth auth;
    MaterialButton register_btn;
    TextView forgotpw;
    String name;
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
        DatabaseReference ref = FirebaseDatabase.getInstance("https://taskrabbits-1621680681859-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Users");

        source = getIntent().getIntExtra("source", 55);
        //Toast.makeText(getApplicationContext(), "" + source, Toast.LENGTH_LONG).show();

        if (source == 1) {
            register_btn.setText("Login");
        } else if (source == 0) {
            forgotpw.setVisibility(View.GONE);
            email.setHelperTextEnabled(true);
            password.setHelperTextEnabled(true);
            email.setHelperText("Enter valid email address for verification");
            password.setHelperText("Min. 6 characters");
        }

        email.getEditText().requestFocus();

        //handle login and register
        findViewById(R.id.registerbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (source == 0) { // create new user, save email pwd for login
                    sEmail = email.getEditText().getText().toString().trim();
                    sPass = password.getEditText().getText().toString().trim();
                    if (sEmail.isEmpty()) {
                        email.setError("*Required");
                        email.getEditText().requestFocus();
                    } else if (!Patterns.EMAIL_ADDRESS.matcher(sEmail).matches()) {
                        email.setError("*Invalid email address");
                        email.getEditText().requestFocus();
                    } else {
                        email.setErrorEnabled(false);
                        password.getEditText().requestFocus();
                    }

                    if (sPass.length() == 0) {
                        password.setError("*Required");
                        password.getEditText().requestFocus();
                    } else if (sPass.length() > 0 && sPass.length() < 6) {
                        password.setError("*Password is too short! Min. 6 characters");
                        password.getEditText().requestFocus();
                    } else {
                        password.setErrorEnabled(false);
                    }

                    if (sPass.length() > 5 && sEmail.length() > 0 && Patterns.EMAIL_ADDRESS.matcher(sEmail).matches()) {
                        auth.createUserWithEmailAndPassword(sEmail, sPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    ref.child(auth.getUid()).child("email").setValue(sEmail);
                                    ref.child(auth.getUid()).child("password").setValue(sPass);
                                    startActivity(new Intent(Register.this, CreateProfile.class));
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

                } else { //check email and password for login
                    sEmail = email.getEditText().getText().toString().trim();
                    sPass = password.getEditText().getText().toString().trim();

                    if (sEmail.isEmpty()) {
                        email.setError("*Required");
                        email.getEditText().requestFocus();
                    } else if (!Patterns.EMAIL_ADDRESS.matcher(sEmail).matches()) {
                        email.setError("Please enter your valid email address");
                        email.getEditText().requestFocus();
                    } else {
                        email.setErrorEnabled(false);
                        password.getEditText().requestFocus();
                    }

                    if (sPass.length() == 0) {
                        password.setError("*Required");
                        password.getEditText().requestFocus();
                    } else if (sPass.length() > 0 && sPass.length() < 6) {
                        password.setError("Password is minimum 6 characters long");
                        password.getEditText().requestFocus();
                    } else {
                        password.setErrorEnabled(false);
                    }

                    if (sEmail.length() > 0 && Patterns.EMAIL_ADDRESS.matcher(sEmail).matches() && sPass.length() > 5) {
                        auth.signInWithEmailAndPassword(sEmail, sPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    ref.child(auth.getUid()).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                            name = snapshot.getValue(String.class);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull @NotNull DatabaseError error) {
                                            Toast.makeText(Register.this, "Unable to access database/r", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    Toast.makeText(Register.this, "Welcome back " + (name != null ? name : ""), Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(Register.this, MainActivity.class));
                                    finish();
                                } else {
                                    String n = task.getException().toString().toLowerCase();
                                    if (n.contains("no user")) {
                                        Toast.makeText(Register.this, "Please register first", Toast.LENGTH_SHORT).show();
                                    } else if (n.contains("blocked")) {
                                        Toast.makeText(Register.this, "Account has been disabled due to many failed attempts", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(Register.this, "Invalid password. Please try again!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });

        //reset password
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
                } else {
                    email.setErrorEnabled(false);
                }

                if (sEmail.length() > 0 && Patterns.EMAIL_ADDRESS.matcher(sEmail).matches()) {
                    auth.sendPasswordResetEmail(sEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Register.this, "Check email for password reset", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Register.this, "Unable to send password reset email", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }
}