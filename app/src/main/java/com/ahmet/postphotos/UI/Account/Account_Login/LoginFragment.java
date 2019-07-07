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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ahmet.postphotos.Config.CheckInternetConnection;
import com.ahmet.postphotos.Config.Common;
import com.ahmet.postphotos.R;
import com.ahmet.postphotos.UI.Main.Home.HomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginFragment extends Fragment {


    private EditText mEmail;
    private EditText mPassword;
    private Button mLogin;
    private TextView mRestPassword;
    private TextView mTextResgister;
    private ImageView mShowPassword;

    private Toolbar mToolbar;

    private ProgressBar mProgressBar;

    private FirebaseAuth mAuth;
    private DatabaseReference mRefDatabase;

    private CheckInternetConnection mCheckInternetConnection;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        //mLoginProg = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        mRefDatabase = FirebaseDatabase.getInstance().getReference().child("users");

//        mToolbar = findViewById(R.id.app_loginPage_toolbar);
//        setSupportActionBar(mToolbar);
//        getSupportActionBar().setTitle(R.string.login);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mCheckInternetConnection = new CheckInternetConnection(getActivity());


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View convertView = inflater.inflate(R.layout.fragment_login, container, false);

        mEmail = convertView.findViewById(R.id.textLoginEmail);
        mPassword = convertView.findViewById(R.id.textLoginPassword);
        mLogin = convertView.findViewById(R.id.btnLogin);
        mRestPassword = convertView.findViewById(R.id.textRestPasswordLogin);
        mTextResgister = convertView.findViewById(R.id.textResgister);
        mProgressBar = convertView.findViewById(R.id.progressBarLogin);
        //mShowPassword = convertView.findViewById(R.id.imgShowPassword);

        return convertView;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mRestPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.replaceFragments(new ResetPasswordFragment(),
                        R.id.frame_layout_login,
                        getActivity().getSupportFragmentManager());
            }
        });
        mTextResgister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.replaceFragments(new RegisterFragment(),
                        R.id.frame_layout_login,
                        getActivity().getSupportFragmentManager());
            }
        });

//        mShowPassword.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//                mPassword.setInputType(android.R.attr.password);
//                return true;
//            }
//        });

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //mProgressBar.setVisibility(View.VISIBLE);


                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();

                if (!Common.isEmailValid(email)){
                    mEmail.setError(getString(R.string.email_pattern));//لقد أدخلت رسالة إلكترونية بشكل غير صحيح
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



                if (password.length() < 6 ){
                    mPassword.setError(getString(R.string.password_must_be_more_than_6_characters_long));
                    return;
                }


                mProgressBar.setVisibility(View.VISIBLE);


//                boolean checkInternet = mCheckInternetConnection.isConnectingToInternet();
//                    if (!checkInternet){
//                        Snackbar snackbar = Snackbar.make(view,
//                                R.string.check_internet,
//                                Snackbar.LENGTH_LONG)
//                                .setAction(R.string.retry, new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View view) {
//                                        loginUser(email,password);
//                                    }
//                                });
//                        snackbar.show();
//                }



                if (!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)){

                    //mProgressBar.setVisibility(View.INVISIBLE);
                    loginUser(email, password);
                }
            }
        });


    }

    private void loginUser(final String email, final String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){

                            //mLoginProg.dismiss();
                            mProgressBar.setVisibility(View.INVISIBLE);

                            String userUID = mAuth.getCurrentUser().getUid();
                            String deviceToken = FirebaseInstanceId.getInstance().getToken();
                            mRefDatabase.child(userUID).child("online").setValue(ServerValue.TIMESTAMP);
                            mRefDatabase.child(userUID).child("device_token").setValue(deviceToken)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            Intent mainIntent = new Intent(getActivity(), HomeActivity.class);
                                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(mainIntent);
                                            getActivity().finish();

                                        }
                                    });



                        }else {
                            mProgressBar.setVisibility(View.INVISIBLE);
                            Snackbar snackbar = Snackbar.make(mLogin,
                                    R.string.cannot_Log_in,
                                    Snackbar.LENGTH_SHORT)
                                    .setAction(R.string.retry, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            loginUser(email,password);
                                        }
                                    });
                            snackbar.show();

                            // Toast.makeText(LoginFragment.this, "Cannot Sign in. Please check the form and try again.", Toast.LENGTH_SHORT).show();

                            boolean checkInternet = mCheckInternetConnection.isConnectingToInternet();
                            if (!checkInternet){
                                Snackbar snackbar2 = Snackbar.make(mLogin,
                                        R.string.check_internet,
                                        Snackbar.LENGTH_SHORT)
                                        .setAction(R.string.retry, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                loginUser(email,password);
                                            }
                                        });
                                snackbar2.show();
                            }
                        }
                    }
                });

    }


}
