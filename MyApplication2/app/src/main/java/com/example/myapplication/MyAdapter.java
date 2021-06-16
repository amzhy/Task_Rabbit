package com.example.myapplication;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static java.lang.String.valueOf;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>  {
    private Context context;
    private List<NewTask> myTasks;
    private FragmentManager mgr;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private tasks t;

    //to differentiate taskview chat/accept btn from tasks and homepg
    private int i = 0;

    public MyAdapter(Context context, List<NewTask> myTasks, FragmentManager fragmentManager) {
        this.i = 1;
        this.context = context;
        this.myTasks = myTasks;
        this.mgr = fragmentManager;
    }

    public MyAdapter(Context context, List<NewTask> tasks, tasks t, FragmentManager supportFragmentManager) {
        this.i = 0;
        this.context = context;
        this.myTasks = tasks;
        this.t = t;
        this.mgr = supportFragmentManager;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.new_task_card, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        if (holder != null && holder.title != null && holder.location != null
        && holder.price!= null && holder.time!= null) {
            String test = myTasks.get(position).getDate() + "\n" + myTasks.get(position).getTime();
            holder.time.setText(test);
            holder.price.setText(myTasks.get(position).getPrice());
            holder.title.setText(myTasks.get(position).getTitle());
            holder.location.setText(myTasks.get(position).getLocation());

            String state = myTasks.get(position).getTag();
            //System.out.println("                                                                    THIS IS MY TAG " + state);
            if (state.equals("0")) {
               // System.out.println("                                                                    THIS IS MY progress " + state);
                holder.tag.setBackgroundColor(Color.parseColor("#ffbf00"));
                holder.bar.setVisibility(View.VISIBLE);
            } if (state.equals("1")) {
             //   System.out.println("                                                                    THIS IS MY completed " + state);
                holder.tag.setBackgroundColor(Color.parseColor("#008000"));
            }
        } else {
            holder.time.setText("error");
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskCardClick(v, position);
            }
        });
    }

    @Override
    public int getItemCount() { return myTasks.size(); }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, price, time, location;
        ProgressBar bar; Button tag;
        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tasktitle);
            price = itemView.findViewById(R.id.price);
            time = itemView.findViewById(R.id.time);
            location = itemView.findViewById(R.id.taskLocation);
            tag = itemView.findViewById(R.id.taskTag);
            bar = itemView.findViewById(R.id.taskProgressBar);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void updateData(int position) {
        NewTask item = myTasks.get(position);
        Bundle bundle = new Bundle();
        bundle.putString("uUserId", item.getUserId());
        bundle.putString("utaskId", item.getTaskId());
        bundle.putString("uTitle", item.getTitle());
        bundle.putString("uPrice", item.getPrice());
        bundle.putString("uDate", item.getDate());
        bundle.putString("uDesc", item.getDescription());
        bundle.putString("uLocation", item.getLocation());
        bundle.putString("uTime", item.getTime());

        Intent i = new Intent(context, create_new_task.class);
        i.putExtras(bundle);
        context.startActivity(i);
    }

    public void deleteData(int position) {
        NewTask item = myTasks.get(position);
        db.collection("Tasks").document(item.getTaskId()).delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //have to comment this out to prevent stopping app
                            //possible reason: because of getting taskID from chat? so there is nullpointerexception
                            //notifyRemoved(position);
                        } else {
                            Toast.makeText(context, "ERROR" + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void restoreData(int position, NewTask deleted) {
        HashMap<String, Object> map = new HashMap<>();
        map.put(deleted.getTaskId(), deleted);
        db.collection("Tasks").document(deleted.getTaskId()).set(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //Toast.makeText(context, "Task added back", Toast.LENGTH_SHORT).show();
                            notifyItemInserted(position);
                            myTasks.add(position, deleted);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) { }
        });
    }

    private void notifyRemoved(int position){
        myTasks.remove(position);
        notifyItemRemoved(position);
//        if (t != null) {
//            t.showData();
//        }
    }

    private void taskCardClick(View v, int position) {
        NewTask item = myTasks.get(position);
        task_view p = new task_view();
        Bundle bundle = new Bundle();

        bundle.putString("uUserId", item.getUserId());
        bundle.putString("utaskId", item.getTaskId());
        bundle.putString("uTitle", item.getTitle());
        bundle.putString("uPrice", item.getPrice());
        bundle.putString("uDate", item.getDate());
        bundle.putString("uDesc", item.getDescription());
        bundle.putString("uLocation", item.getLocation());
        bundle.putString("uTime", item.getTime());
        bundle.putInt("source", i);

        p.setArguments(bundle);

        //System.out.println("                                                                     this is my context2 " + context + "     " + i);

        FragmentTransaction transaction = mgr.beginTransaction();
        transaction.addToBackStack(null);
        transaction.add(R.id.fragmentContainerView, p);
        transaction.commit();
    }

}
