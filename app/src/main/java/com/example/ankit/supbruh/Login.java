package com.example.ankit.supbruh;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.Registrar;

import java.util.HashMap;

public class Login extends AppCompatActivity {

    EditText emailtext ;
    EditText password ;
    EditText username ;
    EditText confirmpassword ;
    Button registeruser;
    Button returntologin;
    FirebaseAuth mauth;
    ProgressDialog registerdialog;
    DatabaseReference mDAtabse;
    private android.support.v7.widget.Toolbar usertablayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        usertablayout = (android.support.v7.widget.Toolbar) findViewById(R.id.blackbarontop);
        setSupportActionBar(usertablayout);
        getSupportActionBar().setTitle("");
        returntologin =(Button)findViewById(R.id.backgologin);


        emailtext=(EditText)findViewById(R.id.registeremail);
        password=(EditText)findViewById(R.id.regissterpassword);
        confirmpassword=(EditText)findViewById(R.id.confirmregisterpassword);
        registeruser=(Button)findViewById(R.id.registerbttn);
        mauth=FirebaseAuth.getInstance();
        username=(EditText)findViewById(R.id.registerusername);
        registerdialog = new ProgressDialog(this);
        registerdialog.setTitle("Registering user");
        registerdialog.setMessage("Please wait while we create your Account");
        registerdialog.setCanceledOnTouchOutside(false);


        returntologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bactologin = new Intent(Login.this, REGISTER.class);
                startActivity(bactologin);
            }
        });


        registeruser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailtext.getText().toString();
                String passwordd = password.getText().toString();
                String confirmpasswordd = confirmpassword.getText().toString();
                String registerusername= username.getText().toString();
                registerdialog.show();

                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(passwordd) && !TextUtils.isEmpty(confirmpasswordd))
                {
                    if(passwordd.equals(confirmpasswordd))
                    {

                        register_user(registerusername,passwordd,email);


                    }
                    else
                    {
                        registerdialog.hide();
                        Toast.makeText(Login.this,"Password does not match  ",Toast.LENGTH_LONG).show();

                    }

                }
                else
                {
                    registerdialog.hide();
                    Toast.makeText(Login.this,"Field empty my dude ",Toast.LENGTH_LONG).show();

                }
            }
        });




    }

    private void register_user(final String registerusername, String passwordd, final String email) {

        mauth.createUserWithEmailAndPassword(email,passwordd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {

                    FirebaseUser current_uesr;
                    current_uesr = FirebaseAuth.getInstance().getCurrentUser();
                    String user_idd =current_uesr.getUid();
                    String token;
                    token = FirebaseInstanceId.getInstance().getToken();

                    mDAtabse=FirebaseDatabase.getInstance().getReference().child("Users").child(user_idd);

                    HashMap<String ,String> usermap = new HashMap<>();
                    usermap.put("name",registerusername);
                    usermap.put("status","Hi im using this app its amazing lol");
                    usermap.put("image","default");
                    usermap.put("thumbimage","default");
                    usermap.put("online","no");
                    usermap.put("device_token",token);
                    usermap.put("id",email);


                    mDAtabse.setValue(usermap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful())
                            {
                                registerdialog.dismiss();
                                Intent loginnow = new Intent(Login.this, REGISTER.class);
                                startActivity(loginnow);
                                finish();

                            }

                        }
                    });
                }
            }
        });
    }
}
