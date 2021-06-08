package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
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
import java.util.Map;
import java.util.UUID;

public class create_new_task extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private Button confirm;
    private Bundle bundle;

    private TextInputLayout title, date, time, price, description;
    private AutoCompleteTextView location;
    private String[] arr;
    private String userId, taskId;
    private String uTitle, uUserId, uPrice, uLocation, uDate, uDesc, uTime, utaskId;


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
        //location = findViewById(R.id.spinner);

        //need to convert date
        date = findViewById(R.id.editDate);
        time = findViewById(R.id.editTime);

        description = findViewById(R.id.editTaskDetails);

        arr = new String[] { "UTown", "PGP", "Raffles Hall", "RVRC", "Sheares Hall" };
        ArrayAdapter<String> a = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, arr);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Location,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        location.setAdapter(a);
        //location.setOnItemSelectedListener(this);

        //connect with the user
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        userId = user.getUid();

        if(bundle == null) {
            confirm.setText("Confirm");
            setTitle("New Task");
        } else {
            confirm.setText("Update");
            setData(bundle);
            setTitle("Update Task");
        }

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToFireStore();
                Intent i = new Intent(create_new_task.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    public void saveToFireStore(){
        String sTitle = title.getEditText().getText().toString();
        String sPrice = price.getEditText().getText().toString();
        String sLocation = location.getText().toString();
        String sDesc = description.getEditText().getText().toString();
        String sDate = date.getEditText().getText().toString();
        String sTime = time.getEditText().getText().toString();
        NewTask newTask;

        if (!(sTitle.isEmpty() || sDate.isEmpty() ||
                sDesc.isEmpty()||sLocation.isEmpty()||sPrice.isEmpty() ||sTime.isEmpty())) {

            if (bundle == null) {
                taskId = UUID.randomUUID().toString();
                newTask = getTask(sTitle, sPrice, sLocation, sDesc, sDate, sTime);
                HashMap<String, Object> map = new HashMap<>();
                map.put(taskId, newTask);
                db.collection("Tasks").document(taskId).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
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
        } else {
            Toast.makeText(getApplicationContext(), "Empty Fields are not allowed!", Toast.LENGTH_SHORT).show();
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

        title.getEditText().setText(uTitle);
        description.getEditText().setText(uDesc);
        date.getEditText().setText(uDate);
        location.setText(uLocation, false);
        price.getEditText().setText(uPrice);
        time.getEditText().setText(uTime);
        taskId = utaskId;
        userId = uUserId;
    }

    private int getIndex(Spinner spinner, String myString){
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                return i;
            }
        }
        return 0;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
