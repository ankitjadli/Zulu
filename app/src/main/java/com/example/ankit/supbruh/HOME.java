package com.example.ankit.supbruh;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HOME extends AppCompatActivity {

    FirebaseAuth matuth;
    DatabaseReference mdatabse;

    String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        matuth=FirebaseAuth.getInstance();




}

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser crrntuser;
        crrntuser = matuth.getCurrentUser();
        if (crrntuser==null)
        {
            Intent gotoregisteractivity = new Intent(HOME.this,REGISTER.class);
            startActivity(gotoregisteractivity);
            finish();
        }
        else
        {
            Intent stayandchat = new Intent(HOME.this,Chatlogs.class);
            startActivity(stayandchat);
            finish();

        }
    }
}
