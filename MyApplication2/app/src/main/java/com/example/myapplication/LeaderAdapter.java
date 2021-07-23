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
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

public class LeaderAdapter extends RecyclerView.Adapter<LeaderAdapter.ViewHolder>{

    private Context mContext;
    List<Leader> leaders;
    private DatabaseReference reference = FirebaseDatabase.getInstance("https://taskrabbits-1621680681859-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users");

    public LeaderAdapter(Context mContext, List<Leader> leaders) {
        this.mContext = mContext;
        this.leaders = leaders;
    }

    @NonNull
    @NotNull
    @Override
    public LeaderAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.leader, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull LeaderAdapter.ViewHolder holder, int position) {
        Leader leader = leaders.get(position);
        holder.name.setText(leader.getName());
        holder.rank.setText(position+1+"");
        if (leader.getPhoto()!=null) {
            setUploadPhoto(holder.leader_img, leader.getUserID());
        } else { holder.leader_img.setImageResource(R.drawable.greyprof); }
    }

    @Override
    public int getItemCount() {
        return leaders.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView leader_img;
        TextView name, rank;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            leader_img = itemView.findViewById(R.id.leader_img);
            name = itemView.findViewById(R.id.leader_name);
            rank = itemView.findViewById(R.id.leader_rank);
        }
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
