// Generated by view binder compiler. Do not edit!
package com.example.myapplication.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Guideline;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import com.example.myapplication.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class FragmentMyTasksBinding implements ViewBinding {
  @NonNull
  private final FrameLayout rootView;

  @NonNull
  public final Button button11;

  @NonNull
  public final Guideline guideline52;

  @NonNull
  public final RecyclerView items;

  private FragmentMyTasksBinding(@NonNull FrameLayout rootView, @NonNull Button button11,
      @NonNull Guideline guideline52, @NonNull RecyclerView items) {
    this.rootView = rootView;
    this.button11 = button11;
    this.guideline52 = guideline52;
    this.items = items;
  }

  @Override
  @NonNull
  public FrameLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static FragmentMyTasksBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FragmentMyTasksBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.fragment_my_tasks, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FragmentMyTasksBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.button11;
      Button button11 = rootView.findViewById(id);
      if (button11 == null) {
        break missingId;
      }

      id = R.id.guideline52;
      Guideline guideline52 = rootView.findViewById(id);
      if (guideline52 == null) {
        break missingId;
      }

      id = R.id.items;
      RecyclerView items = rootView.findViewById(id);
      if (items == null) {
        break missingId;
      }

      return new FragmentMyTasksBinding((FrameLayout) rootView, button11, guideline52, items);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
