package com.example.myapplication;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileView#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileView extends Fragment {

    MaterialTextView nametv, rating;
    ImageView photo;
    RatingBar star;
    TabLayout tab;
    String source, my_profile, user_id;
    boolean setName=false, setPhoto=false;
    ViewPager2 comment;
    ProfileAdapter adapter;

    DatabaseReference ref;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public ProfileView() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileView.
     */

    public static ProfileView newInstance(String param1, String param2) {
        ProfileView fragment = new ProfileView();
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

        setHasOptionsMenu(false);
        user_id = getArguments().getString("user");
        my_profile = getArguments().getString("profile");
        source = getArguments().getString("source");
        //Toast.makeText(getContext(), user_id, Toast.LENGTH_SHORT).show();
        ref = FirebaseDatabase.
                getInstance("https://taskrabbits-1621680681859-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Users").child(user_id);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        photo = getView().findViewById(R.id.viewphoto);
        comment = getView().findViewById(R.id.profile_pager);
        adapter= new ProfileAdapter(getActivity().getSupportFragmentManager(), getLifecycle(), user_id);
        tab = getView().findViewById(R.id.reviewtabs);
        comment.setAdapter(adapter);
        comment.setVerticalScrollBarEnabled(true);
        nametv = getView().findViewById(R.id.viewname);
        star = getView().findViewById(R.id.view_bar);

        if (my_profile != null) {
            getView().findViewById(R.id.card).setBackgroundResource(R.color.testcolor1);
        }

        setImage();
        rating = getView().findViewById(R.id.view_rating);
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

    public void setImage() {
        ref.addValueEventListener(new ValueEventListener() {
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

    public void setUploadPhoto(ImageView iv) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage
                .getReferenceFromUrl("gs://taskrabbits-1621680681859.appspot.com/images/"
                        + user_id + ".jpg");
        storageReference.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if (getActivity() != null) {
                            Glide.with(getContext())
                                    .load(uri)
                                    .apply(new RequestOptions().override(500, 500))
                                    .centerCrop().into(iv);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        iv.setImageResource(R.drawable.greyprof);
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        if (source != null ) {
            ((AppCompatActivity)getActivity()).getSupportActionBar().setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.bg_toolbar));
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
          //  ((AppCompatActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.bg_toolbar));
            //((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

     @Override
    public void onStop() {
        super.onStop();
         ((AppCompatActivity)getActivity()).getSupportActionBar().setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.color.testcolor1));
         ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
         if (source != null) {
             ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
         }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setRating() {
        ref.child("Comment").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                HashMap<String, Object> hashMap = (HashMap<String, Object>)dataSnapshot.getValue();
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
                star.setRating((float)ave);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        setImage();
    }
}
