package com.example.myapplication;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.icu.text.TimeZoneFormat;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.resources.TextAppearance;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.kevincodes.recyclerview.ItemDecorator;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import static java.lang.String.valueOf;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link my_tasks#newInstance} factory method to
 * create an instance of this fragment.
 */
public class my_tasks extends Fragment {
    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private List<NewTask> myTasks;
    private String taskId = UUID.randomUUID().toString();
    private String userId;
    private SwipeRefreshLayout swipeRefreshLayout;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private FirebaseUser firebaseUser;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public my_tasks() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static my_tasks newInstance(String param1, String param2) {
        my_tasks fragment = new my_tasks();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        setHasOptionsMenu(true);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userId = firebaseUser.getUid();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_tasks, container, false);
    }

    //need to change recyclerview to item inside
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable
            Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = getView().findViewById(R.id.items);
//        recyclerView.setHasFixedSize(false);
        recyclerView.setVerticalScrollBarEnabled(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        db = FirebaseFirestore.getInstance();
        myTasks = new ArrayList<>();

        adapter = new MyAdapter(getContext(), myTasks, getActivity().getSupportFragmentManager(), getActivity(), getView());

        recyclerView.setAdapter(adapter);
        showData();

        //ItemTouchHelper touchHelper = new ItemTouchHelper(new TouchHelper(adapter, getContext(), recyclerView, myTasks));
        //touchHelper.attachToRecyclerView(recyclerView);

        swipeRefreshLayout = getView().findViewById(R.id.taskstab_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        content();
    }

    public void showData() {
    db.collection("Tasks").get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                    myTasks.clear();
                    for (DocumentSnapshot snapshot : task.getResult()) {
                        HashMap<String, String> taskStored = (HashMap<String, String>) snapshot.getData().get(snapshot.getId());
                        if (taskStored.get("userId").equals(userId)) {
                            NewTask newTask = new NewTask(taskStored.get("title"),
                                    taskStored.get("description"), taskStored.get("location"),
                                    taskStored.get("price"), taskStored.get("date"),
                                    taskStored.get("time"), taskStored.get("userId"),
                                    taskStored.get("taskId"), taskStored.get("tag"),
                                    taskStored.get("taskerId"), taskStored.get("category"));
                            myTasks.add(newTask);
                            myTasks.sort(new Comparator<NewTask>() {
                                @Override
                                public int compare(NewTask o1, NewTask o2) {
                                    int task1 = Integer.parseInt(o1.getTag());
                                    int task2 = Integer.parseInt(o2.getTag());
                                    if (task1 > task2) {
                                        return 1;
                                    } else if (task1 == task2) {
                                        return  0;
                                    } else {
                                        return -1;
                                    }
                                }
                            });
                            adapter.notifyDataSetChanged();
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull @NotNull Exception e) {
            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    });
    }

    private void content(){
        showData();
        refresh(1000);
    }

    private void refresh(int millisecond) {
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                content();
            }
        };
        handler.postDelayed(runnable, millisecond);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        MenuInflater inflater1 = getActivity().getMenuInflater();
        inflater1.inflate(R.menu.mytasks_menu, menu);
        super.onCreateOptionsMenu(menu, inflater1);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mytasks_add: {
                Intent i = new Intent(getActivity(), create_new_task.class);
                startActivity(i);
                return true;
            } case R.id.mytasks_delete: {
                if (adapter != null) { adapter.menu_delete = 1; }
            } default:
                return super.onOptionsItemSelected(item);
        }
    }

}