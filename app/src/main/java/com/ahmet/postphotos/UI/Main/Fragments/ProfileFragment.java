package com.ahmet.postphotos.UI.Main.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

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

    private ProgressBar mProgressProfile;

    Bitmap thumbBitmap;

    private Toolbar mToolbar;

    private CheckInternetConnection mCheckInternetConnection;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCheckInternetConnection = new CheckInternetConnection(getActivity());

        setHasOptionsMenu(true);

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
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View convertView = inflater.inflate(R.layout.fragment_profile, container, false);

        mDisplayImage = convertView.findViewById(R.id.image_setting);
        mName = convertView.findViewById(R.id.username_setting);
        mPhone = convertView.findViewById(R.id.textShowPhone);
        mAge = convertView.findViewById(R.id.textShowAge);
        mGender = convertView.findViewById(R.id.textShowGender);
        mStatus = convertView.findViewById(R.id.status_setting);
        mCountFriends = convertView.findViewById(R.id.text_count_friends);
        mCountFriendsOnline = convertView.findViewById(R.id.text_count_friends_online);
        mRecyclerProfile = convertView.findViewById(R.id.recycler_profile_home);

        mSetImageCoverProfile = convertView.findViewById(R.id.set_image_cover_profile);
        mProfileUserCoverImage = convertView.findViewById(R.id.profile_user_cover_image);

        mProgressProfile = convertView.findViewById(R.id.progressBarProfile);

        return convertView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mRecyclerProfile.setLayoutManager(new StaggeredGridLayoutManager(3, LinearLayout.VERTICAL));

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
                    Picasso.with(getActivity())
                            .load(image)
                            .placeholder(R.drawable.default_avatar2)
                            .into(mDisplayImage);

                    if (!image.equals("default")) {

                        //Picasso.with(Profile.this).load(image).placeholder(R.drawable.default_avatar3).into(mDisplayImage);

                        Picasso.with(getActivity())
                                .load(image)
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.default_avatar2)
                                .into(mDisplayImage, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {
                                        Picasso.with(getActivity()).load(image).placeholder(R.drawable.default_avatar2).into(mDisplayImage);
                                    }
                                });

                    }

                    Picasso.with(getActivity())
                            .load(coverImage)
                            .networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.cover_image)
                            .into(mProfileUserCoverImage, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError() {
                                    Picasso.with(getActivity())
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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        boolean checkInternet = mCheckInternetConnection.isConnectingToInternet();
        if (!checkInternet){
            Snackbar snackbar = Snackbar.make(mProgressProfile,
                    R.string.check_internet,
                    Snackbar.LENGTH_LONG);
            snackbar.show();
        }

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK){

            Uri coverImageUri = data.getData();

            CropImage.activity(coverImageUri)
                    .setAspectRatio(1, 1)
                    .setMinCropWindowSize(500, 500)
                    .start(getActivity());
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult activityResult = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mProgressProfile.setVisibility(View.VISIBLE);

                Uri resultUri = activityResult.getUri();

                File thumbPathFile = new File(resultUri.getPath());

                String userUID = firebaseUser.getUid();

                try {
                    thumbBitmap = new Compressor(getActivity())
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_profile, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.menu_account_settings){
            startActivity(new Intent(getActivity(),Settings.class));
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
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
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
            protected void populateViewHolder(final ProfileViewholder profileViewholder, final Blog blog, int position) {

                final String postKey = getRef(position).getKey();



                mQueryCurrentUser.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        mCountPosts = dataSnapshot.getChildrenCount();
                        mCountFriendsOnline.setText(String.valueOf(mCountPosts));

                        profileViewholder.setImagePost(getActivity(), blog.getImage());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                profileViewholder.convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent postSingleIntent = new Intent(getActivity(),ShowPostActivity.class);
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
