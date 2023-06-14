package com.example.btl_v2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.btl_v2.adapter.AdapterAcceptFriend;
import com.example.btl_v2.model.Friend;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FriendActivity extends AppCompatActivity {
    private ImageView leftArrow,btnsearch;
    private RecyclerView rview;
    private AdapterAcceptFriend adapter;
    private TextView countInviteFriend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        initView();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
        String auth= FirebaseAuth.getInstance().getUid();
        leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),SearchFriendActivity.class);
                intent.putExtra("action","friend");
                startActivity(intent);
            }
        });
        adapter=new AdapterAcceptFriend();
        LinearLayoutManager manager=new LinearLayoutManager(getApplicationContext(),RecyclerView.VERTICAL,false);
        rview.setLayoutManager(manager);
        rview.setAdapter(adapter);

        reference.child("Friends")
                .child(auth).orderByChild("friend")
                .equalTo("cho chap nhan").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Friend>list=new ArrayList<>();
                        for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                            Friend friend=dataSnapshot.getValue(Friend.class);
                            friend.setFriendId(dataSnapshot.getKey());
                            list.add(friend);
                        }
                        countInviteFriend.setText(list.size()+"");
                        adapter.setList(list);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void initView() {
        leftArrow=findViewById(R.id.leftArrow);
        btnsearch=findViewById(R.id.btnsearch);
        rview=findViewById(R.id.rviewInviteFriend);
        countInviteFriend=findViewById(R.id.countInviteFriend);
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
}