package com.ahmet.postphotos.UI.Main.Chat;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmet.postphotos.Adapter.MessagesAdapter;
import com.ahmet.postphotos.Config.CheckInternetConnection;
import com.ahmet.postphotos.Config.GetTimeAgo;
import com.ahmet.postphotos.Model.Messages;
import com.ahmet.postphotos.R;
import com.ahmet.postphotos.UI.Account.Settings.Settings;
import com.ahmet.postphotos.UI.UserData.User.ProfileUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatRoom extends AppCompatActivity {

    // Variables
    private String mConvUser;
    private String username;

    private Toolbar mToolbar;
    private ActionBar actionBar;
    private View actionBarView;
    private LayoutInflater layoutInflater;

     // Firebase
    private DatabaseReference mRootRef;
    private DatabaseReference mDatabaseMessages;
    private FirebaseAuth mAuth;
    private String mcurrentUserUID;
    private StorageReference mImageStorageRef;
    private StorageReference mAudioStorageRef;

     // Views
    private TextView mUsername;
    private TextView mLastSeen;
    private CircleImageView mProfileUserImage;
    private ImageButton mAddImagetoConv;
    private ImageButton mConvSendMessage;
    private ImageButton mRecoredAudio;
    private EditText mConvWriteMessage;
    private RecyclerView mMessagesRecycler;
    private SwipeRefreshLayout mSwipeRefresh;
    private RelativeLayout mRelativeLayout;

    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager mLinearLayout;

    private MessagesAdapter messagesAdapter;

    private static final int TOTAL_ITEMS_TO_LOAD = 10;
    private int mCurrentPage = 1;

    private int itemPosition = 0;
    private String mLastKey = "";
    private String mPrevKey = "";

    private static final int GALLERY_PICK = 88;

    private CheckInternetConnection mCheckInternetConnection;

    private static final String LOG_TAG = "AudioRecordTest";

    private static String mFileName;

    private static final int REQUEST_PERMISSION_RECORD_AUDIO = 88;
    private static final int REQUEST_PERMISSION_STORAGE = 80;


    private MediaRecorder mAudioRecord;


    @Override
    protected void onStop() {
        super.onStop();

        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            mRootRef.child("users").child(mAuth.getCurrentUser().getUid())
                    .child("online").setValue(ServerValue.TIMESTAMP);
            // mRefUser.child("online2").setValue(currentTime);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatroom);

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mDatabaseMessages = FirebaseDatabase.getInstance().getReference();
        mImageStorageRef = FirebaseStorage.getInstance().getReference();
        mAudioStorageRef = FirebaseStorage.getInstance().getReference();
        //mRootRef.keepSynced(true);
        mAuth = FirebaseAuth.getInstance();
        mcurrentUserUID = mAuth.getCurrentUser().getUid();

        mConvUser = getIntent().getStringExtra("user_uid");
        username = getIntent().getStringExtra("username");
        String message = getIntent().getStringExtra("messageReplay");


        requestPermissionAccessToRecordAudio();

        mCheckInternetConnection = new CheckInternetConnection(this);

        mToolbar = findViewById(R.id.app_ConversationPage_toolbar);
        setSupportActionBar(mToolbar);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("");

        layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        actionBarView = layoutInflater.inflate(R.layout.raw_conversation_in_toobar, null);

        actionBar.setCustomView(actionBarView);


        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        //mFileName = getExternalCacheDir().getAbsolutePath();
        mFileName += "/audiorecord.3gp";
        mAudioRecord = new MediaRecorder();

        /*-----Custom Action bar Items ----*/

        mUsername = findViewById(R.id.conv_bar_Username);
        mLastSeen = findViewById(R.id.conv_bar_Userlastseen);
        mProfileUserImage = findViewById(R.id.conv_bar_UserImage);

        mRelativeLayout = findViewById(R.id.layout_conversation);

        mAddImagetoConv = findViewById(R.id.imgAddImagetoConv);
        mConvSendMessage = findViewById(R.id.btncConvSend);
        mConvWriteMessage = findViewById(R.id.textConvWriteMessage);
        mRecoredAudio = findViewById(R.id.btn_recored_voice);
        mRecoredAudio.setImageResource(R.drawable.ic_recored_voice);

        mConvWriteMessage.setText(message);

        //mRelativeLayout.setBackgroundResource(R.drawable.cover_image);

        messagesAdapter = new MessagesAdapter(this, messagesList);


        mMessagesRecycler = findViewById(R.id.recyclerMessages);
        mSwipeRefresh = findViewById(R.id.messageSwipe);
        mLinearLayout = new LinearLayoutManager(this);

        mMessagesRecycler.setLayoutManager(mLinearLayout);
        mMessagesRecycler.setHasFixedSize(true);
        mMessagesRecycler.setAdapter(messagesAdapter);



        loadMessages();

        mUsername.setText(username);

        mRootRef.child("users").child(mConvUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                    String online = dataSnapshot.child("online").getValue().toString();
                    String image = dataSnapshot.child("image").getValue().toString();

//                        if (dataSnapshot.hasChild("false")) {
//                            mRootRef.child("users").child(mConvUser).setValue("true");
//                        }

                        if (online.equals("true")) {
                            mLastSeen.setText("online");
                        } else {
                           // if (online.equals("false") || online.equals("true")){
                                //mRootRef.child("users").child(mConvUser).child("online").setValue(ServerValue.TIMESTAMP);
                           // }
                            long lastTime = Long.parseLong(online);
                            String lastSeenTime = GetTimeAgo.GetTimeAgo(getApplicationContext(), lastTime);

                            mLastSeen.setText(lastSeenTime);
                        }


                Picasso.with(getApplicationContext())
                        .load(image)
                        .placeholder(R.drawable.default_avatar2)
                        .into(mProfileUserImage);

                mProfileUserImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent profileIntent = new Intent(ChatRoom.this,ProfileUser.class);
                        profileIntent.putExtra("user_uid", mConvUser);
                        startActivity(profileIntent);
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

            mRootRef.child("conversation").child(mcurrentUserUID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (!dataSnapshot.hasChild(mConvUser)){

                        Map convAddMap = new HashMap();
                        convAddMap.put("seen", false);
                        convAddMap.put("timestamp", ServerValue.TIMESTAMP);

                        Map convUserMap = new HashMap();
                        convUserMap.put("conversation/" + mcurrentUserUID + "/" + mConvUser, convAddMap);
                        convUserMap.put("conversation/" +  mConvUser + "/" + mcurrentUserUID , convAddMap);

                        mRootRef.updateChildren(convUserMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError != null){
                                    Log.e("Chat_Log", databaseError.getMessage().toString());
//                                    Toast.makeText(ChatRoom.this, databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        mConvSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendMessage();

            }
        });

        mAddImagetoConv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent, "Select Photo"), GALLERY_PICK);
            }
        });


        mRecoredAudio.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    //requestPermissionAccessToMicrophone();
                    startRecord();
                   // mConvWriteMessage.setText("Start Record");
//                    mRecoredAudio.setText("Start Record");
                    mRecoredAudio.setImageResource(R.drawable.ic_recored_voice_clicked);
                }else if (event.getAction() == MotionEvent.ACTION_UP){
                    stopRecord();
                   // mConvWriteMessage.setText("Stop Record");
//                    mRecoredAudio.setText("Stop Record");
                    mRecoredAudio.setImageResource(R.drawable.ic_recored_voice);
                }

                return false;
            }
        });

        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                mCurrentPage++;
                itemPosition = 0;
                loadMoreMessages();
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK & resultCode == RESULT_OK){

            final Uri imageUri = data.getData();

            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_show_image_message);

            ImageView showImageMessage = dialog.findViewById(R.id.show_image_message);
            FloatingActionButton btnSendImageMessage = dialog.findViewById(R.id.btn_send_image_message);

            Picasso.with(this).load(imageUri).into(showImageMessage);

            btnSendImageMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {



            final String currentUserRef = "messages/" + mcurrentUserUID + "/" + mConvUser;
            final String convUserRef = "messages/" + mConvUser + "/" + mcurrentUserUID;

            DatabaseReference mUserMessageRef = mRootRef.child("messages")
                    .child(mcurrentUserUID).child(mConvUser).push();
            final String pushID = mUserMessageRef.getKey();

            Date curDate = new Date();
            SimpleDateFormat format = new SimpleDateFormat("hh:mm a");
            final String currentTime = format.format(curDate);


            StorageReference mFilePathRef = mImageStorageRef.child("messages").child("message_images").child(pushID + ".jpg");
            mFilePathRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if (task.isSuccessful()){

                        String downloadUrl = task.getResult().getDownloadUrl().toString();

                        Map messageMap = new HashMap();
                        messageMap.put("message", downloadUrl);
                        messageMap.put("seen", false);
                        messageMap.put("type", "image");
                        messageMap.put("time", ServerValue.TIMESTAMP);
                        messageMap.put("currentTime", currentTime);
                        messageMap.put("from", mcurrentUserUID);
                        messageMap.put("to", mConvUser);

                        Map userMessageMap = new HashMap();
                        userMessageMap.put(currentUserRef + "/" + pushID, messageMap);
                        userMessageMap.put(convUserRef + "/" + pushID, messageMap);

                        mConvWriteMessage.setText("");

                        mRootRef.updateChildren(userMessageMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError != null){
                                    Log.e("Chat_Log", databaseError.getMessage());
//                        Toast.makeText(ChatRoom.this, databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                }
            });

            loadMessages();
            dialog.dismiss();
                }
            });

            dialog.show();
            dialog.setCancelable(false);
        }
    }

    private void loadMoreMessages() {

        DatabaseReference messageRef = mRootRef.child("messages").child(mcurrentUserUID).child(mConvUser);
        Query messageQuery = messageRef.orderByKey().endAt(mLastKey).limitToLast(10);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {



                Messages messages = dataSnapshot.getValue(Messages.class);

                String messageKey = dataSnapshot.getKey();

                if (!mPrevKey.equals(messageKey)) {
                    messagesList.add(itemPosition++, messages);
                }else {
                    mPrevKey = mLastKey;
                }

                if (itemPosition == 1){

                    mLastKey = messageKey;
                }

                Log.e("TOTALKEYS", "Last Key : " + mLastKey + "Priv Key : " + mPrevKey + "Message Key : " + messageKey);

                messagesAdapter.notifyDataSetChanged();

                mSwipeRefresh.setRefreshing(false);

                mLinearLayout.scrollToPositionWithOffset(10,0);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void loadMessages() {

        DatabaseReference messageRef = mRootRef.child("messages").child(mcurrentUserUID).child(mConvUser);
        Query messageQuery = messageRef.limitToLast(mCurrentPage * TOTAL_ITEMS_TO_LOAD);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                itemPosition++;

                if (itemPosition == 1){
                    String messageKey = dataSnapshot.getKey();
                    mLastKey = messageKey;
                    mPrevKey = messageKey;
                }

                Messages messages = dataSnapshot.getValue(Messages.class);
                messagesList.add(messages);
                messagesAdapter.notifyDataSetChanged();


                mMessagesRecycler.scrollToPosition(messagesList.size() - 1);
                mSwipeRefresh.setRefreshing(false);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage() {

        String mMessage = mConvWriteMessage.getText().toString();

        Date curDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat("hh:mm a");
        String currentTime = format.format(curDate);

        if (!TextUtils.isEmpty(mMessage)){
            String currentUserRef = "messages/" + mcurrentUserUID + "/" + mConvUser;
            String convUserRef = "messages/" + mConvUser + "/" + mcurrentUserUID;

            DatabaseReference mUserMessageRef = mRootRef.child("messages")
                    .child(mcurrentUserUID).child(mConvUser).push();
            String pushID = mUserMessageRef.getKey();

            Map messageMap = new HashMap();
            messageMap.put("message", mMessage);
            messageMap.put("seen", false);
            messageMap.put("type", "text");
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("currentTime", currentTime);
            messageMap.put("from", mcurrentUserUID);
            messageMap.put("to",mConvUser);

            Map userMessageMap = new HashMap();
            userMessageMap.put(currentUserRef + "/" + pushID, messageMap);
            userMessageMap.put(convUserRef + "/" + pushID, messageMap);

            mConvWriteMessage.setText("");

            mRootRef.updateChildren(userMessageMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null){
                        Log.e("Chat_Log", databaseError.getMessage().toString());
//                        Toast.makeText(ChatRoom.this, databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }





    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_conversation, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.settings){
            startActivity(new Intent(ChatRoom.this, Settings.class));
        }else if (id == R.id.background){
            wallpaper();
        }

        return super.onOptionsItemSelected(item);
    }

    private void wallpaper(){

        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.raw_option_change_image);
        Toast.makeText(this, R.string.background, Toast.LENGTH_SHORT).show();
        dialog.show();

    }


    private void startRecord(){
        mAudioRecord.setAudioSource(MediaRecorder.AudioSource.MIC);
        mAudioRecord.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        mAudioRecord.setOutputFile(mFileName);
        mAudioRecord.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mAudioRecord.prepare();
            mAudioRecord.start();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(LOG_TAG, e.getMessage());
            Log.d(LOG_TAG, "Eroor Prepare");
        }

    }

    private void stopRecord(){
        // stopRequestPermission();
        mAudioRecord.stop();
        mAudioRecord.release();
        mAudioRecord = null;

        uploadAudio();
    }

    private void requestPermissionAccessToRecordAudio(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{
                    Manifest.permission.RECORD_AUDIO
            },REQUEST_PERMISSION_RECORD_AUDIO);

            return;
        }
    }

    private void uploadAudio() {

        final String currentUserRef = "messages/" + mcurrentUserUID + "/" + mConvUser;
        final String convUserRef = "messages/" + mConvUser + "/" + mcurrentUserUID;

        DatabaseReference mUserMessageRef = mRootRef.child("messages")
                .child(mcurrentUserUID).child(mConvUser).push();
        final String pushID = mUserMessageRef.getKey();

        Date curDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat("hh:mm a");
        final String currentTime = format.format(curDate);

        StorageReference filePath = mAudioStorageRef.child("messages").child("message_audio").child(pushID + ".3gp");
        Uri uriAudio = Uri.fromFile(new File(mFileName));
        filePath.putFile(uriAudio).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    String audioUrl = task.getResult().getDownloadUrl().toString();

                    Map messageMap = new HashMap();
                    messageMap.put("message", audioUrl);
                    messageMap.put("seen", false);
                    messageMap.put("type", "audio");
                    messageMap.put("time", ServerValue.TIMESTAMP);
                    messageMap.put("currentTime", currentTime);
                    messageMap.put("from", mcurrentUserUID);
                    messageMap.put("to", mConvUser);

                    Map userMessageMap = new HashMap();
                    userMessageMap.put(currentUserRef + "/" + pushID, messageMap);
                    userMessageMap.put(convUserRef + "/" + pushID, messageMap);

                    mRootRef.updateChildren(userMessageMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null){
                                Log.e("Chat_Log", databaseError.getMessage());
//                        Toast.makeText(ChatRoom.this, databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        loadMessages();
    }



}
