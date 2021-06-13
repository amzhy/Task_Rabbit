package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class create_new_task extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private Button confirm;
    private Bundle bundle;
    private static final int SUCCESS = 1, FAILURE = 0;

    private TextInputLayout title, date, time, price, description;
    private AutoCompleteTextView location;
    private String[] arr;
    private String userId, taskId;
    private String uTitle, uUserId, uPrice, uLocation, uDate, uDesc, uTime, utaskId;
    private String sTitle, sUserId, sPrice, sLocation, sDate, sDesc, sTime, staskId;
    private int hr, min;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_task);

        db = FirebaseFirestore.getInstance();
        bundle = getIntent().getExtras();

        confirm = findViewById(R.id.editTaskSavebtn);
        title = findViewById(R.id.editTaskTitle);
        price = findViewById(R.id.editAddress);
        location = findViewById(R.id.outlined_exposed_dropdown_editable);
        date = findViewById(R.id.editDate);
        time = findViewById(R.id.editTime);
        description = findViewById(R.id.editTaskDetails);

        arr = new String[] { "UTown", "PGP", "Raffles Hall", "RVRC", "Sheares Hall" };
        ArrayAdapter<String> a = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, arr);
        //ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,.array.Location, android.R.layout.simple_spinner_item);
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        location.setAdapter(a);
        //location.setOnItemSelectedListener(this);

        //connect with the user
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        userId = user.getUid();

        if(bundle == null) {
            confirm.setText("Confirm");
            getSupportActionBar().hide();
            //setTitle("New Task");
        } else {
            confirm.setText("Update");
            setData(bundle);
            //getSupportActionBar().hide(); setTitle("Update Task");
        }

        //date input display - set sDate
        date.getEditText().addTextChangedListener(new TextWatcher() {
            private String current = "";
            private String ddmmyyyy = "ddmmyyyy";
            private Calendar cal = Calendar.getInstance();
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
                    String cleanC = current.replaceAll("[^\\d.]|\\.", "");

                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 6; i += 2) {  sel++; }

                    if (clean.equals(cleanC)) sel--;
                    if (clean.length() < 8){
                        clean = clean + ddmmyyyy.substring(clean.length());
                    } else {
                        int day  = Integer.parseInt(clean.substring(0,2));
                        int mon  = Integer.parseInt(clean.substring(2,4));
                        int year = Integer.parseInt(clean.substring(4,8));

                        //date can only be set in the future
                        mon = mon > 12 ? 12 : mon < 1 ? 1 : Math.max(mon, cal.get(Calendar.MONTH));
                        cal.set(Calendar.MONTH, mon);

                        year = year < 2021 ? 2021 : Math.min(year, 2024);
                        cal.set(Calendar.YEAR, year);
                        day = Math.max(Math.min(day, cal.getActualMaximum(Calendar.DATE)), cal.get(Calendar.DATE));

                        clean = String.format("%02d%02d%02d",day, mon, year);
                    }
                    clean = String.format("%s/%s/%s", clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, 8));

                    sel = Math.max(sel, 0);
                    current = clean;
                    sDate = current;
                    date.getEditText().setText(current);
                    date.getEditText().setSelection(sel < current.length() ? sel : current.length());
                }
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        //choose time from dialog - set sTime
        time.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { popTimePicker(); }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = saveToFireStore();
//                Intent i = new Intent(create_new_task.this, MainActivity.class);
//                startActivity(i);
                if (i != FAILURE) {
                    finish();
                }
            }
        });
    }

    public int saveToFireStore() {
        NewTask newTask;
        sTitle = title.getEditText().getText().toString();
        sDesc = description.getEditText().getText().toString();
        sLocation = location.getText().toString();
        sPrice  = price.getEditText().getText().toString();
        sDate = date.getEditText().getText().toString();
        sTime = time.getEditText().getText().toString();

        if (!(sTitle.isEmpty() || sDate.isEmpty() ||
                sDesc.isEmpty()||sLocation.isEmpty()||sPrice.isEmpty() || sTime.isEmpty())) {
            if (bundle == null) {
                taskId = UUID.randomUUID().toString();
                newTask = getTask(sTitle, sPrice, sLocation, sDesc, sDate, sTime);
                HashMap<String, Object> map = new HashMap<>();
                map.put(taskId, newTask);

                db.collection("Tasks").document(taskId).set(map)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Task added", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Data not saved", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                //task Id
                newTask = getTask(sTitle, sPrice, sLocation, sDesc, sDate, sTime);
                db.collection("Tasks").document(taskId).update(taskId, newTask).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Data updated", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return SUCCESS;
        } else {
            Toast.makeText(getApplicationContext(), "Empty Fields are not allowed!", Toast.LENGTH_SHORT).show();
            return FAILURE;
        }

    }

    public NewTask getTask(String title, String price, String location, String desc, String date, String time){
        return new NewTask(title, desc, location, price, date, time, userId, taskId);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void setData(Bundle bundle) {
        uDate = bundle.getString("uDate");
        uDesc = bundle.getString("uDesc");
        uUserId = bundle.getString("uUserId");
        utaskId = bundle.getString("utaskId");
        uLocation = bundle.getString("uLocation");
        uPrice = bundle.getString("uPrice");
        uTitle = bundle.getString("uTitle");
        uTime = bundle.getString("uTime");

        //update - actionbar shows task title
        setTitle(uTitle);

        title.getEditText().setText(uTitle);
        description.getEditText().setText(uDesc);
        date.getEditText().setText(uDate);
        location.setText(uLocation, false);
        price.getEditText().setText(uPrice);
        time.getEditText().setText(uTime);
        taskId = utaskId;
        userId = uUserId;
    }
    
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { }

    private void popTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(create_new_task.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        hr = hourOfDay; min = minute;
                        time.getEditText().setText(String.format(Locale.getDefault(), "%02d:%02d", hr, min));
                        sTime = String.format(Locale.getDefault(), "%02d:%02d", hr, min);
                    }
                }, hr, min, true);
        timePickerDialog.setTitle("Due by");
        timePickerDialog.show();
    }
}
