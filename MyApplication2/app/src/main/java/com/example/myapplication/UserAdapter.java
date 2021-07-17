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
    private DatabaseReference userDb = FirebaseDatabase.
            getInstance("https://taskrabbits-1621680681859-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("Users");

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
        setUser(holder, newBox);

        String chatter = newBox.getSenderID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())
        ? newBox.getReceiverID() : newBox.getSenderID();

        userDb.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                Boolean status = (Boolean) ((HashMap<String, Object>)task.getResult().child(chatter).getValue()).get("connections");

                if (status != null && status) {
                    holder.img_on.setVisibility(View.VISIBLE);
                    holder.img_off.setVisibility(View.GONE);
                } else {
                    holder.img_on.setVisibility(View.GONE);
                    holder.img_off.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mBox.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView title, username, status;
        public ImageView profile_image, img_on, img_off;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.chat_item_title);
            username = itemView.findViewById(R.id.chat_item_username);
            profile_image = itemView.findViewById(R.id.chat_item_profile_image);
            status = itemView.findViewById(R.id.chat_item_status);
            img_off = itemView.findViewById(R.id.img_off);
            img_on = itemView.findViewById(R.id.img_on);
        }
    }

    public void setUser(UserAdapter.ViewHolder holder, ChatBox box) {
        String taskID = box.getTaskID();
        String receiver = box.getReceiverID(); String sender = box.getSenderID();
        String myID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("Tasks").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot snapshot : task.getResult()) {
                            HashMap<String, String> taskStored = (HashMap<String, String>) snapshot.getData().get(snapshot.getId());
                            if (taskStored.get("taskId").equals(taskID)) {

                                if (taskStored.get("tag").equals("-1")) {
                                    if (taskStored.get("userId").equals(myID)) {
                                        holder.status.setText("YOU HAVE NOT ACCEPTED A TASKER YET");
                                    } else {
                                        holder.status.setText("TASKER HAS NOT BEEN ASSIGNED YET");
                                    }
                                } else if (taskStored.get("tag").equals("0")) {
                                    if (taskStored.get("taskerId").equals(myID)) {
                                        holder.status.setText("YOU ARE ASSIGNED THIS TASK");
                                    } else if (taskStored.get("userId").equals(myID) && sender.equals(taskStored.get("taskerId"))) {
                                        holder.status.setText("TASKER IS COMPLETING YOUR TASK");
                                    }
                                } else if (taskStored.get("tag").equals("1")) {
                                    if (taskStored.get("taskerId").equals(myID)) {
                                        holder.status.setText("YOU HAVE COMPLETED THIS TASK");
                                    } else if (taskStored.get("userId").equals(myID) && sender.equals(taskStored.get("taskerId"))) {
                                        holder.status.setText("YOUR TASK WAS COMPLETED BY THIS TASKER");
                                    }
                                }
                                holder.title.setText(taskStored.get("title"));
                                String image = receiver.equals(myID) ? sender : receiver;
                                setImage(holder, image);
                                userDb.child(image).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                        String name = snapshot.getValue(String.class);
                                        holder.username.setText(name);
                                    }
                                    @Override
                                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                                        holder.username.setText("default username");
                                    }
                                });
                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
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
}
