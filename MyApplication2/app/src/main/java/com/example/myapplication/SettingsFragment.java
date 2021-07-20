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

    private FirebaseFirestore db;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        user_id = FirebaseAuth.getInstance().getUid();

        //get switches
        inbox_sw = (SwitchPreferenceCompat) findPreference("inbox_alert");
        task_sw = (SwitchPreferenceCompat) findPreference("task_status");
        leader_sw = (SwitchPreferenceCompat) findPreference("leaderbd");
        alert_sw = (ListPreference) findPreference("tasker_alert");

        db.collection("Settings").document(user_id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            inbox = (boolean) task.getResult().get("inbox");
                            tasker = (boolean) task.getResult().get("task_status");
                            reminder = (String) task.getResult().get("tasker_alert");
                            leader = (boolean) task.getResult().get("leaderboard");
                            inbox_sw.setChecked(inbox); task_sw.setChecked(tasker);
                            leader_sw.setChecked(leader); alert_sw.setValueIndex(getIndex(reminder));
                        }
                    }
                });

        inbox_sw.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                inbox = (boolean) newValue;
                return  true;
            }
        });
        task_sw.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                tasker = (boolean) newValue;

                return true;
            }
        });
        leader_sw.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                leader = (boolean) newValue;
                return true;
            }
        });
        alert_sw.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                reminder = newValue.toString();
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

    public void refresh() {
        HashMap<String, Object> s = new HashMap<>();
        s.put("inbox", inbox); s.put("task_status", tasker); s.put("tasker_alert", reminder); s.put("leaderboard",leader);
        db.collection("Settings").document(user_id).update(s).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if (task.isSuccessful()) {
                   // Toast.makeText(getContext(), "update setting successfully", Toast.LENGTH_SHORT).show();
                } else {
                    //Toast.makeText(getContext(), "update setting failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
