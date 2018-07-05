package com.example.ankit.supbruh;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;


public class FriendsFragment extends Fragment {

    private RecyclerView muserslist;
    private View mViewmain;
    private FirebaseUser currentuser;
    private Button addnew_friends;



    public FriendsFragment() {
        
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mViewmain = inflater.inflate(R.layout.fragment_friends,container,false);
        muserslist =(RecyclerView)mViewmain.findViewById(R.id.showfriends);
        muserslist.setHasFixedSize(true);
        muserslist.setLayoutManager(new LinearLayoutManager(getContext()));
        currentuser = FirebaseAuth.getInstance().getCurrentUser();
        addnew_friends = (Button)mViewmain.findViewById(R.id.addfriend);

        addnew_friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext() , searchuser.class);
                startActivity(intent);

            }
        });


        return mViewmain;
    }

    @Override
    public void onStart() {
        super.onStart();
        startListening();
    }
    public void startListening(){
         final DatabaseReference mdatabase;
        mdatabase = FirebaseDatabase.getInstance().getReference();
        String user_now = currentuser.getUid().toString();
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Friends")
                .child(user_now)
                .limitToLast(50);
        FirebaseRecyclerOptions<showmyfriends> options =
                new FirebaseRecyclerOptions.Builder<showmyfriends>()
                        .setQuery(query, showmyfriends.class)
                        .build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<showmyfriends , FriendsFragment.UserViewHolder>(options) {
            @Override
            public FriendsFragment.UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.showfriends, parent, false);

                return new FriendsFragment.UserViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final FriendsFragment.UserViewHolder holder, int i, @NonNull showmyfriends user) {
                // Bind the Chat object to the ChatHolder

                //final String req_type = user.getFrom();
                final String user_id = getRef(i).getKey();
                holder.setName(user.getFrom());

                mdatabase.child("Users").child(user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        String user_image = dataSnapshot.child("image").getValue().toString();
                        String user_name = dataSnapshot.child("name").getValue().toString();
                        String user_status = dataSnapshot.child("status").getValue().toString();

                        holder.getid(user_image,user_name,user_status);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent showprofile = new Intent(getContext(),userProfile.class);
                        showprofile.putExtra("user_id",user_id);
                        startActivity(showprofile);
                    }
                });

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
            }


        public void getid(String image,String name,String stats) {
           CircleImageView user_req_img = (CircleImageView)mView.findViewById(R.id.picoffriends);
            Picasso.get().load(image).placeholder(R.drawable.defaultuser).into(user_req_img);
            TextView user_naame = (TextView)mView.findViewById(R.id.nameoffriends);
            user_naame.setText(name);
            TextView Status = (TextView)mView.findViewById(R.id.statusoffriends);
            Status.setText(stats);


        }




    }
}
