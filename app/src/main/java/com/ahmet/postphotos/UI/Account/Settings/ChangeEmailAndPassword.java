package com.ahmet.postphotos.UI.Account.Settings;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ahmet.postphotos.R;

public class ChangeEmailAndPassword extends AppCompatActivity {

    private TextView titleChangeEmail, titleChangePassword;
    private EditText mEmail, mOldPassword, mNewPassword, mConfirmPassword;
    private Button mChangeEmail, mChangePassword;

    private String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email_and_password);

        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");

        titleChangeEmail = findViewById(R.id.text_title_change_email);
        titleChangePassword = findViewById(R.id.text_title_change_password);
        mEmail = findViewById(R.id.text_change_email);
        mOldPassword = findViewById(R.id.text_old_password);
        mNewPassword = findViewById(R.id.text_new_password);
        mConfirmPassword = findViewById(R.id.text_confirm_new_password);
        mChangeEmail = findViewById(R.id.btn_change_email);
        mChangePassword = findViewById(R.id.btn_change_password);

//        if (email.equals(email)){
//            titleChangePassword.setVisibility(View.INVISIBLE);
//            mOldPassword.setVisibility(View.INVISIBLE);
//            mNewPassword.setVisibility(View.INVISIBLE);
//            mConfirmPassword.setVisibility(View.INVISIBLE);
//            mChangePassword.setVisibility(View.INVISIBLE);
//        }else if (password.equals(password)){
//            titleChangeEmail.setVisibility(View.INVISIBLE);
//            mEmail.setVisibility(View.INVISIBLE);
//            mChangeEmail.setVisibility(View.INVISIBLE);
//        }

    }
}
