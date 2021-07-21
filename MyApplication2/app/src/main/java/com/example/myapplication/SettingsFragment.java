package com.example.myapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
    private boolean inbox=true, tasker=true, leader=true;
    private String reminder = "10min";
    private String user_id;

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

        //get switches
        inbox_sw = (SwitchPreferenceCompat) findPreference("inbox_alert");
        task_sw = (SwitchPreferenceCompat) findPreference("task_status");
        leader_sw = (SwitchPreferenceCompat) findPreference("leaderbd");
        alert_sw = (ListPreference) findPreference("tasker_alert");

        db.child(user_id)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        inbox = snapshot.child("inbox").getValue(Boolean.class);
                        tasker = snapshot.child("task_status").getValue(Boolean.class);
                        reminder = snapshot.child("tasker_alert").getValue(String.class);
                        leader = snapshot.child("leaderboard").getValue(Boolean.class);
                        inbox_sw.setChecked(inbox); task_sw.setChecked(tasker);
                        leader_sw.setChecked(leader); alert_sw.setValueIndex(getIndex(reminder));
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                        inbox_sw.setChecked(true); task_sw.setChecked(true);
                        leader_sw.setChecked(true); alert_sw.setValueIndex(getIndex("10min"));
                    }
                });

        inbox_sw.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                inbox = (boolean) newValue;
                db.child(user_id).child("inbox").setValue(inbox);
                return  true;
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
    public void onDestroy() {
        super.onDestroy();
    }

    /*
    public void refresh() {
        db.child(user_id).child("inbox").setValue(inbox);
        db.child(user_id).child("task_status").setValue(tasker);
        db.child(user_id).child("leaderboard").setValue(leader);
        db.child(user_id).child("tasker_alert").setValue(reminder);
    }

     */
}
