package com.ahmet.postphotos.UI.Account.Profile;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ahmet.postphotos.Config.CheckInternetConnection;
import com.ahmet.postphotos.Model.Blog;
import com.ahmet.postphotos.R;
import com.ahmet.postphotos.UI.Account.Settings.Settings;
import com.ahmet.postphotos.UI.Main.Fragments.Post.ShowPostActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class Profile extends AppCompatActivity {

    private DatabaseReference mRef;
    private FirebaseUser firebaseUser;
    private StorageReference mRefStorageIMG;

    private CircleImageView mDisplayImage;
    private TextView mName;
    private TextView mPhone;
    private TextView mAge;
    private TextView mGender;
    private TextView mStatus;
    private TextView mCountFriends,mCountFriendsOnline;
    private RecyclerView mRecyclerProfile;
    private Button btnChangeImage,btnChangeStatus;
    //private ProgressStatusBar mProgressStatusBar;
    //private ImageView mEditProfile,mAccountSettings,mEditImage,mEditStatus;
    private ImageView mEditStatus, mProfileUserCoverImage;
    private ImageButton mSetImageCoverProfile;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseCurrentUser;
    private Query mQueryCurrentUser;

    private String mCurrentUser;

    private static int GALLERY_PICK = 8;

    private long countFriends = 0;
    private long mCountPosts = 0;

    private ProgressBar  mProgressProfile;

    Bitmap thumbBitmap;

    private Toolbar mToolbar;

    private CheckInternetConnection mCheckInternetConnection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        mToolbar = findViewById(R.id.app_profile_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.profile);

        mDisplayImage = findViewById(R.id.image_setting);
        mName = findViewById(R.id.username_setting);
        mPhone = findViewById(R.id.textShowPhone);
        mAge = findViewById(R.id.textShowAge);
        mGender = findViewById(R.id.textShowGender);
        mStatus = findViewById(R.id.status_setting);
        mCountFriends = findViewById(R.id.text_count_friends);
        mCountFriendsOnline = findViewById(R.id.text_count_friends_online);
        mRecyclerProfile = findViewById(R.id.recycler_profile);

        mRecyclerProfile.setLayoutManager(new StaggeredGridLayoutManager(3, LinearLayout.VERTICAL));
        //btnChangeImage = (Button) findViewById(R.id.btnChangeImage);
        //btnChangeStatus = (Button) findViewById(R.id.btnChangeStatus);

//        mEditProfile = findViewById(R.id.imgEditProfile);
//        mEditImage = findViewById(R.id.imgEditImage);
        //mEditStatus = findViewById(R.id.imgEditStatus);
//        mAccountSettings = findViewById(R.id.imgAccountSettings);
        mSetImageCoverProfile = findViewById(R.id.set_image_cover_profile);
        mProfileUserCoverImage = findViewById(R.id.profile_user_cover_image);

        mProgressProfile = findViewById(R.id.progressBarProfile);

        mCheckInternetConnection = new CheckInternetConnection(this);

        mRefStorageIMG = FirebaseStorage.getInstance().getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUID = firebaseUser.getUid();

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser().getUid();

        mRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUID);
        mRef.keepSynced(true);
        mDatabaseCurrentUser = FirebaseDatabase.getInstance().getReference().child("blog");
        mDatabaseCurrentUser.keepSynced(true);
        mQueryCurrentUser = mDatabaseCurrentUser.orderByChild("uid").equalTo(mCurrentUser);
        mQueryCurrentUser.keepSynced(true);


        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {

                    String name = dataSnapshot.child("name").getValue().toString();
                    final String image = dataSnapshot.child("image").getValue().toString();
                    final String coverImage = dataSnapshot.child("cover_image").getValue().toString();
                    String status = dataSnapshot.child("status").getValue().toString();
                    String thumbImage = dataSnapshot.child("thumb_image").getValue().toString();
                    String phone = dataSnapshot.child("phone").getValue().toString();
                    String age = dataSnapshot.child("age").getValue().toString();
                    String gender = dataSnapshot.child("gender").getValue().toString();

                    mName.setText(name);
                    mPhone.setText(phone);
                    mAge.setText(age);
                    mGender.setText(gender);
                    mStatus.setText(status);
                    Picasso.with(getApplicationContext())
                            .load(image)
                            .placeholder(R.drawable.default_avatar2)
                            .into(mDisplayImage);

                    if (!image.equals("default")) {

                        //Picasso.with(Profile.this).load(image).placeholder(R.drawable.default_avatar3).into(mDisplayImage);

                        Picasso.with(Profile.this)
                                .load(image)
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.default_avatar2)
                                .into(mDisplayImage, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {
                                        Picasso.with(Profile.this).load(image).placeholder(R.drawable.default_avatar2).into(mDisplayImage);
                                    }
                                });

                    }

                    Picasso.with(Profile.this)
                            .load(coverImage)
                            .networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.cover_image)
                            .into(mProfileUserCoverImage, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError() {
                                    Picasso.with(Profile.this)
                                            .load(coverImage)
                                            .placeholder(R.drawable.cover_image)
                                            .into(mProfileUserCoverImage);
                                }
                            });


                }catch (Exception ex){
                    //Toast.makeText(Profile.this, "Profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//        mEditProfile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                String username = mName.getText().toString();
//                String phone = mPhone.getText().toString();
//                String age = mAge.getText().toString();
//                String gender = mGender.getText().toString();
//                String statusValue = mStatus.getText().toString();
//
//
//                Intent editProfileIntent = new Intent(Profile.this,EditData.class);
//                editProfileIntent.putExtra("username",username);
//                editProfileIntent.putExtra("phone",phone);
//                editProfileIntent.putExtra("age",age);
//                //editProfileIntent.putExtra("gender",gender);
//                editProfileIntent.putExtra("statusValue",statusValue);
//                startActivity(editProfileIntent);
//
//            }
//        });

//        mEditImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Intent galleryIntent = new Intent();
//                galleryIntent.setType("image/*");
//                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
//
//                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);
//
////                CropImage.activity()
////                        .setGuidelines(CropImageView.Guidelines.ON)
////                        .start(Profile.this);
//            }
//        });

//        mEditStatus.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent statusIntent = new Intent(Profile.this,EditStatus.class);
//                statusIntent.putExtra("status",mStatus.getText().toString());
//                startActivity(statusIntent);
//            }
//        });

        mSetImageCoverProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkPermissions();

                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);
            }
        });

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String currentUser = mAuth.getCurrentUser().getUid();
        DatabaseReference mReference = FirebaseDatabase.getInstance().getReference()
                .child("friends").child(currentUser);
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                countFriends = dataSnapshot.getChildrenCount();
                mCountFriends.setText(String.valueOf(countFriends));
                Log.e("Count", String.valueOf(countFriends));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//        mAccountSettings.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(Profile.this,SettingsItems.class));
//            }
//        });
    }

//    @Override
//    protected void onPause() {
//        mProgressStatusBar.remove(); //remove progress view in case user went out before the progress end
//        super.onPause();
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK){
//
//            Uri imageURI = data.getData();
//
//            CropImage.activity(imageURI)
//                    .setAspectRatio(1, 1)
//                    //.setMinCropWindowSize(500, 500)
//                    .start(this);
//
////            Toast.makeText(this, imageURI, Toast.LENGTH_SHORT).show();
//        }
//
//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//            if (resultCode == RESULT_OK) {
//
//                mProgressProfile.setVisibility(View.VISIBLE);
//
//                Uri resultUri = result.getUri();
//
//                File thumbFilePath = new File(resultUri.getPath());
//
//                String currentUID = firebaseUser.getUid();
//
//                try {
//                    thumbBitmap = new Compressor(this)
//                            .setMaxWidth(200)
//                            .setMaxHeight(200)
//                            .setQuality(75)
//                            .compressToBitmap(thumbFilePath);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                thumbBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                final byte[] thumb_byte = baos.toByteArray();
//
//                StorageReference mRefImage = mRefStorageIMG.child("profile_images").child(currentUID + ".jpg");
//                final StorageReference mRefFilePath = mRefStorageIMG.child("profile_images").child("thumbs").child(currentUID + ".jpg");
//
//                mRefImage.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                        if (task.isSuccessful()){
//
//                            final String imageUrl = task.getResult().getDownloadUrl().toString();
//
//                            final UploadTask uploadTask = mRefFilePath.putBytes(thumb_byte);
//                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                                @Override
//                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumbTask) {
//
//                                    String thumbImageUrl = thumbTask.getResult().getDownloadUrl().toString();
//
//                                    if (thumbTask.isSuccessful()){
//
//                                        Map uploadThumbImageMap = new HashMap<>();
//                                        uploadThumbImageMap.put("image", imageUrl);
//                                        uploadThumbImageMap.put("thumb_image", thumbImageUrl);
//
//                                        mRef.updateChildren(uploadThumbImageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                            @Override
//                                            public void onComplete(@NonNull Task<Void> task) {
//
//                                                if (task.isSuccessful()){
//                                                    //mAccountProg.dismiss();
//                                                    mProgressProfile.setVisibility(View.INVISIBLE);
//                                                    //Toast.makeText(Profile.this, "Success Thumb Image Uploading.", Toast.LENGTH_SHORT).show();
//                                                }
//
//                                            }
//                                        });
//
//                                    }else {
//                                       // mAccountProg.dismiss();
//                                        mProgressProfile.setVisibility(View.INVISIBLE);
//                                        Toast.makeText(Profile.this, "Error in uploading thumbnail.", Toast.LENGTH_SHORT).show();
//                                    }
//
//                                }
//                            });
//
//
//                        }else {
//                            //mAccountProg.dismiss();
//                            mProgressProfile.setVisibility(View.INVISIBLE);
//                            Toast.makeText(Profile.this, "Not Working", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//
//            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//                Exception error = result.getError();
//            }
//        }
//
//    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        boolean checkInternet = mCheckInternetConnection.isConnectingToInternet();
        if (!checkInternet){
            Snackbar snackbar = Snackbar.make(findViewById(R.id.activity_main),
                    R.string.check_internet,
                    Snackbar.LENGTH_LONG);
            snackbar.show();
        }

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK){

            Uri coverImageUri = data.getData();

            CropImage.activity(coverImageUri)
                    .setAspectRatio(1, 1)
                    .setMinCropWindowSize(500, 500)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult activityResult = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mProgressProfile.setVisibility(View.VISIBLE);

                Uri resultUri = activityResult.getUri();

                File thumbPathFile = new File(resultUri.getPath());

                String userUID = firebaseUser.getUid();

                try {
                    thumbBitmap = new Compressor(this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(75)
                            .compressToBitmap(thumbPathFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                thumbBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                final byte[] thumbByte = outputStream.toByteArray();

                final StorageReference mStorageRef = mRefStorageIMG.child("Cover_Images").child(userUID + ".jpg");
                //StorageReference mFilePath;
                mStorageRef.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        String coverImageUrl = task.getResult().getDownloadUrl().toString();

                        if (task.isSuccessful()) {
                            Map coverImageMap = new HashMap();
                            coverImageMap.put("cover_image", coverImageUrl);
                            mRef.updateChildren(coverImageMap).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    mProgressProfile.setVisibility(View.INVISIBLE);
                                }
                            });
                        } else {
                            mProgressProfile.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error = activityResult.getError();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.menu_account_settings){
            startActivity(new Intent(Profile.this,Settings.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

        }
    }

    private void checkPermissions(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},101);
                return;
            }
        }
    }


    @Override
    public void onStart() {
        super.onStart();


        FirebaseRecyclerAdapter<Blog, ProfileViewholder> blogProfileViewholderFirebaseAdapter = new FirebaseRecyclerAdapter<Blog, ProfileViewholder>(
                Blog.class,
                R.layout.raw_post_profile,
                ProfileViewholder.class,
                mQueryCurrentUser
        ) {
            @Override
            protected void populateViewHolder(ProfileViewholder profileViewholder, Blog blog, int position) {

                final String postKey = getRef(position).getKey();

                profileViewholder.setImagePost(Profile.this, blog.getImage());

                mQueryCurrentUser.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        mCountPosts = dataSnapshot.getChildrenCount();
                        mCountFriendsOnline.setText(String.valueOf(mCountPosts));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                profileViewholder.convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent postSingleIntent = new Intent(Profile.this,ShowPostActivity.class);
                        postSingleIntent.putExtra("postId", postKey);
                        startActivity(postSingleIntent);
                    }
                });

            }
        };

        mRecyclerProfile.setAdapter(blogProfileViewholderFirebaseAdapter);
    }


    protected static class ProfileViewholder extends RecyclerView.ViewHolder{

        View convertView;

        public ProfileViewholder(View itemView) {
            super(itemView);

            convertView = itemView;
        }

        public void setImagePost(Context context, String postImage){
            ImageView imagePost = convertView.findViewById(R.id.image_profile_post);
            Picasso.with(context)
                    .load(postImage)
                    .placeholder(R.drawable.default_post)
                    .into(imagePost);
        }
    }

}
