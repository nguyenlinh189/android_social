package com.example.btl_v2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class UpdateAccount extends AppCompatActivity {
    private ImageView avatar;
    private EditText ename,enghenghiep;
    private Button btnupdateAccount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_account);
        initView();
        // hien thi thong tin cua user
        Intent t=getIntent();
        String userId=t.getStringExtra("userId");
        String name=t.getStringExtra("userName");
        String nghenghiep=t.getStringExtra("nghenghiep");
        String imageUser=t.getStringExtra("avatar");
        ename.setText(name);
        enghenghiep.setText(nghenghiep);
        Picasso.get().load(imageUser).placeholder(R.drawable.default_avatar)
                .into(avatar);
        btnupdateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
                reference.child("Users").child(userId)
                        .child("name").setValue(ename.getText().toString());
                reference.child("Users").child(userId)
                        .child("profession").setValue(enghenghiep.getText().toString());
                Toast.makeText(UpdateAccount.this, "Bạn đã cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void initView() {
        avatar=findViewById(R.id.avatar);
        ename=findViewById(R.id.ename);
        enghenghiep=findViewById(R.id.enghenghiep);
        btnupdateAccount=findViewById(R.id.btnupdateaccount);
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