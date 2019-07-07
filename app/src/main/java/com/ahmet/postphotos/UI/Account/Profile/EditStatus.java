package com.ahmet.postphotos.UI.Account.Profile;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmet.postphotos.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditStatus extends AppCompatActivity {

    private Toolbar mToolbar;

    private TextView mStatus;
    private ImageButton mEditStatus;
    private ListView mListStatus;

    ArrayAdapter<String> arrayAdapter;

    private String status;

    private DatabaseReference mRef;
    private FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.status_user);

        mToolbar = findViewById(R.id.app_statusPage_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.status);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mStatus = findViewById(R.id.textStatus);
        mEditStatus = findViewById(R.id.btnSaveStatus);
        mListStatus = findViewById(R.id.list_status);



        String[] indexStatus = getResources().getStringArray(R.array.status);
        arrayAdapter = new ArrayAdapter<String>(this,R.layout.raw_status,R.id.text_select_status,indexStatus);
        mListStatus.setAdapter(arrayAdapter);

        mListStatus.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

//                Toast.makeText(EditStatus.this, adapterView.getItemAtPosition(position)+"", Toast.LENGTH_SHORT).show();
//                mStatus.setText(adapterView.getItemAtPosition(position).toString());

                String status = adapterView.getItemAtPosition(position).toString();
//                Toast.makeText(EditStatus.this, status, Toast.LENGTH_SHORT).show();
                mRef.child("status").setValue(status)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()){

                                }else {
                                    Toast.makeText(getApplicationContext(), R.string.error_to_change_email_pass, Toast.LENGTH_LONG).show();
                                }
                            }
                        });

            }
        });

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUID = firebaseUser.getUid();

        mRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUID);

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                status = dataSnapshot.child("status").getValue().toString();
                mStatus.setText(status);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mEditStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final Dialog editStstusDialog = new Dialog(EditStatus.this);
                editStstusDialog.setContentView(R.layout.raw_dialog_edit_status);
                final EditText mNewStatus = editStstusDialog.findViewById(R.id.text_new_status);
                TextView mDone = editStstusDialog.findViewById(R.id.text_done_edit_status);
                TextView mCancel = editStstusDialog.findViewById(R.id.text_cancel_edit_status);
                editStstusDialog.setCanceledOnTouchOutside(false);
                mNewStatus.setText(status);

                mDone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String status = mNewStatus.getText().toString();

                        mRef.child("status").setValue(status)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()){

                                        }else {
                                            Toast.makeText(getApplicationContext(), R.string.error_to_change_email_pass, Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                        editStstusDialog.dismiss();
                    }
                });

                mCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editStstusDialog.dismiss();
                    }
                });




                editStstusDialog.show();

            }
        });


    }
}
