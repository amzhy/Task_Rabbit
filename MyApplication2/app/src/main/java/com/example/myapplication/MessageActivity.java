package com.example.myapplication;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.google.android.gms.common.util.CollectionUtils.mapOf;
import static java.lang.String.valueOf;

public class MessageActivity extends AppCompatActivity implements CompleteDialog.CompleteDialogListener {
    CircleImageView profile_image;
    TextView topUserID;
    String tasker, taskAcceptId, publisherID;
    NewTask newTask;
    Button btn_accept, btn_complete;
    ImageButton btn_send;
    TextView text_send;

    String user_token, name;
    boolean task_status, inbox_status;
    APIService apiService;
    DatabaseReference users_ref, chats_ref, tasks_ref, ref;

    private boolean accGone = false;
    MessageAdapter messageAdapter = null;
    ArrayList<Chat> mChat;

    RecyclerView recyclerView;
    FirebaseUser fuser;
    FirebaseFirestore firestore;
    int msgNo = 0;

    Intent intent;

    boolean asPublisher, openComment;
    String lastMsg, lastLast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        Toolbar toolbar = findViewById(R.id.msg_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (asPublisher && chats_ref!=null) {
                    if(lastLast!=null) {
                        chats_ref.child(lastLast).child("isLast").setValue("false");
                    }
                    chats_ref.child(lastMsg).child("isLast").setValue("true");

                } else if(!asPublisher && chats_ref!=null) {
                    if(lastLast!=null) {
                        chats_ref.child(lastLast).child("isAlsoLast").setValue("false");
                    }
                    if (lastMsg != null) {
                        chats_ref.child(lastMsg).child("isAlsoLast").setValue("true");
                    }
                }
                undoFromDeleted();
                finish();
            }
        });
        tasks_ref = FirebaseDatabase.getInstance("https://taskrabbits-1621680681859-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Tasks");
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
        asPublisher = intent.getBooleanExtra("asPublisher", false);

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
                            if (snapshot.getData().get("Commented") == null){
                                openComment = true;
                            } else{
                                openComment = false;
                            }
                            if (asPublisher && openComment && taskStored.get("tag").equals("1")) {
//                                Toast.makeText(getApplicationContext(), taskStored.get("tag"), Toast.LENGTH_SHORT).show();
                                openComment = false;
                                Intent i = new Intent(MessageActivity.this, Rating.class);
                                i.putExtra("publisher", true);
                                i.putExtra("publisherID", fuser.getUid());
                                i.putExtra("taskerID", taskStored.get("taskerId"));
                                i.putExtra("taskID", taskAcceptId);
                                startActivity(i);
                            }
                        }

                    }
                }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(getApplicationContext(), "unable to get task from msgactivity", Toast.LENGTH_SHORT).show();
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

        users_ref = FirebaseDatabase.getInstance("https://taskrabbits-1621680681859-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users").
                child(intent.getStringExtra("userID"));

        users_ref.addValueEventListener(new ValueEventListener() {
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
        DatabaseReference df = reference.child("Chats").push();
        df.setValue(hashMap);
//        lastMsg = df.getKey();


        String chatter = fuser.getUid().equals(sender) ? receiver : sender;
        bringBackBox(taskID, chatter);
        sendMsgNotification(sender, receiver, 0);
    }

    public void sendMsg(String sender, String receiver, String taskID, String message, boolean admin) {
        DatabaseReference reference = FirebaseDatabase.getInstance("https://taskrabbits-1621680681859-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("taskID", taskID);
        hashMap.put("message", message);
        hashMap.put("Admin", true);
        DatabaseReference df = reference.child("Chats").push();
        df.setValue(hashMap);

        //test this
        String chatter = FirebaseAuth.getInstance().getCurrentUser().getUid().equals(sender) ? receiver : sender;
        bringBackBox(taskID, chatter);
//        sendMsgNotification(sender, receiver, 0);
    }

    private void readMessages(String myID, String userID, String taskID, String usrid) {
        msgNo = mChat == null ? 0: mChat.size();
        mChat = new ArrayList<>();
        chats_ref = FirebaseDatabase.getInstance("https://taskrabbits-1621680681859-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Chats");
        chats_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                mChat.clear();
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        Chat chat = snapshot1.getValue(Chat.class);
                        if (chat!=null&&chat.getSender()!=null&&chat.getReceiver()!=null&&(chat.getReceiver().equals(myID) && chat.getSender().equals(userID) && chat.getTaskID().equals(taskID)
                                || chat.getReceiver().equals(userID) && chat.getSender().equals(myID) && chat.getTaskID().equals(taskID)
                        )) {

                            if (chat.isAdmin()) {
                                if (chat.getReceiver().equals(fuser.getUid())) {
                                    mChat.add(chat);
                                }
                            } else {
                                mChat.add(chat);
                            }


                            if (asPublisher) {
                                if (chat.getLast()) {
                                    lastLast = snapshot1.getKey();
                                }
                            } else {
                                if (chat.getAlsoLast()) {
                                    lastLast = snapshot1.getKey();
                                }
                            }

                            System.out.println("                                                button accept check sender = " +
                                    chat.getSender() + "\n" + "userid = " + userID + "\n" + "receiver = " + chat.getReceiver() + "\n"
                                    + newTask.getTag());

                            if ((newTask.getTag().equals("-1") && myID.equals(newTask.getUserId())) && !accGone) {
                                btn_accept.setVisibility(View.VISIBLE);
                                if (myID.equals(chat.getReceiver())) {
                                    tasker = chat.getSender(); //taskAcceptId = chat.getTaskID();
                                } else {
                                    tasker = chat.getReceiver(); //taskAcceptId = chat.getTaskID();
                                }
                            }
                            if (newTask.getTag().equals("0")) {
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
                            lastMsg = snapshot1.getKey();
                        }
                }
                    if (msgNo != 0) {
                        messageAdapter.notifyItemRangeInserted(msgNo, mChat.size()-1);
                        msgNo = mChat.size();
                    } else {
                        messageAdapter = new MessageAdapter(MessageActivity.this, mChat, usrid);
                        recyclerView.setAdapter(messageAdapter);
                        msgNo = mChat.size();
                    }
                recyclerView.scrollToPosition(mChat.size() - 1);
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
                    Bundle b = new Bundle();
                    startActivityForResult(i, 1);
                }
            }
        });

        btn_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });
    }
    private void setImage() {
        users_ref = FirebaseDatabase.getInstance("https://taskrabbits-1621680681859-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Users");
        users_ref.child(intent.getStringExtra("userID")).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                HashMap<String, Object> hashMap = (HashMap<String, Object>)task.getResult().getValue();
                if (hashMap!= null && hashMap.get("photo")!=null){
                    setUploadPhoto(profile_image);
                } else {
                    profile_image.setImageResource(R.drawable.greyprof);
                }
            }
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

    public void openDialog(){
        CompleteDialog completeDialog = new CompleteDialog();
        completeDialog.show(getSupportFragmentManager(), "complete dialog");
    }

    @Override
    public void sendDecision(Boolean d) {
        if (d) {
            newTask.setTag("1");
            sendMsgNotification(newTask.getTaskerId(), newTask.getUserId(), 1);
                HashMap<String, Object> map = new HashMap<>();
                map.put(taskAcceptId, newTask);
                FirebaseFirestore firestore  = FirebaseFirestore.getInstance();
                firestore.collection("Tasks").document(taskAcceptId).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if (task.isSuccessful()) {
//                            MessageActivity ma = new MessageActivity();

                            if (!asPublisher) {
                                sendMsg(publisherID, fuser.getUid(), taskAcceptId, "YOU HAVE COMPLETED THE TASK", true);
                                sendMsg(fuser.getUid(), publisherID, taskAcceptId, "YOUR TASK IS COMPLETED", true);
                            } else {
                                sendMsg(fuser.getUid(), publisherID, taskAcceptId, "YOU HAVE COMPLETED THE TASK", true);
                                sendMsg(publisherID, fuser.getUid(), taskAcceptId, "YOUR TASK IS COMPLETED", true);
                            }
                            Toast.makeText(getApplicationContext(), "Task status : Completed!", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(MessageActivity.this, Rating.class);
                            i.putExtra("publisher", false);
                            i.putExtra("publisherID", publisherID);
                            i.putExtra("taskerID", fuser.getUid());
                            i.putExtra("taskID", taskAcceptId);
                            MessageActivity.this.startActivity(i);
                            btn_complete.setVisibility(View.GONE);
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
        } else {

        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            //Title bar back press triggers onBackPressed()
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (asPublisher) {
            if(lastLast!=null) {
                chats_ref.child(lastLast).child("isLast").setValue("false");
            }
            chats_ref.child(lastMsg).child("isLast").setValue("true");
        } else if(!asPublisher) {
            if(lastLast!=null) {
                chats_ref.child(lastLast).child("isAlsoLast").setValue("false");
            }
            if (lastMsg != null) {
                chats_ref.child(lastMsg).child("isAlsoLast").setValue("true");
            }
        }
        undoFromDeleted();
        finish();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            btn_accept.setVisibility(View.GONE);
            accGone = true;
        }
    }

    private void sendNotif(String uid, String title, String body) {
        users_ref = FirebaseDatabase.getInstance("https://taskrabbits-1621680681859-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Users");
        users_ref.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                user_token = snapshot.child("tokens").getValue(String.class);
                Data data = new Data(title, body);
                NotificationSender sender = new NotificationSender(data, user_token);
                apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
                apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                    @Override
                    public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                        if (response.code() == 200) {
                            if (response.body().success != 1) {
                                Log.d("MSG", "failed");
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<MyResponse> call, Throwable t) { }
                });
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) { }
        });
    }

    private void sendMsgNotification(String sender, String rcvr, int i) {
        ref = FirebaseDatabase.getInstance("https://taskrabbits-1621680681859-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                name = snapshot.child("Users").child(sender).child("name").getValue(String.class);
                task_status = snapshot.child("Settings").child(rcvr).child("task_status").getValue(Boolean.class);
                inbox_status = snapshot.child("Settings").child(rcvr).child("inbox").getValue(Boolean.class);
                if (i == 0 && inbox_status && !FirebaseAuth.getInstance().getUid().equalsIgnoreCase(rcvr)) {
                  //String m = "publisher? " + asPublisher + "\n" + "receiver: " + rcvr_name +  "\n" + "sender: " + name;
                    //Toast.makeText(getApplicationContext(), m, Toast.LENGTH_SHORT).show();
                    String body = "@" + name + " sent you a new message";
                    sendNotif(rcvr, newTask.getTitle() + " : new message!", body);
                } else if (i == 1 && task_status && !rcvr.equalsIgnoreCase(newTask.getTaskerId())) {
                    String body = "Your task has been completed by @"   + name;
                    sendNotif(rcvr, "Task Completed!", body);
                }
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) { }
        });
    }

    private void undoFromDeleted() {
        DatabaseReference my_ref = FirebaseDatabase.getInstance("https://taskrabbits-1621680681859-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Users").child(fuser.getUid());
        if (asPublisher) {
            my_ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                    if (((HashMap<String, Object>) task.getResult().getValue()).get("inboxDeleted") == null) {
                        System.out.println(task.getResult().getValue());
                        return;
                    } else {
                        System.out.println("yeah");
                        ArrayList<String> deleted = (ArrayList<String>) ((HashMap<String, Object>) task.getResult().getValue()).get("inboxDeleted");
                        int posTask;
                        if (deleted.contains(taskAcceptId) && deleted.get(deleted.indexOf(taskAcceptId) + 1).equals(publisherID)) {
                            posTask = deleted.indexOf(taskAcceptId);
                            deleted.remove(posTask);
                            deleted.remove(posTask);
                        }
                        my_ref.child("inboxDeleted").setValue(deleted).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<Void> task) {
//                            Toast.makeText(getApplicationContext(), "Undo successfully", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull @NotNull Exception e) {
//                            Toast.makeText(getContext(), "Undo failed.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
//                Toast.makeText(getApplicationContext(), "Cannot access database, please connect to internet.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            my_ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                    if (((HashMap<String, Object>) task.getResult().getValue()).get("inboxTaskerDeleted") == null) {
                        return;
                    } else {
                        ArrayList<String> deleted = (ArrayList<String>) ((HashMap<String, Object>) task.getResult().getValue()).get("inboxTaskerDeleted");
                        int posTask;
                        if (deleted.contains(taskAcceptId)) {
                            posTask = deleted.indexOf(taskAcceptId);
                            deleted.remove(posTask);
                        }
                        my_ref.child("inboxTaskerDeleted").setValue(deleted).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<Void> task) {
//                            Toast.makeText(getApplicationContext(), "Undo successfully", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull @NotNull Exception e) {
//                            Toast.makeText(getContext(), "Undo failed.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
//                Toast.makeText(getApplicationContext(), "Cannot access database, please connect to internet.", Toast.LENGTH_SHORT).show();
                }
            });

        }

    }

    private void bringBackBox(String taskk, String chatter) {
        System.out.println("Bring back box");
        DatabaseReference my_ref = FirebaseDatabase.getInstance("https://taskrabbits-1621680681859-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Users").child(chatter);
        if (!asPublisher) {
            System.out.println("not publisher");
            my_ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                    if (((HashMap<String, Object>) task.getResult().getValue()).get("inboxDeleted") == null) {
                        return;
                    } else {
                        ArrayList<String> deleted = (ArrayList<String>) ((HashMap<String, Object>) task.getResult().getValue()).get("inboxDeleted");
                        int posTask;
                        if (deleted.contains(taskk) && deleted.get(deleted.indexOf(taskk) + 1).equals(fuser.getUid())) {
                            posTask = deleted.indexOf(taskk);
                            deleted.remove(posTask);
                            deleted.remove(posTask);
                        }
                        my_ref.child("inboxDeleted").setValue(deleted).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<Void> task) {
//                            Toast.makeText(getApplicationContext(), "Undo successfully", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull @NotNull Exception e) {
//                            Toast.makeText(getContext(), "Undo failed.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
//                Toast.makeText(getApplicationContext(), "Cannot access database, please connect to internet.", Toast.LENGTH_SHORT).show();
                }
            });
        } else{
            System.out.println("publisher");
            my_ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                    if (((HashMap<String, Object>) task.getResult().getValue()).get("inboxTaskerDeleted") == null) {
                        return;
                    } else {
                        ArrayList<String> deleted = (ArrayList<String>) ((HashMap<String, Object>) task.getResult().getValue()).get("inboxTaskerDeleted");
                        int posTask;
                        if (deleted.contains(taskk)) {
                            posTask = deleted.indexOf(taskk);
                            deleted.remove(posTask);
                        }
                        my_ref.child("inboxTaskerDeleted").setValue(deleted).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<Void> task) {
//                            Toast.makeText(getApplicationContext(), "Undo successfully", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull @NotNull Exception e) {
//                            Toast.makeText(getContext(), "Undo failed.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
//                Toast.makeText(getApplicationContext(), "Cannot access database, please connect to internet.", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    public void setPublisher(){
        this.asPublisher = true;
    }
}