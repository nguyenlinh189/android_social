package com.example.btl_v2.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl_v2.PersonalPageActivity;
import com.example.btl_v2.R;
import com.example.btl_v2.model.Friend;
import com.example.btl_v2.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AdapterFriend extends RecyclerView.Adapter<AdapterFriend.FollowViewHolder> {
    private List<Friend> list;

    public AdapterFriend() {
        this.list = new ArrayList<>();
    }
    public Friend getFollowers(int position){
        return list.get(position);
    }
    public void setList(List<Friend>list){
        this.list=list;
        notifyDataSetChanged();
    }
    public void setItem(Friend follow){
        list.add(follow);
    }
    @NonNull
    @Override
    public FollowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend,parent,false);
        return new FollowViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowViewHolder holder, int position) {
        Friend friend =getFollowers(position);
        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(friend.getFriendId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user=snapshot.getValue(User.class);
                        Picasso.get().load(user.getProfile_image())
                                .placeholder(R.drawable.default_avatar)
                                .into(holder.profile);
                        holder.name.setText(user.getName());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(friend.getFriendId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user=snapshot.getValue(User.class);
                        user.setUserID(snapshot.getKey());
                        holder.layout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent=new Intent(holder.itemView.getContext(),PersonalPageActivity.class);
                                intent.putExtra("user",user);
                                holder.itemView.getContext().startActivity(intent);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class FollowViewHolder extends RecyclerView.ViewHolder{
        private ImageView profile;
        private TextView name;
        private LinearLayout layout;
        public FollowViewHolder(@NonNull View view) {
            super(view);
            profile=view.findViewById(R.id.profile);
            name=view.findViewById(R.id.name);
            layout=view.findViewById(R.id.layout);
        }
    }
}
