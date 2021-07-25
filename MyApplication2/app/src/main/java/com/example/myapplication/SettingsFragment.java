package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SettingsFragment extends PreferenceFragmentCompat {

    SwitchPreferenceCompat inbox_sw, task_sw, leader_sw;
    ListPreference alert_sw;
    private boolean inbox = true, tasker = true, leader = true;
    private String reminder = "10min";
    private String user_id;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase rtNode;
    DatabaseReference reference;

    Fragment fragment2, fragment1, fragment3;

    private DatabaseReference db;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseDatabase.getInstance("https://taskrabbits-1621680681859-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Settings");
        user_id = FirebaseAuth.getInstance().getUid();

        setHasOptionsMenu(true);
        fragment1 = new user_guide();
        fragment2 = new about_us();
        fragment3 = new ProfileFragment();

        firebaseAuth = FirebaseAuth.getInstance();
        rtNode = FirebaseDatabase.
                getInstance("https://taskrabbits-1621680681859-default-rtdb.asia-southeast1.firebasedatabase.app/");
        reference = rtNode.getReference("Users");

        //get switches
        inbox_sw = (SwitchPreferenceCompat) findPreference("inbox_alert");
        task_sw = (SwitchPreferenceCompat) findPreference("task_status");
        leader_sw = (SwitchPreferenceCompat) findPreference("leaderbd");
        alert_sw = (ListPreference) findPreference("tasker_alert");

        db.child(user_id)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if (snapshot.getValue() != null) {
                            inbox = snapshot.child("inbox").getValue(Boolean.class);
                            tasker = snapshot.child("task_status").getValue(Boolean.class);
                            reminder = snapshot.child("tasker_alert").getValue(String.class);
                            leader = snapshot.child("leaderboard").getValue(Boolean.class);
                        } else {
                            inbox_sw.setChecked(inbox);
                            task_sw.setChecked(tasker);
                            leader_sw.setChecked(leader);
                            alert_sw.setValueIndex(getIndex(reminder));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                        inbox_sw.setChecked(true);
                        task_sw.setChecked(true);
                        leader_sw.setChecked(true);
                        alert_sw.setValueIndex(getIndex("10min"));
                    }
                });

        inbox_sw.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                inbox = (boolean) newValue;
                db.child(user_id).child("inbox").setValue(inbox);
                return true;
            }
        });
        task_sw.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                tasker = (boolean) newValue;
                db.child(user_id).child("task_status").setValue(tasker);
                return true;
            }
        });
        leader_sw.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                leader = (boolean) newValue;
                db.child(user_id).child("leaderboard").setValue(leader);
                return true;
            }
        });
        alert_sw.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                reminder = newValue.toString();
                db.child(user_id).child("tasker_alert").setValue(reminder);
                return true;
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private int getIndex(String p) {
        return (p.contains("10")) ? 1 : p.contains("15") ? 2 : p.contains("30") ? 3 : 0;
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        switch (preference.getKey()) {
            case "guide": {
                getParentFragmentManager().beginTransaction().add(R.id.fragmentContainerView2, fragment1).addToBackStack(null).commit();
                return true;
            }
            case "about": {
                getParentFragmentManager().beginTransaction().add(R.id.fragmentContainerView2, fragment2).addToBackStack(null).commit();
                return true;
            }
            case "profile": {
                getParentFragmentManager().beginTransaction().add(R.id.fragmentContainerView2, fragment3).addToBackStack(null).commit();
                return true;
            }
            case "logout": {
                firebaseAuth = FirebaseAuth.getInstance();
                reference = rtNode.getReference("Users");

                if (firebaseAuth.getCurrentUser() != null) {
                    final DatabaseReference lastOnlineRef = rtNode.getReference("/Users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/lastOnline");
                    final DatabaseReference myConnectionsRef = rtNode.getReference("Users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/connections");

                    myConnectionsRef.setValue(false);
                    lastOnlineRef.setValue(ServerValue.TIMESTAMP);

                    final DatabaseReference tokens = reference.child(firebaseAuth.getUid()).child("tokens");
                    if (tokens != null) {
                        tokens.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<Void> task) {
                            }
                        });
                    }
                }
                firebaseAuth.signOut();
                startActivity(new Intent(getContext(), LoginActivity.class));

            }
            default:
                return super.onPreferenceTreeClick(preference);
        }
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull @NotNull Menu menu) {
        if (menu.findItem(R.id.settings_notifications) != null) {
            menu.findItem(R.id.settings_notifications).setVisible(false);
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        v.setBackgroundColor(getResources().getColor(R.color.white));
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        //Toast.makeText(getContext(), "settings resume", Toast.LENGTH_SHORT).show();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Settings");
        getActivity().findViewById(R.id.bottomNavigationView).setVisibility(View.INVISIBLE);
    }

    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Profile");
        getActivity().findViewById(R.id.bottomNavigationView).setVisibility(View.VISIBLE);
    }
}
