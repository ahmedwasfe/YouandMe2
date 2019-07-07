package com.ahmet.postphotos.UI.Main.Home;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.ahmet.postphotos.Adapter.SectionsPagerAdapter;
import com.ahmet.postphotos.R;
import com.ahmet.postphotos.UI.Account.Account_Login.LoginFragment;
import com.ahmet.postphotos.UI.Account.Profile.Profile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mRefUser;

    private Toolbar mToolbar;

    private ViewPager mViewPager;

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {

            mRefUser = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());
        }
        mToolbar = findViewById(R.id.app_mainPage_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.app_name);

        mViewPager = findViewById(R.id.main_tab_ViewPager);
        mTabLayout = findViewById(R.id.mainTabLayout);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mTabLayout.setupWithViewPager(mViewPager);


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
    }

    @Override
    protected void onStop() {
        super.onStop();

        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            mRefUser.child("online").setValue(ServerValue.TIMESTAMP);
        }
    }

    private void sendToStart() {

        Intent startIntent = new Intent(MainActivity.this, LoginFragment.class);
        startActivity(startIntent);
        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        int id = item.getItemId();

        if (id == R.id.main_account_settings){
            Intent accountSettingIntent = new Intent(MainActivity.this, Profile.class);
            startActivity(accountSettingIntent);
        }
//        if (id == R.id.main_all_user){
//            Intent allUsersIntent = new Intent(MainActivity.this,AllUsers.class);
//            startActivity(allUsersIntent);
//        }
//        if (id == R.id.main_logout){
//            FirebaseAuth.getInstance().signOut();
//            sendToStart();
//        }

        return true;
    }
}
