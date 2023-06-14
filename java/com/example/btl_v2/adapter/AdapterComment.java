package com.example.btl_v2.adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl_v2.R;
import com.example.btl_v2.model.Comment;
import com.example.btl_v2.model.User;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AdapterComment extends RecyclerView.Adapter<AdapterComment.CommentViewHolder> {
    private List<Comment>list;

    public AdapterComment() {
        list=new ArrayList<>();
    }
    public void setList(List<Comment> list) {
        this.list = list;
        notifyDataSetChanged();
    }
    public Comment getCommend(int position){
        return list.get(position);
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment,parent,false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment=getCommend(position);
        FirebaseDatabase.getInstance().getReference().child("Users").child(comment.getCommentBy())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists())
                                {
                                    User user=snapshot.getValue(User.class);
                                    Picasso.get().load(user.getProfile_image())
                                            .placeholder(R.drawable.default_avatar)
                                            .into(holder.profile);
                                    holder.comment.setText(Html.fromHtml("<b>"+user.getName()+"</b>"+" "+comment.getContent()));
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
        //holder.comment.setText(comment.getContent());
        //Đổi từ thời gian tính bằng milis sang thời gian ago
        String text= TimeAgo.using(comment.getCommentAt());
        holder.time.setText(text);
        // xoa binh luan
        holder.constraint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(view.getContext())
                        .setTitle("Xóa bình luận").setMessage("Bạn có chắc muốn xóa bình luận này không?")
                        .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FirebaseDatabase.getInstance().getReference()
                                        .child("posts").child(comment.getPostId())
                                        .child("comments").child(comment.getCommentId())
                                        .setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                FirebaseDatabase.getInstance().getReference()
                                                        .child("posts").child(comment.getPostId())
                                                        .child("commentCount").setValue(list.size());
                                            }
                                        });
                            }
                        })
                        .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }



    public class CommentViewHolder extends RecyclerView.ViewHolder{
        private ImageView profile;
        private TextView comment;
        private TextView time;
        private ConstraintLayout constraint;
        public CommentViewHolder(@NonNull View view) {
            super(view);
            profile=view.findViewById(R.id.profile);
            comment=view.findViewById(R.id.comment);
            time=view.findViewById(R.id.time);
            constraint=view.findViewById(R.id.constraint);
        }
    }
}
