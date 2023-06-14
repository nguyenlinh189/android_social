package com.example.btl_v2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.btl_v2.model.Post;
import com.example.btl_v2.model.Reel;
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

public class AddReelsActivity extends AppCompatActivity {
    private ImageView profile,selectVideo;
    private TextView name;
    private EditText contentReel;
    private VideoView video;
    private Button btnpost;
    private DatabaseReference reference;
    private String auth;
    private Uri uri1;
    private ProgressDialog dialog;
    private FirebaseStorage storage;
    private ImageView leftarrow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reels);
        initView();
        dialog=new ProgressDialog(AddReelsActivity.this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Thước phim đang được tải lên");
        dialog.setMessage("Vui lòng chờ đợi ....");
        dialog.setCancelable(false);
        reference= FirebaseDatabase.getInstance().getReference();
        auth= FirebaseAuth.getInstance().getUid();
        storage=FirebaseStorage.getInstance();
        // lấy thông tin người đăng
        reference.child("Users").child(auth)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            User user = snapshot.getValue(User.class);
                            Picasso.get().load(user.getProfile_image())
                                    .placeholder(R.drawable.default_avatar)
                                    .into(profile);
                            name.setText(user.getName());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        // nếu có sự thay đổi khi nhập nội dung thì sẽ bật nút đăng lên
        contentReel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String content=contentReel.getText().toString();
                if(!content.isEmpty()){
                    btnpost.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.btn_follow));
                    btnpost.setText("Đăng");
                    btnpost.setTextColor(getResources().getColor(R.color.white));
                    btnpost.setEnabled(true);
                }else if(uri1==null){
                    btnpost.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.btn_follow_active));
                    btnpost.setText("Đăng");
                    btnpost.setTextColor(getResources().getColor(R.color.gray));
                    btnpost.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        //thêm video
        selectVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("video/*");
                startActivityForResult(intent,10);
            }
        });
        btnpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                // luu tru anh post trong storage

                reference.child("timestamp").setValue(ServerValue.TIMESTAMP);
                reference.child("timestamp").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        long time=snapshot.getValue(Long.class);
                        final StorageReference reference1=storage.getReference()
                                .child("reels")
                                .child(time+"");
                        reference1.putFile(uri1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                reference1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Reel reel=new Reel();
                                        reel.setContent(contentReel.getText().toString());
                                        reel.setReelAt(time);
                                        reel.setVideoUrl(uri.toString());
                                        reel.setReelBy(auth);

                                        // luu tru thong tin reel trong database
                                        reference.child("reels")
                                                .push()
                                                .setValue(reel)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful())
                                                        {
                                                            dialog.dismiss();
                                                            Toast.makeText(getApplicationContext(), "Bạn tạo thành công thước phim", Toast.LENGTH_SHORT).show();
                                                            contentReel.setText("");
                                                            video.setVisibility(View.GONE);
                                                            uri1=null;
                                                            btnpost.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.btn_follow_active));
                                                            btnpost.setText("Đăng");
                                                            btnpost.setTextColor(getResources().getColor(R.color.gray));
                                                            btnpost.setEnabled(false);
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
        leftarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initView() {
        profile=findViewById(R.id.profile);
        name=findViewById(R.id.name);
        contentReel=findViewById(R.id.contentReel);
        video=findViewById(R.id.video);
        btnpost=findViewById(R.id.btnpost);
        selectVideo=findViewById(R.id.selectVideo);
        leftarrow=findViewById(R.id.leftarrow);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data.getData()!=null){
            uri1=data.getData();
            video.setVideoURI(uri1);
            video.setVisibility(View.VISIBLE);
            video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    video.start();
                }
            });
            btnpost.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.btn_follow));
            btnpost.setText("Đăng");
            btnpost.setTextColor(getResources().getColor(R.color.white));
            btnpost.setEnabled(true);
        }
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