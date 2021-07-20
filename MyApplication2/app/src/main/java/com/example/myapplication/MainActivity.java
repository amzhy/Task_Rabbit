package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
    private FirebaseDatabase db;
    private DatabaseReference reference;
    private FirebaseAuth auth;
    private BottomNavigationView navigation;
    private BadgeDrawable inboxNot;


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
        db = FirebaseDatabase.getInstance("https://taskrabbits-1621680681859-default-rtdb.asia-southeast1.firebasedatabase.app/");


        manageConnections();
        navigation = (BottomNavigationView) findViewById(R.id.bottomNavigationView);

        navigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
        inboxNot = navigation.getOrCreateBadge(R.id.navigation_inbox);
        inboxNot.setVisible(false);
        ((TabbedInbox)fragment3).setView(inboxNot);

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

    private void manageConnections(){
        final DatabaseReference infoConnected = db.getReference(".info/connected");
        final DatabaseReference lastOnlineRef = db.getReference("/Users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/lastOnline");
        final DatabaseReference myConnectionsRef = db.getReference("Users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/connections");

        infoConnected.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);

                if (connected) {
                    // When this device disconnects, remove it
                    myConnectionsRef.onDisconnect().setValue(false);
                    lastOnlineRef.onDisconnect().setValue(ServerValue.TIMESTAMP);
                    myConnectionsRef.setValue(true);
                } else {
                    myConnectionsRef.setValue(false);
                    lastOnlineRef.setValue(ServerValue.TIMESTAMP);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                System.out.println("Error" + error);

            }
        });
    }
//backpress to logout but will refresh inbox
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        final DatabaseReference infoConnected = db.getReference(".info/connected");
//        final DatabaseReference lastOnlineRef = db.getReference("/Users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/lastOnline");
//        final DatabaseReference myConnectionsRef = db.getReference("Users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/connections");
//
//        infoConnected.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
//                boolean connected = snapshot.getValue(Boolean.class);
//
//                if (connected) {
//                    myConnectionsRef.setValue(false);
//                    lastOnlineRef.setValue(ServerValue.TIMESTAMP);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull @NotNull DatabaseError error) {
//                System.out.println("Error" + error);
//
//            }
//        });
//
//    }
    public interface OnIntegerChangeListener
    {
        public void onIntegerChanged(int newValue);
    }

    public class ObservableInteger
    {
        private OnIntegerChangeListener listener;

        private int value = 0;

        public void setOnIntegerChangeListener(OnIntegerChangeListener listener)
        {
            this.listener = listener;
        }

        public int get()
        {
            return value;
        }

        public void set(int value)
        {
            this.value = value;

            if(listener != null)
            {
                listener.onIntegerChanged(value);
            }
        }

        public void add(int i) {
            this.value+=i;
            if(listener != null)
            {
                listener.onIntegerChanged(value);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            //Title bar back press triggers onBackPressed()
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@Nullable @org.jetbrains.annotations.Nullable View parent, @NonNull @NotNull String name, @NonNull @NotNull Context context, @NonNull @NotNull AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
    }
}

