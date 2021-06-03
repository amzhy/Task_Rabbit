package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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
 * Use the {@link notifications#newInstance} factory method to
 * create an instance of this fragment.
 */
public class notifications extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public notifications() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment chat_fullscreen.
     */
    // TODO: Rename and change types and number of parameters
    public static notifications newInstance(String param1, String param2) {
        notifications fragment = new notifications();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Notifications");
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }
    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        MenuInflater inflater1 = getActivity().getMenuInflater();
        inflater1.inflate(R.menu.profile_menu, menu);
        menu.findItem(R.id.profile_upload_photo).setVisible(false);
        super.onCreateOptionsMenu(menu, inflater1);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings_guide: {
                FragmentManager fm = getFragmentManager();
                Fragment n = new user_guide();
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.replace(R.id.fragmentContainerView, n);
                transaction.commit();
                return true;
            } case R.id.settings_about: {
                FragmentManager fm = getFragmentManager();
                Fragment n = new about_us();
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.replace(R.id.fragmentContainerView, n);
                transaction.commit();
                return true;
            } case R.id.settings_notifications: {
                FragmentManager fm = getFragmentManager();
                Fragment n = new notifications();
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.replace(R.id.fragmentContainerView, n);
                transaction.commit();
                return true;
            } default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

}