package com.example.ankit.supbruh;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Statusupdation extends AppCompatActivity {

    private android.support.v7.widget.Toolbar mtablayout;
    private TextInputEditText newstatus;
    private Button changestatsubttn;
    private FirebaseUser currentuser;
    private DatabaseReference mdatabse;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statusupdation);

        mtablayout=(android.support.v7.widget.Toolbar)findViewById(R.id.statusupdatetoolbar);
        setSupportActionBar(mtablayout);
        getSupportActionBar().setTitle("SUP BRUH");

        newstatus=(TextInputEditText)findViewById(R.id.enterstatus);
        changestatsubttn=(Button)findViewById(R.id.updatestatus);
        currentuser= FirebaseAuth.getInstance().getCurrentUser();
        final String user_id;
        user_id=currentuser.getUid();
        mdatabse= FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("status");
        progressDialog= new ProgressDialog(this);
        progressDialog.setTitle("Updating Status");
        progressDialog.setMessage("Please wait while we are updating your status");
        progressDialog.setCanceledOnTouchOutside(false);


        changestatsubttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_status = newstatus.getText().toString();
                if(!TextUtils.isEmpty(user_status))
                {
                    progressDialog.show();
                    mdatabse.setValue(user_status).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                progressDialog.hide();
                                Toast.makeText(Statusupdation.this,"Status is Updated",Toast.LENGTH_LONG).show();

                            }
                            else
                            {
                                String error = task.getException().toString();
                                Toast.makeText(Statusupdation.this,""+error,Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                else
                {
                    Toast.makeText(Statusupdation.this,"Field is Empty",Toast.LENGTH_LONG).show();
                }
            }
        });

    }




}
