package com.example.btl_v2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.btl_v2.model.User;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Field;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText username,password;
    private Button btnlogin,btngoogle;
    private TextView tvregister,forgetpassword;
    private ProgressDialog dialog;
    private FirebaseAuth auth;
    GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        auth=FirebaseAuth.getInstance();
        dialog=new ProgressDialog(this);
        tvregister.setOnClickListener(this);
        btnlogin.setOnClickListener(this);
        forgetpassword.setOnClickListener(this);
        // khởi tạo với các cấu hình cho việc đăng nhập google
        GoogleSignInOptions gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient= GoogleSignIn.getClient(this,gso);
        btngoogle.setOnClickListener(this);

    }
    int RC_SIGN_IN=65;
    private void signIn(){
        Intent signInIntent=googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_SIGN_IN){
            Task<GoogleSignInAccount>task=GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account=task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            }catch(NumberFormatException e){

            } catch (ApiException e) {
                Toast.makeText(this, "Dang nhap that bai", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(firebaseCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = auth.getCurrentUser();
                            FirebaseDatabase.getInstance().getReference()
                                    .child("Users").child(user.getUid())
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if(!snapshot.exists()){
                                                User u=new User();
                                                u.setStatus(true);
                                                u.setEmail(user.getEmail());
                                                u.setName(user.getDisplayName());
                                                FirebaseDatabase.getInstance().getReference()
                                                        .child("Users").child(user.getUid())
                                                        .setValue(u);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                            Intent intent=new Intent(getApplicationContext(),HomeActivity.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void initView() {
        username=findViewById(R.id.eemail);
        password=findViewById(R.id.epassword);
        btnlogin=findViewById(R.id.btnlogin);
        tvregister=findViewById(R.id.tvregister);
        forgetpassword=findViewById(R.id.forgetpassword);
        btngoogle=findViewById(R.id.btngoogle);
    }

    @Override
    public void onClick(View view) {
        if(view==btngoogle){
            //Toast.makeText(this, "Duoc an", Toast.LENGTH_SHORT).show();
            signIn();

        }
        if(view==tvregister){
            Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
            startActivity(intent);
        }
        if(view==forgetpassword){
            String email=username.getText().toString();
            if(username.getText().toString().isEmpty())
                Toast.makeText(this, "Nhập email để đặt lại mật khẩu", Toast.LENGTH_SHORT).show();
            else
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                    Toast.makeText(LoginActivity.this, "Kiểm tra email", Toast.LENGTH_SHORT).show();
                            }
                        });
        }
        if(view==btnlogin){
            dialog.show();
            String email=username.getText().toString();
            String pass=password.getText().toString();
            if(email.isEmpty())
            {
                dialog.dismiss();
                Toast.makeText(getApplicationContext(),"Phải điền email",Toast.LENGTH_SHORT).show();
                return;
            }
            if (pass.isEmpty())
            {
                dialog.dismiss();
                Toast.makeText(getApplicationContext(),"Phải nhập pasword",Toast.LENGTH_SHORT).show();
                return;
            }
            if(!email.isEmpty()&& !pass.isEmpty())
            {
                auth.signInWithEmailAndPassword(email.trim(),pass.trim())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                dialog.dismiss();
                                if(task.isSuccessful())
                                {
                                    FirebaseDatabase.getInstance().getReference()
                                            .child("Users")
                                            .child(auth.getUid()).child("status").setValue(true);
                                    Intent intent=new Intent(LoginActivity.this,HomeActivity.class);
                                    startActivity(intent);
                                }
                                else{
                                    Toast.makeText(getApplicationContext(),"Sai thông tin đăng nhập",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }

        }
    }


}