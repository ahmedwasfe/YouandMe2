package com.ahmet.postphotos.UI.UserData.User;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmet.postphotos.Model.Blog;
import com.ahmet.postphotos.R;
import com.ahmet.postphotos.UI.Main.Fragments.Post.ShowPostActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileUser extends AppCompatActivity {

    private ImageView profile_any_user_cover_image;
    private CircleImageView mProfileImage;
    private TextView mProfileName,mProfileStatus,mTotalFriends,mSendFriendRequest;
    // private Button btnSendRequest,btnDeclineFriend;
    private RecyclerView mRecyclerPostUser;

    private Toolbar mToolbar;



    private DatabaseReference mRef;
    private DatabaseReference mFriendsReqDatabase;
    private DatabaseReference mFriendsAccept;
    private DatabaseReference mNotificatonDatabase;
    private DatabaseReference mRootRef;
    private DatabaseReference mRefUser;
    private FirebaseUser firebaseUser;
    private DatabaseReference mReferencePosts;
    private Query mQueryOtherUser;

    private String currentRequestState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_user);

        final String userUID = getIntent().getStringExtra("user_uid");

        mRootRef = FirebaseDatabase.getInstance().getReference();

        mRef = FirebaseDatabase.getInstance().getReference().child("users").child(userUID);
        mFriendsReqDatabase = FirebaseDatabase.getInstance().getReference().child("friend_req");
        mFriendsAccept = FirebaseDatabase.getInstance().getReference().child("friends");
        mNotificatonDatabase = FirebaseDatabase.getInstance().getReference().child("notifications");
        mReferencePosts = FirebaseDatabase.getInstance().getReference().child("blog");
        mReferencePosts.keepSynced(true);
        mQueryOtherUser = mReferencePosts.orderByChild("uid").equalTo(userUID);
        mQueryOtherUser.keepSynced(true);

        if (firebaseUser != null) {
            mRefUser = FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid());
        }
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //mToolbar = findViewById(R.id.toolbar_profile_user);
//        setSupportActionBar(mToolbar);
//        getSupportActionBar().setTitle("");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProfileImage = findViewById(R.id.profileUserImage);
        mProfileName = findViewById(R.id.profileUserName);
        mProfileStatus = findViewById(R.id.profileUserStatus);
        mSendFriendRequest = findViewById(R.id.text_profile_send_req);
        profile_any_user_cover_image = findViewById(R.id.profile_any_user_cover_image);
        mRecyclerPostUser = findViewById(R.id.recycler_show_post_user);

        mRecyclerPostUser.setLayoutManager(new StaggeredGridLayoutManager(3, LinearLayout.VERTICAL));

        // mTotalFriends = findViewById(R.id.profileTotalFriends);
        //btnSendRequest = findViewById(R.id.btn_profile_send_req);
        // btnDeclineFriend = findViewById(R.id.btn_profile_decline);

        currentRequestState = "not_friends";

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                String coverImage = dataSnapshot.child("cover_image").getValue().toString();
//                getSupportActionBar().setTitle(name);

                mProfileName.setText(name);
                mProfileStatus.setText(status);
                Picasso.with(getApplicationContext())
                        .load(image)
                        .placeholder(R.drawable.default_avatar2)
                        .into(mProfileImage);

                Picasso.with(getApplicationContext())
                        .load(coverImage)
                        .placeholder(R.drawable.cover_image)
                        .into(profile_any_user_cover_image);

                // --------------- FRIENDS LIST / REQUEST FEATURE -----

                mFriendsReqDatabase.child(firebaseUser.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (dataSnapshot.hasChild(userUID)){
                                    String reqType = dataSnapshot.child(userUID).child("request_type").getValue().toString();
                                    if (reqType.equals("received")){
                                        currentRequestState = "request_received";
                                        // btnSendRequest.setText("Accept Friend Request");
                                        mSendFriendRequest.setText(R.string.accept_friend_request);

                                        // btnDeclineFriend.setVisibility(View.VISIBLE);
                                        //btnDeclineFriend.setEnabled(true);
                                    }else if (reqType.equals("sent")){
                                        currentRequestState = "request_sent";
                                        // btnSendRequest.setText("Cancel Friend Request");
                                        mSendFriendRequest.setText(R.string.cancel_friend_request);

                                        // btnDeclineFriend.setVisibility(View.INVISIBLE);
                                        // btnDeclineFriend.setEnabled(false);
                                    }
                                }else{
                                    mFriendsAccept.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            if (dataSnapshot.hasChild(userUID)){
                                                currentRequestState = "friends";
                                                // btnSendRequest.setText("UnFriend this Person");
                                                mSendFriendRequest.setText(R.string.unFriend_this_person);

                                                // btnDeclineFriend.setVisibility(View.INVISIBLE);
                                                // btnDeclineFriend.setEnabled(false);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mSendFriendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //btnSendRequest.setEnabled(false);
                mSendFriendRequest.setEnabled(false);

                // --------------- NOT FRIENDS STATE ------------   Send Request Friendly إرسال طلب صداقة

                if (currentRequestState.equals("not_friends")){

                    DatabaseReference mNotificationRef = mRootRef.child("notifications").child(userUID).push();
                    String newNotificationId = mNotificationRef.getKey();

                    final HashMap<String, String> notificationData = new HashMap<>();
                    notificationData.put("from", firebaseUser.getUid());
                    notificationData.put("type", "request");

                    Map requestMap = new HashMap();
                    requestMap.put("friend_req/" + firebaseUser.getUid() + "/" + userUID + "/request_type", "sent");
                    requestMap.put("friend_req/" + userUID + "/" + firebaseUser.getUid() + "/request_type", "received");
                    requestMap.put("notifications/" + userUID + "/" + newNotificationId, notificationData);

                    mRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null){
                                Toast.makeText(ProfileUser.this, R.string.error_in_sending_request, Toast.LENGTH_SHORT).show();
                            }

                            currentRequestState = "request_sent";
                            // btnSendRequest.setText("Cancle Friend Request");
                            mSendFriendRequest.setText(R.string.cancel_friend_request);
                        }
                    });


                }

                // - -------------- CANCEL REQUEST STATE ------------ إلغاء طلب لبصداقة   Cancel Request Friendly

                if (currentRequestState.equals("request_sent")){

                    mFriendsReqDatabase.child(firebaseUser.getUid()).child(userUID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            mFriendsReqDatabase.child(userUID).child(firebaseUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    // btnSendRequest.setEnabled(true);
                                    mSendFriendRequest.setEnabled(true);
                                    currentRequestState = "not_friends";
                                    //btnSendRequest.setText("Sent Friend Request");
                                    mSendFriendRequest.setText(R.string.sent_friend_request);

                                    // btnDeclineFriend.setVisibility(View.INVISIBLE);
                                    // btnDeclineFriend.setEnabled(false);

                                }
                            });

                        }
                    });

                }

                // ------------ REQ RECEIVED STATE ---------- قبول طلب الصداقة  Accept Request Friendly

                if (currentRequestState.equals("request_received")) {

                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                    Map friendsMap = new HashMap();
                    friendsMap.put("friends/" + firebaseUser.getUid() + "/" + userUID + "/date", currentDate);
                    friendsMap.put("friends/" + userUID + "/" + firebaseUser.getUid() + "/date", currentDate);

                    friendsMap.put("friend_req/" + firebaseUser.getUid() + "/" + userUID + "/request_type", null);
                    friendsMap.put("friend_req/" + userUID + "/" + firebaseUser.getUid() + "/request_type", null);

                    mRootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if(databaseError == null){

                                // btnSendRequest.setEnabled(true);
                                mSendFriendRequest.setEnabled(true);
                                currentRequestState = "friends";
                                //btnSendRequest.setText("Unfriend this Person");
                                mSendFriendRequest.setText(R.string.unFriend_this_person);

                                // btnDeclineFriend.setVisibility(View.INVISIBLE);
                                // btnDeclineFriend.setEnabled(false);

                            } else {

                                String error = databaseError.getMessage();

                                Toast.makeText(ProfileUser.this, error, Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }

                if (currentRequestState.equals("friends")){

                    Map unFriendMap = new HashMap();
                    unFriendMap.put("friends/" + firebaseUser.getUid() + "/" + userUID, null);
                    unFriendMap.put("friends/" + userUID + "/" + firebaseUser.getUid(), null);

                    mRootRef.updateChildren(unFriendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if(databaseError == null){

                                currentRequestState = "not_friends";
                                // btnSendRequest.setText("Friend");
                                mSendFriendRequest.setText(R.string.friend);

                                // btnDeclineFriend.setVisibility(View.INVISIBLE);
                                // btnDeclineFriend.setEnabled(false);

                            } else {

                                String error = databaseError.getMessage();

                                Toast.makeText(ProfileUser.this, error, Toast.LENGTH_SHORT).show();


                            }

                        }
                    });

                }

            }
        });


    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Blog, ProfileUserHolder> blogProfileUserHolderFirebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Blog, ProfileUserHolder>(

                Blog.class,
                R.layout.raw_post_profile,
                ProfileUserHolder.class,
                mQueryOtherUser

        ) {
            @Override
            protected void populateViewHolder(ProfileUserHolder profileUserHolder, Blog blog, int position) {

                final String postKey = getRef(position).getKey();

                profileUserHolder.setImagePost(getApplicationContext(), blog.getImage());

                mQueryOtherUser.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

//                        mCountPost = dataSnapshot.getChildrenCount();
//                        mCountPostUser.setText(String.valueOf(mCountPost));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                profileUserHolder.convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intentPost = new Intent(ProfileUser.this, ShowPostActivity.class);
                        intentPost.putExtra("postId", postKey);
                        startActivity(intentPost);
                    }
                });
            }
        };

        mRecyclerPostUser.setAdapter(blogProfileUserHolderFirebaseRecyclerAdapter);
    }

    protected static class ProfileUserHolder extends RecyclerView.ViewHolder{

        View convertView;

        public ProfileUserHolder(View itemView) {
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
