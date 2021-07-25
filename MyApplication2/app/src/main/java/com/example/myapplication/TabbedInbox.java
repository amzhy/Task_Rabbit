package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.Toast;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.badge.BadgeUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;

import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TabbedInbox#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TabbedInbox extends Fragment {
    private TabLayout tabLayout;
    ViewPager viewPager;
    private BadgeDrawable b;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TabbedInbox() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TabbedInbox.
     */
    // TODO: Rename and change types and number of parameters
    public static TabbedInbox newInstance(String param1, String param2) {
        TabbedInbox fragment = new TabbedInbox();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tabbed_inbox, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.addFragment();
    }

    @SuppressLint("UnsafeExperimentalUsageError")
    private void addFragment(){
        tabLayout = getActivity().findViewById(R.id.inboxTab);
        viewPager = getActivity().findViewById(R.id.viewPagerInbox);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());

        inbox inbox = new inbox(tabLayout, b);
        InboxTasker inboxTasker = new InboxTasker(tabLayout, b);
        adapter.addFragment(inbox, "As Publisher");
        adapter.addFragment(inboxTasker, "As Tasker");


        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
//        tabLayout.getTabAt(0).getOrCreateBadge().setNumber(3);
//        Toast.makeText(getContext(), tabLayout.getTabAt(0), Toast.LENGTH_SHORT).show();

    }


    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        MenuInflater inflater1 = getActivity().getMenuInflater();
        inflater1.inflate(R.menu.inbox_menu, menu);
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

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        int pos = tabLayout.getSelectedTabPosition();
        String message = "";
        if (pos == 0) {
            message = "Swipe right to delete chat from As Publisher.";
        } else if(pos == 1) {
            message = "Swipe left to delete chat from As Publisher.";
        }
        switch (item.getItemId()) {
            case R.id.inbox_delete: {
                if (message!="") {
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                }
                return true;
            } default:
                return super.onOptionsItemSelected(item);
        }
    }


    //    public int showUnread(){
//        int i1 = 0, i2 = 0;
//        if (this.tabLayout.getTabAt(0).getBadge() != null) {
//            i1 = this.tabLayout.getTabAt(0).getBadge().getNumber();
//        }
//        if (this.tabLayout.getTabAt(1).getBadge() != null) {
//            i2 = this.tabLayout.getTabAt(1).getBadge().getNumber();
//        }
//        return i1+i2;
//
//    }
    public void setView(BadgeDrawable b){
        this.b = b;
    }
//
//    public void setBadge() {
//        int i = showUnread();
//        if (b != null) {
//            if (i == 0) {
//                this.b.setVisible(false);
//            } else {
//                this.b.setNumber(i);
//                this.b.setVisible(true);
//            }
//        }
//
//    }
    public BadgeDrawable getB() {
        return b;
    }

}