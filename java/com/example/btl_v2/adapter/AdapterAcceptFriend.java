package com.example.btl_v2.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.btl_v2.PersonalPageActivity;
import com.example.btl_v2.R;
import com.example.btl_v2.model.Friend;
import com.example.btl_v2.model.Notification;
import com.example.btl_v2.model.User;
import com.github.marlonlom.utilities.timeago.TimeAgo;
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

public class AdapterAcceptFriend extends RecyclerView.Adapter<AdapterAcceptFriend.FViewHome> {
    private List<Friend>list;
    private User reUser;



    public AdapterAcceptFriend() {
        list=new ArrayList<>();
    }

    public void setList(List<Friend> list) {
        this.list = list;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public FViewHome onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_accept_friend,parent,false);
        return new FViewHome(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FViewHome holder, int position) {
        Friend friend=list.get(position);
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
        String auth= FirebaseAuth.getInstance().getUid();
        reference.child("Users")
                .child(friend.getFriendId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user=snapshot.getValue(User.class);
                        reUser=user;
                        user.setUserID(snapshot.getKey());
                        holder.name.setText(user.getName());
                        Picasso.get().load(user.getProfile_image())
                                .placeholder(R.drawable.default_avatar)
                                .into(holder.profile);
                        String strtime= TimeAgo.using(friend.getTimestamp());
                        holder.time.setText(strtime);
                        // nhay sang trang ca nhan chi tiet
                        holder.constraint.setOnClickListener(new View.OnClickListener() {
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
        holder.btnchapnhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference.child("timestamp").setValue(ServerValue.TIMESTAMP);
                reference.child("timestamp").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        long time=snapshot.getValue(Long.class);
                        Friend friend1=new Friend("banbe",time);
                        reference.child("Friends")
                                .child(friend.getFriendId()).child(auth)
                                .setValue(friend1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        reference.child("Friends").child(auth).child(friend.getFriendId())
                                                .setValue(friend1);

                                        // thong bao đã chap nhan loi moi ket ban
                                        Notification notification=new Notification();
                                        notification.setNotificationBy(auth);
                                        notification.setNotificationAt(time);
                                        notification.setType("acceptFriend");
                                        notification.setCheckOpen(false);
                                        reference.child("notification").child(friend.getFriendId())
                                                .push().setValue(notification).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(holder.itemView.getContext(), "Đã chấp nhận lời mời kết bạn", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                        reference.child("Users").child(auth).child("name").addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                String name=snapshot.getValue(String.class);
                                                sendNotification("App Demo",name+" chấp nhận lời kết bạn của bạn",reUser.getToken(),holder.itemView.getContext());
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
        holder.btnxoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(view.getContext())
                        .setTitle("Xóa lời yêu cầu kết bạn").setMessage("Bạn có chắc muốn xóa lời yêu cầu này không?")
                        .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                reference.child("Friends").child(auth)
                                        .child(friend.getFriendId())
                                        .setValue(null);
                                reference.child("Friends").child(friend.getFriendId())
                                        .child(auth)
                                        .setValue(null);
                            }
                        })
                        .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).show();
            }
        });
        // lấy số bạn chung

        reference.child("Friends")
                .child(friend.getFriendId()).orderByChild("friend")
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
                                        int countbanchung=0;
                                        List<Friend>list2=new ArrayList<>();
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
                                                    countbanchung=countbanchung+1;
                                                    break;
                                                }
                                            }
                                            //System.out.println(countbanchung);
                                        }
                                        holder.banchung.setText(countbanchung+" bạn chung");
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

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class FViewHome extends RecyclerView.ViewHolder{
        private ImageView profile;
        private TextView name, banchung, time;
        private Button btnchapnhan, btnxoa;
        private ConstraintLayout constraint;
        public FViewHome(@NonNull View view) {
            super(view);
            profile=view.findViewById(R.id.profile);
            name=view.findViewById(R.id.name);
            banchung=view.findViewById(R.id.banchung);
            time=view.findViewById(R.id.time);
            btnchapnhan=view.findViewById(R.id.btnchapnhan);
            btnxoa=view.findViewById(R.id.btnxoa);
            constraint=view.findViewById(R.id.constraint);
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
