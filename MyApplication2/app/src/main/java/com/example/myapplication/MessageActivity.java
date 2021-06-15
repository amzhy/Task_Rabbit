package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

import static java.lang.String.valueOf;

public class MessageActivity extends AppCompatActivity {
    CircleImageView profile_image;
    TextView topUserID;

    ImageButton btn_send;
    TextView text_send;

    MessageAdapter messageAdapter;
    List<Chat> mChat;

    RecyclerView recyclerView;


    FirebaseUser fuser;
    DatabaseReference reference;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Toolbar toolbar = findViewById(R.id.msg_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerView = findViewById(R.id.msg_recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        profile_image = findViewById(R.id.profile_image_right);
        topUserID = findViewById(R.id.user_name);
        btn_send = findViewById(R.id.btn_send);
        text_send = findViewById(R.id.text_send);

        intent = getIntent();
        String publisherID = intent.getStringExtra("userID");
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = text_send.getText().toString();
                if (!msg.equals("")) {
                    sendMsg(fuser.getUid(), publisherID, intent.getStringExtra("taskID"), msg);
                } else {
                    Toast.makeText(MessageActivity.this, "You cannot send empty message", Toast.LENGTH_SHORT).show();
                }
                text_send.setText("");
            }
        });


        reference = FirebaseDatabase.getInstance("https://taskrabbits-1621680681859-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users").child(intent.getStringExtra("userID"));

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                topUserID.setText(intent.getStringExtra("taskTitle"));

                setImage();
                readMessages(fuser.getUid(), intent.getStringExtra("userID"), intent.getStringExtra("taskID"), publisherID);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


    }

    private void setImage() {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.hasChild("photo")) {
                    setUploadPhoto(profile_image);
                } else {
                    profile_image.setImageResource(R.drawable.greyprof);
                }
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) { }
        });
    }

    private void setUploadPhoto(ImageView iv) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage
                .getReferenceFromUrl("gs://taskrabbits-1621680681859.appspot.com/images/"
                        + intent.getStringExtra("userID") + ".jpg");

        storageReference.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if (this != null) {
                            Glide.with(getApplicationContext())
                                    .load(uri)
                                    .apply(new RequestOptions().override(500, 500))
                                    .centerCrop().into(iv);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        iv.setImageResource(R.drawable.greyprof);
                    }
                });
    }

    private void sendMsg(String sender, String receiver, String taskID, String message) {
        DatabaseReference reference = FirebaseDatabase.getInstance("https://taskrabbits-1621680681859-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("taskID", taskID);
        hashMap.put("message", message);
        reference.child("Chats").push().setValue(hashMap);
    }

    private void readMessages(String myID, String userID, String taskID, String usrid) {
        mChat = new ArrayList<>();

        reference = FirebaseDatabase.getInstance("https://taskrabbits-1621680681859-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                mChat.clear();
                for (DataSnapshot snapshot1: snapshot.getChildren()) {
                    Chat chat = snapshot1.getValue(Chat.class);


                   if (chat.getReceiver().equals(myID) && chat.getSender().equals(userID) && chat.getTaskID().equals(taskID)
                    || chat.getReceiver().equals(userID) && chat.getSender().equals(myID) && chat.getTaskID().equals(taskID)) {
                        mChat.add(chat);
                    }

                    messageAdapter = new MessageAdapter(MessageActivity.this, mChat, usrid);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}