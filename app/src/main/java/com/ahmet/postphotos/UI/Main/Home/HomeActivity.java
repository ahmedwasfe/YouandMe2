package com.ahmet.postphotos.UI.Main.Home;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmet.postphotos.Adapter.SectionsPagerAdapter;
import com.ahmet.postphotos.R;
import com.ahmet.postphotos.UI.Account.Account_Login.LoginActivity;
import com.ahmet.postphotos.UI.Account.Settings.Settings;
import com.ahmet.postphotos.UI.Account.Profile.Profile;
import com.ahmet.postphotos.UI.Main.Fragments.ConversationFragment;
import com.ahmet.postphotos.UI.Main.Fragments.FriendsFragment;
import com.ahmet.postphotos.UI.Main.Fragments.MainFragment;
import com.ahmet.postphotos.UI.UserData.User.AddFriends;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private DatabaseReference mRefUser;
    private DatabaseReference mRefUsers;

    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private TextView mUsername;
    private CircleImageView mImageUser;

    private static final int REQUEST_PERMISSION_STORAGE = 80;;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mToolbar = findViewById(R.id.app_mainPage_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.app_name);

        requestPermissionAccessToStorage();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_home);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view_home);
        navigationView.setNavigationItemSelectedListener(this);

        try {
            mUsername = navigationView.getHeaderView(0).findViewById(R.id.UsernamenavHeaderHome);
            mImageUser = navigationView.getHeaderView(0).findViewById(R.id.imageUsernavHeaderHome);

            mImageUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(HomeActivity.this, Profile.class));
                }
            });
        }catch (Exception e){
            startActivity(new Intent(HomeActivity.this,LoginActivity.class));
        }

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {

            mRefUser = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());
        }

        mRefUsers = FirebaseDatabase.getInstance().getReference().child("users");
        mRefUsers.keepSynced(true);

//        mToolbar = findViewById(R.id.app_mainPage_toolbar);
//        setSupportActionBar(mToolbar);
//        getSupportActionBar().setTitle(R.string.app_name);

        mViewPager = findViewById(R.id.main_tab_ViewPager);
        mTabLayout = findViewById(R.id.mainTabLayout);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mSectionsPagerAdapter.setFragment(new MainFragment(), "");
        mSectionsPagerAdapter.setFragment(new ConversationFragment(), "");
        mSectionsPagerAdapter.setFragment(new FriendsFragment(), "");

        mViewPager.setAdapter(mSectionsPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        mTabLayout.getTabAt(0)
                .setText(R.string.home)
                .setIcon(R.drawable.ic_home_white);
        mTabLayout.getTabAt(1)
                .setText(R.string.chats)
                .setIcon(R.drawable.ic_conversation_white);
        mTabLayout.getTabAt(2)
                .setText(R.string.friends)
                .setIcon(R.drawable.user_groups);

    }

    public void logOut(){

        final Dialog dialog = new Dialog(HomeActivity.this);
        dialog.setContentView(R.layout.raw_dialog_logout);
        TextView textLogout = dialog.findViewById(R.id.textLogOut);
        TextView textCancel = dialog.findViewById(R.id.textCancel);

        textLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mRefUser.child("online").setValue("false");
                mRefUser.child("online").setValue(ServerValue.TIMESTAMP);
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
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

        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser == null){
            sendToStart();
        } else {
            mRefUser.child("online").setValue("true");
        }

        try {
            mRefUsers.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    try {
                        String username = dataSnapshot.child("name").getValue().toString();
                        String userImage = dataSnapshot.child("image").getValue().toString();

                        mUsername.setText(username);
                        Picasso.with(getApplicationContext())
                                .load(userImage)
                                .placeholder(R.drawable.default_avatar2)
                                .into(mImageUser);
                    }catch (Exception ex){
                        //Toast.makeText(HomeActivity.this, "Home", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }catch (Exception ex){
           // Toast.makeText(this, ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
           // Toast.makeText(this, "Please re-fragment_login", Toast.LENGTH_SHORT).show(); //يرجى اعادة تسجيل الدخول
        }

    }


    @Override
    protected void onStop() {
        super.onStop();

        Date curDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat("hh:mm a");
        String currentTime = format.format(curDate);

        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            mRefUser.child("online").setValue(ServerValue.TIMESTAMP);
           // mRefUser.child("online2").setValue(currentTime);
        }
    }

    private void sendToStart() {

        Intent startIntent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(startIntent);
        finish();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_home);
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
//
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        /*int id = item.getItemId();

        noinspection SimplifiableIfStatement
       * if (id == R.id.main_account_settings) {
       *     Intent accountSettingIntent = new Intent(HomeActivity.this, Profile.class);
       *    startActivity(accountSettingIntent);
       * }
       * if (id == R.id.main_all_user) {
       *     Intent allUsersIntent = new Intent(HomeActivity.this,AllUsers.class);
       *     startActivity(allUsersIntent);
       * }
       * if (id == R.id.main_logout) {
           logOut();
       * }*/

        if (item.getItemId() == R.id.main_account_settings){
            startActivity(new Intent(HomeActivity.this, Settings.class));
//            Snackbar snackbar = Snackbar.make(mUsername,"About An You & Me",Snackbar.LENGTH_LONG)
//                    .setAction("About", new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            Toast.makeText(HomeActivity.this, "About An You & Me", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//            snackbar.show();
        }else if (item.getItemId() == R.id.nav_add_friends){
            startActivity(new Intent(HomeActivity.this, AddFriends.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.nav_myconversation) {

        } else if (id == R.id.nav_myFriends) {
           // Common.replaceFragments(new FriendsFragment(), R.id.frame_layout_home, getSupportFragmentManager());
        } else if (id == R.id.nav_addFriends) {
            startActivity(new Intent(HomeActivity.this, AddFriends.class));
        } else if (id == R.id.nav_profile) {
            Intent accountSettingIntent = new Intent(HomeActivity.this, Profile.class);
            startActivity(accountSettingIntent);
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(HomeActivity.this, Settings.class));
        } else if (id == R.id.nav_logout) {
            logOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_home);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void requestPermissionAccessToStorage() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            },REQUEST_PERMISSION_STORAGE);
            return;
        }
    }

    private void shareApp(){
        try {
            String textShare = "App You & Me";
            String linkShare = "https://play.google.com/store/apps/details?id=com.ahmet.youandme";

            Intent intentShare = new Intent(Intent.ACTION_SEND)
                    .setType("text/plain")
                    .putExtra(Intent.EXTRA_TEXT, textShare + "\n\n" + linkShare);
            startActivity(intentShare);
        }catch (Exception ex){
            Toast.makeText(this, R.string.error_to_invite_friends, Toast.LENGTH_SHORT).show();
        }
    }


}
