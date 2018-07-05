package com.example.ankit.supbruh;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class myuserprofile extends AppCompatActivity {

    private TextView username;
    private TextView userstatus;
    private Button changeimage;
    private Button changestatus;
    private StorageReference mstorage;
    private FirebaseUser mcurrentuser;
    private String midofuser;
    private DatabaseReference mdatabse;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myuserprofile);


        username = (TextView) findViewById(R.id.hiusertext);
        userstatus = (TextView) findViewById(R.id.userstatushere);
        changeimage = (Button) findViewById(R.id.changge_img);
        changestatus =(Button) findViewById(R.id.changestats);
        mstorage= FirebaseStorage.getInstance().getReference();
        progressDialog = new  ProgressDialog(this);
        progressDialog.setTitle("Uploading profile Image");
        progressDialog.setMessage("Please wait while we are Uploading your image to our servers");
        progressDialog.setCanceledOnTouchOutside(false);
        mcurrentuser = FirebaseAuth.getInstance().getCurrentUser();
        midofuser = mcurrentuser.getUid().toString();
        mdatabse = FirebaseDatabase.getInstance().getReference().child("Users").child(midofuser);
        mdatabse.keepSynced(true);


        mdatabse.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String getusername;
                String getuserstatus;
               getusername =  dataSnapshot.child("name").getValue().toString().toUpperCase();
                getuserstatus =  dataSnapshot.child("status").getValue().toString();
               username.setText("Hi! "+getusername);
               userstatus.setText(getuserstatus);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        changestatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent changestatusactivity = new Intent(myuserprofile.this,Statusupdation.class);
                startActivity(changestatusactivity);
            }
        });

        changeimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(myuserprofile.this);

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
                usernow=mcurrentuser.getUid();
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
