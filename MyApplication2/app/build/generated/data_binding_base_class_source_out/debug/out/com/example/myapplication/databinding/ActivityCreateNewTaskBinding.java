// Generated by view binder compiler. Do not edit!
package com.example.myapplication.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import com.example.myapplication.R;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityCreateNewTaskBinding implements ViewBinding {
  @NonNull
  private final ScrollView rootView;

  @NonNull
  public final TextInputEditText TaskDetails;

  @NonNull
  public final AutoCompleteTextView addLocation;

  @NonNull
  public final LinearLayout container;

  @NonNull
  public final Switch createRemote;

  @NonNull
  public final TextInputLayout editDate;

  @NonNull
  public final TextInputLayout editPrice;

  @NonNull
  public final TextInputLayout editTaskDetails;

  @NonNull
  public final Button editTaskSavebtn;

  @NonNull
  public final TextInputLayout editTaskTitle;

  @NonNull
  public final EditText editTextDate;

  @NonNull
  public final EditText editTextTime;

  @NonNull
  public final TextInputLayout editTime;

  @NonNull
  public final MaterialAutoCompleteTextView outlinedExposedDropdownEditableCategory;

  @NonNull
  public final EditText price;

  @NonNull
  public final TextInputLayout surroundLocation;

  private ActivityCreateNewTaskBinding(@NonNull ScrollView rootView,
      @NonNull TextInputEditText TaskDetails, @NonNull AutoCompleteTextView addLocation,
      @NonNull LinearLayout container, @NonNull Switch createRemote,
      @NonNull TextInputLayout editDate, @NonNull TextInputLayout editPrice,
      @NonNull TextInputLayout editTaskDetails, @NonNull Button editTaskSavebtn,
      @NonNull TextInputLayout editTaskTitle, @NonNull EditText editTextDate,
      @NonNull EditText editTextTime, @NonNull TextInputLayout editTime,
      @NonNull MaterialAutoCompleteTextView outlinedExposedDropdownEditableCategory,
      @NonNull EditText price, @NonNull TextInputLayout surroundLocation) {
    this.rootView = rootView;
    this.TaskDetails = TaskDetails;
    this.addLocation = addLocation;
    this.container = container;
    this.createRemote = createRemote;
    this.editDate = editDate;
    this.editPrice = editPrice;
    this.editTaskDetails = editTaskDetails;
    this.editTaskSavebtn = editTaskSavebtn;
    this.editTaskTitle = editTaskTitle;
    this.editTextDate = editTextDate;
    this.editTextTime = editTextTime;
    this.editTime = editTime;
    this.outlinedExposedDropdownEditableCategory = outlinedExposedDropdownEditableCategory;
    this.price = price;
    this.surroundLocation = surroundLocation;
  }

  @Override
  @NonNull
  public ScrollView getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityCreateNewTaskBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityCreateNewTaskBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_create_new_task, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityCreateNewTaskBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.TaskDetails;
      TextInputEditText TaskDetails = rootView.findViewById(id);
      if (TaskDetails == null) {
        break missingId;
      }

      id = R.id.addLocation;
      AutoCompleteTextView addLocation = rootView.findViewById(id);
      if (addLocation == null) {
        break missingId;
      }

      id = R.id.container;
      LinearLayout container = rootView.findViewById(id);
      if (container == null) {
        break missingId;
      }

      id = R.id.createRemote;
      Switch createRemote = rootView.findViewById(id);
      if (createRemote == null) {
        break missingId;
      }

      id = R.id.editDate;
      TextInputLayout editDate = rootView.findViewById(id);
      if (editDate == null) {
        break missingId;
      }

      id = R.id.editPrice;
      TextInputLayout editPrice = rootView.findViewById(id);
      if (editPrice == null) {
        break missingId;
      }

      id = R.id.editTaskDetails;
      TextInputLayout editTaskDetails = rootView.findViewById(id);
      if (editTaskDetails == null) {
        break missingId;
      }

      id = R.id.editTaskSavebtn;
      Button editTaskSavebtn = rootView.findViewById(id);
      if (editTaskSavebtn == null) {
        break missingId;
      }

      id = R.id.editTaskTitle;
      TextInputLayout editTaskTitle = rootView.findViewById(id);
      if (editTaskTitle == null) {
        break missingId;
      }

      id = R.id.editTextDate;
      EditText editTextDate = rootView.findViewById(id);
      if (editTextDate == null) {
        break missingId;
      }

      id = R.id.editTextTime;
      EditText editTextTime = rootView.findViewById(id);
      if (editTextTime == null) {
        break missingId;
      }

      id = R.id.editTime;
      TextInputLayout editTime = rootView.findViewById(id);
      if (editTime == null) {
        break missingId;
      }

      id = R.id.outlined_exposed_dropdown_editable_category;
      MaterialAutoCompleteTextView outlinedExposedDropdownEditableCategory = rootView.findViewById(id);
      if (outlinedExposedDropdownEditableCategory == null) {
        break missingId;
      }

      id = R.id.price;
      EditText price = rootView.findViewById(id);
      if (price == null) {
        break missingId;
      }

      id = R.id.surroundLocation;
      TextInputLayout surroundLocation = rootView.findViewById(id);
      if (surroundLocation == null) {
        break missingId;
      }

      return new ActivityCreateNewTaskBinding((ScrollView) rootView, TaskDetails, addLocation,
          container, createRemote, editDate, editPrice, editTaskDetails, editTaskSavebtn,
          editTaskTitle, editTextDate, editTextTime, editTime,
          outlinedExposedDropdownEditableCategory, price, surroundLocation);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
