package com.example.btl_v2.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.devlomi.circularstatusview.CircularStatusView;
import com.example.btl_v2.R;
import com.example.btl_v2.model.Story;
import com.example.btl_v2.model.User;
import com.example.btl_v2.model.UserStories;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

public class AdapterStory extends RecyclerView.Adapter<AdapterStory.StoryViewHolder>{
    private List<Story>list;

    public AdapterStory() {
        this.list = new ArrayList<>();
    }
    public void setStory(Story story){
        list.add(story);
    }
    public void setList(List<Story>list){
        this.list=list;
        notifyDataSetChanged();
    }
    public Story getStory(int position){
        return list.get(position);
    }


    @NonNull
    @Override
    public StoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_story_home,parent,false);
        return new StoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoryViewHolder holder, int position) {
        Story story=list.get(position);
        //System.out.println(story.getStories().size());

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference();
        if(story.getStories().size()>0)
        {
            UserStories lastStory=story.getStories().get(story.getStories().size()-1);
            Picasso.get().load(lastStory.getImage())
                    .placeholder(R.drawable.default_avatar)
                    .into(holder.imageStory);
        }
        holder.circular_status_view.setPortionsCount(story.getStories().size());

        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(story.getStoryBy()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user=snapshot.getValue(User.class);
                        Picasso.get().load(user.getProfile_image())
                                .placeholder(R.drawable.default_avatar)
                                .into(holder.avatar);
                        holder.name.setText(user.getName());
                        // check xem story nay da duoc xem chua
                        if(FirebaseAuth.getInstance().getUid()!=null){
                            reference.child("stories").child(story.getStoryBy())
                                    .child("seen").child(FirebaseAuth.getInstance().getUid())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if(snapshot.exists()){
                                                holder.circular_status_view.setPortionsColor(R.color.white);
                                                holder.imageStory.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        holder.circular_status_view.setPortionsColor(R.color.white);
                                                        ArrayList<MyStory>myStories=new ArrayList<>();
                                                        for (UserStories userStories:story.getStories())
                                                            myStories.add(new MyStory(userStories.getImage()));
                                                        new StoryView.Builder(((AppCompatActivity)view.getContext()).getSupportFragmentManager())
                                                                .setStoriesList(myStories)
                                                                .setStoryDuration(5000)
                                                                .setTitleText(user.getName())
                                                                .setSubtitleText("")
                                                                .setTitleLogoUrl(user.getProfile_image())
                                                                .setStoryClickListeners(new StoryClickListeners() {
                                                                    @Override
                                                                    public void onDescriptionClickListener(int position) {

                                                                    }

                                                                    @Override
                                                                    public void onTitleIconClickListener(int position) {


                                                                    }
                                                                }).build().show();
                                                    }
                                                });
                                            }
                                            else{
                                                holder.imageStory.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        holder.circular_status_view.setPortionsColor(R.color.white);
                                                        reference.child("stories").child(story.getStoryBy())
                                                                .child("seen").child(FirebaseAuth.getInstance().getUid())
                                                                .setValue(true);
                                                        ArrayList<MyStory>myStories=new ArrayList<>();
                                                        for (UserStories userStories:story.getStories())
                                                            myStories.add(new MyStory(userStories.getImage()));
                                                        new StoryView.Builder(((AppCompatActivity)view.getContext()).getSupportFragmentManager())
                                                                .setStoriesList(myStories)
                                                                .setStoryDuration(5000)
                                                                .setTitleText(user.getName())
                                                                .setSubtitleText("")
                                                                .setTitleLogoUrl(user.getProfile_image())
                                                                .setStoryClickListeners(new StoryClickListeners() {
                                                                    @Override
                                                                    public void onDescriptionClickListener(int position) {

                                                                    }

                                                                    @Override
                                                                    public void onTitleIconClickListener(int position) {


                                                                    }
                                                                }).build().show();
                                                    }
                                                });
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                        }
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

    public class StoryViewHolder extends RecyclerView.ViewHolder {
        private ImageView avatar,imageStory;
        private TextView name;
        private ConstraintLayout card;
        private CircularStatusView circular_status_view;
        public StoryViewHolder(@NonNull View view) {
            super(view);
            avatar= view.findViewById(R.id.avatar);
            imageStory=view.findViewById(R.id.imagestory);
            name=view.findViewById(R.id.nameaccount);
            card=view.findViewById(R.id.card);
            circular_status_view=view.findViewById(R.id.circular_status_view);
        }
    }
}
