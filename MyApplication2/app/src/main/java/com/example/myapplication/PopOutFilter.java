package com.example.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.icu.number.NumberFormatter;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.RangeSlider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

public class PopOutFilter extends AppCompatDialogFragment {
    private Spinner spinnerType, spinnerTime;
    private RangeSlider rangeSlider;
    private AutoCompleteTextView autoCompleteTextView;
//    private FirebaseAuth firebaseAuth;
//    private FirebaseUser user;
    private FilterDialogListener filterDialogListener;
    private tasks t;
    private FilterPref fp;
    private Switch remote;
//    private static final String[] locations = new String[]{"All Locations", "Eusoff Hall", "Kent Ridge Hall ", "King Edward VII Hall", "Raffles Hall", "Sheares Hall",
//            "Temasek Hall", "Prince George's Park House", "Price George's Park Residence", "UTown Residence", "Centre for Department of Teaching and Learning (CDTL)",
//            "Centre for English Language Communication (CELC)", "Duke-NUS Medical School", "Faculty of Arts and Social Sciences", "Faculty of Dentistry",
//            "Faculty of Engineering", "Faculty of Law", "Faculty of Science", "Institute of Systems Science", "Lee Kuan Yew School of Public Policy",
//            "NUS Graduate School for Integrative Sciences and Engineering", "Saw Swee Hock School of Public Health", "School of Business", "School of Computing",
//            "School of Continuing and Lifelong Education", "School of Design and Environment", "University Scholars Programme", "Yale-NUS College",
//            "Yong Loo Lin School of Medicine", "Yong Siew Toh Conservatory of Music"
//    };
    private static final String[] locations = new String[]{"All Locations", "Eusoff Hall", "Kent Ridge Hall ", "King Edward VII Hall", "Raffles Hall", "Sheares Hall",
            "Temasek Hall", "PGPH", "PGPR", "UTR", "CDTL",
            "CELC", "Duke-NUS Medical School", "FASS", "FOD",
            "FOE", "FOL", "FOS", "ISS", "LKYSPP",
            "NGSISE", "SSHSPH", "BIZ", "SOC",
            "SCLE", "SDE", "Cinnamon College", "Yale-NUS College",
            "YLLSM", "YSTCM"
    };

    public PopOutFilter(tasks t) {
        this.t = t;
    }

    public PopOutFilter(tasks t, FilterPref fp) {
        this.t = t;
        this.fp = fp;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_tasks_filter, null);


        autoCompleteTextView = view.findViewById(R.id.autoLocation);
        ArrayAdapter<String> a = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_list_item_1, locations);
        autoCompleteTextView.setAdapter(a);

        spinnerType = view.findViewById(R.id.spinner3);
        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this.getContext(), R.array.TypeFilter,
                android.R.layout.simple_spinner_item);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter3);

        spinnerTime = view.findViewById(R.id.spinner4);
        ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(this.getContext(), R.array.Deadline,
                android.R.layout.simple_spinner_item);
        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTime.setAdapter(adapter4);


        rangeSlider = view.findViewById(R.id.seekBar);
        remote = view.findViewById(R.id.remote);
        if (this.fp != null) {
            setData(fp);
        }
        rangeSlider.setLabelFormatter(new LabelFormatter() {
            @NonNull
            @NotNull
            @Override
            public String getFormattedValue(float value) {
                NumberFormat format = NumberFormat.getCurrencyInstance();
                format.setCurrency(Currency.getInstance("SGD"));
                return format.format(value);
            }
        });
        rangeSlider.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                return false;
            }
        });
        Context that = this.getContext();
        remote.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    autoCompleteTextView.setText("");
                    autoCompleteTextView.setFocusable(false);
                } else {
                    autoCompleteTextView.setFocusableInTouchMode(true);

                    autoCompleteTextView.setFocusable(true);
                }
            }
        });

        builder.setView(view)
                .setTitle("Filter")
                .setNegativeButton("Show All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        List<Float> f = new ArrayList<>();
                        f.add((float)0);
                        f.add((float)500);
                        filterDialogListener.applyTexts("All Locations",
                                "All Types", f, 0);
                        t.savePreference(null);

                    }
                })
                .setPositiveButton("Apply", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String location;
                        if (remote.isChecked()) {
                            location = "Remote";
                        } else {
                            location = autoCompleteTextView.getText().toString().equals("") ?
                            "All Locations"
                            : autoCompleteTextView.getText().toString();
                        }
                        String type = spinnerType.getSelectedItem().toString();
                        int deadline = spinnerTime.getSelectedItemPosition();
                        List<Float> price = rangeSlider.getValues();
                        Toast.makeText(getContext(), location, Toast.LENGTH_SHORT).show();
                        filterDialogListener.applyTexts(location, type, price, deadline);
                        t.savePreference(new FilterPref(location,
                                spinnerType.getSelectedItemPosition(), deadline, price));
                    }
                });

        return builder.create();
    }

    private void setData(FilterPref fp) {
        if (fp.getLoc() == "Remote") {
            remote.setChecked(true);
            autoCompleteTextView.setFocusable(false);
        } else {
            autoCompleteTextView.setText(fp.getLoc());
        }
        spinnerType.setSelection(fp.getType());
        spinnerTime.setSelection(fp.getDdl());
        rangeSlider.setValues(fp.getPrice());
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);

        try {
            filterDialogListener = (FilterDialogListener) context;
        } catch (ClassCastException e) {
            throw
                    new ClassCastException(context.toString() +
                            "must implement FilterDialogListener");
        }


    }


    public interface FilterDialogListener{
        void applyTexts(String location, String taskType, List<Float> priceRange, int deadline);
    }

//    public void saveToCloud(FilterPref fp){
//        firebaseAuth = FirebaseAuth.getInstance();
//        user = firebaseAuth.getCurrentUser();
//        FirebaseDatabase rtNode = FirebaseDatabase.
//                getInstance("https://taskrabbits-1621680681859-default-rtdb.asia-southeast1.firebasedatabase.app/");
//        DatabaseReference reference = rtNode.getReference("Users");
//        reference.child(user.getUid()).child("filter preference").setValue(fp);
//    }
    public class FilterPref {
        private String loc;
        private int type;
        private int ddl;
        private List<Float> price;
        public FilterPref(String loc, int type, int ddl, List<Float> price) {
            this.ddl = ddl;
            this.loc = loc;
            this.price = price;
            this.type = type;
        }
        public String getLoc() {
            return loc;
        }

        public int getType() {
            return type;
        }

        public int getDdl() {
            return ddl;
        }

        public List<Float> getPrice() {
            return price;
        }
    }
}
