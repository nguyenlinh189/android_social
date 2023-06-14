package com.example.btl_v2.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.btl_v2.R;
import com.example.btl_v2.adapter.AdapterReel;
import com.example.btl_v2.model.Reel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ReelFragment extends Fragment {
    private ViewPager2 viewPager2;
    private AdapterReel adapterReel;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reel, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        getActivity().requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        viewPager2=view.findViewById(R.id.viewPager2);
        getActivity().getTheme().applyStyle(R.style.FullScreen,false);
        adapterReel=new AdapterReel();
        viewPager2.setAdapter(adapterReel);
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
        reference.child("reels").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Reel>list=new ArrayList<>();
                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    Reel reel=dataSnapshot.getValue(Reel.class);
                    list.add(reel);
                }
                adapterReel.setList(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
