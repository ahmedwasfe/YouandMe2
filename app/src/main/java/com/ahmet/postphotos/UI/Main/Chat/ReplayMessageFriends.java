package com.ahmet.postphotos.UI.Main.Chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.ahmet.postphotos.Model.Friends;
import com.ahmet.postphotos.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReplayMessageFriends extends AppCompatActivity {


    private Toolbar mToolbar;
    private RecyclerView recyclerReplayMessage;

    private FirebaseAuth mAuth;
    private DatabaseReference mRefFriends;
    private DatabaseReference mRefUsers;

    private String mCurrentUserUID;
    private String messageReplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.replay_message_friends);

        mToolbar = findViewById(R.id.app_ReplayMessageFriends_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.forward_message);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerReplayMessage = findViewById(R.id.recyclerReplayMessage);
        recyclerReplayMessage.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

        messageReplay = getIntent().getStringExtra("messageReplay");

        mAuth = FirebaseAuth.getInstance();
        mCurrentUserUID = mAuth.getCurrentUser().getUid();
        mRefFriends = FirebaseDatabase.getInstance().getReference().child("friends").child(mCurrentUserUID);
        mRefUsers = FirebaseDatabase.getInstance().getReference().child("users");


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Friends, FriendsViewHolder> friendsViewHolderFirebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(
                Friends.class,
                R.layout.raw_replay_message_friends,
                FriendsViewHolder.class,
                mRefFriends
        ) {
            @Override
            protected void populateViewHolder(final FriendsViewHolder friendsViewHolder, final Friends friends, int position) {

                final String userUID = getRef(position).getKey();

                mRefUsers.child(userUID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String name = dataSnapshot.child("name").getValue().toString();
                        String image = dataSnapshot.child("image").getValue().toString();

                        friendsViewHolder.setUserName(name);
                        friendsViewHolder.setUserImage(ReplayMessageFriends.this, image);

                        friendsViewHolder.convertView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Snackbar snackbar = Snackbar.make(view, name, Snackbar.LENGTH_INDEFINITE)
                                        .setAction(R.string.send, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Intent intentReplayMessage = new Intent(ReplayMessageFriends.this,ChatRoom.class);
                                                intentReplayMessage.putExtra("messageReplay", messageReplay);
                                                intentReplayMessage.putExtra("user_uid", userUID);
                                                intentReplayMessage.putExtra("username", name);
                                                startActivity(intentReplayMessage);
                                                finish();
                                            }
                                        });

                                snackbar.show();
                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };

        recyclerReplayMessage.setAdapter(friendsViewHolderFirebaseRecyclerAdapter);
    }



    public static class FriendsViewHolder extends RecyclerView.ViewHolder{

        View convertView;
        public FriendsViewHolder(View itemView) {
            super(itemView);

            convertView = itemView;
        }

        public void setUserStaus(String date) {
            TextView textStatusMyFriend = convertView.findViewById(R.id.text_ReplayMessage_FreiendStatus);
            textStatusMyFriend.setText(date);
        }

        public void setUserName(String name) {
            TextView textNameMyFriend = convertView.findViewById(R.id.text_ReplayMessage_FreiendName);
            textNameMyFriend.setText(name);
        }
        public void setUserImage(Context context, String image){
            CircleImageView imgMyFriend = convertView.findViewById(R.id.image_ReplayMessage_Freiend);
            Picasso.with(context).load(image).placeholder(R.drawable.default_avatar2).into(imgMyFriend);
        }
    }
}
