package com.example.btl_v2.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.btl_v2.R;
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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


public class AddPostFragment extends Fragment {
    private ImageView profile,imagePost,selectImage;
    private TextView name,profession;
    private EditText contentPost;
    private Button btnpost;
    private Uri uri;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private ProgressDialog dialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return  inflater.inflate(R.layout.fragment_addpost, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profile=view.findViewById(R.id.profile);
        imagePost=view.findViewById(R.id.imagePost);
        selectImage=view.findViewById(R.id.selectImage);
        name=view.findViewById(R.id.name);
        contentPost=view.findViewById(R.id.contentPost);
        btnpost=view.findViewById(R.id.btnpost);
        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        profession=view.findViewById(R.id.profession);
        storage=FirebaseStorage.getInstance();
        dialog=new ProgressDialog(getContext());
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Bài viết đang tải");
        dialog.setMessage("Vui lòng chờ đợi ....");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        // lấy thông tin người đăng
        database.getReference().child("Users").child(auth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            User user = snapshot.getValue(User.class);
                            Picasso.get().load(user.getProfile_image())
                                    .placeholder(R.drawable.default_avatar)
                                    .into(profile);
                            name.setText(user.getName());
                            profession.setText(user.getProfession());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        // nếu có sự thay đổi khi nhập nội dung thì sẽ bật nút đăng lên
        contentPost.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String content=contentPost.getText().toString();
                if(!content.isEmpty()){
                    btnpost.setBackgroundDrawable(ContextCompat.getDrawable(getContext(),R.drawable.btn_follow));
                    btnpost.setText("Đăng");
                    btnpost.setTextColor(getContext().getResources().getColor(R.color.white));
                    btnpost.setEnabled(true);
                }else if(uri==null){
                    btnpost.setBackgroundDrawable(ContextCompat.getDrawable(getContext(),R.drawable.btn_follow_active));
                    btnpost.setText("Đăng");
                    btnpost.setTextColor(getContext().getResources().getColor(R.color.gray));
                    btnpost.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //thêm ảnh
        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,10);
            }
        });
        // an nut dang bai
        btnpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                // luu tru anh post trong storage

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
                                        Post post=new Post();
                                        post.setPostImgae(uri.toString());
                                        post.setPostedBy(FirebaseAuth.getInstance().getUid());
                                        post.setPostContent(contentPost.getText().toString());
                                        post.setPostAt(time);

                                        // luu tru thong tin post trong database
                                        database.getReference().child("posts")
                                                .push()
                                                .setValue(post)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful())
                                                        {
                                                            contentPost.setText("");
                                                            imagePost.setVisibility(View.GONE);
                                                            dialog.dismiss();
                                                            Toast.makeText(getContext(), "Bạn đăng thành công bài viết", Toast.LENGTH_SHORT).show();
                                                        }
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
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data.getData()!=null){
            uri=data.getData();
            imagePost.setImageURI(uri);
            btnpost.setBackgroundDrawable(ContextCompat.getDrawable(getContext(),R.drawable.btn_follow));
            btnpost.setText("Đăng");
            btnpost.setTextColor(getContext().getResources().getColor(R.color.white));
            btnpost.setEnabled(true);
        }
    }
}
