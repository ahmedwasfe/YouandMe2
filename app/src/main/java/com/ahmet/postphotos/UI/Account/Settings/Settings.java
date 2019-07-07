package com.ahmet.postphotos.UI.Account.Settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmet.postphotos.Config.CheckInternetConnection;
import com.ahmet.postphotos.R;
import com.ahmet.postphotos.UI.UserData.Data.EditData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Settings extends AppCompatActivity {

    private Toolbar mToolbar;

    private EditText mEmail,mOldPassword,mNewPassword,mConfirmNewPassword;
    private Button mChangeEmail,mChangePassword;
    private Button mDeleteAccount,mChangeLanguage;
    private ProgressBar mProgreesSettings;

    private CheckInternetConnection mCheckInternetConnection;

    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    // private AuthCredential mAuthCredential;
    private DatabaseReference mReferenceData;

    private String[] listSettings;
    private ListView mListSettings;

    private CircleImageView mImageUser;
    private TextView mTextUsername,mTextStatus;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_settings);

        mToolbar = findViewById(R.id.app_AccountSettings_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.account_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mListSettings = findViewById(R.id.list_settings);
        mImageUser = findViewById(R.id.image_user_settings);
        mTextUsername = findViewById(R.id.text_username_settings);
        mTextStatus = findViewById(R.id.text_user_status_settings);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        String mCurrentUser = firebaseUser.getUid();

        mReferenceData = FirebaseDatabase.getInstance().getReference().child("users").child(mCurrentUser);
        mReferenceData.keepSynced(true);

        mReferenceData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                Picasso.with(getApplicationContext())
                        .load(image)
                        .placeholder(R.drawable.default_avatar2)
                        .into(mImageUser);
                mTextUsername.setText(name);
                mTextStatus.setText(status);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listSettings = getResources().getStringArray(R.array.settings);


        ArrayAdapter arrayAdapter = new ArrayAdapter(this,R.layout.raw_status,R.id.text_select_status,listSettings);

        mListSettings.setAdapter(arrayAdapter);

        findViewById(R.id.card_view_edit_data).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.this, EditData.class));
            }
        });

        mListSettings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                String positionName = (String) adapterView.getItemAtPosition(position);
                if (position == 0){
                    Intent changeEmailIntent = new Intent(Settings.this, ChangeEmailAndPassword.class);
                    changeEmailIntent.putExtra("email", "email");
                    startActivity(changeEmailIntent);
                }else if (position == 1){
                    Intent changePasswordIntent = new Intent(Settings.this, ChangeEmailAndPassword.class);
                    changePasswordIntent.putExtra("password", "password");
                    startActivity(changePasswordIntent);
                }else if (position == 2){
                    startActivity(new Intent(Settings.this,LanguageSettings.class));
                }else if (position == 3){
                    Toast.makeText(Settings.this, positionName, Toast.LENGTH_SHORT).show();
                }else if (position == 4){
                    inviteAFriend();
                }else if (position == 5){
                    help();
                }


            }
        });

//        mEmail = findViewById(R.id.textEmailSettings);
//        mOldPassword = findViewById(R.id.textOldPassword);
//        mNewPassword = findViewById(R.id.textNewPassword);
//        mConfirmNewPassword = findViewById(R.id.textConfirmNewPassword);
//
//        mChangeEmail = findViewById(R.id.btnChangeEmail);
//        mChangePassword = findViewById(R.id.btnChangePassword);
//        mDeleteAccount = findViewById(R.id.btnDeleteAccount);
//        mChangeLanguage = findViewById(R.id.btn_change_language);
//
//        mProgreesSettings = findViewById(R.id.progressBarSettings);

        mCheckInternetConnection = new CheckInternetConnection(this);

//        String email = firebaseUser.getEmail();
//        mEmail.setText(email);


        // mAuthCredential = EmailAuthProvider.getCredential(email,mOldPassword.getText().toString());

//        mChangeLanguage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//               startActivity(new Intent(Settings.this, LanguageSettings.class));
//            }
//        });

//        mChangeEmail.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(final View view) {
//
//                final String newEmail = mEmail.getText().toString();
//
//                if (TextUtils.isEmpty(newEmail)){
//                    mEmail.setError(getString(R.string.enterEmail));
//
//                    return;
//                }
//
////                boolean checkInternet = mCheckInternetConnection.isConnectingToInternet();
////                if (!checkInternet){
////                    Snackbar snackbar = Snackbar.make(view,
////                            R.string.check_internet,
////                            Snackbar.LENGTH_LONG);
////                    snackbar.show();
////                }
//
//                firebaseUser.updateEmail(newEmail)
//                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if (task.isSuccessful()){
//                                   // Toast.makeText(SettingsItems.this, "Change Email Successfull", Toast.LENGTH_SHORT).show();
//                                    final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
//                                    mRef.child("users").child(firebaseUser.getUid()).child("email").setValue(newEmail);
//                                    Snackbar mSnackbar = Snackbar.make(view,R.string.change_email_successfull,Snackbar.LENGTH_LONG);
//                                    mSnackbar.show();
//                                   // firebaseUser.sendEmailVerification();
//                                   // firebaseUser.isEmailVerified();
//                                } else {
//                                    //Toast.makeText(SettingsItems.this, "Something went wrong. Please try again later", Toast.LENGTH_SHORT).show();
////                                    Snackbar mSnackbar = Snackbar.make(view,R.string.error_to_change_email_pass,Snackbar.LENGTH_LONG);
////                                    mSnackbar.show();
//                                    boolean checkInternet = mCheckInternetConnection.isConnectingToInternet();
//                                    if (!checkInternet){
//                                        Snackbar snackbar = Snackbar.make(view,
//                                                R.string.check_internet,
//                                                Snackbar.LENGTH_SHORT);
//                                        snackbar.show();
//                                    }
//                                }
//                            }
//                        });
//
//
//            }
//        });

//        mChangePassword.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(final View view) {
//
//                String oldPassword = mOldPassword.getText().toString();
//                String newPassword = mNewPassword.getText().toString();
//                String confirmNewPassword = mConfirmNewPassword.getText().toString();
//
//                if (TextUtils.isEmpty(oldPassword)){
//                    mOldPassword.setError(getString(R.string.old_password));
//                    return;
//                }
//                if (TextUtils.isEmpty(newPassword)){
//                    mNewPassword.setError(getString(R.string.new_password));
//                    return;
//                }
//                if (TextUtils.isEmpty(confirmNewPassword)){
//                    mConfirmNewPassword.setError(getString(R.string.confirm_new_password));
//                    return;
//                }
//
//                if (!newPassword.equals(confirmNewPassword)){
//                    mConfirmNewPassword.setError(getString(R.string.two_passwords_match));
//                    return;
//                }
//                if (newPassword.length() < 6){
//                    mNewPassword.setError(getString(R.string.password_must_be_more_than_6_characters_long));
//                    return;
//                }
//                if (oldPassword.length() < 6){
//                    mNewPassword.setError(getString(R.string.password_must_be_more_than_6_characters_long));
//                    return;
//                }
//
//
//                AuthCredential mAuthCredential = EmailAuthProvider
//                        .getCredential(firebaseUser.getEmail(),
//                                mOldPassword.getText().toString());
//                firebaseUser.reauthenticate(mAuthCredential)
//                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if (task.isSuccessful()) {
//
//                                    firebaseUser.updatePassword(mNewPassword.getText().toString())
//                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                @Override
//                                                public void onComplete(@NonNull Task<Void> task) {
//
//                                                    if (task.isSuccessful()) {
////                                                        Toast.makeText(SettingsItems.this, "Success to Change Password", Toast.LENGTH_SHORT).show();
//                                                        Snackbar mSnackbar = Snackbar.make(view,R.string.success_to_change_password,Snackbar.LENGTH_LONG);
//                                                        mSnackbar.show();
//                                                    } else {
////                                                        Toast.makeText(SettingsItems.this, "Something went wrong. Please try again later", Toast.LENGTH_SHORT).show();
////                                                        Snackbar mSnackbar = Snackbar.make(view,R.string.error_to_change_email_pass,Snackbar.LENGTH_LONG);
////                                                        mSnackbar.show();
//                                                        boolean checkInternet = mCheckInternetConnection.isConnectingToInternet();
//                                                        if (!checkInternet){
//                                                            Snackbar snackbar = Snackbar.make(view,
//                                                                    R.string.check_internet,
//                                                                    Snackbar.LENGTH_SHORT);
//                                                            snackbar.show();
//                                                        }
//                                                    }
//
//                                                }
//                                            });
//                                }else {
////                                    Toast.makeText(SettingsItems.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
////                                    Snackbar mSnackbar = Snackbar.make(view,R.string.error_to_change_email_pass,Snackbar.LENGTH_LONG);
////                                    mSnackbar.show();
//                                    boolean checkInternet = mCheckInternetConnection.isConnectingToInternet();
//                                    if (!checkInternet){
//                                        Snackbar snackbar = Snackbar.make(view,
//                                                R.string.check_internet,
//                                                Snackbar.LENGTH_SHORT);
//                                        snackbar.show();
//                                    }
//
//                                }
//
//                            }
//                        });
//
//
//            }
//        });

//        mDeleteAccount.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(final View view) {
//
//                boolean checkInternet = mCheckInternetConnection.isConnectingToInternet();
//                if (!checkInternet){
//                    Snackbar snackbar = Snackbar.make(view,
//                            R.string.check_internet,
//                            Snackbar.LENGTH_LONG);
//                    snackbar.show();
//                }
//
//                final DatabaseReference mRef = FirebaseDatabase.getInstance()
//                        .getReference().child("users").child(firebaseUser.getUid());
//
//                if (firebaseUser != null) {
//                    //You need to get here the token you saved at logging-in time.
//                    String token = FirebaseInstanceId.getInstance().getToken();;
//                    //You need to get here the password you saved at logging-in time.
//                    String password = mOldPassword.getText().toString();
//
//                    AuthCredential mAuthCredential;
//
//                    //This means you didn't have the token because user used like Facebook Sign-in method.
//                    if (token == null) {
//                        mAuthCredential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), password);
//                    } else {
//                        //Doesn't matter if it was Facebook Sign-in or others. It will always work using GoogleAuthProvider for whatever the provider.
//                        mAuthCredential = GoogleAuthProvider.getCredential(token, null);
//                    }
//
//                    //We have to reauthenticate user because we don't know how long
//                    //it was the sign-in. Calling reauthenticate, will update the
//                    //user fragment_login and prevent FirebaseException (CREDENTIAL_TOO_OLD_LOGIN_AGAIN) on user.delete()
//                    firebaseUser.reauthenticate(mAuthCredential)
//                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    //Calling delete to remove the user and wait for a result.
//                                    AlertDialog.Builder builderDialog = new AlertDialog.Builder(Settings.this);
//                                    builderDialog.setTitle(R.string.delete_account);
//                                    builderDialog.setMessage(R.string.really_delete_accouny);
//                                    builderDialog.setIcon(R.drawable.ic_delete_black_24dp);
//
//                                    builderDialog.setPositiveButton(R.string.delete_account, new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialogInterface, int i) {
//
//                                            mProgreesSettings.setVisibility(View.VISIBLE);
//
//                                            mRef.removeValue();
//                                            firebaseUser.delete()
//                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                        @Override
//                                                        public void onComplete(@NonNull Task<Void> task) {
//                                                            if (task.isSuccessful()) {
//
//                                                                startActivity(new Intent(Settings.this,Profile.class));
//                                                                finish();
//                                                                startActivity(new Intent(Settings.this,HomeActivity.class));
//                                                                finish();
//                                                                startActivity(new Intent(Settings.this,LoginFragment.class));
//                                                                //Ok, user remove
//                                                                finish();
//                                                                //Toast.makeText(SettingsItems.this, "Account Deleted", Toast.LENGTH_SHORT).show();
//                //                                                Snackbar mSnackbar = Snackbar.make(view,"Account Deleted",Snackbar.LENGTH_LONG);
//                //                                                mSnackbar.show();
//                                                            } else {
//                                                                //Handle the exception
//                                                                task.getException();
//                                                                //Toast.makeText(SettingsItems.this, "Something went wrong. Please try again later", Toast.LENGTH_SHORT).show();
//                                                                Snackbar mSnackbar = Snackbar.make(view,R.string.error_to_change_email_pass,Snackbar.LENGTH_LONG);
//                                                                mSnackbar.show();
//                                                            }
//                                                        }
//                                                    });
//                                        }
//                                    });
//                                    builderDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialogInterface, int i) {
//                                            mProgreesSettings.setVisibility(View.INVISIBLE);
//                                        }
//                                    });
//                                    builderDialog.show();
//                                    startActivity(new Intent(Settings.this,LoginFragment.class));
//                                    finish();
//
//
//                                }
//                            });
//                }
//
//            }
//        });
    }

    private void inviteAFriend() {

        String appNameShare = "App You & Me";
        String appLinkShare = "https://play.google.com/store/apps/details?id=com.ahmet.youandme";

        try {


            Intent inviteIntent = new Intent(Intent.ACTION_SEND);
            inviteIntent.setType("text/plane");
            inviteIntent.putExtra(Intent.EXTRA_TEXT, appNameShare + "\n\n" + appLinkShare);
            startActivity(Intent.createChooser(inviteIntent, "Invite a friends" ));
        }catch (Exception ex){
            Toast.makeText(this, R.string.error_to_invite_friends, Toast.LENGTH_SHORT).show();
        }
    }

    private void help() {
        Toast.makeText(Settings.this, R.string.help, Toast.LENGTH_SHORT).show();
    }

}
