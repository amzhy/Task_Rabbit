package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.SwitchPreferenceCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class CreateProfile extends AppCompatActivity {
    private DatabaseReference db;
    MaterialButton next;
    private TextInputLayout name, hp;
    private String sname, shp;
    FirebaseDatabase rtNode;
    FirebaseUser user;
    private DatabaseReference reference;
    private int success = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
        db = FirebaseDatabase.
                getInstance("https://taskrabbits-1621680681859-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference();
        next = findViewById(R.id.create_next);
        name = findViewById(R.id.createUsername);
        hp = findViewById(R.id.createPhone);

        user = FirebaseAuth.getInstance().getCurrentUser();
        rtNode = FirebaseDatabase.
                getInstance("https://taskrabbits-1621680681859-default-rtdb.asia-southeast1.firebasedatabase.app/");
        reference = rtNode.getReference("Users");

        name.getEditText().requestFocus();
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sname = name.getEditText().getText().toString();
                shp = hp.getEditText().getText().toString();

                if (sname.length() == 0) {
                    name.setError("*Required");
                    //Toast.makeText(getApplicationContext(), "Empty fields are not allowed!", Toast.LENGTH_SHORT).show();
                } else if (sname.length() > 0 && sname.length() < 5) {
                    name.setError("*Username is too short!");
                } else { name.setErrorEnabled(false); }

                if (shp.length() == 0) {
                    hp.setError("*Required");
                } else if (shp.length() > 0 && shp.length() < 8) {
                    hp.setError("Please input valid phone number");
                } else { hp.setErrorEnabled(false); }

                if (sname.length() > 4 && shp.length() == 8) {
                    hp.setErrorEnabled(false);
                    name.setErrorEnabled(false);
                    StoreProfile n = new StoreProfile("", "");
                    FirebaseAuth.getInstance().getCurrentUser().reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            if (FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
                                reference.child(user.getUid()).setValue(n);
                                reference.child(user.getUid()).child("hp").setValue(shp);
                                reference.child(user.getUid()).child("name").setValue(sname);
                                updateToken();
                                success = 1;

                                Toast.makeText(getApplicationContext(), "Account created!", Toast.LENGTH_SHORT).show();
                                updateToken();
                                startActivity(new Intent(CreateProfile.this, MainActivity.class));
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "Please verify your email!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (success == 0) { // user decides to not create an account -- remove user from authentication
            user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        finish();
                    }
                }
            });
        } else { //add profile w default setting + device token
            db.child("Settings").child(FirebaseAuth.getInstance().getUid()).child("inbox").setValue(true);
            db.child("Settings").child(FirebaseAuth.getInstance().getUid()).child("task_status").setValue(true);
            db.child("Settings").child(FirebaseAuth.getInstance().getUid()).child("leaderboard").setValue(true);
            db.child("Settings").child(FirebaseAuth.getInstance().getUid()).child("tasker_alert").setValue(true);
            updateToken();
        }
    }

    public void updateToken() {
        if (FirebaseAuth.getInstance().getUid() != null) {
            DatabaseReference reference = FirebaseDatabase.
                    getInstance("https://taskrabbits-1621680681859-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
            String refreshtoken = FirebaseInstanceId.getInstance().getToken();
            reference.child("Users").child(FirebaseAuth.getInstance().getUid()).child("tokens").setValue(refreshtoken);

            reference.child("Settings").child(FirebaseAuth.getInstance().getUid()).child("inbox").setValue(true);
            reference.child("Settings").child(FirebaseAuth.getInstance().getUid()).child("task_status").setValue(true);
            reference.child("Settings").child(FirebaseAuth.getInstance().getUid()).child("leaderboard").setValue(true);
            reference.child("Settings").child(FirebaseAuth.getInstance().getUid()).child("tasker_alert").setValue("10min");
        }
    }
}