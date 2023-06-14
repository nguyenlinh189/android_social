package com.example.btl_v2.adapter;

import android.content.Intent;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl_v2.AddReelsActivity;
import com.example.btl_v2.R;
import com.example.btl_v2.model.Reel;
import com.example.btl_v2.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AdapterReel extends RecyclerView.Adapter<AdapterReel.VideoViewHolder> {
    private List<Reel>list;
    private boolean isStatus=false;

    public void setList(List<Reel> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public AdapterReel() {
        list=new ArrayList<>();
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.reel_layout,parent,false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        Reel reel=list.get(position);
        holder.videoView.setVideoPath(reel.getVideoUrl());


        holder.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
                isStatus=true;
                holder.seekBar.setMax(holder.videoView.getDuration());
            }
        });
        holder.videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
                isStatus=true;
            }
        });
        holder.videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.videoView.isPlaying()==true){
                    holder.videoView.pause();
                    isStatus=false;
                    holder.ic_pause.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            holder.ic_pause.setVisibility(view.GONE);
                        }
                    },1000);
                }else{
                    //holder.videoView.resume();
                    holder.videoView.seekTo(holder.videoView.getCurrentPosition());
                    holder.videoView.start();
                    holder.ic_play.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            holder.ic_play.setVisibility(view.GONE);
                        }
                    },1000);
                    isStatus=true;
                }
            }
        });

        holder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                holder.videoView.seekTo(seekBar.getProgress());
            }
        });
        // cap nhat trang thai cho thanh thoi gian
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(holder.videoView!=null){
                    holder.seekBar.setProgress(holder.videoView.getCurrentPosition());
                    handler.postDelayed(this,30);
                }
            }
        },30);
        // them reels moi
        holder.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(holder.itemView.getContext(), AddReelsActivity.class);
                holder.itemView.getContext().startActivity(intent);
            }
        });
        holder.content.setText(reel.getContent());
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
            reference.child("Users")
                    .child(reel.getReelBy()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User user=snapshot.getValue(User.class);
                            Picasso.get().load(user.getProfile_image())
                                    .placeholder(R.drawable.default_avatar)
                                    .into(holder.profile);
                            holder.name.setText(user.getName());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder{
        private VideoView videoView;
        private ImageView profile;
        private TextView name,content;
        private ImageView ic_play, ic_pause;
        private SeekBar seekBar;
        private Button btnAdd;
        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            videoView=itemView.findViewById(R.id.videoView);
            profile=itemView.findViewById(R.id.profile);
            name=itemView.findViewById(R.id.name);
            ic_play=itemView.findViewById(R.id.ic_play);
            ic_pause=itemView.findViewById(R.id.ic_pause);
            seekBar=itemView.findViewById(R.id.seekBar);
            btnAdd=itemView.findViewById(R.id.btnAdd);
            content=itemView.findViewById(R.id.contentReel);
        }
    }

}
