package com.example.ankit.supbruh;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Chatlogs extends AppCompatActivity {

    private android.support.v7.widget.Toolbar mToolbar;
    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionPagerAdapter;
    private TabLayout mtablayout;
    private DatabaseReference mdatabase;
    private FirebaseUser muser;
    private String userr_now;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatlogs);

        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.mainpagetoolbar);
        mViewPager=(ViewPager)findViewById(R.id.main_tab_pager);
        mSectionPagerAdapter=new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionPagerAdapter);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(" ");

        mtablayout=(TabLayout)findViewById(R.id.main_tabs);

        mtablayout.setupWithViewPager(mViewPager);
        mtablayout.getTabAt(0).setIcon(R.drawable.req);
        mtablayout.getTabAt(1).setIcon(R.drawable.chat);
        mtablayout.getTabAt(2).setIcon(R.drawable.friends);

        muser = FirebaseAuth.getInstance().getCurrentUser();
        userr_now = muser.getUid().toString();
        mdatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userr_now).child("online");


    }

    @Override
    protected void onStart() {
        mdatabase.setValue("yes");
        super.onStart();

    }

    @Override
    protected void onStop() {
        mdatabase.setValue("no");
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);

         getMenuInflater().inflate(R.menu.mainmenu,menu);
    return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId()==R.id.mainlogoutbutton)
        {
            FirebaseAuth.getInstance().signOut();
            Intent loggegout = new Intent(Chatlogs.this,REGISTER.class);
            startActivity(loggegout);
            finish();
        }

        return true;
    }
}
