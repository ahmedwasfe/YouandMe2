package com.ahmet.postphotos.UI.UserData.User;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmet.postphotos.Model.Users;
import com.ahmet.postphotos.R;
import com.ahmet.postphotos.UI.Account.Profile.Profile;
import com.ahmet.postphotos.UI.Main.Home.HomeActivity;
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

public class AllUsers extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private Toolbar mToolbar;

    RecyclerView recyclerAllUsers;

    private DatabaseReference mUsersRef;
    private DatabaseReference mRootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_users);

        mToolbar = findViewById(R.id.app_UsersPage_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.all_users);


        mUsersRef = FirebaseDatabase.getInstance().getReference().child("users");
        mUsersRef.keepSynced(true);

        recyclerAllUsers = findViewById(R.id.recyclerAllUsers);
        recyclerAllUsers.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL,false));

        mRootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference mNotificatonDatabase = FirebaseDatabase.getInstance().getReference().child("notifications");

    }
//
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
//                final FirebaseAuth mAuth = FirebaseAuth.getInstance();
//                final FirebaseUser firebaseUser = mAuth.getCurrentUser();
//                final String currentUserUID = firebaseUser.getUid();
//
//
//                    viewHolder.setUsername(user.getName());
//                    viewHolder.setUserImage(getApplicationContext(), user.getImage());
//
//                final String userUID = getRef(position).getKey();
//
//                viewHolder.convertView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//
//                        if (!currentUserUID.equals(userUID)) {
//                            Toast.makeText(AllUsers.this, "test", Toast.LENGTH_SHORT).show();
//                            Intent profileIntent = new Intent(AllUsers.this, ProfileUser.class);
//                            profileIntent.putExtra("user_uid", userUID);
//                            startActivity(profileIntent);
//                        }else {
//                            startActivity(new Intent(AllUsers.this, Profile.class));
//                        }
//                    }
//                });
//
//
//                viewHolder.btnAddFriends.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//
//
//
//                        if (viewHolder.btnAddFriends.isPressed()) {
//
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
//                                        Toast.makeText(AllUsers.this, R.string.error_in_sending_request, Toast.LENGTH_SHORT).show();
//                                    }
//
//                                }
//                            });
//
//                            viewHolder.btnAddFriends.setImageResource(R.drawable.ic_cliked_added_24dp);
//                            viewHolder.btnAddFriends.setPressed(true);
//                        }
//
//                    }
//                });
//
//            }
//        };
//
//        recyclerAllUsers.setAdapter(usersHolderFirebaseAdapter);
//    }



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


        public void setUserImage(final Context context, final String thumb_image){

            final CircleImageView userImageView =  convertView.findViewById(R.id.imageUserShow);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.add_data_menu,menu);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.m_search_alluser));
        searchView.setOnQueryTextListener(this);


        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.menu_nextAddFriends){
            startActivity(new Intent(AllUsers.this,HomeActivity.class));
            finish();
        }
        return true;
    }






    private void search(String wordSearch){

        // Toast.makeText(AddFriends.this, "Started Search", Toast.LENGTH_LONG).show();

        final Query query = mUsersRef.orderByChild("name").startAt(wordSearch).endAt(wordSearch + "\uf8ff" );

        FirebaseRecyclerAdapter<Users, UsersViewHolder> usersHolderFirebaseAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(

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

                //if (!user.getUID().equals(firebaseUser.getUid())) {
                viewHolder.setUsername(user.getName());
                viewHolder.setUserImage(getApplicationContext(), user.getImage());
                //}




                viewHolder.convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (!currentUserUID.equals(userUID)) {
                            Toast.makeText(AllUsers.this, "test", Toast.LENGTH_SHORT).show();
                            Intent profileIntent = new Intent(AllUsers.this, ProfileUser.class);
                            profileIntent.putExtra("user_uid", userUID);
                            startActivity(profileIntent);
                        }else {
                            startActivity(new Intent(AllUsers.this, Profile.class));
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
                                        Toast.makeText(AllUsers.this, R.string.error_in_sending_request, Toast.LENGTH_SHORT).show();
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

        recyclerAllUsers.setAdapter(usersHolderFirebaseAdapter);


    }
}
