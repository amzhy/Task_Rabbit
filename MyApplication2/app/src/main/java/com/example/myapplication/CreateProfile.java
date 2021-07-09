package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

import org.jetbrains.annotations.NotNull;

public class CreateProfile extends AppCompatActivity {

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

        next = findViewById(R.id.create_next);
        name = findViewById(R.id.createUsername);
        hp = findViewById(R.id.createPhone);

        user = FirebaseAuth.getInstance().getCurrentUser();
        rtNode = FirebaseDatabase.
                getInstance("https://taskrabbits-1621680681859-default-rtdb.asia-southeast1.firebasedatabase.app/");
        reference = rtNode.getReference("Users");

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

                if (sname.length() > 5 && shp.length() == 8) {
                    hp.setErrorEnabled(false);
                    name.setErrorEnabled(false);

                    StoreProfile n = new StoreProfile("", "", "");
                    reference.child(user.getUid()).setValue(n);
                    reference.child(user.getUid()).child("hp").setValue(shp);
                    reference.child(user.getUid()).child("name").setValue(sname);
                    success = 1;

                    Toast.makeText(getApplicationContext(), "Account created!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(CreateProfile.this, MainActivity.class));
                    finish();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (success == 0) { // user decides to not create an account -- remove user from authentication
            FirebaseAuth.getInstance().getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                    } else {
                        //Toast.makeText(getApplicationContext(), "not ok", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}