package com.example.btl_v2.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl_v2.R;
import com.example.btl_v2.adapter.AdapterNotification;
import com.example.btl_v2.model.Notification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NotificationFragment extends Fragment {
    private RecyclerView recyclerView;
    private AdapterNotification adapterNotification;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return  inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView=view.findViewById(R.id.rviewNotification);
        //

        adapterNotification=new AdapterNotification(getContext());
        LinearLayoutManager managerFriend=new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false);
        recyclerView.setLayoutManager(managerFriend);
        recyclerView.setAdapter(adapterNotification);
        FirebaseDatabase.getInstance().getReference().child("notification")
                .child(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists())
                        {
                            List<Notification>list=new ArrayList<>();
                            for(DataSnapshot snapshot1:snapshot.getChildren())
                            {
                                Notification notification=snapshot1.getValue(Notification.class);
                                notification.setNotificationID(snapshot1.getKey());
                                list.add(notification);
                            }
                            adapterNotification.setList(list);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
    private int getNotification(){
        return (int)new Date().getTime();
    }
}
