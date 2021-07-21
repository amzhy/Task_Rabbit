package com.example.myapplication;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder>{
    private Context mContext;
    List<Commenter> commenters;
    private DatabaseReference reference = FirebaseDatabase.getInstance("https://taskrabbits-1621680681859-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users");

    public CommentAdapter(Context mContext, List<Commenter> commenters) {
        this.mContext = mContext;
        this.commenters = commenters;
    }


    @NonNull
    @NotNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.commentprofile, parent, false);
        return new CommentAdapter.CommentViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CommentViewHolder holder, int position) {
        Commenter commenter = commenters.get(position);
        holder.comments.setText(commenter.getComment());
        holder.rating.setText(commenter.getRating()+"");
        getUser(commenter.getId(), holder);
    }

    @Override
    public int getItemCount() {
        return commenters.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView commenterImg;
        TextView name, comments, rating;
        public CommentViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            commenterImg = itemView.findViewById(R.id.commenter_img);
            name = itemView.findViewById(R.id.commenterName);
            comments = itemView.findViewById(R.id.comments);
            rating = itemView.findViewById(R.id.ratingComment);
        }
    }

    private void getUser(String userID, CommentViewHolder holder){
        reference.child(userID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                HashMap<String, Object> hashMap = (HashMap<String, Object>)task.getResult().getValue();
                String name = (String)hashMap.get("name");
                holder.name.setText(name);
                if ((String)hashMap.get("photo") != null) {
                    setUploadPhoto(holder.commenterImg, userID);
                } else {
                    holder.commenterImg.setImageResource(R.drawable.greyprof);
                }
            }
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
