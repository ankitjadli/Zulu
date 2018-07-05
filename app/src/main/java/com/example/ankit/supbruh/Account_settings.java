package com.example.ankit.supbruh;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.icu.util.Freezable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class Account_settings extends AppCompatActivity {

    private DatabaseReference mdatabse;
    private FirebaseUser currentuser;
    private TextView user_name;
    private TextView use_status;
    private Button changestatus;
    private Button changeimage;
    private CircleImageView user_profile_pic;
    private StorageReference mstorage;
    private ProgressDialog progressDialog;
    private ProgressDialog retriveuserdata;
    private CircleImageView mdisplayimage;

    String current_user;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        currentuser = FirebaseAuth.getInstance().getCurrentUser();
        current_user=currentuser.getUid().toString();
        String Curntuser = currentuser.getUid();
        mdatabse= FirebaseDatabase.getInstance().getReference().child("Users").child(Curntuser) ;
        user_name=(TextView)findViewById(R.id.userdefaultname);
        use_status=(TextView)findViewById(R.id.defaultstatus);
        changestatus=(Button)findViewById(R.id.changestatus);

        changeimage=(Button)findViewById(R.id.changeprofileimage);
        mstorage= FirebaseStorage.getInstance().getReference();
        mdisplayimage=(CircleImageView)findViewById(R.id.settings_image);
        progressDialog = new  ProgressDialog(this);
        progressDialog.setTitle("Uploading profile Image");
        progressDialog.setMessage("Please wait while we are Uploading your image to our servers");
        progressDialog.setCanceledOnTouchOutside(false);


        mdatabse.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                final String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String thumbimage = dataSnapshot.child("thumbimage").getValue().toString();

                user_name.setText(name);
                use_status.setText(status);
                Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.defaultuser).into(mdisplayimage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(image).placeholder(R.drawable.defaultuser).into(mdisplayimage);
                    }
                });
          //      retriveuserdata.dismiss();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        changestatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent changestatusactivity = new Intent(Account_settings.this,Statusupdation.class);
                startActivity(changestatusactivity);
            }
        });

        changeimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(Account_settings.this);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                progressDialog.show();
                final String usernow;
                usernow=currentuser.getUid();
                Uri resultUri = result.getUri();
                StorageReference filepath  = mstorage.child("Profileimages").child(usernow + ".jpg");
                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful())

                        {
                            mstorage.child("Profileimages").child(usernow+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String download_url = uri.toString();
                                    mdatabse.child("image").setValue(download_url).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful())
                                            {
                                                progressDialog.dismiss();
                                            }
                                        }
                                    });

                                }
                            });


                        }
                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();


            }
        }
    }



}
