package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Leaderboard extends AppCompatActivity {
    RecyclerView recyclerView;
    LeaderAdapter adapter;
    List<Leader> leaders;
    DatabaseReference reference = FirebaseDatabase.getInstance("https://taskrabbits-1621680681859-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users");
    DatabaseReference settingRef = FirebaseDatabase.getInstance("https://taskrabbits-1621680681859-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Settings");
    private FirebaseFirestore db=FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        setTitle("Most Earning Taskers");
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.leaderRecycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        leaders = new ArrayList<>();
        db.collection("Tasks").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot snapshot : task.getResult()) {
                            HashMap<String, String> taskStored = (HashMap<String, String>) snapshot.getData().get(snapshot.getId());
                          if (taskStored.get("tag")!=null && (Integer.parseInt(taskStored.get("tag")))==1) {
                              String leaderid = taskStored.get("taskerId");
                              double price = Double.parseDouble(taskStored.get("price"));
                              Leader newLeader = new Leader(price, leaderid);

                              if(!leaders.contains(newLeader)) {
                                  leaders.add(newLeader);
                              } else {
                                  leaders.get(leaders.indexOf(newLeader)).addEarning(price);
                              }
                          }
                        }
                        Collections.sort(leaders, new compareEarning());
                        settingRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                                for (DataSnapshot s: task.getResult().getChildren()){
                                    Leader l = new Leader(0, s.getKey());
                                    if (leaders.contains(l)) {
                                        if(!s.child("leaderboard").getValue(Boolean.TYPE)){
                                            leaders.remove(l);
                                        }
                                    }
                                }
                                if (leaders.size()>10) {
                                    leaders = leaders.subList(0, 9);
                                }
                                getName();
                            }
                        });

                    }
                });
    }

    private class compareEarning implements Comparator<Leader> {

        @Override
        public int compare(Leader o1, Leader o2) {
            if (o1.getEarning() > o2.getEarning()) {
                return -1;
            } else if (o1.getEarning() == o2.getEarning()){
                return 0;
            } else {
                return 1;
            }

        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void getName(){
        reference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                for (DataSnapshot s: task.getResult().getChildren()){
                    String id = s.getKey();
                    Leader l = new Leader(0, id);
                    if (leaders.contains(l)){
                        Leader realLeader =leaders.get(leaders.indexOf(l));
                        realLeader.setName(s.child("name").getValue(String.class));
                        realLeader.setPhoto(s.child("photo").getValue(String.class));
                    }
                }
                adapter = new LeaderAdapter(getApplicationContext(), leaders);
                recyclerView.setAdapter(adapter);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            //Title bar back press triggers onBackPressed()
            Intent i = new Intent(Leaderboard.this, MainActivity.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}