package com.ahmet.postphotos.UI.Account.Account_Login;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ahmet.postphotos.Config.CheckInternetConnection;
import com.ahmet.postphotos.R;
import com.ahmet.postphotos.UI.UserData.Data.AddData;
import com.google.firebase.auth.FirebaseAuth;

public class EmailVerification extends AppCompatActivity {

    private TextView mEmailVerification;
    private ProgressBar mProgressBarEmailVerivication;

    private CheckInternetConnection mCheckInternetConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.email_verification);

        Toolbar mToolbar = findViewById(R.id.app_EmailVerification_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.email_verification);

        mCheckInternetConnection = new CheckInternetConnection(this);

        mEmailVerification = findViewById(R.id.textEmailVerificatio);
        mProgressBarEmailVerivication = findViewById(R.id.progressBarEmailVerivication);

        if (!FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()){
            mProgressBarEmailVerivication.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        boolean checkInternet = mCheckInternetConnection.isConnectingToInternet();
        if (!checkInternet){
            Snackbar snackbar = Snackbar.make(findViewById(R.id.layout_email_verification),
                    R.string.check_internet,
                    Snackbar.LENGTH_LONG);
            snackbar.show();
        }

        if (FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()){
            mProgressBarEmailVerivication.setVisibility(View.INVISIBLE);
            mEmailVerification.setText("Email Verification Done");
            startActivity(new Intent(EmailVerification.this, AddData.class));
            finish();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_data, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_nextAddDAta){
            setVisible(false);
        }
        return true;
    }
}
