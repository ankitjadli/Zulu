package com.example.ankit.supbruh;


import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    private RecyclerView muserslist;
    private View mViewmain;
    private FirebaseUser currentuser;


    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mViewmain = inflater.inflate(R.layout.fragment_chat,container,false);
        muserslist =(RecyclerView)mViewmain.findViewById(R.id.chatvieww);
        muserslist.setHasFixedSize(true);
        muserslist.setLayoutManager(new LinearLayoutManager(getContext()));
        currentuser = FirebaseAuth.getInstance().getCurrentUser();


        return mViewmain;
    }

    @Override
    public void onStart() {
        super.onStart();
        startListening();
    }
    public void startListening(){
        final DatabaseReference mdatabase;
        final DatabaseReference lastmssg;
        lastmssg=FirebaseDatabase.getInstance().getReference().child("Lasttext");
        mdatabase = FirebaseDatabase.getInstance().getReference();
        final String user_now = currentuser.getUid().toString();
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Friends")
                .child(user_now)
                .limitToLast(50);
        FirebaseRecyclerOptions<showmyfriends> options =
                new FirebaseRecyclerOptions.Builder<showmyfriends>()
                        .setQuery(query, showmyfriends.class)
                        .build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<showmyfriends , ChatFragment.UserViewHolder>(options) {
            @Override
            public ChatFragment.UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.chatboxx, parent, false);

                return new ChatFragment.UserViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final ChatFragment.UserViewHolder holder, int i, @NonNull showmyfriends user) {

                final String user_id = getRef(i).getKey();
                holder.setName(user.getFrom());

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent showfrofile = new Intent(getContext(),chatlayoutttt.class);
                        showfrofile.putExtra("user_id",user_id);
                        startActivity(showfrofile);
                    }
                });
               mdatabase.child("Users").child(user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        String user_image = dataSnapshot.child("image").getValue().toString();
                        String user_name = dataSnapshot.child("name").getValue().toString();
                        String user_status = dataSnapshot.child("status").getValue().toString();
                        String user_is_online = dataSnapshot.child("online").getValue().toString();

                        holder.getid(user_image,user_name,user_status,user_is_online);

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                });


                 lastmssg.child(user_id).child(user_now).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String last_message = dataSnapshot.child("lastmessage").getValue().toString();
                        if (!last_message.equals("")) {
                            holder.setLastmessage(last_message);

                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

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
        public void setLastmessage(String msg) {
            TextView user_naame = (TextView)mView.findViewById(R.id.last_msg);
            user_naame.setText(msg);

        }
        public void getid(String image,String name,String status,String online) {
            CircleImageView imgg = (CircleImageView)mView.findViewById(R.id.chatboyimage);
            Picasso.get().load(image).placeholder(R.drawable.defaultuser).into(imgg);
            TextView user_naame = (TextView)mView.findViewById(R.id.nameofchatperson);
            user_naame.setText(name);
            if (online.equals("yes"))
            {
                ImageView onlinehaikya = (ImageView)mView.findViewById(R.id.online_hai);
                onlinehaikya.setVisibility(View.VISIBLE);
            }
            else
            {
                ImageView onlinehaikya = (ImageView)mView.findViewById(R.id.online_hai);
                onlinehaikya.setVisibility(View.INVISIBLE);

            }
        }
    }
}
