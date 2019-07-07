package com.ahmet.postphotos.UI.Account.Account_Login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ahmet.postphotos.Config.CheckInternetConnection;
import com.ahmet.postphotos.Config.Common;
import com.ahmet.postphotos.R;
import com.ahmet.postphotos.UI.UserData.Data.AddData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class RegisterFragment extends Fragment {


    // private TextInputLayout mUserName;
    private EditText mEmail;
    private EditText mPassword;
    private EditText mConfirmPass;

    private Button mRegister;
    private TextView mTextLogin;

    private FirebaseAuth mAuth;
    private DatabaseReference mRef;

    private Toolbar mToolbar;

    private ProgressBar mProgressBar;

    private CheckInternetConnection mCheckInternetConnection;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        mAuth = FirebaseAuth.getInstance();

//        mToolbar = findViewById(R.id.app_registerPage_toolbar);
//
//        setSupportActionBar(mToolbar);
//        getSupportActionBar().setTitle(R.string.create_account);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        mCheckInternetConnection = new CheckInternetConnection(getActivity());

    }

    @Nullable
    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View convertView = inflater.inflate(R.layout.fragment_register, container, false);

        mEmail = convertView.findViewById(R.id.textEmail);
        mPassword = convertView.findViewById(R.id.textPassword);
        mConfirmPass = convertView.findViewById(R.id.textConfirmPassword);
        mRegister = convertView.findViewById(R.id.btnRegister);
        mTextLogin = convertView.findViewById(R.id.text_Login_Reg);
        mProgressBar = convertView.findViewById(R.id.progressBarReg);

        return convertView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mTextLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.replaceFragments(new LoginFragment(),
                        R.id.frame_layout_login,
                        getActivity().getSupportFragmentManager());

            }
        });

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();
                String confirmPass = mConfirmPass.getText().toString();


                if (!Common.isEmailValid(email)){
                    mEmail.setError(getString(R.string.email_pattern));//لقد أدخلت رسالة إلكترونية بشكل غير صحيح
                    return;
                }

                if (!password.equals(confirmPass)){
                    mConfirmPass.setError(getString(R.string.two_passwords_match));//كلمتا المرور غير متطابقتين
                    return;
                }
                if (password.length() < 6 ){
                    mPassword.setError(getString(R.string.password_must_be_more_than_6_characters_long));
                    return;
                }

                if (TextUtils.isEmpty(email)){
                    mEmail.setError(getString(R.string.enterEmail));
                    return;
                }

                if (TextUtils.isEmpty(password)){
                    mPassword.setError(getString(R.string.enterPass));
                    return;
                }
                if (TextUtils.isEmpty(confirmPass)){
                    mPassword.setError(getString(R.string.enterConfPass));
                    return;
                }

                mProgressBar.setVisibility(View.VISIBLE);


                if ( !TextUtils.isEmpty(email) || !TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPass) ) {


                    registerUser(email, password);

                }
            }
        });

    }

    private void registerUser(final String email, final String password) {

        final String defaultPhotoProfile = "https://firebasestorage.googleapis.com/v0/b/you-me-3461b.appspot.com/o/profile_images%2Fdefault_avatar2.png?alt=media&token=90623716-6fe6-4dc2-8a5b-dcfa5c07d8d1";
        final String defaultPhotoProfile2 = "https://firebasestorage.googleapis.com/v0/b/you-me-3461b.appspot.com/o/profile_images%2F1MTcyHMnEVXfyJk7cypgZXuPRER2.jpg?alt=media&token=e2415fa7-0bf6-4d8d-a055-f3a892a523b8";

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){

                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            String uid = firebaseUser.getUid();
                            mRef = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
                            String deviceToken = FirebaseInstanceId.getInstance().getToken();

                            HashMap<String, String> userMap = new HashMap<>();
                            userMap.put("name", "You & Me User");
                            userMap.put("email", firebaseUser.getEmail());
                            userMap.put("status", "Hi there, I'm using You & Me");
                            userMap.put("phone", "000000000");
                            userMap.put("age", "00");
                            userMap.put("online", "true");
                            userMap.put("gender", "mail");
                            userMap.put("image", defaultPhotoProfile);
                            userMap.put("thumb_image", "default");
                            userMap.put("cover_image", "default");
                            userMap.put("device_token", deviceToken);
                            mRef.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()){

                                        mProgressBar.setVisibility(View.INVISIBLE);

                                        //mAuth.getCurrentUser().sendEmailVerification();

                                        Intent mainIntent = new Intent(getActivity(), AddData.class);
                                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(mainIntent);
                                        getActivity().finish();

                                    }
                                }
                            });

                        }else {
                            mProgressBar.setVisibility(View.INVISIBLE);
                            Snackbar snackbar = Snackbar.make(mRegister,
                                    R.string.cannot_Log_up,
                                    Snackbar.LENGTH_SHORT)

                                    .setAction(R.string.retry, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            registerUser(email,password);
                                        }
                                    });
                            snackbar.show();
                            //  Toast.makeText(RegisterFragment.this, "Cannot Sign in. Please check the form and try again.", Toast.LENGTH_SHORT).show();
                            boolean checkInternet = mCheckInternetConnection.isConnectingToInternet();
                            if (!checkInternet){
                                Snackbar snackbar2 = Snackbar.make(mRegister,
                                        R.string.check_internet,
                                        Snackbar.LENGTH_SHORT)
                                        .setAction(R.string.retry, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                registerUser(email,password);
                                            }
                                        });
                                snackbar2.show();
                            }
                        }

                    }
                });
    }

}
