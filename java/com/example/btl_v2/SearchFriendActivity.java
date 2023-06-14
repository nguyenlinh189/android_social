package com.example.btl_v2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;

import com.example.btl_v2.adapter.AdapterSearchFriend;
import com.example.btl_v2.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchFriendActivity extends AppCompatActivity {
    private SearchView searchView;
    private RecyclerView rview;
    private ConstraintLayout constraint;
    private AdapterSearchFriend adapter;
    private ImageView leftArrow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friend);
        searchView=findViewById(R.id.searchView);
        rview=findViewById(R.id.rviewSearch);
        leftArrow=findViewById(R.id.leftArrow);

        Intent intent=getIntent();
        String action=intent.getStringExtra("action");
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
        adapter=new AdapterSearchFriend(action);
        LinearLayoutManager manager=new LinearLayoutManager(getApplicationContext(),RecyclerView.VERTICAL,false);
        rview.setLayoutManager(manager);
        rview.setAdapter(adapter);

        leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String key) {
                reference.child("Users").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<User>list=new ArrayList<>();
                        for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                            if(!dataSnapshot.getKey().equals(FirebaseAuth.getInstance().getUid())){
                                User user=dataSnapshot.getValue(User.class);
                                user.setUserID(dataSnapshot.getKey());
                                list.add(user);
                            }
                        }
                        List<User>listSearch=new ArrayList<>();
                        for(User u:list)
                            if(u.getName().toLowerCase().contains(key.toLowerCase()))
                                listSearch.add(u);
                        adapter.setList(listSearch);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                return true;
            }
        });
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