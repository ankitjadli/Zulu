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

import java.util.Date;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestFragment extends Fragment {
    private RecyclerView muserslist;
    private View mViewmain;
    private FirebaseUser currentuser;
    private Button userstaponpprofile;



    public RequestFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mViewmain = inflater.inflate(R.layout.fragment_request,container,false);
        muserslist =(RecyclerView)mViewmain.findViewById(R.id.showrequests);
        muserslist.setHasFixedSize(true);
        muserslist.setLayoutManager(new LinearLayoutManager(getContext()));

        currentuser = FirebaseAuth.getInstance().getCurrentUser();
        userstaponpprofile = (Button)mViewmain.findViewById(R.id.showprofile);

        userstaponpprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newin = new Intent(getContext(),myuserprofile.class);
                startActivity(newin);
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
                .child("Request_Fragment")
                .child(user_now)
                .limitToLast(50);
        FirebaseRecyclerOptions<requests> options =
                new FirebaseRecyclerOptions.Builder<requests>()
                        .setQuery(query, requests.class)
                        .build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<requests , RequestFragment.UserViewHolder>(options) {
            @Override
            public RequestFragment.UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.gotfriendrequest, parent, false);

                return new RequestFragment.UserViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final RequestFragment.UserViewHolder holder, int i, @NonNull requests user) {
                // Bind the Chat object to the ChatHolder

                final String req_type = user.getMode();
                final String user_id = getRef(i).getKey();
                holder.setName(user.getMode());

                mdatabase.child("Users").child(user_id).addValueEventListener(new ValueEventListener() {
                @Override
                 public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 // String user_image = dataSnapshot.child("image").getValue().toString();
                 String user_name = dataSnapshot.child("name").getValue().toString();

                    holder.getid(user_id,user_name,req_type);



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
        public void getid(String id,String name,String typrofreq) {

            DatabaseReference req = FirebaseDatabase.getInstance().getReference();
            Integer[] images =  {R.drawable.spiderr,
                    R.drawable.blade,
                    R.drawable.capp,
                    R.drawable.bow,
                    R.drawable.spiderr,
                    R.drawable.shield,
                    R.drawable.ironman,
                    R.drawable.hammer,
                    R.drawable.spiders,
                    R.drawable.wolf,
            };

            Random r;
            final String idd = id;
            String capname = name.toUpperCase();
            Date d = new Date();
            final CharSequence s  = DateFormat.format("MMMM d, yyyy ", d.getTime());
            CircleImageView user_req_img = (CircleImageView)mView.findViewById(R.id.picofrequestedperson);
            r = new Random();
           user_req_img.setImageResource(images[r.nextInt(images.length)]);
            TextView user_naame = (TextView)mView.findViewById(R.id.nameofrequestedperson);
            user_naame.setText(capname);
            Button accept = (Button)mView.findViewById(R.id.acceptreq);
            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String user;
                    DatabaseReference removereq;
                    DatabaseReference lastmssg;
                    DatabaseReference removerec;
                    lastmssg=FirebaseDatabase.getInstance().getReference().child("Lasttext");
                    removerec=FirebaseDatabase.getInstance().getReference().child("FriendRequests");
                    FirebaseUser crrnt;
                    crrnt = FirebaseAuth.getInstance().getCurrentUser();
                    removereq = FirebaseDatabase.getInstance().getReference().child("Request_Fragment");
                    user = crrnt.getUid().toString();
                    removerec.child(user).child(idd).child("request_type").removeValue();
                    removerec.child(idd).child(user).child("request_type").removeValue();
                    DatabaseReference req = FirebaseDatabase.getInstance().getReference();
                    req.child("Friends").child(user).child(idd).child("from").setValue(s);
                    req.child("Friends").child(user).child(idd).child("confirm").setValue("yes");
                    req.child("Friends").child(idd).child(user).child("from").setValue(s);
                    req.child("Friends").child(idd).child(user).child("confirm").setValue("yes");
                    lastmssg.child(user).child(idd).child("lastmessage").setValue("Start Chatting");
                    lastmssg.child(idd).child(user).child("lastmessage").setValue("Start Chatting");
                    removereq.child(user).child(idd).child("mode").removeValue();


                }
            });



        }




    }
}
