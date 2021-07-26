package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AcceptTask extends AppCompatActivity {
    private String stitle, slocation, sprice, stype, sdate, stime, sdesc, taskId, tasker, publisher;
    private NewTask acceptedTask;
    private FirebaseFirestore db;

    DatabaseReference users_ref, setting_ref, ref;
    String user_token, publisher_name, reminder_time;
    boolean task_status, inbox_status;

    TextInputLayout title, price, type, date, time, desc;
    private AutoCompleteTextView location, category;
    MaterialButton btn_confirm;

    private String myID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_task);
        db = FirebaseFirestore.getInstance();
        //publisher = getIntent().getStringExtra("publisher");
        tasker = getIntent().getStringExtra("tasker");
        taskId = getIntent().getStringExtra("taskId");

        users_ref = FirebaseDatabase.getInstance("https://taskrabbits-1621680681859-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Users");

        //access db to get rest of the details for confirming task
        setDetails(taskId);

        myID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        title = findViewById(R.id.accept_title);
        location = findViewById(R.id.accept_outlined_exposed_dropdown_editable);
        category = findViewById(R.id.accept_outlined_exposed_dropdown_editable_category);

        ArrayAdapter<CharSequence> adapterTypeL = ArrayAdapter.createFromResource(this, R.array.CreateLocation,
                android.R.layout.simple_spinner_dropdown_item);
        adapterTypeL.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        location.setAdapter(adapterTypeL);

        ArrayAdapter<CharSequence> adapterType = ArrayAdapter.createFromResource(this, R.array.TypeCreate,
                android.R.layout.simple_spinner_dropdown_item);
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(adapterType);

        price =findViewById(R.id.accept_price);
        date = findViewById(R.id.accept_date);
        time = findViewById(R.id.accept_time);
        desc = findViewById(R.id.accept_desc);
        btn_confirm = findViewById(R.id.accept_confirm);

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDetails(taskId);
                progressTask();
                MessageActivity ma = new MessageActivity();
                ma.setPublisher();
                    ma.sendMsg(myID, tasker, taskId, "YOU ARE ASSIGNED THIS TASK", true);
                    ma.sendMsg(tasker, myID, taskId, "YOU HAVE ASSIGNED THIS TASKER", true);
            }
        });
    }

    private void setDetails(String taskId) {
        db.collection("Tasks").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot snapshot : task.getResult()) {
                            HashMap<String, String> taskStored = (HashMap<String, String>) snapshot.getData().get(snapshot.getId());
                            if (taskStored.get("taskId").equals(taskId)) {
                                sdesc = taskStored.get("description");
                                slocation = taskStored.get("location");
                                sprice = taskStored.get("price");
                                sdate = taskStored.get("date");
                                stime = taskStored.get("time");
                                stype = taskStored.get("category");
                                stitle = taskStored.get("title");
                                publisher = taskStored.get("userId");


                                acceptedTask = new NewTask(stitle, sdesc, slocation, sprice, sdate, stime, publisher, taskId,
                                        "0", tasker, stype);

                                title.getEditText().setText(stitle);
                                location.setText(slocation, false);
                                category.setText(stype, false);
                                price.getEditText().setText(sprice);
                                date.getEditText().setText(sdate);
                                time.getEditText().setText(stime);
                                desc.getEditText().setText(sdesc);
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(getApplicationContext(), "Unable to fetch data for accept", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void progressTask() {
        db.collection("Tasks").document(taskId).update(taskId, acceptedTask)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if (task.isSuccessful()) {

                    sendTasker(acceptedTask);

                    Toast.makeText(getApplicationContext(), "Task in progress...", Toast.LENGTH_SHORT).show();
                    setResult(1);
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

    private void sendNotif(String uid, String title, String body) {
        users_ref = FirebaseDatabase.getInstance("https://taskrabbits-1621680681859-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Users");
        users_ref.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                user_token = snapshot.child("tokens").getValue(String.class);
                Data data = new Data(title, body);
                NotificationSender sender = new NotificationSender(data, user_token);
                APIService apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
                apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                    @Override
                    public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                        if (response.code() == 200){
                            if (response.body().success != 1){ Log.d("MSG", "failed"); }
                        }
                    }
                    @Override
                    public void onFailure(Call<MyResponse> call, Throwable t) { }
                });
                //Toast.makeText(getApplicationContext(), user_token +" token", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) { }
        });
    }

    public void sendTasker(NewTask tsk) {
        ref = FirebaseDatabase.getInstance("https://taskrabbits-1621680681859-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference();
        if (tsk.getUserId() != null) {
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    publisher_name = snapshot.child("Users").child(tsk.getUserId()).child("name").getValue(String.class);
                    task_status = snapshot.child("Settings").child(tsk.getTaskerId()).child("task_status").getValue(Boolean.class);
                    reminder_time = snapshot.child("Settings").child(tsk.getTaskerId()).child("tasker_alert").getValue(String.class);
                    if (task_status) {
                        String body = "You are assigned to " + acceptedTask.getTitle() + " task by @" + publisher_name;
                        sendNotif(acceptedTask.getTaskerId(), "Task Assigned!", body);
                    } if (reminder_time.contains("min")) {
                        String body =  reminder_time + " reminder";
                        sendNotif(acceptedTask.getTaskerId(), acceptedTask.getDate() + " " + acceptedTask.getTime(), body);
                    }
                }
                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) { }
            });
        }
    }
}