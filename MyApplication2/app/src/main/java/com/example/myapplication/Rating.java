package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class Rating extends AppCompatActivity {
    Button confirm;
    RatingBar rating;
    EditText comment;
    private int myRating = -1;
    private String textComment;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private String pubID, taskerID, taskID;
    private boolean isPublisher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);
        Intent i = getIntent();
        isPublisher = i.getBooleanExtra("publisher", true);
        pubID = i.getStringExtra("publisherID");
        taskerID = i.getStringExtra("taskerID");
        taskID = i.getStringExtra("taskID");

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.
                getInstance("https://taskrabbits-1621680681859-default-rtdb.asia-southeast1.firebasedatabase.app/");

        reference = database.getReference("Users").child(firebaseAuth.getCurrentUser().getUid());

        confirm = findViewById(R.id.review_confirm);
        rating = findViewById(R.id.ratingBar);
        comment = findViewById(R.id.reviewComment);
        rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                int r = (int)rating;
                myRating = r;
                String message = "";

                switch (r) {
                    case (1):
                        message = "Sorry to hear that. You can contact us if facing any issue.";
                        break;
                    case (2):
                        message = "Sorry to hear that. You can contact us if facing any issue.";
                        break;
                    case (3):
                        message = "Good job!";
                        break;
                    case (4):
                        message = "Great! Thank you!";
                        break;
                    case(5):
                        message = "Awesome!";
                        break;
                }
                Toast.makeText(Rating.this,message, Toast.LENGTH_SHORT).show();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myRating==-1) {
                    Toast.makeText(Rating.this,"Please choose a rating", Toast.LENGTH_SHORT).show();
                } else {
                    textComment = comment.getText().toString();
                    store();
                    finish();
                }
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void store(){
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("taskID", taskerID);

        hashMap.put("rating", myRating);
        hashMap.put("comment", textComment);
        if (!isPublisher) {
            hashMap.put("publiser", pubID);
//            hashMap.put("tasker", taskerID);
            reference.child("Comment").child("AsTasker").child(taskID).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<Void> task) {
                    Toast.makeText(Rating.this,"Comment Received", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}