// Generated by view binder compiler. Do not edit!
package com.example.myapplication.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.viewbinding.ViewBinding;
import com.example.myapplication.R;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class NewTaskCardBinding implements ViewBinding {
  @NonNull
  private final RelativeLayout rootView;

  @NonNull
  public final TextView price;

  @NonNull
  public final TextView taskLocation;

  @NonNull
  public final LinearProgressIndicator taskProgressBar;

  @NonNull
  public final AppCompatButton taskTag;

  @NonNull
  public final TextView tasktitle;

  @NonNull
  public final TextView time;

  private NewTaskCardBinding(@NonNull RelativeLayout rootView, @NonNull TextView price,
      @NonNull TextView taskLocation, @NonNull LinearProgressIndicator taskProgressBar,
      @NonNull AppCompatButton taskTag, @NonNull TextView tasktitle, @NonNull TextView time) {
    this.rootView = rootView;
    this.price = price;
    this.taskLocation = taskLocation;
    this.taskProgressBar = taskProgressBar;
    this.taskTag = taskTag;
    this.tasktitle = tasktitle;
    this.time = time;
  }

  @Override
  @NonNull
  public RelativeLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static NewTaskCardBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static NewTaskCardBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.new_task_card, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static NewTaskCardBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.price;
      TextView price = rootView.findViewById(id);
      if (price == null) {
        break missingId;
      }

      id = R.id.taskLocation;
      TextView taskLocation = rootView.findViewById(id);
      if (taskLocation == null) {
        break missingId;
      }

      id = R.id.taskProgressBar;
      LinearProgressIndicator taskProgressBar = rootView.findViewById(id);
      if (taskProgressBar == null) {
        break missingId;
      }

      id = R.id.taskTag;
      AppCompatButton taskTag = rootView.findViewById(id);
      if (taskTag == null) {
        break missingId;
      }

      id = R.id.tasktitle;
      TextView tasktitle = rootView.findViewById(id);
      if (tasktitle == null) {
        break missingId;
      }

      id = R.id.time;
      TextView time = rootView.findViewById(id);
      if (time == null) {
        break missingId;
      }

      return new NewTaskCardBinding((RelativeLayout) rootView, price, taskLocation, taskProgressBar,
          taskTag, tasktitle, time);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
