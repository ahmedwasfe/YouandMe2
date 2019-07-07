package com.ahmet.postphotos.UI.UserData.User;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmet.postphotos.Model.Users;
import com.ahmet.postphotos.R;
import com.ahmet.postphotos.UI.Account.Profile.Profile;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddFriends extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private Toolbar mToolbar;
    private EditText mSearch;
    private ImageView search;

    RecyclerView recyclerAddFriends;

    private DatabaseReference mUsersRef;
    private DatabaseReference mRootRef;
    //private DatabaseReference getmUsersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_friends);


        mToolbar = findViewById(R.id.app_AddFriendsPage_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.add_Friends);


        mUsersRef = FirebaseDatabase.getInstance().getReference().child("users");
        mUsersRef.keepSynced(true);

        recyclerAddFriends = findViewById(R.id.recyclerAddFriends);
        recyclerAddFriends.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayout.VERTICAL));

        mRootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference mNotificatonDatabase = FirebaseDatabase.getInstance().getReference().child("notifications");


    }


    private void search(String wordSearch){

       // Toast.makeText(AddFriends.this, "Started Search", Toast.LENGTH_LONG).show();

        String t = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";

        final Query query = mUsersRef.orderByChild("name").startAt(wordSearch).endAt(wordSearch + "\uf8ff" );

        FirebaseRecyclerAdapter<Users, UsersViewHolder> usersHolderFirebaseAdapter =
                new FirebaseRecyclerAdapter<Users, UsersViewHolder>(

                Users.class,
                R.layout.raw_show_users,
                UsersViewHolder.class,
                query
        ) {
            @Override
            protected void populateViewHolder(final UsersViewHolder viewHolder, Users user, int position) {

                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                final FirebaseUser firebaseUser = mAuth.getCurrentUser();
                mRootRef.child(firebaseUser.getUid());
                final String currentUserUID = firebaseUser.getUid();
                final String userUID = getRef(position).getKey();

                viewHolder.setUsername(user.getName());
                viewHolder.setUserImage(getApplicationContext(), user.getImage());


                viewHolder.convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Toast.makeText(AddFriends.this, "test", Toast.LENGTH_SHORT).show();
                        if (!userUID.equals(currentUserUID)) {

                            Intent profileIntent = new Intent(AddFriends.this, ProfileUser.class);
                            profileIntent.putExtra("user_uid", userUID);
                            startActivity(profileIntent);
                        }else {
                            startActivity(new Intent(AddFriends.this, Profile.class));
                        }
                    }
                });


                viewHolder.btnAddFriends.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

//                        viewHolder.btnAddFriends.setImageResource(R.drawable.ic_cliked_added_24dp);
//                        viewHolder.btnAddFriends.setPressed(true);

                        if (viewHolder.btnAddFriends.isPressed()) {
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
                                    if (databaseError != null) {
                                        Toast.makeText(AddFriends.this, R.string.error_in_sending_request, Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
//
                            viewHolder.btnAddFriends.setImageResource(R.drawable.ic_cliked_added_24dp);
                            viewHolder.btnAddFriends.setPressed(true);

                        }
                    }
                });

//                }

            }
        };

        recyclerAddFriends.setAdapter(usersHolderFirebaseAdapter);


    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        FirebaseRecyclerAdapter<Users, UsersViewHolder> usersHolderFirebaseAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(
//
//                Users.class,
//                R.layout.raw_show_users,
//                UsersViewHolder.class,
//                mUsersRef
//        ) {
//            @Override
//            protected void populateViewHolder(final UsersViewHolder viewHolder, Users user, int position) {
//
//                FirebaseAuth mAuth = FirebaseAuth.getInstance();
//                final FirebaseUser firebaseUser = mAuth.getCurrentUser();
//                final String currentUser = firebaseUser.getUid();
//                final String userUID = getRef(position).getKey();
////                if (!user.getUID().equals(UID)) {
//
//
//                    viewHolder.setUsername(user.getName());
////                viewHolder.setUserGender(user.getGender());
////                viewHolder.setUserAge(user.getAge());
////                viewHolder.setUserPhone(user.getPhone());
//                    viewHolder.setUserImage(getApplicationContext(), user.getImage());
//
//
////                mUsersRef.addValueEventListener(new ValueEventListener() {
////                    @Override
////                    public void onDataChange(DataSnapshot dataSnapshot) {
////                        Users user = dataSnapshot.getValue(Users.class);
////                        if (user.getUID().equals(userUID)){
////                            viewHolder.setUsername(user.getName());
////                            viewHolder.setUserImage(getApplicationContext(), user.getImage());
////                        }
////                    }
////
////                    @Override
////                    public void onCancelled(DatabaseError databaseError) {
////
////                    }
////                });
//
//
//
//                viewHolder.convertView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                       // Toast.makeText(AddFriends.this, "test", Toast.LENGTH_SHORT).show();
//
//                        if (!currentUser.equals(userUID)) {
//                            Intent profileIntent = new Intent(AddFriends.this, ProfileUser.class);
//                            profileIntent.putExtra("user_uid", userUID);
//                            startActivity(profileIntent);
//                        }else {
//                            startActivity(new Intent(AddFriends.this, Profile.class));
//                        }
//                    }
//                });
//
//
//                viewHolder.btnAddFriends.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//
////                        viewHolder.btnAddFriends.setImageResource(R.drawable.ic_cliked_added_24dp);
////                        viewHolder.btnAddFriends.setPressed(true);
//
//                        if (viewHolder.btnAddFriends.isPressed()) {
//                            DatabaseReference mNotificationRef = mRootRef.child("notifications").child(userUID).push();
//                            String newNotificationId = mNotificationRef.getKey();
//
//                            final HashMap<String, String> notificationData = new HashMap<>();
//                            notificationData.put("from", firebaseUser.getUid());
//                            notificationData.put("type", "request");
//
//                            Map requestMap = new HashMap();
//                            requestMap.put("friend_req/" + firebaseUser.getUid() + "/" + userUID + "/request_type", "sent");
//                            requestMap.put("friend_req/" + userUID + "/" + firebaseUser.getUid() + "/request_type", "received");
//                            requestMap.put("notifications/" + userUID + "/" + newNotificationId, notificationData);
//
//                            mRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
//                                @Override
//                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//                                    if (databaseError != null) {
//                                        Toast.makeText(AddFriends.this, R.string.error_in_sending_request, Toast.LENGTH_SHORT).show();
//                                    }
//
//                                }
//                            });
////                            Picasso.with(AddFriends.this)
////                                    .load(R.drawable.ic_cliked_added_24dp)
////                                    .placeholder(R.drawable.ic_click_add_black_24dp)
////                                    .into(viewHolder.btnAddFriends);
//                            viewHolder.btnAddFriends.setImageResource(R.drawable.ic_cliked_added_24dp);
//                            viewHolder.btnAddFriends.setPressed(true);
//
//                        }
//                    }
//                });
//
////                }
//
//            }
//        };
//
//        recyclerAddFriends.setAdapter(usersHolderFirebaseAdapter);
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_search, menu);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.m_search));
        searchView.setOnQueryTextListener(this);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String textSearch) {
        search(textSearch);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        search(newText);
        return false;
    }


    public static class UsersViewHolder extends RecyclerView.ViewHolder{

        View convertView;
        ImageView btnAddFriends;

        public UsersViewHolder(View itemView) {
            super(itemView);

            convertView = itemView;

            btnAddFriends = convertView.findViewById(R.id.btnAddFriends);

        }



        public void setUsername(String username){
            TextView mUsername = convertView.findViewById(R.id.textNameShow);
            mUsername.setText(username);
        }

//        public void setUserGender(String gender){
//            TextView mStatus = convertView.findViewById(R.id.textgenderShow);
//            mStatus.setText(gender);
//        }
//        public void setUserAge(String age){
//            TextView mStatus = convertView.findViewById(R.id.textAgeShow);
//            mStatus.setText(age);
//        }
//        public void setUserPhone(String phone){
//            TextView mStatus = convertView.findViewById(R.id.textPhoneShow);
//            mStatus.setText(phone);
//        }


        public void setUserImage(final Context context, final String thumb_image){

            final CircleImageView userImageView =  convertView.findViewById(R.id.imageUserShow);

            //Picasso.with(context).load(thumb_image).placeholder(R.drawable.default_avatar3).into(userImageView);

            Picasso.with(context)
                    .load(thumb_image)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.default_avatar2)
                    .into(userImageView, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(context).load(thumb_image).placeholder(R.drawable.default_avatar2).into(userImageView);
                        }
                    });

        }
    }

}
