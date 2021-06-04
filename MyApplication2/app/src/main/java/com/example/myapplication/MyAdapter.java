package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import static java.lang.String.valueOf;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private Context context;
    private List<NewTask> myTasks;


    public MyAdapter(Context context, List<NewTask> tasks) {
        this.context = context;
        this.myTasks = tasks;
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
            String test = myTasks.get(position).getDate();
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
}
