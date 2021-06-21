package com.example.myapplication;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static java.lang.String.valueOf;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link tasks#newInstance} factory method to
 * create an instance of this fragment.
 */
public class tasks extends Fragment {
    private RecyclerView recyclerView;
    private SearchView searchView;
    private FirebaseFirestore db;
    private MyAdapter adapter;
    private List<NewTask> newTasks;
    private SwipeRefreshLayout swipeRefreshLayout;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public tasks() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment tasks.
     */
    // TODO: Rename and change types and number of parameters
    public static tasks newInstance(String param1, String param2) {
        tasks fragment = new tasks();
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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tasks, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = getView().findViewById(R.id.recycleTasks);
        searchView = getView().findViewById(R.id.tasksSearch);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        db = FirebaseFirestore.getInstance();
        newTasks = new ArrayList<>();
        adapter = new MyAdapter(getContext(), newTasks, getActivity().getSupportFragmentManager());
        recyclerView.setAdapter(adapter);

        showData();

        swipeRefreshLayout = getView().findViewById(R.id.refreshTasks);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        content();

        initSearchWidget();
    }
    
    public void showData() {
        db.collection("Tasks").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        newTasks.clear();
                        for (DocumentSnapshot snapshot : task.getResult()) {
                            HashMap<String, String> taskStored = (HashMap<String, String>) snapshot.getData().get(snapshot.getId());
                            if (taskStored.get("tag").equals("-1")) {
                                NewTask newTask = new NewTask(taskStored.get("title"),
                                        taskStored.get("description"), taskStored.get("location"),
                                        taskStored.get("price"), taskStored.get("date"),
                                        taskStored.get("time"), taskStored.get("userId"),
                                        taskStored.get("taskId"), taskStored.get("tag"),
                                        taskStored.get("taskerId"),
                                        taskStored.get("category"));
                                newTasks.add(newTask);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT);
            }
        });
    }

    public void openDialog() {
        PopOutFilter filter_task = new PopOutFilter();
        filter_task.show(getFragmentManager(), "filter dialog");

    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        MenuInflater inflater1 = getActivity().getMenuInflater();
        inflater1.inflate(R.menu.home_menu, menu);
        super.onCreateOptionsMenu(menu, inflater1);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home_add: {
                Intent i = new Intent(getActivity(), create_new_task.class);
                startActivity(i);
                return true;
            } case R.id.home_filter:
                openDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

    private void initSearchWidget() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<NewTask> searchResult = new ArrayList<>();
                for (NewTask nt: newTasks) {
                    if (nt.getTitle().toLowerCase().contains(newText.toLowerCase())){
                        searchResult.add(nt);
                    }
                }
                MyAdapter ma = new MyAdapter(getContext(), searchResult, getActivity().getSupportFragmentManager());
                recyclerView.setAdapter(ma);

                return false;
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void filter(String location, String type, List<Float> price, int deadline) {
        ArrayList<NewTask> filtered = new ArrayList<>();
        for (NewTask nt: newTasks) {
            if ((nt.getLocation().equals(location) || location.equals("All Locations") || nt.getLocation().equals("All Locations"))
                    && (nt.getCategory().equals(type)||type.equals("All Types") || nt.getCategory().equals("All Types"))
                    && (Float.parseFloat(nt.getPrice()) <= price.get(1)) && Float.parseFloat(nt.getPrice()) >= price.get(0)
                && checkTime(nt.getDate(), deadline)) {
                filtered.add(nt);
            }
        }
        MyAdapter ma = new MyAdapter(getContext(), filtered, getActivity().getSupportFragmentManager());
        recyclerView.setAdapter(ma);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean checkTime(String taskTime, int deadline) {
        if (deadline == 0) {
            return true;
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate localDate = LocalDate.now();
        String now = dtf.format(localDate);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        try {
            Date firstDate = sdf.parse(now);
            Date secondDate = sdf.parse(taskTime);
            long diffInMillies = Math.abs(secondDate.getTime() - firstDate.getTime());
            long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
            return diff < deadline && diffInMillies >= 0;
        } catch (ClassCastException | ParseException c) {
            Toast.makeText(getContext(), c.getMessage()+"Date cannot be parsed", Toast.LENGTH_SHORT).show();
        }




        return false;
    }

}