package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>  {
    private Context context;
    private List<NewTask> myTasks;
    private FragmentManager mgr;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private int isSelectAll = -1;
    private boolean isEnable = false;
    private List<String> selectList = new ArrayList<>();
    MainViewModel mainViewModel;
    private FragmentActivity activity;
    public int menu_delete = 0;
    private View view;

    //0homepg 1mytask 2other
    private int i = 0;

    public MyAdapter(Context context, List<NewTask> myTasks, FragmentManager fragmentManager, FragmentActivity activity, View view) {
        this.i = 1;
        this.context = context;
        this.myTasks = myTasks;
        this.mgr = fragmentManager;
        this.activity = activity;
        this.view = view;
    }

    public MyAdapter(Context context, List<NewTask> tasks, FragmentManager supportFragmentManager) {
        this.i = 0;
        this.context = context;
        this.myTasks = tasks;
        this.mgr = supportFragmentManager;
    }

    public MyAdapter(Context context, List<NewTask> otherTasks, FragmentManager supportFragmentManager, FragmentActivity activity) {
        this.i = 2;
        this.context = context;
        this.myTasks = otherTasks;
        this.mgr = supportFragmentManager;
    }

    @Override
    public int getItemCount() { return myTasks.size(); }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, price, time, location;
        Button tag; CheckBox checkBox;
        TextView watch;
        CardView background;
        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tasktitle);
            price = itemView.findViewById(R.id.price);
            time = itemView.findViewById(R.id.time);
            location = itemView.findViewById(R.id.taskLocation);
            tag = itemView.findViewById(R.id.taskTag);
            checkBox = itemView.findViewById(R.id.selectDelete);
            background = itemView.findViewById(R.id.cardbg);
            watch = itemView.findViewById(R.id.task_stopwatch);
        }
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        if (i == 1 && activity != null) {
            mainViewModel = ViewModelProviders.of(this.activity).get(MainViewModel.class);
        }
        View v = LayoutInflater.from(context).inflate(R.layout.new_task_card, parent, false);
        return new MyViewHolder(v);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        if (holder != null && holder.title != null && holder.location != null
        && holder.price!= null && holder.time!= null) {
            String test = myTasks.get(position).getDate() + "\n" + myTasks.get(position).getTime();
            holder.time.setText(test);
            holder.price.setText(myTasks.get(position).getPrice());
            holder.title.setText(myTasks.get(position).getTitle());
            holder.location.setText(myTasks.get(position).getLocation());
            if(myTasks.get(position).getTag().equals("-1")) {
                holder.tag.setBackgroundColor(Color.parseColor("#dc143c"));
                if (i==0){ holder.tag.setVisibility(View.INVISIBLE); }
                holder.watch.setVisibility(View.GONE); holder.time.setVisibility(View.VISIBLE);
            } else if (myTasks.get(position).getTag().equals("0")) {
                holder.tag.setBackgroundColor(Color.parseColor("#ffbf00"));
                holder.watch.setVisibility(View.VISIBLE); holder.time.setVisibility(View.GONE);
                try {
                    startStopwatch(position, holder.watch);
                } catch (ParseException e) {
                    System.out.println("                                                       PARSING ERROR ");
                    e.printStackTrace();
                }
            } else if (myTasks.get(position).getTag().equals("1")) {
                holder.tag.setBackgroundColor(Color.parseColor("#008000"));
                holder.watch.setVisibility(View.GONE); holder.time.setVisibility(View.VISIBLE);
            } else if (myTasks.get(position).getTag().equals("2")) {
                holder.tag.setBackgroundColor(Color.parseColor("#000000"));
                holder.watch.setVisibility(View.GONE); holder.time.setVisibility(View.VISIBLE);
            }
        } else { holder.time.setText("error"); }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (isEnable && i == 1) {
                    selectItem(holder);
                } else if (i==1){
                    updateData(position); notifyDataSetChanged();
                } else { taskCardClick(position); }
            }
        });

        if (menu_delete == 1) {
            if (i == 1 && !isEnable) {
                ActionMode.Callback callback = new ActionMode.Callback() {
                    @Override
                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        MenuInflater menuInflater = mode.getMenuInflater();
                        menuInflater.inflate(R.menu.delete_menu, menu);
                        return true;
                    }
                    @Override
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        isEnable = true; selectList.clear();
                        mainViewModel.getData().observe((LifecycleOwner) activity,
                                new Observer<String>() {
                                    @Override
                                    public void onChanged(String s) {
                                        mode.setTitle(String.format("%s selected", selectList.size()));
                                    }
                                });
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
                            //unselect all, selectList != mytasks size if there are inprogress tasks
                            if (selectList.size() == myTasks.size() || isSelectAll == 1) {
                                item.setIcon(R.drawable.ic_baseline_check_box_24);
                                isSelectAll=0; selectList.clear();
                            } else { //select all
                                item.setIcon(R.drawable.ic_baseline_check_box_outline_blank_24);
                                isSelectAll=1; selectList.clear();
                                for (NewTask tsk : myTasks) {
                                    if (!tsk.getTag().equals("0") && !tsk.getTag().equals("1")) {
                                        selectList.add(tsk.getTaskId());
                                    }
                                }
                            }
                            mainViewModel.setData(String.valueOf(selectList.size()));
                            notifyDataSetChanged();
                        }
                        return true;
                    }
                    @Override
                    public void onDestroyActionMode(ActionMode mode) {
                        menu_delete = 0; isEnable = false; isSelectAll = -1; selectList.clear();
                        notifyDataSetChanged();
                    }
                };
                view.startActionMode(callback);
            }
        }

        if (isEnable) {
            holder.checkBox.setVisibility(View.VISIBLE);
            if (selectList.contains(myTasks.get(holder.getAdapterPosition()).getTaskId())) {
                holder.checkBox.setChecked(true);
                holder.background.setAlpha((float) 0.4);
            } else { holder.checkBox.setChecked(false); holder.background.setAlpha((float) 1); }
        } else {
            holder.checkBox.setVisibility(View.GONE);
            holder.background.setAlpha((float) 1);
        }
    }

    private void selectItem(MyViewHolder holder) {
        NewTask item = myTasks.get(holder.getAdapterPosition());
        if (item.getTag().equals("0")) {
            Toast.makeText(context, "Cannot delete tasks in progress", Toast.LENGTH_SHORT).show();
        } else if (item.getTag().equals("1")) {
            Toast.makeText(context, "Cannot delete completed tasks", Toast.LENGTH_SHORT).show();
        } else {
            if (!holder.checkBox.isChecked()) {
                holder.checkBox.setChecked(true);
                holder.background.setAlpha((float) 0.4);
                selectList.add(item.getTaskId());
            } else {
                holder.checkBox.setChecked(false);
                holder.background.setAlpha((float) 1);
                selectList.remove(item.getTaskId());
            }
        }
        mainViewModel.setData(String.valueOf(selectList.size()));
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

    //uneditable - for task view
    private void taskCardClick(int position) {
        task_view p = new task_view();
        Bundle bundle = getDataFromPosition(position);
        bundle.putInt("source", i);
        p.setArguments(bundle);
        FragmentTransaction transaction = mgr.beginTransaction();
        transaction.add(R.id.fragmentContainerView, p).addToBackStack(null).commit();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void updateData(int position) {
        //update only allowed for incomplete tasks
        if (myTasks.get(position).getTag().equals("-1")) {
            Intent i = new Intent(context, create_new_task.class);
            i.putExtras(getDataFromPosition(position));
            context.startActivity(i);
        } else {
            taskCardClick(position);
        }
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
        bundle.putString("uCategory", item.getCategory());
        bundle.putString("uTag", item.getTag());
        bundle.putString("uTasker", item.getTaskerId());
        return bundle;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void startStopwatch(int position, TextView holder) throws ParseException {
        NewTask ip = myTasks.get(position);
        long diff = 0, end, now = Calendar.getInstance().getTime().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy, HH:mm");
        String later = ip.getDate() + ", " + ip.getTime();
        try {
            end = formatter.parse(later).getTime();
            diff = end - now;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        MyCount counter = new MyCount(diff, 1000, holder, position);
        counter.start();
    }

    public class MyCount extends CountDownTimer {
        String hms; TextView tv; int pos;
        MyCount(long millisInFuture, long countDownInterval, TextView holder, int position) {
            super(millisInFuture, countDownInterval);
            this.tv=holder;
            this.pos = position;
        }

        @Override
        public void onFinish() {
            tv.setText("EXPIRED");
            NewTask task = myTasks.get(pos);
            task.setTag("2");
            FirebaseFirestore.getInstance().collection("Tasks")
                    .document(task.getTaskId()).update(task.getTaskId(), task).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<Void> task) { }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) { }
            });
        }

        @Override
        public void onTick(long millisUntilFinished) {
            long millis = millisUntilFinished;
            hms = (TimeUnit.MILLISECONDS.toDays(millis)) + " days, "
                    + (TimeUnit.MILLISECONDS.toHours(millis) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(millis)) + ":")
                    + (TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)) + ":"
                    + (TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))));
            tv.setText(hms);
        }
    }
}

