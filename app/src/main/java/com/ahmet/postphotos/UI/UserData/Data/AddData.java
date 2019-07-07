package com.ahmet.postphotos.UI.UserData.Data;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.ahmet.postphotos.R;
import com.ahmet.postphotos.UI.UserData.User.AllUsers;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddData extends AppCompatActivity {

    private ViewGroup rootLayout;

    private CircleImageView mImageUserAdd;
    private EditText mTextUsernameAdd,mTextUserPhone,mTextUserAge;
    private RadioGroup mGenderGroup;;
    private RadioButton mMail,mFemail;
    private Button mAddData;

    private Toolbar mToolbar;

    private String mName;
    private String mPhone;
    private String mAge;
    private String mGender;

    private DatabaseReference mRefAddDataUser;
    private FirebaseUser firebaseUser;

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_data);

        rootLayout = findViewById(R.id.main_container);

        mToolbar = findViewById(R.id.app_AddDatPage_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.add_data_user);


        mAddData = findViewById(R.id.btnAddData);

        mTextUsernameAdd = findViewById(R.id.textNameAddUser);
        mTextUserPhone = findViewById(R.id.textPhoneAddUser);
        mTextUserAge = findViewById(R.id.textAgeAddUser);

        mGenderGroup = findViewById(R.id.Gender);
        mMail = findViewById(R.id.mail);
        mFemail = findViewById(R.id.femail);

//        if (mName.isEmpty() && mPhone.isEmpty() && mAge.isEmpty()) {
//            mTextUsernameAdd.setText("You & Me User");
//            mTextUserPhone.setText("0000000000");
//            mTextUserAge.setText("00");
//
//        }

        dialog = new ProgressDialog(this);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final String userUID = firebaseUser.getUid();

        mRefAddDataUser = FirebaseDatabase.getInstance().getReference().child("users").child(userUID);

        mGenderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int position) {

                if (position == R.id.mail){
                    mGender = mMail.getText().toString();
                    // Toast.makeText(AddData.this, mGender, Toast.LENGTH_SHORT).show();
                }else if (position == R.id.femail){
                    mGender = mFemail.getText().toString();
                    // Toast.makeText(AddData.this, mGender, Toast.LENGTH_SHORT).show();
                }

            }
        });

        mAddData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                mName = mTextUsernameAdd.getText().toString();
                mPhone = mTextUserPhone.getText().toString();
                mAge = mTextUserAge.getText().toString();

                if (TextUtils.isEmpty(mName)){
                    mTextUsernameAdd.setError("Pleasr Enter Your Name");
                    return;
                }

                if (TextUtils.isEmpty(mPhone)){
                    mTextUsernameAdd.setError("Pleasr Enter Your Phone");
                    return;
                }

                if (TextUtils.isEmpty(mAge)){
                    mTextUsernameAdd.setError("Pleasr Enter Your Age");
                    return;
                }


                if (!TextUtils.isEmpty(mName) && !TextUtils.isEmpty(mPhone) && !TextUtils.isEmpty(mAge)) {
                    mRefAddDataUser.child("name").setValue(mName);
                    mRefAddDataUser.child("phone").setValue(mPhone);
                    mRefAddDataUser.child("age").setValue(mAge);
                    mRefAddDataUser.child("gender").setValue(mGender);
                    Toast.makeText(AddData.this, "Add Data Successfull", Toast.LENGTH_SHORT).show();
                    //startActivity(new Intent(AddData.this, AllUsers.class));
                }else {


                    Toast.makeText(AddData.this, R.string.error_to_change_email_pass, Toast.LENGTH_SHORT).show();
                    if (TextUtils.isEmpty(mName)){
                        mTextUsernameAdd.setError("Pleasr Enter Your Name");
                        return;
                    }

                    if (TextUtils.isEmpty(mPhone)){
                        mTextUsernameAdd.setError("Pleasr Enter Your Phone");
                        return;
                    }

                    if (TextUtils.isEmpty(mAge)){
                        mTextUsernameAdd.setError("Pleasr Enter Your Age");
                        return;
                    }

                }
//                    dialog.setTitle("Add Data");
//                    dialog.setMessage("Pleas Wait while Add Data!");
//                    dialog.setCanceledOnTouchOutside(true);
//                    dialog.show();


            }
        });


    }

    private void addData(String name,String phone,String age,String gender){


        Map userDataMap = new HashMap();
        userDataMap.put("name", name);
        userDataMap.put("phone", phone);
        userDataMap.put("age", age);
        userDataMap.put("gender", gender);
        mRefAddDataUser.setValue(userDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){

                    dialog.dismiss();
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.layout_add_data),R.string.add_data_success,Snackbar.LENGTH_SHORT);
                    snackbar.show();

                }else {
                    Toast.makeText(AddData.this, R.string.error_to_change_email_pass, Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.menu_add_data,menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.menu_nextAddDAta){
            startActivity(new Intent(AddData.this,AllUsers.class));
            finish();
        }
        return true;

    }


}
