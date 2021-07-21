package com.example.myapplication;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toast;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import org.jetbrains.annotations.NotNull;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link task_view#newInstance} factory method to
 * create an instance of this fragment.
 */
public class task_view extends Fragment {
    private TextInputEditText title, description, date, price, time;
    private String uDate, uDesc, uUserId, utaskId, uLocation, uPrice, uTitle, uTime, uType, uTag, uTasker, hp, name;
    private String contact_id, bar_title;
    private Bundle view_user = new Bundle();
    private AutoCompleteTextView location, category;
    private Button chatButton, callButton;
    private int source = -1;
    ProfileView p;
    private BottomNavigationView navigation;
    private int width, height;

    DatabaseReference ref;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public task_view() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static task_view newInstance(String param1, String param2) {
        task_view fragment = new task_view();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        p = new ProfileView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_task_view, container, false);
        navigation = getActivity().findViewById(R.id.bottomNavigationView);
        ViewGroup.LayoutParams params = navigation.getLayoutParams();
        width = params.width;
        height = params.height;
        params.height = 0;
        navigation.setLayoutParams(params);
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ref = FirebaseDatabase.
                getInstance("https://taskrabbits-1621680681859-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Users");

        Bundle b = getArguments();
        uDate = b.getString("uDate");
        uDesc = b.getString("uDesc");
        uUserId = b.getString("uUserId");
        utaskId = b.getString("utaskId");
        uLocation = b.getString("uLocation");
        uPrice = b.getString("uPrice");
        uTitle = b.getString("uTitle");
        uTime = b.getString("uTime");
        uType = b.getString("uCategory");
        uTag = b.getString("uTag");
        uTasker = b.getString("uTasker");
        source = b.getInt("source");

        title = v.findViewById(R.id.taskViewTitle);
        description = v.findViewById(R.id.taskViewDesc);
        location = v.findViewById(R.id.taskViewLocation);
        category = v.findViewById(R.id.taskViewCategory);
        price = v.findViewById(R.id.taskViewPrice);
        date = v.findViewById(R.id.taskViewDate);
        time = v.findViewById(R.id.taskViewTime);
        chatButton = v.findViewById(R.id.taskViewChat);
        callButton = v.findViewById(R.id.taskViewCall);

        if (source == 0) {
            callButton.setText("View Publisher");
            view_user.putString("user", uUserId);
        }
        if (uTag.equals("0")) {
            callButton.setText((source == 2) ? "Call publisher" : "Call tasker");
            contact_id = (source == 2) ? uUserId : uTasker;
            chatButton.setText("View Chat");
        } else if (uTag.equals("1")) {
            chatButton.setText("View Chat");
            callButton.setText((source == 2) ? "View publisher" : "View tasker");
            view_user.putString("user", (source == 2) ? uUserId : uTasker);
        } else {
            chatButton.setVisibility((uTag.equals("2") ? View.GONE :View.VISIBLE));
            callButton.setVisibility((uTag.equals("2") ? View.GONE :View.VISIBLE));
        }

        if (contact_id != null) {
            ref.child(contact_id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    hp = snapshot.child("hp").getValue(String.class);
                }
                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    Toast.makeText(getContext(), "unable to get tasker contact number", Toast.LENGTH_SHORT).show();
                }
            });
        }

        title.setText(uTitle);
        description.setText(uDesc);
        date.setText(uDate);
        location.setText(uLocation, false);
        category.setText(uType, false);
        price.setText(uPrice);
        time.setText(uTime);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (source == 0 && FirebaseAuth.getInstance().getCurrentUser().getUid().equals(uUserId)) {
            chatButton.setVisibility(View.GONE);
        }
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), MessageActivity.class);
                i.putExtra("userID", source == 1 && !uTag.equals("-1") ? uTasker : uUserId);
                i.putExtra("taskID", utaskId);
                i.putExtra("taskTitle", uTitle);
                startActivity(i);
            }
        });

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uTag.equals("0")) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "+65" + hp));
                    startActivity(intent);
                } else {
                    p.setArguments(view_user);
                    getParentFragmentManager().beginTransaction()
                            .add(R.id.fragmentContainerView, p).addToBackStack(null).commit();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = navigation.getLayoutParams();
        params.height = 0;
        navigation.setLayoutParams(params);
        bar_title = ((AppCompatActivity)getActivity()).getSupportActionBar().getTitle().toString();
        //((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("");
    }

    @Override
    public void onStop() {
        super.onStop();
        //Toast.makeText(getContext(), "on stop " + bar_title, Toast.LENGTH_SHORT).show();
        //((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(bar_title);
    }

    @Override
    public void onPause() {
        super.onPause();
        ViewGroup.LayoutParams params = navigation.getLayoutParams();
        params.height = height;
        params.width = width;
        navigation.setLayoutParams(params);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (menu.findItem(R.id.home_add) != null) {
            menu.removeItem(R.id.home_add);
        } if (menu.findItem(R.id.home_filter) != null) {
            menu.removeItem(R.id.home_filter);
        } if (menu.findItem(R.id.mytasks_delete) != null) {
            menu.removeItem(R.id.mytasks_delete);
        } if (menu.findItem(R.id.mytasks_add) != null) {
            menu.removeItem(R.id.mytasks_add);
        } if (menu.findItem(R.id.home_leader) != null) {
            menu.removeItem(R.id.home_leader);
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }
}