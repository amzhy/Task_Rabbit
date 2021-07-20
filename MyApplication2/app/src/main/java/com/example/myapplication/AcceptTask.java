package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class AcceptTask extends AppCompatActivity {
    private String stitle, slocation, sprice, stype, sdate, stime, sdesc, taskId, tasker, publisher;
    private NewTask acceptedTask;
    private FirebaseFirestore db;

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
                    Toast.makeText(getApplicationContext(), "Task in progress...", Toast.LENGTH_SHORT).show();
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
}