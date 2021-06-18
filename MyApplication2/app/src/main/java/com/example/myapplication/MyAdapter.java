package com.example.myapplication;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
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

import java.util.ArrayList;
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

    private int isSelectAll = -1;
    private boolean isEnable = false;
    private List<String> selectList = new ArrayList<>();
    MainViewModel mainViewModel;

    //to differentiate between adapter for tasks n homepage
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

    @Override
    public int getItemCount() { return myTasks.size(); }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, price, time, location;
        ProgressBar bar; Button tag; ImageView checkBox;
        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tasktitle);
            price = itemView.findViewById(R.id.price);
            time = itemView.findViewById(R.id.time);
            location = itemView.findViewById(R.id.taskLocation);
            tag = itemView.findViewById(R.id.taskTag);
            bar = itemView.findViewById(R.id.taskProgressBar);
            checkBox = itemView.findViewById(R.id.selectDelete);
        }
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
            if (myTasks.get(position).getTag().equals("0")) {
                holder.tag.setBackgroundColor(Color.parseColor("#ffbf00"));
                holder.bar.setVisibility(View.VISIBLE);
            } if (myTasks.get(position).getTag().equals("1")) {
                holder.tag.setBackgroundColor(Color.parseColor("#008000"));
            }
        } else { holder.time.setText("error"); }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (i == 1 && !isEnable) {
                   // Toast.makeText(context, "long click", Toast.LENGTH_SHORT).show();
                    mutipleDelete(v, holder);
                } else if (i==1) {
                    selectItem(holder);
                }
                return true;
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (isEnable && i == 1) {
                    isSelectAll=-1;  selectItem(holder);
                } else if (i == 1) {
                    updateData(position); notifyDataSetChanged();
                } else { taskCardClick(v, position); }
            }
        });

        if (!isEnable || (isEnable && isSelectAll == 0)) {
            holder.checkBox.setVisibility(View.GONE);
        } else if (isEnable && isSelectAll == 1) {
            holder.checkBox.setVisibility(View.VISIBLE);
        }
    }

    private void selectItem(MyViewHolder holder) {
        NewTask item = myTasks.get(holder.getAdapterPosition());
        if (holder.checkBox.getVisibility() == View.GONE) {
            holder.checkBox.setVisibility(View.VISIBLE);
            //holder.itemView.setBackgroundResource(R.color.black_overlay);
            selectList.add(item.getTaskId());
        } else {
            holder.checkBox.setVisibility(View.GONE);
            selectList.remove(item.getTaskId());
        }
    }

    public void deleteItem(String taskId) {
        db.collection("Tasks").document(taskId).delete()
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        //notifyRemoved(position);
                    } else {
                        Toast.makeText(context, "ERROR" + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

    private void notifyRemoved(int position){
        myTasks.remove(position);
        notifyItemRemoved(position);
    }

    private void mutipleDelete(View v, MyViewHolder holder) {
        ActionMode.Callback callback = new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater menuInflater = mode.getMenuInflater();
                menuInflater.inflate(R.menu.delete_menu, menu);
                return true;
            }
            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                isEnable = true; selectItem(holder);
                return true;
            }
            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                if (item.getItemId() == R.id.tasks_delete) {
                    for (String tskId : selectList) { deleteItem(tskId); }
                    selectList.clear();
                    notifyDataSetChanged();
                    mode.finish();
                } else if (item.getItemId() == R.id.tasks_select_all) {
                    if (selectList.size() == myTasks.size()) { //unselect all
                        item.setIcon(R.drawable.ic_baseline_check_box_24);
                        isSelectAll=0; selectList.clear();
                    } else { //select all
                        item.setIcon(R.drawable.ic_baseline_check_box_outline_blank_24);
                        isSelectAll=1; selectList.clear();
                        for (NewTask tsk : myTasks) { selectList.add(tsk.getTaskId()); }
                    }
                    notifyDataSetChanged();
                }
                return true;
            }
            @Override
            public void onDestroyActionMode(ActionMode mode) {
                isEnable = false; isSelectAll = -1; selectList.clear();
                notifyDataSetChanged();
            }
        };
        v.startActionMode(callback);
    }

    //swipe delete undo
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

    //for homepg task view
    private void taskCardClick(View v, int position) {
        task_view p = new task_view();
        Bundle bundle = getDataFromPosition(position);
        bundle.putInt("source", i);
        p.setArguments(bundle);
        FragmentTransaction transaction = mgr.beginTransaction();
        transaction.add(R.id.fragmentContainerView, p).addToBackStack(null).commit();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void updateData(int position) {
        Intent i = new Intent(context, create_new_task.class);
        i.putExtras(getDataFromPosition(position));
        context.startActivity(i);
    }

    public Bundle getDataFromPosition(int position) {
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
        return bundle;
    }
}

