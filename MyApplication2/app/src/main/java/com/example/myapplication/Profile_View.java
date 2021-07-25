package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class Profile_View extends AppCompatActivity {


    MaterialTextView nametv, rating;
    ImageView photo;
    RatingBar star;
    TabLayout tab;
    String source, my_profile, user_id;
    boolean setName=false, setPhoto=false;
    ViewPager2 comment;
    ProfileAdapter adapter;

    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);

        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        user_id = getIntent().getStringExtra("user");
        //my_profile = getIntent().getStringExtra("profile");
        source = getIntent().getStringExtra("source");

        //Toast.makeText(getContext(), user_id, Toast.LENGTH_SHORT).show();
        ref = FirebaseDatabase.
                getInstance("https://taskrabbits-1621680681859-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Users").child(user_id);


        photo = findViewById(R.id.viewphot);
        comment = findViewById(R.id.profile_page);
        adapter= new ProfileAdapter(getSupportFragmentManager(), getLifecycle(), user_id);
        tab = findViewById(R.id.reviewtab);
        comment.setAdapter(adapter);
        comment.setVerticalScrollBarEnabled(true);
        nametv = findViewById(R.id.viewnam);
        star = findViewById(R.id.view_ba);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bg_toolbar));


        setImage();
        rating = findViewById(R.id.view_ratin);
        setRating();
        tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                comment.setCurrentItem(tab.getPosition(), true);
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
        comment.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tab.selectTab(tab.getTabAt(position));
            }
        });
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@Nullable @org.jetbrains.annotations.Nullable View parent, @NonNull @NotNull String name, @NonNull @NotNull Context context, @NonNull @NotNull AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
    }

    private void setImage() {
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                nametv.setText("@" + snapshot.child("name").getValue(String.class));
                if (snapshot.hasChild("photo")) {
                    setUploadPhoto(photo);
                } else { photo.setImageResource(R.drawable.greyprof); }
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) { }
        });
    }

    private void setUploadPhoto(ImageView iv) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage
                .getReferenceFromUrl("gs://taskrabbits-1621680681859.appspot.com/images/"
                        + user_id + ".jpg");
        storageReference.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getApplicationContext())
                                .load(uri)
                                .apply(new RequestOptions().override(500, 500))
                                .centerCrop().into(iv);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        iv.setImageResource(R.drawable.greyprof);
                    }
                });
    }

    private void setRating(){
        ref.child("Comment").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                HashMap<String, Object> hashMap = (HashMap<String, Object>)task.getResult().getValue();
                if (hashMap== null) {
                    return;
                }
                HashMap<String, Object> hashPub = (HashMap<String, Object>) hashMap.get("AsPublisher");
                HashMap<String, Object> hashTasker = (HashMap<String, Object>) hashMap.get("AsTasker");
                double total=0;
                int num = 0;
                double ave = 0;
                if (hashPub != null) {
                    for(Object o: hashPub.values()){
                        HashMap<String, Object> hashPubItem = (HashMap<String, Object>) o;
                        total+=(long)hashPubItem.get("rating");
                        num+=1;
                    }
                }
                if (hashTasker != null) {
                    for(Object o: hashTasker.values()){
                        HashMap<String, Object> hashTaskerItem = (HashMap<String, Object>) o;
                        total+=(long)hashTaskerItem.get("rating");
                        num+=1;
                    }
                }
                if (num != 0) {
                    ave = Math.round(total/num * 100.0) / 100.0;
                }
                rating.setText(ave+" ("+num+")");
                star.setNumStars((int)ave);
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getFragmentManager().popBackStack();
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            //overridePendingTransition(android.R.anim.slide_in, android.R.anim.slide_out_right);
        }
        return super.onOptionsItemSelected(item);
    }
}