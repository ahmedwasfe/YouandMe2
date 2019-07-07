package com.ahmet.postphotos.UI.UserData.Data;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmet.postphotos.Model.Friends;
import com.ahmet.postphotos.R;
import com.ahmet.postphotos.UI.Account.Account_Login.LoginFragment;
import com.ahmet.postphotos.UI.Account.Settings.Settings;
import com.ahmet.postphotos.UI.Account.Profile.Profile;
import com.ahmet.postphotos.UI.Main.Chat.ChatRoom;
import com.ahmet.postphotos.UI.Main.Home.HomeActivity;
import com.ahmet.postphotos.UI.UserData.User.AddFriends;
import com.ahmet.postphotos.UI.UserData.User.ProfileUserMyFriends;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar mToolbar;

    private TextView mUsername;
    private CircleImageView mImageUser;

    private RecyclerView recyclerFragmentFriends;

    private FirebaseAuth mAuth;
    private DatabaseReference mRefFriends;
    private DatabaseReference mRefUsers;
    private String userCurrentUID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        mToolbar = findViewById(R.id.app_FriendsPage_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.friends2);



        DrawerLayout drawer = findViewById(R.id.drawer_layout_friends);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view_friends);
        navigationView.setNavigationItemSelectedListener(this);

        mUsername = navigationView.getHeaderView(0).findViewById(R.id.UsernamenavHeaderFriends);
        mImageUser = navigationView.getHeaderView(0).findViewById(R.id.imageUsernavHeaderFriends);

        mImageUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FriendsActivity.this,Profile.class));
            }
        });

        recyclerFragmentFriends = findViewById(R.id.recyclerFriends);
        recyclerFragmentFriends.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        recyclerFragmentFriends.setHasFixedSize(true);

        mAuth = FirebaseAuth.getInstance();
        userCurrentUID = mAuth.getCurrentUser().getUid();

        mRefFriends = FirebaseDatabase.getInstance().getReference().child("friends").child(userCurrentUID);
        mRefFriends.keepSynced(true);

        mRefUsers = FirebaseDatabase.getInstance().getReference().child("users");
        mRefUsers.keepSynced(true);


    }


//    private void search(String wordSearch){
//
//        // Toast.makeText(AddFriends.this, "Started Search", Toast.LENGTH_LONG).show();
//
//        final Query query = mRefUsers.orderByChild("name").startAt(wordSearch).endAt(wordSearch + "\uf8ff" );
//
//        FirebaseRecyclerAdapter<Friends, FriendsViewHolder> friendsViewHolderFirebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(
//
//
//                Friends.class,
//                R.layout.raw_myfriends,
//                FriendsViewHolder.class,
//                query
//        ) {
//
//            @Override
//            protected void populateViewHolder(final FriendsViewHolder friendsViewHolder, final Friends friends, int position) {
//
//                friendsViewHolder.setUserName(friends.getName());
//                friendsViewHolder.setUserStatus(friends.getStatus());
//                friendsViewHolder.setUserImage(FriendsActivity.this, friends.getImage());
//            }
//
//        };
//
//
//        recyclerFragmentFriends.setAdapter(friendsViewHolderFirebaseRecyclerAdapter);
//    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout_friends);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        /* if (id == R.id.action_settings) {
         *
         *  return true;
         *
        }*/
        if(item.getItemId() == R.id.main_account_settings){
            startActivity(new Intent(FriendsActivity.this, Settings.class));
//            Snackbar snackbar = Snackbar.make(mUsername,"About An App You & Me",Snackbar.LENGTH_LONG)
//            .setAction("About", new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Toast.makeText(FriendsActivity.this, "About", Toast.LENGTH_SHORT).show();
//                }
//            });
//            snackbar.show();
        }else if (item.getItemId() == R.id.nav_add_friends){
            startActivity(new Intent(FriendsActivity.this, AddFriends.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_myconversation) {
            startActivity(new Intent(FriendsActivity.this, HomeActivity.class));
            finish();
        } else if (id == R.id.nav_myFriends) {

        } else if (id == R.id.nav_addFriends) {
            startActivity(new Intent(FriendsActivity.this, AddFriends.class));
            finish();
        } else if (id == R.id.nav_profile) {
            Intent accountSettingIntent = new Intent(FriendsActivity.this, Profile.class);
            startActivity(accountSettingIntent);
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(FriendsActivity.this, Settings.class));
        } else if (id == R.id.nav_logout) {
            logOut();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout_friends);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void logOut(){

        final Dialog dialog = new Dialog(FriendsActivity.this);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.raw_dialog_logout);
        TextView textLogout = dialog.findViewById(R.id.textLogOut);
        TextView textCancel = dialog.findViewById(R.id.textCancel);

        textLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(FriendsActivity.this, LoginFragment.class));
                finish();
            }
        });

        textCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
    @Override
    public void onStart() {
        super.onStart();

        mRefUsers.child(userCurrentUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String username = dataSnapshot.child("name").getValue().toString();
                String userImage = dataSnapshot.child("image").getValue().toString();

                mUsername.setText(username);
                Picasso.with(getApplicationContext())
                        .load(userImage)
                        .placeholder(R.drawable.default_avatar2)
                        .into(mImageUser);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        FirebaseRecyclerAdapter<Friends, FriendsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(

                Friends.class,
                R.layout.raw_myfriends,
                FriendsViewHolder.class,
                mRefFriends

        ) {
            @Override
            protected void populateViewHolder(final FriendsViewHolder friendsViewHolder, Friends friends, int position) {

                //  friendsViewHolder.setUserDate(friends.getDate());

                final String userUID = getRef(position).getKey();

                mRefUsers.child(userUID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String username = dataSnapshot.child("name").getValue().toString();
                        final String userImage = dataSnapshot.child("image").getValue().toString();
                        final String userStatus = dataSnapshot.child("status").getValue().toString();
                        final String coverImage = dataSnapshot.child("cover_image").getValue().toString();
//                        String userOnline = dataSnapshot.child("online").getValue().toString();

                        friendsViewHolder.setUserName(username);
                        friendsViewHolder.setUserImage(FriendsActivity.this, userImage);
                        friendsViewHolder.setUserStatus(userStatus);

                        if (dataSnapshot.hasChild("online")){
                            String userOnline = dataSnapshot.child("online").getValue().toString();
                            friendsViewHolder.setUserOnline(userOnline);

                        }

                        friendsViewHolder.convertView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {

                                final Dialog dialog = new Dialog(FriendsActivity.this);
                                //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                dialog.setContentView(R.layout.raw_dialog_user);

                                CircleImageView mImageUser = dialog.findViewById(R.id.imageUserDialog);
                                ImageView mCoverImage = dialog.findViewById(R.id.profile_user_cover_image_dialog);
                                ImageView mSendMessage = dialog.findViewById(R.id.userDialogMessage);
                                ImageView mOpenProfile = dialog.findViewById(R.id.userDialogProfile);
                                TextView mUsername = dialog.findViewById(R.id.userName);
                                TextView mStatus = dialog.findViewById(R.id.userStatus);

                                mUsername.setText(username);
                                mStatus.setText(userStatus);
                                Picasso.with(FriendsActivity.this)
                                        .load(userImage)
                                        .placeholder(R.drawable.default_avatar2)
                                        .into(mImageUser);
                                Picasso.with(getApplicationContext())
                                        .load(coverImage)
                                        .placeholder(R.drawable.cover_image)
                                        .into(mCoverImage);

                                mSendMessage.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        Intent converstionIntent = new Intent(FriendsActivity.this,ChatRoom.class);
                                        converstionIntent.putExtra("user_uid", userUID);
                                        converstionIntent.putExtra("username", username);
                                        startActivity(converstionIntent);
                                        dialog.dismiss();
                                    }
                                });

                                mOpenProfile.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        Intent profileIntent = new Intent(FriendsActivity.this,ProfileUserMyFriends.class);
                                        profileIntent.putExtra("user_uid", userUID);
                                        startActivity(profileIntent);
                                        dialog.dismiss();
                                    }
                                });

                                dialog.show();

                                return true;
                            }
                        });


//                        friendsViewHolder.convertView.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//
//                                CharSequence options[] = new CharSequence[]{"Open Profile", "Send Message"};
//
//                                AlertDialog.Builder builder = new AlertDialog.Builder(FriendsActivity.this);
//                                builder.setTitle("Select Options");
//                                builder.setItems(options, new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialogInterface, int position) {
//
//                                        // Clicke Event for each item
//
//                                        if (position == 0){
//
//                                            Intent profileIntent = new Intent(FriendsActivity.this,ProfileUser.class);
//                                            profileIntent.putExtra("user_uid", userUID);
//                                            startActivity(profileIntent);
//
//                                        }
//
//                                        if (position == 1){
//
//                                            Intent converstionIntent = new Intent(FriendsActivity.this,ChatRoom.class);
//                                            converstionIntent.putExtra("user_uid", userUID);
//                                            converstionIntent.putExtra("username", username);
//                                            startActivity(converstionIntent);
//
//                                        }
//
//                                    }
//                                });
//
//                                builder.show();
//                            }
//                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };

        recyclerFragmentFriends.setAdapter(firebaseRecyclerAdapter);
    }




    public static class FriendsViewHolder extends RecyclerView.ViewHolder{

        View convertView;
        public FriendsViewHolder(View itemView) {
            super(itemView);

            convertView = itemView;
        }

        public void setUserStatus(String status) {
            TextView textStatusMyFriend = convertView.findViewById(R.id.textMyFreiendStatus);
            textStatusMyFriend.setText(status);
        }

        public void setUserName(String name) {
            TextView textNameMyFriend = convertView.findViewById(R.id.textMyFreiendName);
            textNameMyFriend.setText(name);
        }
        public void setUserImage(Context context, String image){
            CircleImageView imgMyFriend = (CircleImageView) convertView.findViewById(R.id.imageMyFreiend);
            Picasso.with(context).load(image).placeholder(R.drawable.default_avatar2).into(imgMyFriend);
        }

        public void setUserOnline(String onlineStatus){
            ImageView imgOnline = convertView.findViewById(R.id.userOnline_icon);

            if (onlineStatus.equals("true")){
                imgOnline.setVisibility(View.VISIBLE);
            }else {
                imgOnline.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void shareApp(){
        try {
            String textShare = "App You & Me";
            String linkShare = "https://play.google.com/store/apps/details?id=com.ahmet.youandme";

            Intent intentShare = new Intent(Intent.ACTION_SEND)
                    .setType("text/plain")
                    .putExtra(Intent.EXTRA_TEXT, textShare + "\n" + linkShare);
            startActivity(intentShare);
        }catch (Exception ex){
            Toast.makeText(this, "عذرا لا يوجد تطبيق للمشاركة", Toast.LENGTH_SHORT).show();
        }
    }

}
