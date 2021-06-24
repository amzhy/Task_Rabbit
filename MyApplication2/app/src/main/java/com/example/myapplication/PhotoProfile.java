package com.example.myapplication;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myapplication.databinding.FragmentProfileBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PhotoProfile extends AppCompatActivity {

    public ImageButton photobtn;
    public Uri imageUri;
    public ImageView profilePic;

    private FirebaseAuth auth;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);

        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        profilePic = findViewById(R.id.editPhoto);
        profilePic.setImageResource(R.drawable.greyprof);

        choosePic();
    }

    private void choosePic() {
        Intent i = new Intent();   i.setType("image/*");   i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            profilePic.setImageURI(imageUri);
        }
        uploadPic();
    }

    private void uploadPic() {
        final String key = auth.getUid();
        StorageReference imgRef = storageReference.child("images/" + key + ".jpg");
        FirebaseAuth auth  = FirebaseAuth.getInstance();

        //reference to user
        DatabaseReference ref = FirebaseDatabase.
                getInstance("https://taskrabbits-1621680681859-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference().child("Users").child(auth.getUid());

        //store photo in cloud storage
        imgRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(PhotoProfile.this,
                                "Profile photo updated!", Toast.LENGTH_SHORT).show();

                        String s  = imgRef.getPath();

                        ref.child("photo").setValue(imageUri.toString());
                        profilePic.setImageURI(imageUri);
                        startActivity(new Intent(PhotoProfile.this, MainActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(PhotoProfile.this,
                                "Image upload unsuccessful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(PhotoProfile.this, MainActivity.class));
                        finish();
                    }
                });
    }
}