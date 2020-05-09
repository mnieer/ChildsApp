package com.childs.childsapp;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
//import com.theartofdev.edmodo.cropper.CropImage;
//import com.theartofdev.edmodo.cropper.CropImageView;

public class ProfileUI extends AppCompatActivity {

    private EditText profUserName;
    private ImageButton imageButton;
    private final static int GALLERY_REQ = 1;
    private Button doneBtn;
    private Uri mImageUri = null;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUsers;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile_ui);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mStorageRef = FirebaseStorage.getInstance().getReference().child("profile_images");
        profUserName = (EditText)findViewById(R.id.profUserName);
        imageButton = (ImageButton)findViewById(R.id.imagebutton);
        doneBtn = (Button)findViewById(R.id.doneBtn);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQ);
            }
        });

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = profUserName.getText().toString().trim();
                final String userID = mAuth.getCurrentUser().getUid();
                if (!TextUtils.isEmpty(name) && mImageUri != null){

                    StorageReference filepath = mStorageRef.child(mImageUri.getLastPathSegment());
                    filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener() {
                                @Override
                                public void onSuccess(Object o) {
                                    @SuppressWarnings("VisibleForTests")
                                    String downloadUrl = o.toString();
                                    mDatabaseUsers.child(userID).child("name").setValue(name);
                                    mDatabaseUsers.child(userID).child("image").setValue(downloadUrl);

                                    Toast.makeText(ProfileUI.this, "Successful", Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(ProfileUI.this, LoginUI.class);
                                    startActivity(i);
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Error in Upload !", Toast.LENGTH_LONG).show();
                        }
                    });


                }
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQ && resultCode == RESULT_OK){

            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK){
                mImageUri = result.getUri();
                imageButton.setImageURI(mImageUri);
            }else {
                if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                    Exception err = result.getError();
                }
            }
        }
    }

}
