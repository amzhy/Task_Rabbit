package com.example.myapplication;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link task_view#newInstance} factory method to
 * create an instance of this fragment.
 */
public class task_view extends Fragment {
    private TextInputEditText title, description, date, price, time;
    private String uDate, uDesc, uUserId, utaskId, uLocation, uPrice, uTitle, uTime;
    private AutoCompleteTextView location;
    private Button chatButton;

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
        Bundle b = this.getArguments();
        if (b != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_task_view, container, false);

        Bundle b = getArguments();
        uDate = b.getString("uDate");
        uDesc = b.getString("uDesc");
        uUserId = b.getString("uUserId");
        utaskId = b.getString("utaskId");
        uLocation = b.getString("uLocation");
        uPrice = b.getString("uPrice");
        uTitle = b.getString("uTitle");
        uTime = b.getString("uTime");

        title = v.findViewById(R.id.taskViewTitle);
        description = v.findViewById(R.id.taskViewDesc);
        location = v.findViewById(R.id.taskViewLocation);
        price = v.findViewById(R.id.taskViewPrice);
        date = v.findViewById(R.id.taskViewDate);
        time = v.findViewById(R.id.taskViewTime);
        chatButton = v.findViewById(R.id.taskViewChat);

        System.out.println("                                                         VALUES GOTTEN on createview " + "\n" +
                uTitle + "\n" + uDate + "\n" + uLocation + "\n" + uPrice + "\n" + uDesc + "\n" + uTime);

        title.setText(uTitle);
        description.setText(uDesc);
        date.setText(uDate);
        location.setText(uLocation, false);
        price.setText(uPrice);
        time.setText(uTime);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(uUserId)) {
                    Toast.makeText(getActivity(), "Cannot chat with own account", Toast.LENGTH_SHORT).show();
                } else {
                    Intent i = new Intent(getContext(), MessageActivity.class);
                    i.putExtra("userID", uUserId);
                    i.putExtra("taskID", utaskId);
                    i.putExtra("taskTitle", uTitle);
                    startActivity(i);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
        ((AppCompatActivity)getActivity()).findViewById(R.id.bottomNavigationView).setVisibility(View.INVISIBLE);
    }

    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
        ((AppCompatActivity)getActivity()).findViewById(R.id.bottomNavigationView).setVisibility(View.VISIBLE);
    }
}