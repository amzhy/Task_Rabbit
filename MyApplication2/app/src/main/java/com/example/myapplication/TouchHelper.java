package com.example.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.kevincodes.recyclerview.ItemDecorator;

import org.jetbrains.annotations.NotNull;

public class TouchHelper extends ItemTouchHelper.SimpleCallback {
    private MyAdapter adapter;
    private Drawable bg, deleteIcon;
    private int deleteIconMargin;
    private boolean initiated;
    private Context context;

    public TouchHelper(MyAdapter adapter, Context context) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.adapter = adapter;
        this.context = context;
    }

    @Override
    public boolean onMove(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull
            RecyclerView.ViewHolder viewHolder, @NonNull @NotNull RecyclerView.ViewHolder target) {
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

        if (viewHolder.getAdapterPosition() == -1) return;
        new ItemDecorator.Builder(c, recyclerView, viewHolder, dX, actionState)
                .setDefaultIconTintColor(ContextCompat.getColor(context, R.color.white))
                .setDefaultTypeFace(Typeface.SANS_SERIF)
                .setDefaultTextSize(1, 18)
                .setDefaultTextColor(ContextCompat.getColor(context, R.color.white))
                .setFromStartToEndIcon(R.drawable.ic_baseline_delete_24)
                .setFromEndToStartIcon(R.drawable.ic_baseline_edit_24)
                .setFromStartToEndText("Delete")
                .setFromEndToStartText("Edit")
                .setFromStartToEndBgColor(Color.parseColor("#d7011d"))
                .setFromEndToStartBgColor(Color.parseColor("#4d934d"))
                .create()
                .decorate();
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}










