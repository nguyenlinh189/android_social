package com.example.btl_v2.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.btl_v2.CommentPostActivity;
import com.example.btl_v2.R;
import com.example.btl_v2.model.Notification;
import com.example.btl_v2.model.Post;
import com.example.btl_v2.model.User;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdapterPost extends RecyclerView.Adapter<AdapterPost.PostViewHolder>{
    private List<Post>list;
    private int mark=0;
    private User reUser;

    public AdapterPost() {
        this.list = new ArrayList<>();
    }
    public void setPost(Post post){
        list.add(post);
    }
    public void setList(List<Post>list){
        this.list=list;
        notifyDataSetChanged();
    }
    public Post getPost(int position){
        return list.get(position);}

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dasboard_home,parent,false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        //lay noi dung bai dang tu database
        Post post=list.get(position);
        Picasso.get().load(post.getPostImgae()).placeholder(R.drawable.anhbiadefault).into(holder.imagePost);
        String content=post.getPostContent();
        String time= TimeAgo.using(post.getPostAt());
        holder.date.setText(time);
        // neu noi dung rong thi an phan chua noi dung di
        if(content.equals("")){
            holder.content.setVisibility(View.GONE);
        }else{
            holder.content.setText(post.getPostContent());
            holder.content.setVisibility(View.VISIBLE);
        }
        holder.comment.setText(post.getCommentCount()+"");
        holder.like.setText(post.getPostLike()+"");
        FirebaseDatabase.getInstance().getReference().child("Users").child(post.getPostedBy())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user=snapshot.getValue(User.class);
                        reUser=user;
                        Picasso.get().load(user.getProfile_image()).placeholder(R.drawable.default_avatar)
                                .into(holder.avatar);
                        holder.name.setText(user.getName());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        // xu ly nut like
        int reactions[]=new int[]{
                R.drawable.ic_gray_like,
                R.drawable.ic_like,
                R.drawable.ic_heart,
                R.drawable.ic_happy,
                R.drawable.ic_angry,
                R.drawable.ic_sad,
                R.drawable.ic_surprise
        };
        ReactionsConfig config = new ReactionsConfigBuilder(holder.itemView.getContext())
                .withReactions(reactions)
                .build();
        ReactionPopup popup = new ReactionPopup(holder.itemView.getContext(), config, (pos) -> {
            if(pos==0){
                FirebaseDatabase.getInstance().getReference().child("posts")
                        .child(post.getPostId())
                        .child("likes")
                        .child(FirebaseAuth.getInstance().getUid()).setValue(null)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    FirebaseDatabase.getInstance().getReference().child("posts")
                                            .child(post.getPostId())
                                            .child("postLike")
                                            .setValue(post.getPostLike()-1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    holder.like.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_gray_like,0,0,0);
                                                }
                                            });
                                }
                            }
                        });
            }
            if (pos>0 && pos<=6){
                holder.like.setCompoundDrawablesRelativeWithIntrinsicBounds(reactions[pos],0,0,0);
                FirebaseDatabase.getInstance().getReference().child("posts")
                        .child(post.getPostId())
                        .child("likes")
                        .child(FirebaseAuth.getInstance().getUid()).setValue(pos)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    DatabaseReference reference=FirebaseDatabase.getInstance().getReference();
                                    reference.child("timestamp").setValue(ServerValue.TIMESTAMP);
                                    reference.child("timestamp").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            long time=snapshot.getValue(Long.class);
                                            if(mark==0){
                                                FirebaseDatabase.getInstance().getReference().child("posts")
                                                        .child(post.getPostId())
                                                        .child("postLike")
                                                        .setValue(post.getPostLike()+1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                //holder.like.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.icon_like_active,0,0,0);

                                                                Notification notification=new Notification();
                                                                notification.setPostedBy(FirebaseAuth.getInstance().getUid());
                                                                notification.setNotificationAt(time);
                                                                notification.setPostID(post.getPostId());
                                                                notification.setPostedBy(post.getPostedBy());
                                                                notification.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                                                notification.setChecknotification(false);

                                                                notification.setType("like");
                                                                FirebaseDatabase.getInstance().getReference()
                                                                        .child("notification")
                                                                        .child(post.getPostedBy())
                                                                        .push()
                                                                        .setValue(notification);
                                                                sendNotification("App demo",reUser.getName()+"đã thả cảm xúc bài viết của bạn",reUser.getToken(),holder.itemView.getContext());
                                                            }
                                                        });
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                            }
                        });
            }
            return true; // true is closing popup, false is requesting a new selection
        });
        FirebaseDatabase.getInstance().getReference().child("posts")
                        .child(post.getPostId()).child("likes")
                        .child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists())
                        {
                            int p=snapshot.getValue(Integer.class);
                            holder.like.setCompoundDrawablesRelativeWithIntrinsicBounds(reactions[p],0,0,0);
                            holder.like.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View view, MotionEvent motionEvent) {
                                    mark=1;
                                    popup.onTouch(view,motionEvent);
                                    return true;
                                }
                            });
                        }
                        else{
                            holder.like.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View view, MotionEvent motionEvent) {
                                    mark=0;
                                    popup.onTouch(view,motionEvent);
                                    return true;
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
        // an nut chọn comment bài đăng
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(view.getContext(), CommentPostActivity.class);
                intent.putExtra("postId",post.getPostId());
                intent.putExtra("postedBy",post.getPostedBy());
                view.getContext().startActivity(intent);
            }
        });
        // an nut chon share
    }
    @Override
    public int getItemCount() {
        return list.size();
    }


    public class PostViewHolder extends RecyclerView.ViewHolder {
        private ImageView avatar,imagePost,save;
        private TextView name, date,content,comment, like;
        public PostViewHolder(@NonNull View view) {
            super(view);
            avatar=view.findViewById(R.id.avatar);
            imagePost=view.findViewById(R.id.imagepost);
            name=view.findViewById(R.id.nameaccount);
            date=view.findViewById(R.id.dateCreate);
            content=view.findViewById(R.id.contentPost);
            comment=view.findViewById(R.id.comment);
            like=view.findViewById(R.id.like);
        }

    }
    private void sendNotification(String name, String message, String token, Context context){
        try{
            RequestQueue queue= Volley.newRequestQueue(context);
            String url="https://fcm.googleapis.com/fcm/send";
            JSONObject data=new JSONObject();
            data.put("title",name);
            data.put("body",message);
            JSONObject notificationData=new JSONObject();
            notificationData.put("notification",data);
            notificationData.put("to",token);

            JsonObjectRequest request=new JsonObjectRequest(Request.Method.POST,url, notificationData,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String,String> map=new HashMap<>();
                    String key="AAAALlV5iRM:APA91bEKF-CkhIR-0QxSOx-r2Ji_EH085qrxDeczC8KALMx_FYiN2x7uoK4mSXhXuFPBR2xn7B0dsc0sfhcsUk6wsWqwSZS32zkd-PCivVf-zIiv81Wzne2vqaiLHiUdx7lsf-jcxTrr";
                    map.put("Authorization","key="+key);
                    map.put("Content-Type","application/json");
                    return map;
                }
            };
            queue.add(request);
        }catch(Exception e){

        }
    }
}
