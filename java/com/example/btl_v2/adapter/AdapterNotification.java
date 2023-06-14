package com.example.btl_v2.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl_v2.CommentPostActivity;
import com.example.btl_v2.R;
import com.example.btl_v2.model.Notification;
import com.example.btl_v2.model.User;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AdapterNotification extends RecyclerView.Adapter<AdapterNotification.NotificationViewHolder>{
    private List<Notification>list;
    private Context context;
    private String content="";
    public AdapterNotification(Context context) {
        this.context=context;
        this.list=new ArrayList<>();
    }
    public Notification getItem(int position)
    {
        return list.get(position);
    }
    public List<Notification> getList() {
        return list;
    }

    public void setList(List<Notification> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification,parent,false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference();
        String auth=FirebaseAuth.getInstance().getUid();
        Notification notification=list.get(position);
        boolean trangthai=notification.isChecknotification();
        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(notification.getNotificationBy())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user=snapshot.getValue(User.class);
                        Picasso.get().load(user.getProfile_image())
                                .placeholder(R.drawable.default_avatar)
                                .into(holder.profile);

                        if (notification.getType().equals("like")){
                            holder.content.setText(Html.fromHtml("<b>"+user.getName()+"</b>"+" đã thích bài viết của bạn "));
                        }else if(notification.getType().equals("comment")){
                            holder.content.setText(Html.fromHtml("<b>"+user.getName()+"</b>"+" đã bình luận bài viết của bạn"));
                        }else if(notification.getType().equals("friend")){
                            holder.content.setText(Html.fromHtml("<b>"+user.getName()+"</b>"+" đã yêu cầu kết bạn"));
                        }else if (notification.getType().equals("acceptFriend")){
                            holder.content.setText(Html.fromHtml("<b>"+user.getName()+"</b>"+" đã chấp nhận yêu cầu kết bạn của bạn"));
                        }
                        String thoigian= TimeAgo.using(notification.getNotificationAt());
                        holder.time.setText(thoigian);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        holder.openNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference().child("notification")
                        .child(FirebaseAuth.getInstance().getUid())
                        .child(notification.getNotificationID())
                        .child("checkOpen")
                        .setValue(true);
                holder.openNotification.setBackgroundColor(Color.parseColor("#FFFFFF"));
                if(!notification.getType().equals("follow") && !notification.getType().equals("friend") && !notification.getType().equals("acceptFriend")){
                    Intent intent=new Intent(view.getContext(), CommentPostActivity.class);
                    intent.putExtra("postId",notification.getPostID());
                    intent.putExtra("postedBy",notification.getPostedBy());
                    view.getContext().startActivity(intent);
                }
            }
        });
        boolean checkOpen=notification.isCheckOpen();
        if(checkOpen==true)
            holder.openNotification.setBackgroundColor(Color.parseColor("#FFFFFF"));
        else
            holder.openNotification.setBackgroundColor(Color.parseColor("#ECDBDB"));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder{
        private ImageView profile;
        private TextView time,content;
        private ConstraintLayout openNotification;

        public NotificationViewHolder(@NonNull View view) {
            super(view);
            profile=view.findViewById(R.id.profile);
            time=view.findViewById(R.id.time);
            content=view.findViewById(R.id.content);
            openNotification=view.findViewById(R.id.openNotification);
        }
    }
}
