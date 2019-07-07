package com.ahmet.postphotos.UI.Main.Home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.ahmet.postphotos.R;
import com.ahmet.postphotos.UI.Account.Account_Login.LoginFragment;
import com.ahmet.postphotos.UI.Account.Account_Login.RegisterFragment;

public class StartActivity extends AppCompatActivity {

    private Button mRegBtn;
    private Button mLoginBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mRegBtn = findViewById(R.id.btn_Register);
        mLoginBtn = findViewById(R.id.btn_Login);

        mRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent regIntent = new Intent(StartActivity.this, RegisterFragment.class);
                startActivity(regIntent);
            }
        });

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginIntent = new Intent(StartActivity.this, LoginFragment.class);
                startActivity(loginIntent);
            }
        });
    }
}
