package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.DateFormat;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class create_new_task extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
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
    private boolean dateToday = false;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_task);

        db = FirebaseFirestore.getInstance();
        bundle = getIntent().getExtras();

        confirm = findViewById(R.id.editTaskSavebtn);
        title = findViewById(R.id.editTaskTitle);
        price = findViewById(R.id.editPrice);
        location = findViewById(R.id.outlined_exposed_dropdown_editable);
        date = findViewById(R.id.editDate);
        time = findViewById(R.id.editTime);
        description = findViewById(R.id.editTaskDetails);

        arr = new String[] { "UTown", "PGP", "Raffles Hall", "RVRC", "Sheares Hall" };
        ArrayAdapter<String> a = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, arr);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        location.setAdapter(a);

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

        //choose date first -- disbale past dates
        date.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popTimePicker();
                popDatePicker(v);
            }
        });

        //choose time -- if dateToday disable past time
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
                if (i != FAILURE) { finish(); }
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
        return new NewTask(title, desc, location, price, date, time, userId, taskId, "-1", null);
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
                R.style.DialogTheme,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        hr = hourOfDay; min = minute;
                        Calendar cal = Calendar.getInstance();
                        if (!(dateToday && hr <= cal.get(Calendar.HOUR_OF_DAY) && min <= cal.get(Calendar.MINUTE))) {
                            time.getEditText().setText(String.format(Locale.getDefault(), "%02d:%02d", hr, min));
                            sTime = String.format(Locale.getDefault(), "%02d:%02d", hr, min);
                        } else {
                            Toast.makeText(create_new_task.this, "Unable to set past time!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, hr, min, true);
        timePickerDialog.show();
    }

    private void popDatePicker(View vw) {
        Calendar cal  = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        dateToday = false;

        DatePickerDialog dialog =  new DatePickerDialog(
                create_new_task.this,
                android.R.style.Theme_Material_Light_Dialog_Alert,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month + 1;
                        sDate = new StringBuilder().append(day).append( "/" )
                                .append(month).append( "/" ).append(year).toString();
                        date.getEditText().setText(sDate);
                        if (dayOfMonth == cal.get(Calendar.DAY_OF_MONTH) && (month-1) == cal.get(Calendar.MONTH) && year == cal.get(Calendar.YEAR)) {
                            dateToday = true;
                        }
                    }
                }, year, month, day);
        dialog.getDatePicker().setMinDate(cal.getTimeInMillis());
        dialog.show();
    }
}