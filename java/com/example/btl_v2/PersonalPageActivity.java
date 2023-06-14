package com.example.btl_v2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.btl_v2.adapter.AdapterFriend;
import com.example.btl_v2.adapter.AdapterPost;
import com.example.btl_v2.model.Friend;
import com.example.btl_v2.model.Notification;
import com.example.btl_v2.model.Post;
import com.example.btl_v2.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class PersonalPageActivity extends AppCompatActivity {
    private ImageView coverPhoto, avatar, leftArrow;
    private Button btnThemBanbe, btnNhanTin;
    private TextView name, nghenghiep;
    private RecyclerView rviewpost,rviewbanchung;
    private TextView countbanchung;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_page);
        initView();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
        String auth= FirebaseAuth.getInstance().getUid();
        Intent intent=getIntent();
        User u=(User)intent.getSerializableExtra("user");
        Picasso.get().load(u.getCoverPhoto())
                .placeholder(R.drawable.anhbiadefault)
                .into(coverPhoto);
        Picasso.get().load(u.getProfile_image())
                .placeholder(R.drawable.default_avatar)
                .into(avatar);
        name.setText(u.getName());
        nghenghiep.setText(u.getProfession());
        leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // 1 la ban be
        // 2 : cho chap nhan
        // 3: da yeu cau
        // 0 : chua lam gi ca
        reference.child("Friends").child(auth).child(u.getUserID())
                .child("friend").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists())
                        {
                            String trangthai=snapshot.getValue(String.class);
                            if (trangthai.equals("banbe")){
                                btnThemBanbe.setText("Bạn bè");
                            }else if (trangthai.equals("cho chap nhan")){
                                btnThemBanbe.setText("Trả lời");
                                btnThemBanbe.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        reference.child("timestamp").setValue(ServerValue.TIMESTAMP);
                                        reference.child("timestamp").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                long time=snapshot.getValue(Long.class);
                                                Friend friend1=new Friend("banbe",time);
                                                reference.child("Friends")
                                                        .child(u.getUserID()).child(auth)
                                                        .setValue(friend1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                reference.child("Friends").child(auth).child(u.getUserID())
                                                                        .setValue(friend1);
                                                                // thong bao đã chap nhan loi moi ket ban
                                                                Notification notification=new Notification();
                                                                notification.setNotificationBy(auth);
                                                                notification.setNotificationAt(time);
                                                                notification.setType("acceptFriend");
                                                                notification.setCheckOpen(false);
                                                                reference.child("notification").child(u.getUserID())
                                                                        .push().setValue(notification).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void unused) {
                                                                                Toast.makeText(getApplicationContext(), "Đã chấp nhận lời mời kết bạn", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        });
                                                                reference.child("Users").child(auth)
                                                                                .child("name").addValueEventListener(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                String name=snapshot.getValue(String.class);
                                                                                sendNotification("App demo",name+" đã chấp nhận lời mời kết bạn của bạn",u.getToken());
                                                                            }

                                                                            @Override
                                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                                            }
                                                                        });

                                                                btnThemBanbe.setText("Bạn bè");
                                                            }
                                                        });
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                            }
                                        });
                                    }
                                });
                            }else if(trangthai.equals("da yeu cau")){
                                btnThemBanbe.setText("Đã gửi lời kết bạn");
                            }
                        } else {
                            btnThemBanbe.setText("Thêm bạn bè");
                            btnThemBanbe.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    reference.child("timestamp").setValue(ServerValue.TIMESTAMP);
                                    reference.child("timestamp").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            long time=snapshot.getValue(Long.class);
                                            Friend friend=new Friend("cho chap nhan",time);
                                            Friend friend1=new Friend("da yeu cau",time);
                                            reference.child("Friends").child(u.getUserID())
                                                    .child(auth).setValue(friend).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            reference.child("Friends").child(auth)
                                                                    .child(u.getUserID())
                                                                    .setValue(friend1);

                                                            Notification notification=new Notification();
                                                            notification.setNotificationBy(auth);
                                                            notification.setNotificationAt(time);
                                                            notification.setCheckOpen(false);
                                                            notification.setType("friend");
                                                            reference.child("notification").child(u.getUserID())
                                                                    .push().setValue(notification).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void unused) {
                                                                            Toast.makeText(PersonalPageActivity.this, "Đã gửi yêu cầu kết bạn thành công "
                                                                                    +u.getName()+" thành công!!", Toast.LENGTH_SHORT).show();
                                                                            btnThemBanbe.setText("Đã gửi yêu cầu");
                                                                        }
                                                                    });
                                                            reference.child("Users").child(auth)
                                                                    .child("name").addValueEventListener(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                            String name=snapshot.getValue(String.class);
                                                                            sendNotification("App demo",name+" đã gửi lời kết bạn",u.getToken());
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
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        // lay danh sach cac bai post
        AdapterPost adapterpost=new AdapterPost();
        LinearLayoutManager managerDasboard=new LinearLayoutManager(getApplicationContext(),RecyclerView.VERTICAL,false);
        rviewpost.setLayoutManager(managerDasboard);
        rviewpost.setNestedScrollingEnabled(false);
        rviewpost.setAdapter(adapterpost);
        reference.child("posts").orderByChild("postedBy").equalTo(u.getUserID())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Post> listPost=new ArrayList<>();
                        for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                            Post post=dataSnapshot.getValue(Post.class);
                            post.setPostId(dataSnapshot.getKey());
                            listPost.add(post);
                        }
                        adapterpost.setList(listPost);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        // xu ly nut nhan tin
        btnNhanTin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),DetailMessActivity.class);
                intent.putExtra("receiverId",u.getUserID());
                startActivity(intent);
            }
        });
        // xu ly ly ban chung
        reference.child("Friends")
                .child(u.getUserID()).orderByChild("friend")
                .equalTo("banbe").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        List<Friend>list1=new ArrayList<>();
                        for(DataSnapshot dataSnapshot:snapshot.getChildren())
                        {
                            Friend friend1=dataSnapshot.getValue(Friend.class);
                            friend1.setFriendId(dataSnapshot.getKey());
                            list1.add(friend1);
                        }
                        reference.child("Friends")
                                .child(auth).orderByChild("friend")
                                .equalTo("banbe").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        int count=0;
                                        List<Friend>list2=new ArrayList<>();
                                        List<Friend>listbanchung=new ArrayList<>();
                                        for(DataSnapshot dataSnapshot:snapshot.getChildren())
                                        {
                                            Friend friend2=dataSnapshot.getValue(Friend.class);
                                            friend2.setFriendId(dataSnapshot.getKey());
                                            list2.add(friend2);
                                        }
                                        for(Friend f1:list1)
                                        {
                                            for(Friend f2:list2)
                                            {
                                                if(f1.getFriendId().equals(f2.getFriendId())){
                                                    listbanchung.add(f1);
                                                    count=count+1;
                                                    break;
                                                }
                                            }
                                            //System.out.println(countbanchung);
                                        }
                                        countbanchung.setText(count+" bạn chung");
                                        AdapterFriend adapter=new AdapterFriend();
                                        LinearLayoutManager managerFollowed=new LinearLayoutManager(getApplicationContext(),RecyclerView.HORIZONTAL,false);
                                        rviewbanchung.setNestedScrollingEnabled(false);
                                        rviewbanchung.setLayoutManager(managerFollowed);
                                        rviewbanchung.setAdapter(adapter);
                                        adapter.setList(listbanchung);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void initView() {
        coverPhoto=findViewById(R.id.coverPhoto);
        avatar=findViewById(R.id.avatar);
        name=findViewById(R.id.tvname);
        nghenghiep=findViewById(R.id.tvnghenghiep);
        btnThemBanbe=findViewById(R.id.btnAddFriend);
        btnNhanTin=findViewById(R.id.btnNhanTin);
        leftArrow=findViewById(R.id.leftArrow);
        rviewpost=findViewById(R.id.rviewpost);
        rviewbanchung=findViewById(R.id.rviewbanchung);
        countbanchung=findViewById(R.id.countbanchung);
    }
    private void status(boolean status){
        String auth=FirebaseAuth.getInstance().getUid();
        if(auth!=null ){
            DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Users").child(auth);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists())
                        reference.child("status").setValue(status);
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
    private void sendNotification(String name, String message, String token){
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