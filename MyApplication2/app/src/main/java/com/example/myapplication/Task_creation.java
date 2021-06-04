package com.example.myapplication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

import static java.lang.String.valueOf;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Task_creation#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Task_creation extends Fragment implements AdapterView.OnItemSelectedListener {
//    FirebaseDatabase firebaseDatabase;
//    DatabaseReference databaseReferenceTask, databaseReferenceUser;
//    FirebaseUser user;
//    FirebaseAuth firebaseAuth;
   private TextInputLayout title, date, time, price, description;
   private View photo;
   private Spinner location;
   private Button confirm;
   private LinearLayout linearLayout;
   private FirebaseFirestore db;
   private String id = UUID.randomUUID().toString();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Task_creation() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Task_creation.
     */
    // TODO: Rename and change types and number of parameters
    public static Task_creation newInstance(String param1, String param2) {
        Task_creation fragment = new Task_creation();
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

        db = FirebaseFirestore.getInstance();

        db.collection("test").document(id).set("test");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Task");
        return inflater.inflate(R.layout.fragment_task_creation, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Spinner spinner = getView().findViewById(R.id.spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(), R.array.Location,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        confirm = getView().findViewById(R.id.editTaskSavebtn);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToFireStore(id);
                //Toast.makeText(getContext(), "hi", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    public void saveToFireStore(String id){
        title = getView().findViewById(R.id.editTaskTitle);
        price = getView().findViewById(R.id.editAddress);
        location = getView().findViewById(R.id.spinner);
        date = getView().findViewById(R.id.editDate);
        //need to convert date
        time = getView().findViewById(R.id.editTime);
        description = getView().findViewById(R.id.editTaskDetails);
        photo = getView().findViewById(R.id.editTaskPhoto);

        String sTitle = title.getEditText().getText().toString();
        String sPrice = price.getEditText().getText().toString();
        String sLocation = location.getSelectedItem().toString();
        String sDesc = description.getEditText().getText().toString();
        String sDate = date.getEditText().getText().toString();
        NewTask newTask;

        if (!(sTitle.isEmpty() || sDate.isEmpty() ||
                sDesc.isEmpty()||sLocation.isEmpty()||sPrice.isEmpty())) {

          newTask = getTask(sTitle, sPrice, sLocation, sDesc, sDate);
            HashMap<String, NewTask> map = new HashMap<>();
            map.put(id, newTask);
            db.collection("Tasks").document(id).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Task added", Toast.LENGTH_SHORT);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    Toast.makeText(getContext(), "Data not saved", Toast.LENGTH_SHORT);
                }
            });
        } else {
            Toast.makeText(getContext(), "Empty Fields are not allowed!", Toast.LENGTH_SHORT);
        }

    }



    public NewTask getTask(String title, String price, String location, String desc, String date){
        NewTask newTask = new NewTask(
                title,
                desc,
                location,
                Double.parseDouble(price),
                date);
        return newTask;
    }

}