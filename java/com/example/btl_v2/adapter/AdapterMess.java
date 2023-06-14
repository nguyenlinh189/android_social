package com.example.btl_v2.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl_v2.DetailMessActivity;
import com.example.btl_v2.R;
import com.example.btl_v2.model.Chat;
import com.example.btl_v2.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AdapterMess extends RecyclerView.Adapter<AdapterMess.MessViewHolder> {
    private List<Chat>list;
    public AdapterMess(){
        list=new ArrayList<>();
    }
    public void setList(List<Chat>list){
        this.list=list;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public MessViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mess,parent,false);
        return new MessViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessViewHolder holder, int position) {
        Chat chat=list.get(position);
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference();
        // lay tin nhan cuoi cung
        FirebaseDatabase.getInstance().getReference()
                        .child("Messages").child(chat.getChatId())
                        .orderByChild("timestamp").limitToLast(1)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()&&snapshot.hasChildren()){
                                    for(DataSnapshot snapshot1:snapshot.getChildren())
                                    {
                                        holder.lastMess.setText(snapshot1.child("message").getValue().toString());
                                    }
                                }else{
                                    holder.lastMess.setText("");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
        reference.child("Users").child(chat.getFriendId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User user=snapshot.getValue(User.class);
                Picasso.get().load(user.getProfile_image())
                        .placeholder(R.drawable.default_avatar)
                        .into(holder.profile);
                holder.name.setText(user.getName());
                if (user.isStatus()){
                    holder.offline.setVisibility(View.GONE);
                }else{
                    holder.offline.setVisibility(View.VISIBLE);
                }
                holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(holder.itemView.getContext(), DetailMessActivity.class);
                        intent.putExtra("receiverId",chat.getFriendId());
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

    public class MessViewHolder extends RecyclerView.ViewHolder{
        private ImageView profile,offline;
        private TextView name, lastMess;
        private ConstraintLayout constraintLayout;

        public MessViewHolder(@NonNull View itemView) {
            super(itemView);
            profile=itemView.findViewById(R.id.profile);
            name=itemView.findViewById(R.id.name);
            lastMess=itemView.findViewById(R.id.lastMess);
            constraintLayout=itemView.findViewById(R.id.constraint);
            offline=itemView.findViewById(R.id.offline);
        }
    }
}
