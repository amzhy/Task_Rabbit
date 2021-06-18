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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.kevincodes.recyclerview.ItemDecorator;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TouchHelper extends ItemTouchHelper.SimpleCallback {
    private MyAdapter adapter;
    private RecyclerView v;
    private Context context;
    private List<NewTask> tasks;
    private NewTask deleted = null;

    public TouchHelper(MyAdapter adapter, Context context, RecyclerView v, List<NewTask> tasks) {
        //super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        super(0, ItemTouchHelper.LEFT);
        this.adapter = adapter;
        this.context = context;
        this.v = v;
        this.tasks = tasks;
    }

    @Override
    public boolean onMove(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull
            RecyclerView.ViewHolder viewHolder, @NonNull @NotNull RecyclerView.ViewHolder target) {
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int direction) {
        final int position = viewHolder.getAdapterPosition();
        if (direction == ItemTouchHelper.LEFT) {
            adapter.updateData(position);
            adapter.notifyDataSetChanged();
        } else {
            deleted = tasks.get(position);
            adapter.deleteItem(deleted.getTaskId());
            //undo for delete
            Snackbar undo = Snackbar.make(v, deleted.getTitle() + " deleted", Snackbar.LENGTH_LONG)
                    .setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            adapter.restoreData(position, deleted);
                        }
                    });
            View snackbarLayout = undo.getView();
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 1130, 0, 0);
            snackbarLayout.setLayoutParams(lp);
            snackbarLayout.setElevation(100);
            snackbarLayout.setMinimumHeight(90);
            undo.show();
        }
    }

    @Override
    public void onChildDraw(@NonNull @NotNull Canvas c, @NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull
            RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

        if (viewHolder.getAdapterPosition() == -1) return;
        new ItemDecorator.Builder(c, recyclerView, viewHolder, dX, actionState)
                /*
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
                */
                .create()
                .decorate();
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}