package com.example.myapplication;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
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
import android.util.Log;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    private ProgressDialog progressDialog;
    private PopOutFilter.FilterPref filterPref;

    String user_token;
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
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tasks, container, false);
    }

    @SuppressLint("RestrictedApi")
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
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        newTasks.clear();
                        for (DocumentSnapshot snapshot : task.getResult()) {
                            HashMap<String, String> taskStored = (HashMap<String, String>) snapshot.getData().get(snapshot.getId());
                            if (taskStored.get("tag").equals("-1") && checkTime(taskStored)) {
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
                        Collections.sort(newTasks, new compareDdl());
                        adapter.notifyDataSetChanged();
                        progressDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT);
            }
        });
    }

    private class compareDdl implements Comparator<NewTask> {

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public int compare(NewTask o1, NewTask o2) {
            String date1 = o1.getDate();
            String time1 = o1.getTime(); //24 hours

            String date2 = o2.getDate();
            String time2 = o2.getTime(); //24 hours

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            try {
                Date firstDate = sdf.parse(date1 + " "+time1);
                Date secondDate = sdf.parse(date2 + " "+time2);
                return firstDate.compareTo(secondDate);
            } catch (ClassCastException | ParseException c) {
                Toast.makeText(getContext(), c.getMessage()+"Date cannot be parsed", Toast.LENGTH_SHORT).show();
            }
            return 0;
        }

    }

    public void openDialog() {
        if (this.filterPref != null) {
            PopOutFilter filter_task = new PopOutFilter(this, this.filterPref);
            filter_task.show(getFragmentManager(), "filter dialog");
        } else {
            PopOutFilter filter_task = new PopOutFilter(this);
            filter_task.show(getFragmentManager(), "filter dialog");}
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
                    } else if (nt.getLocation().toLowerCase().startsWith(newText.toLowerCase())){
                        searchResult.add(nt);
                    } else if (nt.getCategory().toLowerCase().startsWith(newText.toLowerCase())) {
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
            if ((nt.getLocation().equals(location) || location.equals("All Locations") || nt.getLocation().equals("Any Location"))
                    && (nt.getCategory().equals(type)||type.equals("All Types"))
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
            long diffInMillies = secondDate.getTime() - firstDate.getTime();
            long diff = TimeUnit.DAYS.convert(Math.abs(diffInMillies), TimeUnit.MILLISECONDS);
            return diff < deadline && diffInMillies >= 0;
        } catch (ClassCastException | ParseException c) {
            Toast.makeText(getContext(), c.getMessage()+"Date cannot be parsed", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean checkTime(HashMap<String, String> taskStored) {
        String date = taskStored.get("date");
        String time = taskStored.get("time"); //24 hours

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate localDate = LocalDate.now();
        String now = dtf.format(localDate);

        DateTimeFormatter tf = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime localTime = LocalTime.now();

        String localTimeString = tf.format(localTime).substring(0, 5);

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try {
            Date firstDate = df.parse(now + " "+localTimeString);
            Date secondDate = df.parse(date + " "+time);
            long diffInMillies = secondDate.getTime() - firstDate.getTime();
            boolean d = diffInMillies>0;
            if (!d) {
                NewTask newTask = new NewTask(taskStored.get("title"), taskStored.get("description"),
                        taskStored.get("location"), taskStored.get("price"), taskStored.get("date"),
                        taskStored.get("time"), taskStored.get("userId"), taskStored.get("taskId"),
                        "2", taskStored.get("taskerId"), taskStored.get("category"));
                if (taskStored.get("taskId") != "test") {
                    db.collection("Tasks").document(taskStored.get("taskId"))
                            .update(taskStored.get("taskId"), newTask);
                    sendNotif(newTask.getUserId(), "Your task: " + newTask.getTitle() + " has expired!",
                            "Sorry, no taskers were available to complete your task");
                }
            }
            return d;
        } catch (ClassCastException | ParseException c) {
            Toast.makeText(getContext(), c.getMessage()+"Date cannot be parsed", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public void savePreference(PopOutFilter.FilterPref fp) {
        this.filterPref = fp;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        MenuInflater inflater1 = getActivity().getMenuInflater();
        inflater1.inflate(R.menu.home_menu, menu);
        super.onCreateOptionsMenu(menu, inflater1);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull @NotNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (menu.findItem(R.id.mytasks_delete) != null) {
            menu.removeItem(R.id.mytasks_delete);
        } if (menu.findItem(R.id.mytasks_add) != null) {
            menu.removeItem(R.id.mytasks_add);
        } if (menu.findItem(R.id.inbox_delete)!=null) {
            menu.removeItem(R.id.inbox_delete);
        }
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
            case R.id.home_leader:
                openLeaderboard();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void openLeaderboard(){
        Intent i = new Intent(getActivity(), Leaderboard.class);
        startActivity(i);
    }

    private void sendNotif(String uid, String title, String body) {
        DatabaseReference users_ref = FirebaseDatabase.getInstance("https://taskrabbits-1621680681859-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Users");
        users_ref.child(uid).addValueEventListener(new ValueEventListener() {
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
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) { }
        });
    }
}