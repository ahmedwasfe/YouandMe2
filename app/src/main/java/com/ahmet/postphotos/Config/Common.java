package com.ahmet.postphotos.Config;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.ahmet.postphotos.UI.Account.Account_Login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ahmet on 2/6/2018.
 */

public class Common extends Application {

    private DatabaseReference mRefUserDatabase;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);


        /* Picasso */

        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {

            mRefUserDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());

            mRefUserDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot != null) {
                        mRefUserDatabase.child("online").onDisconnect().setValue(ServerValue.TIMESTAMP);
//                    mRefUserDatabase.child("online").setValue(true);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

    public static void replaceFragments(Fragment fragment, int id, FragmentManager manager){
        // manager = fragment.getActivity().getSupportFragmentManager();
        manager.beginTransaction()
                .replace(id, fragment)
                .commit();
    }


    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean emailVerivication(String newEmail, String oldEmail, View view){
        if (newEmail.equals(oldEmail)){
            Snackbar.make(view, "هذا الإيميل مستخدم من قبل شخص أخر", Snackbar.LENGTH_SHORT).show();
        }
        return true;
    }

    public static void signOut(Activity activity){
        FirebaseAuth.getInstance().signOut();
        activity.startActivity(new Intent(activity, LoginActivity.class));
        activity.finish();
    }

    public static void getDate() {
        Date curDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat("Mth d, yyyy hh:mm: a");
        final String timePost = format.format(curDate);
    }

}
