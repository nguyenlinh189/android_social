package com.example.btl_v2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.btl_v2.adapter.AdapterMess;
import com.example.btl_v2.model.Chat;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MessActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView rviewMess;
    private AdapterMess adapterMess;
    private FloatingActionButton floating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mess);
        initView();
        //settoolbar
        setSupportActionBar(toolbar);
        MessActivity.this.setTitle("Chat");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //lay danh sach nhung nguoi dang nhan tin

        adapterMess=new AdapterMess();
        LinearLayoutManager manager=new LinearLayoutManager(getApplicationContext(),RecyclerView.VERTICAL,false);
        rviewMess.setLayoutManager(manager);
        rviewMess.setAdapter(adapterMess);
        String auth= FirebaseAuth.getInstance().getUid();
        FirebaseDatabase.getInstance().getReference().child("Chats")
                .child(auth)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Chat>list=new ArrayList<>();
                        if(snapshot.exists())
                        {
                            for(DataSnapshot dataSnapshot:snapshot.getChildren())
                            {
                                Chat chat=new Chat();
                                chat.setFriendId(dataSnapshot.getKey());
                                chat.setChatId(dataSnapshot.getValue(String.class));
                                list.add(chat);
                            }
                        }
                        adapterMess.setList(list);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        // bat su kien them mot cuoc hoi thoai moi
        floating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), SearchFriendActivity.class);
                intent.putExtra("action","mess");
                startActivity(intent);
            }
        });
    }

    private void initView() {
        toolbar=findViewById(R.id.toolbar);
        rviewMess=findViewById(R.id.rviewMess);
        floating=findViewById(R.id.floating);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
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