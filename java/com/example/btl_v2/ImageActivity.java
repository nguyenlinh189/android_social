package com.example.btl_v2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.btl_v2.adapter.AdapterImage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ImageActivity extends AppCompatActivity {
    private ImageView leftArrow;
    private TextView txtname;
    private RecyclerView rviewimgae;
    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        initView();

        Intent intent=getIntent();
        ArrayList<String>list=intent.getStringArrayListExtra("list");

        AdapterImage adapter=new AdapterImage();
        GridLayoutManager manager=new GridLayoutManager(getApplicationContext(),3,RecyclerView.VERTICAL,false);
        rviewimgae.setLayoutManager(manager);
        rviewimgae.setAdapter(adapter);
        adapter.setList(list);

        leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initView() {
        leftArrow=findViewById(R.id.leftArrow);
        txtname=findViewById(R.id.name);
        rviewimgae=findViewById(R.id.rviewImage);
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