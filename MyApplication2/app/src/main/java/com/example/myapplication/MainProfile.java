package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainProfile#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class MainProfile extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Fragment fragment1, fragment2, fragment3, fragment4, active;
    private FragmentManager fragmentManager;
    private int commited = 1;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

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

    public MainProfile() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        setHasOptionsMenu(true);
        fragment1 = new ProfileFragment();
        fragment2 = new about_us();
        fragment3 = new user_guide();
        fragment4 = new SettingsFragment();
        fragmentManager = getFragmentManager();
        active = fragment1;

        fragmentManager.beginTransaction().add(R.id.fragmentContainerView2, fragment4, "4").hide(fragment4).commit();
        fragmentManager.beginTransaction().add(R.id.fragmentContainerView2, fragment3, "3").hide(fragment3).commit();
        fragmentManager.beginTransaction().add(R.id.fragmentContainerView2, fragment2, "2").hide(fragment2).commit();
        fragmentManager.beginTransaction().add(R.id.fragmentContainerView2, fragment1, "1").commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_main_profile, container, false);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile_upload_photo: {
                Intent i = new Intent();   i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                active=fragment1;
                fragment1.startActivityForResult(i, 1);
                return true;
            } case R.id.settings_notifications: {
                ((SettingsFragment) fragment4).refresh();
                fragmentManager.beginTransaction().hide(active).show(fragment4).commit();
                active = fragment4;
                commited = 4;
                getActivity().setTitle("Settings");
                return true;
            } case R.id.settings_guide:{
                fragmentManager.beginTransaction().hide(active).show(fragment3).commit();
                active = fragment3;
                commited = 3;
                getActivity().setTitle("User Guide");
                return true;
            }
            case R.id.settings_about:{
                fragmentManager.beginTransaction().hide(active).show(fragment2).commit();
                active = fragment2;
                commited = 2;
                getActivity().setTitle("About Us");
                return true;
            }
            case R.id.settings_profile:{
                fragmentManager.beginTransaction().hide(active).show(fragment1).commit();
                active = fragment1;
                commited = 1;
                getActivity().setTitle("Profile");
                return  true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    public int showCommit(){
        return this.commited;
    }

    public void reset(){
        fragmentManager.beginTransaction().hide(active).show(fragment1).commit();
        active = fragment1;
        commited = 1;
        getActivity().setTitle("Profile");
    }

    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        MenuInflater inflater1 = getActivity().getMenuInflater();
        inflater1.inflate(R.menu.profile_menu, menu);
        if (active != fragment1) {
            menu.findItem(R.id.profile_upload_photo).setEnabled(false);
            menu.findItem(R.id.profile_upload_photo).setVisible(false);
        }
        super.onCreateOptionsMenu(menu, inflater1);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull @NotNull Menu menu) {
        if (menu.findItem(R.id.mytasks_delete) != null) {
            menu.removeItem(R.id.mytasks_delete);
        } if (menu.findItem(R.id.mytasks_add) != null) {
            menu.removeItem(R.id.mytasks_add);
        }
        super.onPrepareOptionsMenu(menu);
    }
}