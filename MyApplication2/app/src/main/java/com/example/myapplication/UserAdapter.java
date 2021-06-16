package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContext;
    private List<ChatBox> mBox;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public UserAdapter(Context mContext, List<ChatBox> mBox) {
        this.mContext = mContext;
        this.mBox = mBox;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item, parent, false);

        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        ChatBox newBox = mBox.get(position);

        //for each newBox, produce a new layout
        //task is to set username and profile_image
        setUser(holder, newBox);

    }

    @Override
    public int getItemCount() {
        return mBox.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView username;
        public ImageView profile_image;

        public ViewHolder(View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.chat_item_username);
            profile_image = itemView.findViewById(R.id.chat_item_profile_image);


        }
    }

    private void setImage(UserAdapter.ViewHolder holder, String userid) {
        DatabaseReference imgRef = FirebaseDatabase.getInstance("https://taskrabb" +
                "its-1621680681859-default-rtdb.asia-southeast1.firebasedatabase.app/").
                getReference("Users").child(userid);
        imgRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.hasChild("photo")) {
                    setUploadPhoto(holder.profile_image, userid);
                } else {
                    holder.profile_image.setImageResource(R.drawable.greyprof);
                }
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) { }
        });
    }

    private void setUploadPhoto(ImageView iv, String userid) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage
                .getReferenceFromUrl("gs://taskrabbits-1621680681859.appspot.com/images/"
                        + userid + ".jpg");

        storageReference.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if (this != null) {
                            Glide.with(mContext)
                                    .load(uri)
                                    .apply(new RequestOptions().override(500, 500))
                                    .centerCrop().into(iv);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        iv.setImageResource(R.drawable.greyprof);
                    }
                });
    }

    public void setUser(UserAdapter.ViewHolder holder,ChatBox box) {
        String taskID = box.getTaskID();
        db.collection("Tasks").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot snapshot : task.getResult()) {
                            HashMap<String, String> taskStored = (HashMap<String, String>) snapshot.getData().get(snapshot.getId());
                            if (taskStored.get("taskId").equals(taskID)) {
                                holder.username.setText(taskStored.get("title"));
                                    //set image view from userid to profile_image
                                setImage(holder, taskStored.get("userId"));

                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String myID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                        Intent i = new Intent(mContext, MessageActivity.class);

                                        i.putExtra("userID",
                                                !taskStored.get("userId").equals(myID)
                                                        ? taskStored.get("userId")
                                                        : box.getSenderID().equals(myID)
                                                            ? box.getReceiverID()
                                                            : box.getSenderID());
                                        i.putExtra("taskID", taskStored.get("taskId"));
                                        i.putExtra("taskTitle", taskStored.get("title"));
                                        mContext.startActivity(i);
                                    }
                                });
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(mContext.getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT);
            }
        });


    }
}
