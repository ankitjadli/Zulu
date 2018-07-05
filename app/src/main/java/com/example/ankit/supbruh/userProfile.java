package com.example.ankit.supbruh;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.PriorityQueue;

import de.hdodenhof.circleimageview.CircleImageView;

public class userProfile extends AppCompatActivity {

    private TextView username;
    private CircleImageView pic;
    private TextView Status;
    private FirebaseUser currentuser;
    private DatabaseReference mdatabse;
    private Button SendReq;
    private DatabaseReference requests;
 //   private ProgressDialog progressDialog;
 //   private ProgressDialog progressDialo;
    public int i=0;
    private DatabaseReference typeofbttn;
    public String statsuofreq = "not_friends";
    private Button deletereq;
    private DatabaseReference Reqcollector;
    private DatabaseReference pointtouser;
    private String user_now;
    private DatabaseReference iffriends;
    private DatabaseReference notifications;

   @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_user_profile);
       final String user_key = getIntent().getStringExtra("user_id");

       final String uid;
       currentuser = FirebaseAuth.getInstance().getCurrentUser();
       uid = currentuser.getUid();
       pointtouser = FirebaseDatabase.getInstance().getReference().child("Users");
       user_now = currentuser.getUid().toString();
      iffriends = FirebaseDatabase.getInstance().getReference().child("Friends");
       username = (TextView) findViewById(R.id.usernameforprofile);
       pic = (CircleImageView) findViewById(R.id.user_prifile_image);
       Status = (TextView) findViewById(R.id.profilestatus);
       SendReq = (Button) findViewById(R.id.sendreqtouser);
   notifications = FirebaseDatabase.getInstance().getReference().child("notifications");
       typeofbttn = FirebaseDatabase.getInstance().getReference().child("FriendRequests");
       deletereq = (Button)findViewById(R.id.deletereq);
       deletereq.setVisibility(View.INVISIBLE);
       mdatabse = FirebaseDatabase.getInstance().getReference().child("Users");
       requests = FirebaseDatabase.getInstance().getReference().child("FriendRequests");
       Reqcollector = FirebaseDatabase.getInstance().getReference().child("Request_Fragment");
       mdatabse.keepSynced(true);

       mdatabse.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//               progressDialog.show();

               String name = dataSnapshot.child(user_key).child("name").getValue().toString();
               String status = dataSnapshot.child(user_key).child("status").getValue().toString();
               final String image = dataSnapshot.child(user_key).child("image").getValue().toString();

               username.setText(name);
               Status.setText(status);
               //Picasso.get().load(image).placeholder(R.drawable.hackerimg).into(pic);
  //             progressDialog.dismiss();
               Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE)
                       .placeholder(R.drawable.defaultuser).into(pic, new Callback() {
                   @Override
                   public void onSuccess() {

                   }

                   @Override
                   public void onError(Exception e) {
                       Picasso.get().load(image).placeholder(R.drawable.hackerimg).into(pic);
                   }
               });
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });



     iffriends.child(user_now).addValueEventListener(new ValueEventListener() {
         @Override
         public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
             if(dataSnapshot.hasChild(user_key))
             {

             String confirmiffriends = dataSnapshot.child(user_key).child("confirm").getValue().toString();

             if (confirmiffriends.equals("yes"))
             {
                 statsuofreq="alreadyfriends";
                 SendReq.setText("UnFriend");
                 SendReq.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         SendReq.setText("Send Friend Request");
                         iffriends.child(user_now).child(user_key).child("from").removeValue();
                         iffriends.child(user_now).child(user_key).child("confirm").removeValue();
                         iffriends.child(user_key).child(user_now).child("from").removeValue();
                         iffriends.child(user_key).child(user_now).child("confirm").removeValue();
                         statsuofreq="not_friends";
                     }
                 });
             }
         }

         }

         @Override
         public void onCancelled(@NonNull DatabaseError databaseError) {

         }
     });

      typeofbttn.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


               if ((dataSnapshot.hasChild(user_key)))
               {
                   final String type =  dataSnapshot.child(user_key).child("request_type").getValue().toString();

                   if(type.equals("rec"))
                   {
                       statsuofreq="req_recived";
                       SendReq.setText("Accept Friend Request");
                       deletereq.setVisibility(View.VISIBLE);
                       deletereq.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View v) {
                               typeofbttn.child(uid).child(user_key).child("request_type").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                   @Override
                                   public void onSuccess(Void aVoid) {
                                       typeofbttn.child(user_key).child(uid).child("request_type").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                           @Override
                                           public void onSuccess(Void aVoid) {
                                               Reqcollector.child(user_key).child(uid).child("mode").removeValue();
                                               Reqcollector.child(uid).child(user_key).child("mode").removeValue();
                                               deletereq.setVisibility(View.INVISIBLE);
                                               statsuofreq="not_friends";
                                               SendReq.setText("Send Friend Request");

                                           }
                                       });

                                   }
                               });
                           }
                       });
                   }
                   else
                   {
                       statsuofreq="Friends";
                       SendReq.setText("Cancel Friend Request");

                   }


               }

           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });

       if (user_key.equals(uid)) {
           SendReq.setVisibility(View.INVISIBLE);
       }else{

               SendReq.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       if (statsuofreq.equals("not_friends")) {
                       SendReq.setEnabled(false);
                       requests.child(uid).child(user_key).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull Task<Void> task) {
                               if (task.isSuccessful()) {
                                   requests.child(user_key).child(uid).child("request_type").setValue("rec").addOnCompleteListener(new OnCompleteListener<Void>() {
                                       @Override
                                       public void onComplete(@NonNull Task<Void> task) {
                                           if (task.isSuccessful()) {
                                               Reqcollector.child(user_key).child(uid).child("mode").setValue("received");
                                              HashMap<String,String> data_for_notification = new HashMap<>();
                                              data_for_notification.put("from",uid);
                                              data_for_notification.put("type","request");
                                              notifications.child(user_key).push().setValue(data_for_notification);
                                               SendReq.setEnabled(true);
                                               statsuofreq="Friends";
                                               SendReq.setText("Cancel Friend Request");
                                           }
                                       }
                                   });
                               }
                           }
                       });
                   }
                   if (statsuofreq.equals("Friends"))
                   {
                       requests.child(uid).child(user_key).child("request_type").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                           @Override
                           public void onSuccess(Void aVoid) {

                               requests.child(user_key).child(uid).child("request_type").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                   @Override
                                   public void onSuccess(Void aVoid) {
                                       Reqcollector.child(user_key).child(uid).child("mode").removeValue();
                                       SendReq.setEnabled(true);
                                       statsuofreq="not_friends";
                                       SendReq.setText("Send Friend Request");

                                   }
                               });
                           }
                       });
                   }

                   }
               });



       }
   }

}