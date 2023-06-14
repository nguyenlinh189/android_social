package com.example.btl_v2.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl_v2.FriendActivity;
import com.example.btl_v2.ImageActivity;
import com.example.btl_v2.LoginActivity;
import com.example.btl_v2.R;
import com.example.btl_v2.UpdateAccount;
import com.example.btl_v2.adapter.AdapterFriend;
import com.example.btl_v2.adapter.AdapterPost;
import com.example.btl_v2.model.Friend;
import com.example.btl_v2.model.Post;
import com.example.btl_v2.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
public class AccountFragment extends Fragment {
    private ImageView avatar,changeAvatar,changCoverPhoto,coverPhoto,more;
    private TextView tvname,tvprofession,follow,friend,photo;
    private Button btnlogout, btnupdateAccount,btnFriend,btnImage;
    private RecyclerView rviewFollowed,rviewPost;
    private AdapterFriend adapterFollowed;
    private FirebaseAuth auth;
    private FirebaseStorage storage;
    private FirebaseDatabase database;
    private int countPhoto=0;
    private ArrayList<String>lisPhoto=new ArrayList<>();
    private User user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account,container,false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        avatar=view.findViewById(R.id.avatar);
        tvname=view.findViewById(R.id.tvname);
        friend=view.findViewById(R.id.friend);
        btnlogout=view.findViewById(R.id.btnlogout);
        btnupdateAccount=view.findViewById(R.id.updateAccount);
        btnFriend=view.findViewById(R.id.btnfriend);
        rviewFollowed=view.findViewById(R.id.rviewfriend);
        changeAvatar=view.findViewById(R.id.changeAvatar);
        changCoverPhoto=view.findViewById(R.id.changeCoverPhoto);
        coverPhoto=view.findViewById(R.id.coverPhoto);
        tvprofession=view.findViewById(R.id.tvnghenghiep);
        photo=view.findViewById(R.id.photo);
        more=view.findViewById(R.id.bacham);
        rviewPost=view.findViewById(R.id.rviewpost);
        btnImage=view.findViewById(R.id.btnImage);
        registerForContextMenu(more);
//        more.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                getActivity().openContextMenu(view);
//            }
//        });

        auth=FirebaseAuth.getInstance();
        storage=FirebaseStorage.getInstance();
        database=FirebaseDatabase.getInstance();
        // lay thong tin nguoi dung luu tru trong database
        database.getReference().child("Users").child(auth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    user=snapshot.getValue(User.class);
                    user.setUserID(snapshot.getKey());
                    Picasso.get().load(user.getCoverPhoto())
                            .placeholder(R.drawable.anhbiadefault)
                            .into(coverPhoto);
                    Picasso.get().load(user.getProfile_image())
                            .placeholder(R.drawable.default_avatar)
                            .into(avatar);
                    tvname.setText(user.getName());
                    tvprofession.setText(user.getProfession());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // loi ket ban
        btnFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), FriendActivity.class);
                startActivity(intent);
            }
        });
        //Thay ảnh bìa
        changCoverPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,11);
            }
        });
        //Thay ảnh đại diện
        changeAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,22);
            }
        });
        // lay thoong tin so ban

        adapterFollowed=new AdapterFriend();
        LinearLayoutManager managerFollowed=new LinearLayoutManager(getContext(),RecyclerView.HORIZONTAL,false);
        rviewFollowed.setNestedScrollingEnabled(false);
        rviewFollowed.setLayoutManager(managerFollowed);
        rviewFollowed.setAdapter(adapterFollowed);

        //lay thong tin so ban be
        database.getReference().child("Friends")
                .child(auth.getUid()).orderByChild("friend").equalTo("banbe")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                            List<Friend>list=new ArrayList<>();
                        if(snapshot.exists()) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Friend f = dataSnapshot.getValue(Friend.class);
                                f.setFriendId(dataSnapshot.getKey());
                                list.add(f);
                            }
                            adapterFollowed.setList(list);
                        }
                            friend.setText(list.size()+"");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        //lay so anh
        database.getReference().child("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                StorageReference reference= FirebaseStorage.getInstance().getReference();
                reference.child(auth.getUid()).listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        lisPhoto=new ArrayList<>();
                        countPhoto=0;
                        photo.setText(listResult.getItems().size()+"");
                        for(StorageReference item:listResult.getItems()){
                            item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    countPhoto=countPhoto+1;
                                    lisPhoto.add(uri.toString());
                                }
                            });
                        }
                        btnImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent=new Intent(getContext(), ImageActivity.class);
                                intent.putStringArrayListExtra("list",lisPhoto);
                                startActivity(intent);
                            }
                        });
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // lay danh sach bai viet
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference();
        AdapterPost adapterpost=new AdapterPost();
        LinearLayoutManager managerDasboard=new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false);
        rviewPost.setLayoutManager(managerDasboard);
        rviewPost.setNestedScrollingEnabled(false);
        rviewPost.setAdapter(adapterpost);
        reference.child("posts").orderByChild("postedBy").equalTo(FirebaseAuth.getInstance().getUid())
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
    }

    //lay thong tin anh tu thu vien va luu vao database
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==11){
            if(data.getData()!=null){
                Uri uri=data.getData();
                coverPhoto.setImageURI(uri);
                database.getReference().child("timestamp").setValue(ServerValue.TIMESTAMP);
                database.getReference().child("timestamp").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        long time=snapshot.getValue(Long.class);
                        final StorageReference reference=storage.getReference().child(FirebaseAuth.getInstance().getUid()).child(time+"");
                        reference.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if(task.isSuccessful())
                                {
                                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            database.getReference().child("Users").child(auth.getUid()).child("coverPhoto").setValue(uri.toString());
                                            Toast.makeText(getContext(), "Thay ảnh bìa thành công", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }else{
            if(data.getData()!=null){
                Uri uri=data.getData();
                avatar.setImageURI(uri);

                database.getReference().child("timestamp").setValue(ServerValue.TIMESTAMP);
                database.getReference().child("timestamp").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        long time=snapshot.getValue(Long.class);
                        final StorageReference reference=storage.getReference().child(FirebaseAuth.getInstance().getUid()).child(time+"");
                        reference.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if(task.isSuccessful())
                                {

                                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            database.getReference().child("Users").child(auth.getUid()).child("profile_image").setValue(uri.toString());
                                            Toast.makeText(getContext(), "Thay ảnh đại diện thành công", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }
    }
    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.menu_account,menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.btnlogout:
                new AlertDialog.Builder(getContext())
                        .setTitle("Thông báo").setMessage("Bạn có chắc muốn đăng xuất không?")
                        .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FirebaseDatabase.getInstance().getReference().child("Users")
                                        .child(FirebaseAuth.getInstance().getUid()).child("status")
                                        .setValue(false);
                                FirebaseAuth.getInstance().signOut();
                                Intent intent=new Intent(getContext(), LoginActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).show();
                //FirebaseAuth.getInstance().notifyAll();
                break;
            case R.id.updateAccount:
                Intent t=new Intent(getContext(), UpdateAccount.class);
                t.putExtra("userId",user.getUserID());
                t.putExtra("userName",user.getName());
                t.putExtra("nghenghiep",user.getProfession());
                t.putExtra("avatar",user.getProfile_image());
                //System.out.println(user.getName()+" "+user.getProfession()+" "+user.getProfile_image());
                startActivity(t);
                break;
        }
        return super.onContextItemSelected(item);
    }
}
