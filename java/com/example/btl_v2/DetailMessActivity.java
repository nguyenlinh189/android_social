package com.example.btl_v2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.btl_v2.adapter.AdapterMessageDetail;
import com.example.btl_v2.model.Message;
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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetailMessActivity extends AppCompatActivity {
    private ImageView leftArrow, profile;
    private TextView name;
    private RecyclerView rviewDetailMess;
    private EditText contentMess;
    private ImageView btnsend,offline,btnImage,btnFile;
    private AdapterMessageDetail adapterDetail;
    private String chatId;
    private String sendId;
    private ProgressDialog dialog;
    private ImageButton btncall;
    private User reUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_mess);
        initView();
        Intent intent=getIntent();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference();
        sendId= FirebaseAuth.getInstance().getUid();
        String receiverId=intent.getStringExtra("receiverId");


        //bat nut quay lai
        leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        reference.child("Users").child(receiverId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                reUser=user;
                name.setText(user.getName());
                Picasso.get().load(user.getProfile_image())
                        .placeholder(R.drawable.default_avatar)
                        .into(profile);
                if (user.isStatus()){
                    offline.setVisibility(View.GONE);
                }else{
                    offline.setVisibility(View.VISIBLE);
                }
                //Toast.makeText(getApplicationContext(), reUser.getToken(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        dialog=new ProgressDialog(this);
        dialog.setMessage("Đang tải ảnh...");
        dialog.setCancelable(false);
        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,30);
            }
        });

        reference.child("Chats").child(sendId).child(receiverId)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                //neu chua chat lan nao thi tao moi
                                if(!snapshot.exists()){
                                    reference.child("Chats").child(sendId).child(receiverId)
                                            .setValue(sendId+receiverId);
                                    reference.child("Chats").child(receiverId).child(sendId)
                                            .setValue(sendId+receiverId);
                                    chatId=sendId+receiverId;
                                    System.out.println("khong ton tai");
                                }else{
                                    chatId=snapshot.getValue(String.class);
                                    System.out.println("Ton tai");
                                }
                                //System.out.println(chatId);
                                //lay danh sach chi tiet tin nhan
                                adapterDetail=new AdapterMessageDetail(receiverId,chatId);
                                LinearLayoutManager manager=new LinearLayoutManager(getApplicationContext(),RecyclerView.VERTICAL,false);
                                rviewDetailMess.setLayoutManager(manager);
                                rviewDetailMess.setAdapter(adapterDetail);
                                reference.child("Messages").child(chatId)
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                List<Message>list=new ArrayList<>();
                                                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                                                {
                                                    Message mess=dataSnapshot.getValue(Message.class);
                                                    mess.setMessId(dataSnapshot.getKey());
                                                    list.add(mess);
                                                }
                                                adapterDetail.setList(list);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                // bat nut gui
                                btnsend.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        String content=contentMess.getText().toString();
                                        Message mess=new Message(sendId,content);
                                        contentMess.setText("");
                                        String key=reference.push().getKey();
                                        reference.child("timestamp").setValue(ServerValue.TIMESTAMP);
                                        reference.child("timestamp").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                mess.setTimestamp(snapshot.getValue(Long.class));
                                                reference.child("Messages").child(chatId).child(key).setValue(mess)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                sendNotification(reUser.getName(),content,reUser.getToken());
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
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
        btnFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("application/*");
                startActivityForResult(intent,31);
            }
        });
//        btncall.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(reUser.getToken()==null || reUser.getToken().trim().isEmpty())
//                    Toast.makeText(DetailMessActivity.this, "Khong the khoi tao duoc cuoc goi", Toast.LENGTH_SHORT).show();
//                else
//                {
//                    Toast.makeText(DetailMessActivity.this, "Dang goi den"+reUser.getName(), Toast.LENGTH_SHORT).show();
//                    Intent intent=new Intent(getApplicationContext(), VideoCallDi.class);
//                    intent.putExtra("receiverId",receiverId);
//                    intent.putExtra("type","video");
//                    startActivity(intent);
//                }
//            }
//        });
    }

    private void initView() {
        leftArrow=findViewById(R.id.leftArrow);
        profile=findViewById(R.id.profile);
        name=findViewById(R.id.name);
        rviewDetailMess=findViewById(R.id.rviewDetailMess);
        contentMess=findViewById(R.id.contentMess);
        btnsend=findViewById(R.id.btnsend);
        offline=findViewById(R.id.offline);
        btnImage=findViewById(R.id.btnImage);
        btnFile=findViewById(R.id.btnFile);
        btncall=findViewById(R.id.btncall);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        FirebaseStorage storage=FirebaseStorage.getInstance();
        if(requestCode==30){
            //System.out.println(requestCode);
            if(data.getData()!=null){
                dialog.show();
                Uri uri=data.getData();
                database.getReference().child("timestamp").setValue(ServerValue.TIMESTAMP);
                database.getReference().child("timestamp").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        long time=snapshot.getValue(Long.class);
                        final StorageReference reference=storage.getReference().child("messages").child("images").child(time+"");
                        reference.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if(task.isSuccessful())
                                {
                                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            dialog.dismiss();
                                            //Toast.makeText(getApplicationContext(), uri.toString(), Toast.LENGTH_SHORT).show();
                                            //database.getReference().child("Users").child(auth.getUid()).child("coverPhoto").setValue(uri.toString());
                                            String content=contentMess.getText().toString();
                                            Message mess=new Message(sendId,content);
                                            mess.setImage(uri.toString());
                                            mess.setMessage("photo");
                                            contentMess.setText("");
                                            String key=database.getReference().push().getKey();
                                            database.getReference().child("timestamp").setValue(ServerValue.TIMESTAMP);
                                            database.getReference().child("timestamp").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    mess.setTimestamp(snapshot.getValue(Long.class));
                                                    database.getReference().child("Messages").child(chatId).child(key).setValue(mess)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
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
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }
        if (requestCode==31){
            if(data.getData()!=null){
                dialog.setMessage("Đang tải file...");
                dialog.show();
                Uri uri=data.getData();
                // lấy tên file được chọn
                // lớp FileProvider triển khai phương thức query() trả về tên và kích thước của tệp
                // được liên kết với uri trong Cursor. Mặc định trả về hai cột DISPLAY_NAME, và SIZE
                Cursor cursor=getContentResolver().query(uri,null,null,null,null);
                int nameIndex=cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                cursor.moveToFirst();
                String filename=cursor.getString(nameIndex);
                database.getReference().child("timestamp").setValue(ServerValue.TIMESTAMP);
                database.getReference().child("timestamp").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        long time=snapshot.getValue(Long.class);
                        final StorageReference reference=storage.getReference().child("messages").child("files").child(filename);
                        reference.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if(task.isSuccessful())
                                {
                                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            dialog.dismiss();
                                            String content=contentMess.getText().toString();
                                            Message mess=new Message(sendId,content);
                                            mess.setMessage("file");
                                            mess.setFile(uri.toString());
                                            mess.setNameFile(filename);
                                            String key=database.getReference().push().getKey();
                                            database.getReference().child("timestamp").setValue(ServerValue.TIMESTAMP);
                                            database.getReference().child("timestamp").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    mess.setTimestamp(snapshot.getValue(Long.class));
                                                    database.getReference().child("Messages").child(chatId).child(key).setValue(mess)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
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
    private void sendNotification(String name,String message,String token){
        try{
            RequestQueue queue= Volley.newRequestQueue(getApplicationContext());
            String url="https://fcm.googleapis.com/fcm/send";
            JSONObject data=new JSONObject();
            data.put("title",name);
            data.put("body",message);
            JSONObject notificationData=new JSONObject();
            notificationData.put("notification",data);
            notificationData.put("to",token);

            JsonObjectRequest request=new JsonObjectRequest(Request.Method.POST,url, notificationData,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String,String>map=new HashMap<>();
                    String key="AAAALlV5iRM:APA91bEKF-CkhIR-0QxSOx-r2Ji_EH085qrxDeczC8KALMx_FYiN2x7uoK4mSXhXuFPBR2xn7B0dsc0sfhcsUk6wsWqwSZS32zkd-PCivVf-zIiv81Wzne2vqaiLHiUdx7lsf-jcxTrr";
                    map.put("Authorization","key="+key);
                    map.put("Content-Type","application/json");
                    return map;
                }
            };
            queue.add(request);
        }catch(Exception e){

        }
    }
}