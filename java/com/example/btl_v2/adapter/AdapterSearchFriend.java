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
import com.example.btl_v2.PersonalPageActivity;
import com.example.btl_v2.R;
import com.example.btl_v2.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class AdapterSearchFriend extends RecyclerView.Adapter<AdapterSearchFriend.SearchViewHolder> {
    private List<User> list;
    private String action;
    public void setList(List<User>list){
        this.list=list;
        notifyDataSetChanged();
    }

    public AdapterSearchFriend(String action) {
        list=new ArrayList<>();
        this.action=action;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_friend,parent,false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        User u=list.get(position);
        Picasso.get().load(u.getProfile_image()).placeholder(R.drawable.default_avatar)
                .into(holder.profile);
        holder.name.setText(u.getName());

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference();

        String auth= FirebaseAuth.getInstance().getUid();
        reference.child("Friends").child(auth).child(u.getUserID())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists())
                        {
                            String  isbanbe=snapshot.child("friend").getValue(String.class);
                            System.out.println(isbanbe);
                            if (isbanbe.equals("banbe"))
                            {
                                holder.isbanbe.setText("Bạn bè");//đã là bạn bè

                            }else if(isbanbe.equals("cho chap nhan")){
                                holder.isbanbe.setText("chưa phải là bạn bè");
                            }else if(isbanbe.equals("da yeu cau")){
                                holder.isbanbe.setText("chưa phải là bạn bè");
                            }
                        }
                        else{
                            holder.isbanbe.setText("chưa phải bạn bè");// chưa phải là bạn bè
                        }
                        if(action.equals("friend")){
                            holder.constraint.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent=new Intent(holder.itemView.getContext(), PersonalPageActivity.class);
                                    intent.putExtra("user",u);
                                    holder.itemView.getContext().startActivity(intent);
                                }
                            });
                        }else{
                            holder.constraint.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent=new Intent(holder.itemView.getContext(), DetailMessActivity.class);
                                    intent.putExtra("receiverId",u.getUserID());
                                    holder.itemView.getContext().startActivity(intent);
                                }
                            });
                        }

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

    public class SearchViewHolder extends RecyclerView.ViewHolder{
        private ImageView profile;
        private TextView name, isbanbe;
        private ConstraintLayout constraint;
        public SearchViewHolder(@NonNull View view) {
            super(view);
            profile=view.findViewById(R.id.profile);
            name=view.findViewById(R.id.name);
            isbanbe=view.findViewById(R.id.isbanbe);
            constraint=view.findViewById(R.id.constraint);
        }
    }
}
