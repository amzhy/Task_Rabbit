package com.example.myapplication;

import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

public class TouchHelper extends ItemTouchHelper.SimpleCallback {
    private MyAdapter adapter;

    public TouchHelper(MyAdapter adapter) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.adapter = adapter;
    }

    @Override
    public boolean onMove(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, @NonNull @NotNull RecyclerView.ViewHolder target) {
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int direction) {
        String deleted = null;
        final int position = viewHolder.getAdapterPosition();
        if (direction == ItemTouchHelper.LEFT) {
            adapter.updateData(position);
            adapter.notifyDataSetChanged();
        } else {
            adapter.deleteData(position);
        }
    }

    @Override
    public void onChildDraw(@NonNull @NotNull Canvas c, @NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull
            RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {


        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}










