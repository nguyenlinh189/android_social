package com.example.btl_v2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.btl_v2.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText email,password,name,profession;
    private Button btnregister;
    private TextView elogin;
    private FirebaseAuth mAuth;
    private ProgressDialog dialog;
    private FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        btnregister.setOnClickListener(this);
        elogin.setOnClickListener(this);
        mAuth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        dialog=new ProgressDialog(this);
    }

    private void initView() {
        email=findViewById(R.id.eemail);
        password=findViewById(R.id.epassword);
        name=findViewById(R.id.ename);
        profession=findViewById(R.id.enghenghiep);
        btnregister=findViewById(R.id.btnregister);
        elogin=findViewById(R.id.elogin);

    }

    @Override
    public void onClick(View view) {
        if(elogin==view){
            Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
            startActivity(intent);
        }
        if(btnregister==view){
            dialog.show();
            String names=name.getText().toString();
            if(names.isEmpty())
            {
                dialog.dismiss();
                Toast.makeText(getApplicationContext(),"Vui long nhập tên",Toast.LENGTH_SHORT).show();
                return;
            }
            String nghenghiep=profession.getText().toString();
            if(nghenghiep.isEmpty())
            {
                dialog.dismiss();
                Toast.makeText(getApplicationContext(),"Vui long nhập nghề nghiệp",Toast.LENGTH_SHORT).show();
                return;
            }
            String mail=email.getText().toString();
            if(mail.isEmpty())
            {
                dialog.dismiss();
                Toast.makeText(getApplicationContext(),"Vui lòng nhập email",Toast.LENGTH_SHORT).show();
                return;
            }
            String pass=password.getText().toString();
            if(pass.isEmpty())
            {
                dialog.dismiss();
                Toast.makeText(getApplicationContext(),"Vui lòng nhập password",Toast.LENGTH_SHORT).show();
                return;
            }
            if(!mail.isEmpty()&&!pass.isEmpty())
            {

                mAuth.createUserWithEmailAndPassword(mail, pass)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                dialog.dismiss();
                                if(task.isSuccessful())
                                {
                                    Toast.makeText(getApplicationContext(),"Đăng ký thành công",Toast.LENGTH_SHORT).show();
                                    User u=new User(name.getText().toString(),profession.getText().toString(),mail);
                                    String id=task.getResult().getUser().getUid();
                                    database.getReference().child("Users").child(id).setValue(u);

                                    Intent intent=new Intent(RegisterActivity.this,HomeActivity.class);
                                    startActivity(intent);
                                    // dong tat ca cac activity
                                    finishAffinity();
                                }
                                else{
                                    Toast.makeText(RegisterActivity.this, "Đăng kí không thành công", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }

        }
    }
}