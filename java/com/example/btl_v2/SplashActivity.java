package com.example.btl_v2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.example.btl_v2.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                nextActivity();
            }
        },2000);
    }

    private void nextActivity() {
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if(user==null){
            //chua login/register
            Intent intent =new Intent(this,MainActivity.class);
            startActivity(intent);
        }else{
            //da login/register
            FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("status").setValue(true);
            Intent intent=new Intent(SplashActivity.this,HomeActivity.class);
            startActivity(intent);
        }
        finish();
    }
}