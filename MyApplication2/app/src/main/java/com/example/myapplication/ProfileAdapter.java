package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import org.jetbrains.annotations.NotNull;

public class ProfileAdapter extends FragmentStateAdapter {
    String user;

    public ProfileAdapter(@NonNull @NotNull FragmentManager fragmentManager, @NonNull @NotNull Lifecycle lifecycle, String user) {
        super(fragmentManager, lifecycle);
        this.user = user;
    }

    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new TaskerComment(user);
        } else {
            return new PublisherComment(user);
        }

    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
