package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import static java.lang.String.valueOf;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private Context context;
    private List<NewTask> myTasks;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private tasks t;


    public MyAdapter(Context context, List<NewTask> tasks) {
        this.context = context;
        this.myTasks = tasks;
    }

    public MyAdapter(Context context, List<NewTask> tasks, tasks t) {
        this.context = context;
        this.myTasks = tasks;
        this.t = t;
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
            String test = myTasks.get(position).getDate() + " " + myTasks.get(position).getTime();
            holder.time.setText(test);
            holder.price.setText(myTasks.get(position).getPrice() + " dollars");
            holder.title.setText(myTasks.get(position).getTitle());
            holder.location.setText(myTasks.get(position).getLocation());
        } else {
            holder.time.setText("error");
        }

    }

    @Override
    public int getItemCount() {
        return myTasks.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title, price, time, location;
        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tasktitle);
            price = itemView.findViewById(R.id.price);
            time = itemView.findViewById(R.id.time);
            location = itemView.findViewById(R.id.taskLocation);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void updateData(int position) {
        Toast.makeText(context, "Hello", Toast.LENGTH_SHORT);
        NewTask item = this.myTasks.get(position);
        Bundle bundle = new Bundle();
        bundle.putString("uId", item.getId());
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
        NewTask newTask=myTasks.get(position);
        db.collection("Tasks").document(newTask.getId()).delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Task deleted", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "ERROR" + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void notifyRemoved(int position){
        myTasks.remove(position);
        notifyItemRemoved(position);
        t.showData();
    }
}
