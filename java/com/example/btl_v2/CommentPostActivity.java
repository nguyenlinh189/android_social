package com.example.btl_v2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.btl_v2.adapter.AdapterComment;
import com.example.btl_v2.model.Comment;
import com.example.btl_v2.model.Notification;
import com.example.btl_v2.model.Post;
import com.example.btl_v2.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
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

public class CommentPostActivity extends AppCompatActivity {
    private ImageView image,profile,btnsend;
    private TextView name,like,comment,contentPost;
    private EditText contentComment;
    private RecyclerView rviewComment;
    private Post post;
    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private Toolbar toolbar;
    private ImageView leftArrow;
    private User reUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_post);
        initView();

        leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        int reactions[]=new int[]{
                R.drawable.ic_gray_like,
                R.drawable.ic_like,
                R.drawable.ic_heart,
                R.drawable.ic_happy,
                R.drawable.ic_angry,
                R.drawable.ic_sad,
                R.drawable.ic_surprise
        };
        database=FirebaseDatabase.getInstance();
        auth=FirebaseAuth.getInstance();
        Intent intent=getIntent();
        String postId=intent.getStringExtra("postId");
        String postedBy=intent.getStringExtra("postedBy");
        database.getReference().child("posts").child(postId)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                post=snapshot.getValue(Post.class);
                                Picasso.get().load(post.getPostImgae())
                                        .placeholder(R.drawable.anhbiadefault)
                                        .into(image);
                                database.getReference().child("posts").child(postId)
                                        .child("likes").child(auth.getUid())
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if(snapshot.exists())
                                                {
                                                    int pos=snapshot.getValue(Integer.class);
                                                    like.setCompoundDrawablesRelativeWithIntrinsicBounds(reactions[pos],0,0,0);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                like.setText(post.getPostLike()+"");
                                String content=post.getPostContent();
                                // neu noi dung rong thi an phan chua noi dung di
                                if(content.equals("")){
                                    contentPost.setVisibility(View.GONE);
                                }else{
                                    contentPost.setText(post.getPostContent());
                                    contentPost.setVisibility(View.VISIBLE);
                                }
                                comment.setText(post.getCommentCount()+"");
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
        database.getReference().child("Users").child(postedBy)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user=snapshot.getValue(User.class);
                        reUser=user;
                        Picasso.get().load(user.getProfile_image())
                                .placeholder(R.drawable.default_avatar)
                                .into(profile);
                        name.setText(user.getName());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        // lay danh sach comment trong bai post
        AdapterComment adapterComment=new AdapterComment();
        LinearLayoutManager manager=new LinearLayoutManager(getApplicationContext(),RecyclerView.VERTICAL,false);
        rviewComment.setLayoutManager(manager);
        rviewComment.setAdapter(adapterComment);
        database.getReference().child("posts")
                .child(postId).child("comments").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Comment>list=new ArrayList<>();
                        if (snapshot.exists()){
                            for(DataSnapshot dataSnapshot:snapshot.getChildren())
                            {
                                Comment comment=dataSnapshot.getValue(Comment.class);
                                comment.setCommentId(dataSnapshot.getKey());
                                comment.setPostId(postId);
                                list.add(comment);
                            }
                        }
                        adapterComment.setList(list);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        // bat su kien nut gui comment
        btnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Comment comment=new Comment();
                comment.setContent(contentComment.getText().toString());
                comment.setCommentBy(auth.getUid());
                database.getReference().child("timestamp").setValue(ServerValue.TIMESTAMP);
                database.getReference().child("timestamp").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        long time=snapshot.getValue(Long.class);
                        comment.setCommentAt(time);
                        contentComment.setText("");
                        database.getReference().child("posts")
                                .child(postId)
                                .child("comments")
                                .push()
                                .setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        database.getReference().child("posts")
                                                .child(postId)
                                                .child("commentCount")
                                                .setValue(post.getCommentCount()+1);
                                        Notification notification=new Notification();
                                        notification.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                        notification.setNotificationAt(time);
                                        //System.out.println("Notification: "+notification.getNotificationAt());
                                        notification.setType("comment");
                                        notification.setPostID(postId);
                                        notification.setPostedBy(postedBy);
                                        FirebaseDatabase.getInstance().getReference()
                                                .child("notification")
                                                .child(postedBy)
                                                .push()
                                                .setValue(notification);
                                        database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                                                        .child("name").addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        String name=snapshot.getValue(String.class);
                                                        sendNotification("App Demo",name+" đã comment bài viết của bạn",reUser.getToken());
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });

                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
    }

    private void initView() {
        image = findViewById(R.id.image);
        profile = findViewById(R.id.profile);
        btnsend = findViewById(R.id.btnsend);
        name = findViewById(R.id.name);
        like = findViewById(R.id.like);
        comment = findViewById(R.id.comment);
        contentComment = findViewById(R.id.contentComment);
        rviewComment = findViewById(R.id.rviewComment);
        contentPost = findViewById(R.id.contentPost);
        toolbar = findViewById(R.id.toolbar);
        leftArrow = findViewById(R.id.leftArrow);
    }
    private void status(boolean status){
        String auth=FirebaseAuth.getInstance().getUid();
        if(auth!=null ){
            DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Users").child(auth);
                            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists())
                                    {
                                        reference.child("status").setValue(status);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        status(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        status(false);
    }
    private void sendNotification(String name,String message,String token){
        try{
            RequestQueue queue= Volley.newRequestQueue(getApplicationContext());
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