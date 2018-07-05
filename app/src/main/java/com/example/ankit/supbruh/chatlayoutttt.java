package com.example.ankit.supbruh;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import org.w3c.dom.Text;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class chatlayoutttt extends AppCompatActivity {

    EditText message;
    private Button Send_message;
    private FirebaseUser crrntuser;
    public RecyclerView muserslist;
    private DatabaseReference musersdatabse;
    private DatabaseReference mdatabse;
    private DatabaseReference ndatabse;
    private DatabaseReference lastmssg;
    private DatabaseReference online;
    String crnt;

    private android.support.v7.widget.Toolbar usertablayout;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        usertablayout = (android.support.v7.widget.Toolbar) findViewById(R.id.blackbarontop);
        setContentView(R.layout.chatlayout);
        online = FirebaseDatabase.getInstance().getReference().child("Users");
        muserslist = (RecyclerView) findViewById(R.id.chatrecyclerview);
        muserslist.setHasFixedSize(true);
        muserslist.setLayoutManager(new LinearLayoutManager(this));
        final String other_user = getIntent().getStringExtra("user_id");
        message = (EditText) findViewById(R.id.getmsg);
        crrntuser = FirebaseAuth.getInstance().getCurrentUser();
        crnt = crrntuser.getUid().toString();
        lastmssg=FirebaseDatabase.getInstance().getReference().child("Lasttext");
        Send_message = (Button) findViewById(R.id.sendurmsg);
            Send_message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String messagetobesent = message.getText().toString();
                    if(!TextUtils.isEmpty(messagetobesent))
                    {

                        String current_user = crrntuser.getUid().toString();
                        message.setText("");

                        mdatabse = FirebaseDatabase.getInstance().getReference().child("Chats")
                                .child(current_user)
                                .child(other_user)
                                .push();
                        String push_id = mdatabse.getKey();
                        ndatabse = FirebaseDatabase.getInstance().getReference().child("Chats")
                                .child(other_user)
                                .child(current_user)
                                .child(push_id);
                        HashMap<String, String> msg = new HashMap<>();
                        msg.put("message", messagetobesent);
                        msg.put("by", current_user);
                        mdatabse.setValue(msg);
                        ndatabse.setValue(msg);
                        lastmssg.child(current_user).child(other_user).child("lastmessage").setValue(messagetobesent);
                        lastmssg.child(other_user).child(current_user).child("lastmessage").setValue(messagetobesent);
                    }
                    else
                    {
                        Toast.makeText(chatlayoutttt.this,"Empty message not allowed bro",Toast.LENGTH_LONG).show();
                    }
                    }

            });

    }

    @Override
    protected void onStart() {
        super.onStart();
       // online.child(crnt).child("online").setValue("yes");
        startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
       // online.child(crnt).child("online").setValue("no");
    }

    public void startListening(){
        FirebaseUser crr;
        final DatabaseReference finduser;
        finduser = FirebaseDatabase.getInstance().getReference().child("Users");
        crr= FirebaseAuth.getInstance().getCurrentUser();
        final String currentuser;
        String other_user = getIntent().getStringExtra("user_id");
        currentuser = crr.getUid().toString();


        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Chats")
                .child(currentuser)
                .child(other_user)
                .limitToLast(50);
        FirebaseRecyclerOptions<chatvi> options =
                new FirebaseRecyclerOptions.Builder<chatvi>()
                            .setQuery(query, chatvi.class)
                        .build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<chatvi , chatlayoutttt.UserViewHolder>(options) {
            @Override
            public chatlayoutttt.UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.chatmessage, parent, false);

                return new chatlayoutttt.UserViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final chatlayoutttt.UserViewHolder holder, int i, @NonNull chatvi user) {
                final String sendbyname = user.getBy();
                holder.setmessage(user.getMessage(),sendbyname);
                finduser.addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final String user_name;
                        final String get_imglink;
                        user_name = dataSnapshot.child(sendbyname).child("name").getValue().toString();

                        holder.setImage(user_name,sendbyname);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        };
        muserslist.setAdapter(adapter);
        adapter.startListening();
        muserslist.scrollToPosition(1);

    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public UserViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }


        public void setImage(String name,String by) {
            FirebaseUser currentuser;
            currentuser = FirebaseAuth.getInstance().getCurrentUser();
            String crrntuser;
            crrntuser = currentuser.getUid().toString();
            if (crrntuser.equals(by)) {
                TextView userNameView = (TextView) mView.findViewById(R.id.chat_username);
                userNameView.setText(name);
                userNameView.setVisibility(View.VISIBLE);

            }
            else
            {
                TextView friendsname = (TextView) mView.findViewById(R.id.friendsname);
                friendsname.setVisibility(View.VISIBLE);
                friendsname.setText(name);

            }

        }
        public void setmessage(String message,String bywho) {
            FirebaseUser currentusermsg;
            currentusermsg = FirebaseAuth.getInstance().getCurrentUser();
            String crrntuser;
            crrntuser = currentusermsg.getUid().toString();
            if (crrntuser.equals(bywho)) {
                TextView userNameView = (TextView) mView.findViewById(R.id.usermessage);
                userNameView.setText(message);
                userNameView.setVisibility(View.VISIBLE);
            }
            else
            {
                TextView userNameView = (TextView) mView.findViewById(R.id.friendsmag);
                userNameView.setText(message);
                userNameView.setVisibility(View.VISIBLE);

            }


        }
    }
}

