package com.example.myapplication;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.google.android.gms.common.util.CollectionUtils.mapOf;
import static java.lang.String.valueOf;

public class MessageActivity extends AppCompatActivity {
    CircleImageView profile_image;
    TextView topUserID;
    String tasker, taskAcceptId, publisherID;
    NewTask newTask;
    Button btn_accept, btn_complete;
    ImageButton btn_send;
    TextView text_send;

    MessageAdapter messageAdapter;
    List<Chat> mChat;

    RecyclerView recyclerView;
    FirebaseUser fuser;
    DatabaseReference reference;
    FirebaseFirestore firestore;

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
        recyclerView.addItemDecoration(new RecyclerDeco(20, 5));

        profile_image = findViewById(R.id.profile_image_right);
        topUserID = findViewById(R.id.user_name);
        btn_send = findViewById(R.id.btn_send);
        text_send = findViewById(R.id.text_send);
        btn_accept = findViewById(R.id.accept_btn);
        btn_complete = findViewById(R.id.complete_btn);

        intent = getIntent();
        publisherID  = intent.getStringExtra("userID");

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        taskAcceptId = intent.getStringExtra("taskID");
        firestore  = FirebaseFirestore.getInstance();
        firestore.collection("Tasks").get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                    for(DocumentSnapshot snapshot : task.getResult()) {
                        HashMap<String, String> taskStored = (HashMap<String, String>) snapshot.getData().get(snapshot.getId());
                        if (taskStored.get("taskId").equals(taskAcceptId)) {
                            newTask = new NewTask(taskStored.get("title"),
                                    taskStored.get("description"), taskStored.get("location"),
                                    taskStored.get("price"), taskStored.get("date"),
                                    taskStored.get("time"), taskStored.get("userId"),
                                    taskStored.get("taskId"), taskStored.get("tag"),
                                    taskStored.get("taskerId"), taskStored.get("category"));
                        }
                    }
                }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(getApplicationContext(), "unble to get task form msgcitivity", Toast.LENGTH_SHORT).show();
            }
        });

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

        reference = FirebaseDatabase.getInstance("https://taskrabbits-1621680681859-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users").
                child(intent.getStringExtra("userID"));

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

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@Nullable @org.jetbrains.annotations.Nullable View parent, @NonNull @NotNull String name, @NonNull @NotNull Context context, @NonNull @NotNull AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
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

                        System.out.println("                                                button accept check sender = " +
                               chat.getSender() + "\n" + "userid = " + userID + "\n" + "receiver = " + chat.getReceiver()+ "\n"
                        + newTask.getTag());

                       if(newTask.getTag().equals("-1") && myID.equals(newTask.getUserId())) {
                               btn_accept.setVisibility(View.VISIBLE);
                               if (myID.equals(chat.getReceiver())) {
                                   tasker = chat.getSender(); //taskAcceptId = chat.getTaskID();
                               } else {
                                   tasker = chat.getReceiver(); //taskAcceptId = chat.getTaskID();
                               }
                       }
                       if (newTask.getTag().equals("0") ) {
                           if (myID.equals(newTask.getTaskerId())) {
                            btn_complete.setVisibility(View.VISIBLE);
                           } else if (myID.equals(publisherID) || myID.equals(newTask.getUserId())) {
                            btn_complete.setVisibility(View.GONE);
                           } else {
                               btn_complete.setText("Task Taken by Other User");
                               btn_complete.setVisibility(View.VISIBLE);
                               btn_complete.setEnabled(false);
                               btn_complete.getBackground().setColorFilter(ContextCompat.getColor(MessageActivity.this, android.R.color.darker_gray), PorterDuff.Mode.MULTIPLY);
                           }
                       }
                    }
                    messageAdapter = new MessageAdapter(MessageActivity.this, mChat, usrid);
                    recyclerView.setAdapter(messageAdapter);
                }
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) { }
        });


        btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btn_accept.getVisibility() == View.VISIBLE) {
                    Intent i = new Intent(MessageActivity.this, AcceptTask.class);
                    i.putExtra("tasker", tasker);
                    i.putExtra("taskId", taskAcceptId);
                    startActivity(i);
                    finish();
                }
            }
        });

        btn_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newTask.setTag("1");
                HashMap<String, Object> map = new HashMap<>();
                map.put(taskAcceptId, newTask);
                FirebaseFirestore firestore  = FirebaseFirestore.getInstance();
                firestore.collection("Tasks").document(taskAcceptId).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Task status : Completed!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
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
}