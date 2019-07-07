package com.ahmet.postphotos.UI.Account.Account_Login;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ahmet.postphotos.Config.Common;
import com.ahmet.postphotos.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Common.replaceFragments(new LoginFragment(),
                R.id.frame_layout_login,
                getSupportFragmentManager());
    }
}
