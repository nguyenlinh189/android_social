package com.example.btl_v2.adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.media.Image;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl_v2.R;
import com.example.btl_v2.model.Message;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AdapterMessageDetail extends RecyclerView.Adapter{
    private List<Message>listMess=new ArrayList<>();
    private String receiId;
    private String chatId;
    private ProgressDialog dialog;
    int SEND_VIEW_TYPE=1;
    int RECEIVER_VIEW_TYPE=2;
    public AdapterMessageDetail(String receiId, String chatId) {
        listMess=new ArrayList<>();
        this.receiId=receiId;
        this.chatId=chatId;
    }
    public void setList(List<Message>list){
        this.listMess=list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if(listMess.get(position).getuId().equals(FirebaseAuth.getInstance().getUid())){
            return SEND_VIEW_TYPE;
        }else{
            return RECEIVER_VIEW_TYPE;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==SEND_VIEW_TYPE)
        {
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sender,parent,false);
            return new SendViewHolder(view);
        }
        else
        {
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_receiver,parent,false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message=listMess.get(position);
        dialog=new ProgressDialog(holder.itemView.getContext());
        dialog.setTitle("Thông báo");
        dialog.setMessage("Đang tải xuống...");
        dialog.setCancelable(false);
        //Set ngay
        Date date=new Date(message.getTimestamp());
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd/MM h:mm a");
        String strDate=simpleDateFormat.format(date);
        int reactions[]=new int[]{
                R.drawable.ic_like,
                R.drawable.ic_heart,
                R.drawable.ic_happy,
                R.drawable.ic_angry,
                R.drawable.ic_sad,
                R.drawable.ic_surprise
        };
        ReactionsConfig config = new ReactionsConfigBuilder(holder.itemView.getContext())
                .withReactions(reactions)
                .build();
        ReactionPopup popup = new ReactionPopup(holder.itemView.getContext(), config, (pos) -> {
            if (pos>=0 && pos<=5){
                if(holder.getClass()==SendViewHolder.class){
                    SendViewHolder viewHolder=(SendViewHolder) holder;
                    if(pos==message.getFeeling())
                    {
                        ((SendViewHolder) holder).icon.setVisibility(View.GONE);
                        pos=-1;
                    }
                    else{
                        ((SendViewHolder) holder).icon.setImageResource(reactions[pos]);
                        ((SendViewHolder) holder).icon.setVisibility(View.VISIBLE);
                    }
                }else{
                    if(pos==message.getFeeling())
                    {
                        ((ReceiverViewHolder) holder).icon.setVisibility(View.GONE);
                        pos=-1;
                    }else{
                        ((ReceiverViewHolder) holder).icon.setImageResource(reactions[pos]);
                        ((ReceiverViewHolder) holder).icon.setVisibility(View.VISIBLE);
                    }
                }
                message.setFeeling(pos);
                FirebaseDatabase.getInstance().getReference()
                        .child("Messages").child(chatId)
                        .child(message.getMessId()).setValue(message);
            }
            return true; // true is closing popup, false is requesting a new selection
        });

        if(holder.getClass()==SendViewHolder.class)
        {
            ((SendViewHolder) holder).sendText.setText(message.getMessage());
            if(message.getMessage().equals("photo"))
            {
                Picasso.get().load(message.getImage())
                        .into(((SendViewHolder) holder).image);
                ((SendViewHolder) holder).image.setVisibility(View.VISIBLE);
                ((SendViewHolder) holder).sendText.setText("");
            }
            if(message.getMessage().equals("file")){
                ((SendViewHolder) holder).sendText.setText(message.getNameFile());
                ((SendViewHolder) holder).btndownload.setVisibility(View.VISIBLE);
                ((SendViewHolder) holder).btndownload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.show();
                        StorageReference storageRef=FirebaseStorage.getInstance().getReferenceFromUrl(message.getFile());
                        storageRef.getBytes(52428800)// sử dụng để tải file kích thước tệp tối đa là 50Mb
                                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                    @Override
                                    public void onSuccess(byte[] bytes) {// nếu thành công thì lấy byte đã tải xuống và lưu trong một tệp tin trên thiết bị sử dụng out.write
                                        File downloadsFolder= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);// lấy đường dẫn thư mục download trên thiết bị
                                        downloadsFolder.mkdir();// nếu chưa có thì tạo
                                        String filePath=downloadsFolder.getPath()+"/"+message.getNameFile();// tạo đường dẫn đến một tệp cụ thể
                                        try {
                                            FileOutputStream out=new FileOutputStream(filePath);
                                            out.write(bytes);
                                            out.close();
                                            dialog.dismiss();
                                            Toast.makeText(holder.itemView.getContext(), "Tải thành công", Toast.LENGTH_SHORT).show();
                                        } catch (FileNotFoundException e) {
                                            e.printStackTrace();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {// nếu tải xuống không thành công
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        dialog.dismiss();
                                        Toast.makeText(holder.itemView.getContext(), "Tải không thành công", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
            }

            ((SendViewHolder) holder).timerSend.setText(strDate);
            ((SendViewHolder) holder).itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    new AlertDialog.Builder(view.getContext())
                            .setTitle("Xóa tin nhắn").setMessage("Bạn có chắc muốn xóa tin nhắn này không?")
                            .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    FirebaseDatabase.getInstance().getReference()
                                            .child("Messages").child(chatId)
                                            .child(message.getMessId())
                                            .setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                }
                                            });
                                }
                            })
                            .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            }).show();
                    return false;
                }
            });
            ((SendViewHolder) holder).sendText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    popup.onTouch(view,motionEvent);
                    return false;
                }
            });
            ((SendViewHolder) holder).image.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    popup.onTouch(view,motionEvent);
                    return false;
                }
            });
            if(message.getFeeling()>=0 && message.getFeeling()<=6)
            {
                ((SendViewHolder) holder).icon.setImageResource(reactions[message.getFeeling()]);
                ((SendViewHolder) holder).icon.setVisibility(View.VISIBLE);
            }

        }else{
            ((ReceiverViewHolder) holder).receiverText.setText(message.getMessage());
            if(message.getMessage().equals("photo"))
            {
                Picasso.get().load(message.getImage())
                        .into(((ReceiverViewHolder) holder).image);
                ((ReceiverViewHolder) holder).image.setVisibility(View.VISIBLE);
                ((ReceiverViewHolder) holder).receiverText.setText("");
            }
            if(message.getMessage().equals("file")){
                ((ReceiverViewHolder) holder).receiverText.setText(message.getNameFile());
                ((ReceiverViewHolder) holder).btndownload.setVisibility(View.VISIBLE);
                ((ReceiverViewHolder) holder).btndownload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.show();
                        StorageReference storageRef=FirebaseStorage.getInstance().getReferenceFromUrl(message.getFile());
                        storageRef.getBytes(52428800)// sử dụng để tải file kích thước tệp tối đa là 50Mb
                                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                    @Override
                                    public void onSuccess(byte[] bytes) {// nếu thành công thì lấy byte đã tải xuống và lưu trong một tệp tin trên thiết bị sử dụng out.write
                                        File downloadsFolder= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);// lấy đường dẫn thư mục download trên thiết bị
                                        downloadsFolder.mkdir();// nếu chưa có thì tạo
                                        String filePath=downloadsFolder.getPath()+"/"+message.getNameFile();// tạo đường dẫn đến một tệp cụ thể
                                        try {
                                            FileOutputStream out=new FileOutputStream(filePath);
                                            out.write(bytes);
                                            out.close();
                                            dialog.dismiss();
                                            Toast.makeText(holder.itemView.getContext(), "Tải thành công", Toast.LENGTH_SHORT).show();
                                        } catch (FileNotFoundException e) {
                                            e.printStackTrace();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {// nếu tải xuống không thành công
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        dialog.dismiss();
                                        Toast.makeText(holder.itemView.getContext(), "Tải không thành công", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
            }
            ((ReceiverViewHolder) holder).timerReceiver.setText(strDate);
            ((ReceiverViewHolder) holder).itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    new AlertDialog.Builder(view.getContext())
                            .setTitle("Xóa tin nhắn").setMessage("Bạn có chắc muốn xóa tin nhắn này không?")
                            .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    FirebaseDatabase.getInstance().getReference()
                                            .child("Messages").child(chatId)
                                            .child(message.getMessId())
                                            .setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                }
                                            });
                                }
                            })
                            .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            }).show();
                    return false;
                }
            });
            ((ReceiverViewHolder) holder).receiverText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    popup.onTouch(view,motionEvent);
                    return false;
                }
            });
            ((ReceiverViewHolder) holder).image.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    popup.onTouch(view,motionEvent);
                    return false;
                }
            });
            if(message.getFeeling()>=0 && message.getFeeling()<=6)
            {
                ((ReceiverViewHolder) holder).icon.setImageResource(reactions[message.getFeeling()]);
                ((ReceiverViewHolder) holder).icon.setVisibility(View.VISIBLE);
            }
        }

    }

    @Override
    public int getItemCount() {
        return listMess.size();
    }

    public class ReceiverViewHolder extends RecyclerView.ViewHolder{
        private TextView receiverText, timerReceiver;
        private ConstraintLayout constraintLayout;
        private ImageView icon;
        private ImageView image;
        private ImageButton btndownload;
        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            receiverText=itemView.findViewById(R.id.receiverText);
            timerReceiver=itemView.findViewById(R.id.timerReceiver);
            constraintLayout=itemView.findViewById(R.id.constraint);
            icon=itemView.findViewById(R.id.icon);
            image=itemView.findViewById(R.id.image);
            btndownload=itemView.findViewById(R.id.btndownload);
        }
    }
    public class SendViewHolder extends RecyclerView.ViewHolder{
        private TextView sendText, timerSend;
        private ConstraintLayout constraintLayout;
        private ImageView icon;
        private ImageView image;
        private ImageButton btndownload;
        public SendViewHolder(@NonNull View itemView) {
            super(itemView);
            sendText=itemView.findViewById(R.id.sendText);
            timerSend=itemView.findViewById(R.id.timerSend);
            constraintLayout=itemView.findViewById(R.id.constraint);
            icon=itemView.findViewById(R.id.icon);
            image=itemView.findViewById(R.id.image);
            btndownload=itemView.findViewById(R.id.btndownload);
        }
    }
}

