package com.ahmet.postphotos.UI.UserData.Data;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmet.postphotos.Config.CheckInternetConnection;
import com.ahmet.postphotos.R;
import com.ahmet.postphotos.UI.Account.Profile.EditStatus;
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
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

import static android.R.attr.password;
import static com.ahmet.postphotos.R.drawable.email;

public class EditData extends AppCompatActivity {

    Toolbar mToolbar;

    private CircleImageView mImageProfile;
    private EditText mUsername;
    private EditText mPhone;
    private EditText mAge;
    //private EditText mStatus;
    private RadioGroup mGender;
    private RadioButton mMail,mFemail;
    private TextView mOptionChangeImage;
    private Button btnEditProfile;
    private ProgressBar mProgressProfile;
    private TextView mChangeStatus;

    private String genderAll;

    private static int GALLERY_PICK = 88;
    private static final int GALLERY_REQUEST_CODE = 5;
    private static final int CAMERA_REQUEST_CODE = 6;

    Bitmap thumbBitmap;


    private DatabaseReference mRefAddDataUser;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference mRef;
    private StorageReference mRefStorageIMG;


    private CheckInternetConnection mCheckInternetConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_data);

        mRefStorageIMG = FirebaseStorage.getInstance().getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final String userUID = firebaseUser.getUid();

        mRefAddDataUser = FirebaseDatabase.getInstance().getReference().child("users").child(userUID);
        mRef = FirebaseDatabase.getInstance().getReference().child("users").child(userUID);
        mRef.keepSynced(true);
        mAuth = FirebaseAuth.getInstance();

        mCheckInternetConnection = new CheckInternetConnection(this);

        getDataUser();

//        String username = getIntent().getStringExtra("username");
//        String phone = getIntent().getStringExtra("phone");
//        String age = getIntent().getStringExtra("age");
//        final String gender_ = getIntent().getStringExtra("gender");
//        String status = getIntent().getStringExtra("statusValue");

        mToolbar = findViewById(R.id.edit_profile_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.editdata);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //mImageProfile = findViewById(R.id.editImageProfile);
        mUsername = findViewById(R.id.textNameEditUser);
        mPhone = findViewById(R.id.textPhoneEditUser);
        mAge = findViewById(R.id.textAgeEditUser);
        // mStatus = findViewById(R.id.textStatusEditUser);
        mGender = findViewById(R.id.edit_Gender);
        mMail = findViewById(R.id.edit_mail);
        mFemail = findViewById(R.id.edit_femail);
        mImageProfile = findViewById(R.id.editImageProfile);
        mOptionChangeImage = findViewById(R.id.option_change_image);
        btnEditProfile = findViewById(R.id.btnEditData);
        mProgressProfile = findViewById(R.id.progressbar_edit_profile);
        mChangeStatus = findViewById(R.id.change_status);



        mChangeStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EditData.this, EditStatus.class));
            }
        });

        mGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int position) {

                if (position == R.id.edit_mail){
                    genderAll = mMail.getText().toString();
                    // Toast.makeText(EditData.this, genderAll, Toast.LENGTH_SHORT).show();
                }else if (position == R.id.edit_femail){
                    genderAll = mFemail.getText().toString();
                    // Toast.makeText(EditData.this, genderAll, Toast.LENGTH_SHORT).show();
                }

            }
        });

        mOptionChangeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                optionChangeImage();

//                Intent galleryIntent = new Intent();
//                galleryIntent.setType("image/*");
//                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
//
//                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);
//
////                CropImage.activity()
////                        .setGuidelines(CropImageView.Guidelines.ON)
////                        .start(Profile.this);
            }
        });

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = mUsername.getText().toString();
                String phone = mPhone.getText().toString();
                String age = mAge.getText().toString();
                //  String status = mStatus.getText().toString();

                if (true) {
                    mRefAddDataUser.child("name").setValue(name);
                    mRefAddDataUser.child("phone").setValue(phone);
                    mRefAddDataUser.child("age").setValue(age);
                    mRefAddDataUser.child("gemder").setValue(genderAll);
                    // mRefAddDataUser.child("gender").setValue(gender);
                    // mRefAddDataUser.child("status").setValue(status);

                    Snackbar snackbar = Snackbar.make(findViewById(R.id.linear_edit_profile)
                            ,R.string.edit_data_success
                            ,Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    //startActivity(new Intent(AddData.this, AllUsers.class));
                }else {
                    Toast.makeText(EditData.this, R.string.error_to_change_email_pass, Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void optionChangeImage(){

        final Dialog dialogOPtionImage = new Dialog(EditData.this,R.style.Theme_AppCompat_DayNight_Dialog);
        dialogOPtionImage.setContentView(R.layout.raw_option_change_image);
        TextView mOPenCamera = dialogOPtionImage.findViewById(R.id.text_open_camera);
        TextView mChoseGallery = dialogOPtionImage.findViewById(R.id.text_chose_gallery);
        TextView mRemoveImage = dialogOPtionImage.findViewById(R.id.text_remove_image);

        mOPenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkPermissionsCamira();


                Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                try {
                    startActivityForResult(openCameraIntent, CAMERA_REQUEST_CODE);
                }catch (Exception ex){
                    // Toast.makeText(EditData.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                }

                dialogOPtionImage.dismiss();
            }
        });
        mChoseGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkPermissionsGallery();

                Intent openGallerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                openGallerIntent.setType("image/*");
                startActivityForResult(openGallerIntent, GALLERY_REQUEST_CODE);
                dialogOPtionImage.dismiss();

            }
        });
        mRemoveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeImageProfile();
                dialogOPtionImage.dismiss();
            }
        });

        dialogOPtionImage.show();

    }

    private void removeImageProfile(){

        final String currentUser = mAuth.getCurrentUser().getUid();
        mRefAddDataUser.child(currentUser).child("image").removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mRefAddDataUser.child(currentUser).child("image")
                                .setValue(R.drawable.default_avatar2)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Snackbar snackbar = Snackbar.make(findViewById(R.id.linear_edit_profile), "Remove Image Successfull", Snackbar.LENGTH_SHORT);
                                            snackbar.show();
                                        }
                                    }
                                });

                    }
                });

        Picasso.with(getApplicationContext())
                .load(R.drawable.default_avatar2)
                .into(mImageProfile);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        boolean checkInternet = mCheckInternetConnection.isConnectingToInternet();
        if (!checkInternet){
            Snackbar snackbar2 = Snackbar.make(findViewById(R.id.linear_edit_profile),
                    R.string.check_internet,
                    Snackbar.LENGTH_LONG);
            snackbar2.show();
        }

        if ((requestCode == GALLERY_REQUEST_CODE || requestCode == CAMERA_REQUEST_CODE) && resultCode == RESULT_OK){

            Uri imageURI = data.getData();

            CropImage.activity(imageURI)
                    .setAspectRatio(1, 1)
                    //.setMinCropWindowSize(500, 500)
                    .start(this);

//            Toast.makeText(this, imageURI, Toast.LENGTH_SHORT).show();
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mProgressProfile.setVisibility(View.VISIBLE);

                Uri resultUri = result.getUri();

                File thumbFilePath = new File(resultUri.getPath());

                String currentUID = firebaseUser.getUid();

                try {
                    thumbBitmap = new Compressor(this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(75)
                            .compressToBitmap(thumbFilePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumbBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] thumb_byte = baos.toByteArray();

                StorageReference mRefImage = mRefStorageIMG.child("profile_images").child(currentUID + ".jpg");
                final StorageReference mRefFilePath = mRefStorageIMG.child("profile_images").child("thumbs").child(currentUID + ".jpg");

                mRefImage.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){

                            final String imageUrl = task.getResult().getDownloadUrl().toString();

                            final UploadTask uploadTask = mRefFilePath.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumbTask) {

                                    String thumbImageUrl = thumbTask.getResult().getDownloadUrl().toString();

                                    if (thumbTask.isSuccessful()){

                                        Map uploadThumbImageMap = new HashMap<>();
                                        uploadThumbImageMap.put("image", imageUrl);
                                        uploadThumbImageMap.put("thumb_image", thumbImageUrl);

                                        mRef.updateChildren(uploadThumbImageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful()){
                                                    //mAccountProg.dismiss();
                                                    mProgressProfile.setVisibility(View.INVISIBLE);
                                                    //Toast.makeText(Profile.this, "Success Thumb Image Uploading.", Toast.LENGTH_SHORT).show();
                                                }

                                            }
                                        });

                                    }else {
                                        // mAccountProg.dismiss();
                                        mProgressProfile.setVisibility(View.INVISIBLE);
                                        Snackbar snackbar = Snackbar.make(findViewById(R.id.linear_edit_profile)
                                                ,R.string.error_in_uploading_image
                                                ,Snackbar.LENGTH_SHORT);
                                        snackbar.show();
                                    }

                                }
                            });


                        }else {
                            //mAccountProg.dismiss();
                            mProgressProfile.setVisibility(View.INVISIBLE);
                            Snackbar snackbar = Snackbar.make(findViewById(R.id.linear_edit_profile)
                                    ,R.string.error_to_change_email_pass
                                    ,Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        }
                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }


    private void getDataUser(){



        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String imageUser = dataSnapshot.child("image").getValue().toString();
                String name = dataSnapshot.child("name").getValue().toString();
                String phone = dataSnapshot.child("phone").getValue().toString();
                String age = dataSnapshot.child("age").getValue().toString();
                String gender = dataSnapshot.child("gender").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();

                mUsername.setText(name);
                mPhone.setText(phone);
                mAge.setText(age);
                mChangeStatus.setText(status);
                genderAll = gender;
                if (genderAll.equals(gender)){
                    mMail.setText(gender);
                    mGender.check(R.id.mail);
                }else {
                    mFemail.setText(gender);
                    mGender.check(R.id.femail);
                }


                Picasso.with(EditData.this)
                        .load(imageUser)
                        .placeholder(R.drawable.default_avatar2)
                        .into(mImageProfile);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private void checkPermissionsGallery(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},100);
                return;
            }
        }
    }

    private void checkPermissionsCamira(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String[]{Manifest.permission.CAMERA},100);
                return;
            }
        }
    }


}
