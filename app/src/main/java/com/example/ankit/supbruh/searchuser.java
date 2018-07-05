package com.example.ankit.supbruh;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class searchuser extends AppCompatActivity {

    private android.support.v7.widget.Toolbar usertablayout;
    private RecyclerView muserslist;
    private DatabaseReference musersdatabse;
    private DatabaseReference pointtouser;
    private FirebaseUser mcueewntuser;
    private String current_user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchuser);
        muserslist = (RecyclerView) findViewById(R.id.userslist);
        muserslist.setHasFixedSize(true);
        muserslist.setLayoutManager(new LinearLayoutManager(this));
        musersdatabse = FirebaseDatabase.getInstance().getReference().child("Users");
        mcueewntuser = FirebaseAuth.getInstance().getCurrentUser();
        current_user = mcueewntuser.getUid().toString();

    }

    @Override
    protected void onStart() {
        super.onStart();
        final EditText search;
        Button s;
        search = (EditText) findViewById(R.id.searchtext) ;
        s = (Button) findViewById(R.id.search) ;
        s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchtext;
                searchtext = search.getText().toString();

                if (!TextUtils.isEmpty(searchtext)) {

                    startListening(searchtext);
                }
                else

                {
                    Toast.makeText(searchuser.this, "Enter Email Address", Toast.LENGTH_LONG).show();
                }
            }
        });

    }


    public void startListening(String searchtext){
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Users").orderByChild("id").startAt(searchtext).endAt(searchtext + "\uf8ff")
                .limitToLast(50);
        FirebaseRecyclerOptions<users> options =
                new FirebaseRecyclerOptions.Builder<users>()
                        .setQuery(query, users.class)
                        .build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<users , searchuser.UserViewHolder>(options) {
            @Override
            public searchuser.UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.users_single_layout, parent, false);

                return new searchuser.UserViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull searchuser.UserViewHolder holder, int i, @NonNull users user) {
                // Bind the Chat object to the ChatHolder
                holder.setName(user.getName());
                holder.setStatus(user.getStatus());
                holder.setImage(user.getImage());


                final String user_id = getRef(i).getKey();
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent showfrofile = new Intent(searchuser.this,userProfile.class);
                        showfrofile.putExtra("user_id",user_id);
                        startActivity(showfrofile);

                    }
                });
                //ppdialog.dismiss();

                // ...
            }


        };
        muserslist.setAdapter(adapter);
        adapter.startListening();


    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public UserViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String name) {
            TextView userNameView = (TextView) mView.findViewById(R.id.displaytheusername);
            userNameView.setText(name);
        }
        public void setImage(String image) {
            CircleImageView user_image = (CircleImageView) mView.findViewById(R.id.thumbnail);
            Picasso.get().load(image).placeholder(R.drawable.hackerimg).into(user_image);
        }
        public void setStatus(String status ) {

            TextView userNameView = (TextView) mView.findViewById(R.id.status);
            userNameView.setText(status);



        }

    }
}
