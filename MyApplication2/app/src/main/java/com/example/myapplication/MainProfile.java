package com.example.myapplication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.SupportErrorDialogFragment;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainProfile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainProfile extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Fragment fragment1 = new ProfileView();
    Fragment fragment2 = new SettingsFragment();
    FragmentTransaction to_settings;
    Fragment active;
    Bundle b = new Bundle();

    public MainProfile() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainProfile.
     */
    // TODO: Rename and change types and number of parameters
    public static MainProfile newInstance(String param1, String param2) {
        MainProfile fragment = new MainProfile();
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
        setHasOptionsMenu(true);
        b.putString("user", FirebaseAuth.getInstance().getUid());
        b.putString("profile", FirebaseAuth.getInstance().getUid());
        fragment1.setArguments(b);
        active = fragment1;
        getParentFragmentManager().beginTransaction().add(R.id.fragmentContainerView2, fragment1, "pr").commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_profile, container, false);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings_notifications: {
                to_settings = getParentFragmentManager().beginTransaction().add(R.id.fragmentContainerView2, fragment2).addToBackStack(null);
                to_settings.commit();
                active = fragment2;
                item.setVisible(false);
                return true;
            } default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    public void reset(){
        //getParentFragmentManager().beginTransaction().hide(active).show(fragment1).commit();
        fragment1 = new ProfileView();
        b = new Bundle();
        b.putString("user", FirebaseAuth.getInstance().getUid());
        b.putString("profile", FirebaseAuth.getInstance().getUid());
        fragment1.setArguments(b);
        active = fragment1;
        //((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        //getActivity().setTitle("Profile");
    }

    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        MenuInflater inflater1 = getActivity().getMenuInflater();
        inflater1.inflate(R.menu.profile_menu, menu);
        menu.findItem(R.id.profile_upload_photo).setEnabled(false);
        menu.findItem(R.id.profile_upload_photo).setVisible(false);
        super.onCreateOptionsMenu(menu, inflater1);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull @NotNull Menu menu) {
        if (menu.findItem(R.id.mytasks_delete) != null) {
            menu.removeItem(R.id.mytasks_delete);
        } if (menu.findItem(R.id.mytasks_add) != null) {
            menu.removeItem(R.id.mytasks_add);
        } if (menu.findItem(R.id.inbox_delete)!=null) {
            menu.removeItem(R.id.inbox_delete);
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getActivity().findViewById(R.id.bottomNavigationView).setVisibility(View.VISIBLE);
        //Toast.makeText(getContext(), "resume", Toast.LENGTH_LONG).show();
        //reset();
       // ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Profile");
    }

    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        //reset();
        //((AppCompatActivity)getActivity()).getSupportActionBar().hide();
    }

}