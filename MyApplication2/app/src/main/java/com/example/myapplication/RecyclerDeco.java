package com.example.myapplication;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class RecyclerDeco extends RecyclerView.ItemDecoration {
    int sidePadding;
    int topPadding;


    public RecyclerDeco(int sidePadding, int topPadding) {
        this.sidePadding = sidePadding;
        this.topPadding = topPadding;

    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (parent.getChildAdapterPosition(view) == parent.getAdapter().getItemCount()-1) {
            outRect.bottom = 10*topPadding;
            outRect.top = topPadding;
        } else if (parent.getChildAdapterPosition(view) == 0) {
            outRect.top = 85*topPadding;
            outRect.bottom = topPadding;
        }  else {
            outRect.top = topPadding;
            outRect.bottom = topPadding;
        }

        outRect.left = sidePadding;
        outRect.right = sidePadding;
    }

}
