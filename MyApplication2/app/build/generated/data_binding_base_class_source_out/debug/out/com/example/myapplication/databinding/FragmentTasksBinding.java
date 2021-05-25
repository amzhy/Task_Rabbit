// Generated by view binder compiler. Do not edit!
package com.example.myapplication.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Guideline;
import androidx.viewbinding.ViewBinding;
import com.example.myapplication.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class FragmentTasksBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final Button button2;

  @NonNull
  public final EditText editTextNumberDecimal2;

  @NonNull
  public final EditText editTextTime2;

  @NonNull
  public final Guideline guideline28;

  @NonNull
  public final Guideline guideline29;

  @NonNull
  public final Guideline guideline30;

  @NonNull
  public final Guideline guideline33;

  @NonNull
  public final Guideline guideline34;

  @NonNull
  public final ImageButton imageButton12;

  @NonNull
  public final TextView textView13;

  private FragmentTasksBinding(@NonNull ConstraintLayout rootView, @NonNull Button button2,
      @NonNull EditText editTextNumberDecimal2, @NonNull EditText editTextTime2,
      @NonNull Guideline guideline28, @NonNull Guideline guideline29,
      @NonNull Guideline guideline30, @NonNull Guideline guideline33,
      @NonNull Guideline guideline34, @NonNull ImageButton imageButton12,
      @NonNull TextView textView13) {
    this.rootView = rootView;
    this.button2 = button2;
    this.editTextNumberDecimal2 = editTextNumberDecimal2;
    this.editTextTime2 = editTextTime2;
    this.guideline28 = guideline28;
    this.guideline29 = guideline29;
    this.guideline30 = guideline30;
    this.guideline33 = guideline33;
    this.guideline34 = guideline34;
    this.imageButton12 = imageButton12;
    this.textView13 = textView13;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static FragmentTasksBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FragmentTasksBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.fragment_tasks, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FragmentTasksBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.button2;
      Button button2 = rootView.findViewById(id);
      if (button2 == null) {
        break missingId;
      }

      id = R.id.editTextNumberDecimal2;
      EditText editTextNumberDecimal2 = rootView.findViewById(id);
      if (editTextNumberDecimal2 == null) {
        break missingId;
      }

      id = R.id.editTextTime2;
      EditText editTextTime2 = rootView.findViewById(id);
      if (editTextTime2 == null) {
        break missingId;
      }

      id = R.id.guideline28;
      Guideline guideline28 = rootView.findViewById(id);
      if (guideline28 == null) {
        break missingId;
      }

      id = R.id.guideline29;
      Guideline guideline29 = rootView.findViewById(id);
      if (guideline29 == null) {
        break missingId;
      }

      id = R.id.guideline30;
      Guideline guideline30 = rootView.findViewById(id);
      if (guideline30 == null) {
        break missingId;
      }

      id = R.id.guideline33;
      Guideline guideline33 = rootView.findViewById(id);
      if (guideline33 == null) {
        break missingId;
      }

      id = R.id.guideline34;
      Guideline guideline34 = rootView.findViewById(id);
      if (guideline34 == null) {
        break missingId;
      }

      id = R.id.imageButton12;
      ImageButton imageButton12 = rootView.findViewById(id);
      if (imageButton12 == null) {
        break missingId;
      }

      id = R.id.textView13;
      TextView textView13 = rootView.findViewById(id);
      if (textView13 == null) {
        break missingId;
      }

      return new FragmentTasksBinding((ConstraintLayout) rootView, button2, editTextNumberDecimal2,
          editTextTime2, guideline28, guideline29, guideline30, guideline33, guideline34,
          imageButton12, textView13);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
