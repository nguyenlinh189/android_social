package com.example.btl_v2.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl_v2.MessActivity;
import com.example.btl_v2.R;
import com.example.btl_v2.SearchFriendActivity;
import com.example.btl_v2.adapter.AdapterPost;
import com.example.btl_v2.adapter.AdapterStory;
import com.example.btl_v2.model.Post;
import com.example.btl_v2.model.Story;
import com.example.btl_v2.model.User;
import com.example.btl_v2.model.UserStories;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment {
    private RecyclerView rviewstory,rviewpost;    private AdapterStory adapterStory;
    private AdapterPost adapterPost;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private CircleImageView addStory;
    private FirebaseStorage storage;
    private ImageView story1, avatar;
    private Uri uri;
    private ProgressDialog dialog;
    private ImageButton mess;
    private ImageView search;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return  inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);

        //set avatar
        database.getReference().child("Users").child(auth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                Picasso.get().load(user.getProfile_image()).placeholder(R.drawable.default_avatar)
                        .into(avatar);
                Picasso.get().load(user.getProfile_image()).placeholder(R.drawable.default_avatar)
                        .into(story1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // load danh sach story
        adapterStory=new AdapterStory();
        LinearLayoutManager managerStory=new LinearLayoutManager(getContext(),RecyclerView.HORIZONTAL,false);
        rviewstory.setLayoutManager(managerStory);
        rviewstory.setNestedScrollingEnabled(false);
        rviewstory.setAdapter(adapterStory);
        database.getReference().child("stories")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            List<Story>storyList=new ArrayList<>();
                            for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                                Story story=new Story();

                                story.setStoryBy(dataSnapshot.getKey());
                                story.setStoryAt(dataSnapshot.child("postedBy").getValue(Long.class));
                                ArrayList<UserStories>storiesList=new ArrayList<>();
                                for(DataSnapshot snapshot1:dataSnapshot.child("userStories").getChildren()){
                                    UserStories userStories=snapshot1.getValue(UserStories.class);
                                    storiesList.add(userStories);
                                }
                                story.setStories(storiesList);
                                storyList.add(story);
                            }
                            adapterStory.setList(storyList);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        //
        // load du lieu bai post len
        adapterPost=new AdapterPost();
        LinearLayoutManager managerDasboard=new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false);
        rviewpost.setLayoutManager(managerDasboard);
        rviewpost.setNestedScrollingEnabled(false);
        rviewpost.setAdapter(adapterPost);
        database.getReference().child("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Post>listPost=new ArrayList<>();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Post post=dataSnapshot.getValue(Post.class);
                    post.setPostId(dataSnapshot.getKey());
                    listPost.add(post);
                }
                adapterPost.setList(listPost);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        addStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,10);


            }
        });
        //xu ly nut mess
        mess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), MessActivity.class);
                startActivity(intent);

            }
        });
        // xu ly nut search
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), SearchFriendActivity.class);
                intent.putExtra("action","friend");
                startActivity(intent);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data.getData()!=null && requestCode==10) {
            dialog.show();
            uri = data.getData();
            story1.setImageURI(uri);
            database.getReference().child("timestamp").setValue(ServerValue.TIMESTAMP);
            database.getReference().child("timestamp").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    long time=snapshot.getValue(Long.class);
                    final StorageReference reference=storage.getReference()
                            .child(FirebaseAuth.getInstance().getUid())
                            .child(time+"");
                    reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Story story=new Story();
                                    story.setStoryAt(time);
                                    database.getReference().child("stories")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .child("postedBy").setValue(story.getStoryAt())
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    UserStories userStories=new UserStories(uri.toString(),story.getStoryAt());
                                                    database.getReference().child("stories")
                                                            .child(FirebaseAuth.getInstance().getUid())
                                                            .child("userStories")
                                                            .push()
                                                            .setValue(userStories)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    dialog.dismiss();
                                                                    Toast.makeText(getContext(), "Đăng story hành công", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                }
                                            });
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

    }

    private void initView(View view) {
        rviewstory=view.findViewById(R.id.rviewstory);
        rviewpost=view.findViewById(R.id.rviewPost);
        avatar=view.findViewById(R.id.avatar);
        mess=view.findViewById(R.id.mess);
        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        addStory=view.findViewById(R.id.addStory);
        storage=FirebaseStorage.getInstance();
        story1=view.findViewById(R.id.story1);
        search=view.findViewById(R.id.search);
        dialog=new ProgressDialog(getContext());
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Story đang được tải lên");
        dialog.setMessage("Vui lòng chờ....");
        dialog.setCancelable(false);
    }
}
