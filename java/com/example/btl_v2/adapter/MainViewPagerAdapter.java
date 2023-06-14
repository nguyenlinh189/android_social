package com.example.btl_v2.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.btl_v2.fragment.AccountFragment;
import com.example.btl_v2.fragment.NotificationFragment;
import com.example.btl_v2.fragment.HomeFragment;
import com.example.btl_v2.fragment.AddPostFragment;
import com.example.btl_v2.fragment.ReelFragment;

public class MainViewPagerAdapter extends FragmentStatePagerAdapter {
    public MainViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: return new HomeFragment();
            case 1: return new NotificationFragment();
            case 2: return new AddPostFragment();
            case 3: return new ReelFragment();
            case 4: return new AccountFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 5;
    }
}
