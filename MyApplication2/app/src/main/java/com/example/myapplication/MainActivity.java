package com.example.myapplication;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import static java.lang.String.valueOf;

public class MainActivity extends AppCompatActivity implements PopOutFilter.FilterDialogListener {
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseAuth auth;


    final Fragment fragment1 = new tasks();
    final Fragment fragment2 = new MainTasks();
//    final Fragment fragment3 = new inbox();
    final Fragment fragment3 = new TabbedInbox();
    final Fragment fragment4 = new MainProfile();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = fragment1;

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(Html.fromHtml("<font color='#ffffff'>"+title+"</font>"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        navigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);

        fm.beginTransaction().add(R.id.fragmentContainerView, fragment4, "4").hide(fragment4).commit();
        fm.beginTransaction().add(R.id.fragmentContainerView, fragment3, "3").hide(fragment3).commit();
        fm.beginTransaction().add(R.id.fragmentContainerView, fragment2, "2").hide(fragment2).commit();
        fm.beginTransaction().add(R.id.fragmentContainerView, fragment1, "1").commit();
        setTitle(""); //change name of home to nothing


//        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
//        NavController navController = Navigation.findNavController(this, R.id.fragmentContainerView);
//        AppBarConfiguration configuration = new AppBarConfiguration.Builder(bottomNavigationView.getMenu()).build();
//
//        NavigationUI.setupActionBarWithNavController(this, navController, configuration);
//        NavigationUI.setupWithNavController(navigation, navController);


}
    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fm.beginTransaction().hide(active).show(fragment1).commit();
                    active = fragment1;
                    setTitle(""); //change heading of home to nothing
                    return true;
                case R.id.navigation_tasks:
                    fm.beginTransaction().hide(active).show(fragment2).commit();
                    active = fragment2;
                    setTitle("Tasks");
                    return true;

                case R.id.navigation_inbox:
                    fm.beginTransaction().hide(active).show(fragment3).commit();
                    active = fragment3;
                    setTitle("Inbox");
                    return true;

                case R.id.navigation_profile:
                    fm.beginTransaction().hide(active).show(fragment4).commit();
                    active = fragment4;
                    ((MainProfile)fragment4).reset();
                    return true;
            }
            return false;
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void applyTexts(String location, String taskType, List<Float> priceRange, int deadline) {
        ((tasks)fragment1).filter(location, taskType, priceRange, deadline);
    }
}

