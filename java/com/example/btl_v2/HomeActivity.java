package com.example.btl_v2;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.btl_v2.adapter.MainViewPagerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.*;
import com.google.firebase.iid.internal.FirebaseInstanceIdInternal;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.messaging.FirebaseMessaging;

public class HomeActivity extends AppCompatActivity {
    private BottomNavigationView navigationView;
    private ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();

        //can tao behavior de khi quay lai thi no se lam noi du lieu
        MainViewPagerAdapter adapter=new MainViewPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        //set adapter cho view pager
        viewPager.setAdapter(adapter);
        // neu chon chuyen trang trang o viewpager thi cung phai set cho bottom
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0: navigationView.getMenu().findItem(R.id.mHome).setChecked(true);
                        break;
                    case 1: navigationView.getMenu().findItem(R.id.mnotification).setChecked(true);
                        break;
                    case 2: navigationView.getMenu().findItem(R.id.maddpost).setChecked(true);
                        break;
                    case 3: navigationView.getMenu().findItem(R.id.msearch).setChecked(true);
                        break;
                    case 4: navigationView.getMenu().findItem(R.id.maccount).setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // neu chon o bottom thi cung phai set cho viewpager
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.mHome:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.mnotification:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.maddpost:
                        viewPager.setCurrentItem(2);
                        break;
                    case R.id.msearch:
                        viewPager.setCurrentItem(3);
                        break;
                    case R.id.maccount:
                        viewPager.setCurrentItem(4);
                        break;
                }
                return true;
            }
        });
        // gui token len firebase
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(task.isSuccessful() && task.getResult()!=null)
                {
                    FirebaseDatabase.getInstance().getReference()
                            .child("Users").child(FirebaseAuth.getInstance().getUid())
                            .child("token").setValue(task.getResult());
                }
            }
        });
    }

    private void initView() {
        navigationView=findViewById(R.id.bottom_nav);
        viewPager=findViewById(R.id.viewPager);
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