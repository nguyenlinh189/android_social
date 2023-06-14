package com.example.btl_v2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.btl_v2.model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class VideoCallDi extends AppCompatActivity {
    private ImageView profile;
    private TextView name;
    private FloatingActionButton btn_end_call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_di);
        initView();
        Intent intent=getIntent();

        String receiverId=intent.getStringExtra("receiverId");
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
        String auth= FirebaseAuth.getInstance().getUid();
        reference.child("Users").child(receiverId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user=snapshot.getValue(User.class);
                        Picasso.get().load(user.getProfile_image())
                                .placeholder(R.drawable.default_avatar)
                                .into(profile);
                        name.setText(user.getName());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void initView() {
        profile=findViewById(R.id.profile);
        name=findViewById(R.id.name);
        btn_end_call=findViewById(R.id.btn_end_call);
    }
}