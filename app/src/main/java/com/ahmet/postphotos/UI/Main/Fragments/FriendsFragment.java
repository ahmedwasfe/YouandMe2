package com.ahmet.postphotos.UI.Main.Fragments;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ahmet.postphotos.Model.Friends;
import com.ahmet.postphotos.R;
import com.ahmet.postphotos.UI.Main.Chat.ChatRoom;
import com.ahmet.postphotos.UI.UserData.User.ProfileUserMyFriends;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {


    private RecyclerView recyclerFragmentFriends;

    private FirebaseAuth mAuth;
    private DatabaseReference mRefFriends;
    private DatabaseReference mRefUsers;
    private String userCurrentUID;

    private View convertView;

    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        convertView =  inflater.inflate(R.layout.fragment_friends, container, false);

        recyclerFragmentFriends = convertView.findViewById(R.id.recyclerFragmentFriends);
        recyclerFragmentFriends.setLayoutManager(new StaggeredGridLayoutManager(1,LinearLayout.VERTICAL));
        recyclerFragmentFriends.setHasFixedSize(true);

        mAuth = FirebaseAuth.getInstance();
        userCurrentUID = mAuth.getCurrentUser().getUid();

        mRefFriends = FirebaseDatabase.getInstance().getReference().child("friends").child(userCurrentUID);
        mRefFriends.keepSynced(true);

        mRefUsers = FirebaseDatabase.getInstance().getReference().child("users");
        mRefUsers.keepSynced(true);


        return convertView;
    }


    @Override
    public void onStart() {
        super.onStart();


        FirebaseRecyclerAdapter<Friends, FriendsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(

                Friends.class,
                R.layout.raw_myfriends,
                FriendsViewHolder.class,
                mRefFriends

        ) {
            @Override
            protected void populateViewHolder(final FriendsViewHolder friendsViewHolder, Friends friends, int position) {

                //friendsViewHolder.setUserDate(friends.getDate());

                final String userUID = getRef(position).getKey();

                mRefUsers.child(userUID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {

                        final String username = dataSnapshot.child("name").getValue().toString();
                        final String userImage = dataSnapshot.child("image").getValue().toString();
                        final String userStatus = dataSnapshot.child("status").getValue().toString();
                        final String coverImage = dataSnapshot.child("cover_image").getValue().toString();
//                        String userOnline = dataSnapshot.child("online").getValue().toString();

                        friendsViewHolder.setUserName(username);
                        friendsViewHolder.setUserImage(getContext(), userImage);
                        friendsViewHolder.setUserDate(userStatus);

                        if (dataSnapshot.hasChild("online")){
                            String userOnline = dataSnapshot.child("online").getValue().toString();
                            friendsViewHolder.setUserOnline(userOnline);
                        }

                        friendsViewHolder.convertView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                final Dialog dialog = new Dialog(getContext());
                                //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                dialog.setContentView(R.layout.raw_dialog_user);

                                CircleImageView mImageUser = dialog.findViewById(R.id.imageUserDialog);
                                ImageView mCoverImage = dialog.findViewById(R.id.profile_user_cover_image_dialog);
                                LinearLayout mSendMessage = dialog.findViewById(R.id.userDialogMessage);
                                LinearLayout mOpenProfile = dialog.findViewById(R.id.userDialogProfile);
                                TextView mUsername = dialog.findViewById(R.id.userName);
                                TextView mStatus = dialog.findViewById(R.id.userStatus);

                                Picasso.with(getContext())
                                        .load(userImage)
                                        .placeholder(R.drawable.default_avatar2)
                                        .into(mImageUser);
										
                                mUsername.setText(username);
                                mStatus.setText(userStatus);
								
                                Picasso.with(getContext())
                                        .load(coverImage)
                                        .placeholder(R.drawable.cover_image)
                                        .into(mCoverImage);

                                mSendMessage.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent converstionIntent = new Intent(getContext(),ChatRoom.class);
                                        converstionIntent.putExtra("user_uid", userUID);
                                        converstionIntent.putExtra("username", username);
                                        startActivity(converstionIntent);
                                        dialog.dismiss();
                                    }
                                });

                                mOpenProfile.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        Intent profileIntent = new Intent(getContext(),ProfileUserMyFriends.class);
                                        profileIntent.putExtra("user_uid", userUID);
                                        startActivity(profileIntent);
                                        dialog.dismiss();
                                    }
                                });

                                dialog.show();

                            }
                        });
//                        friendsViewHolder.convertView.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//
//                                CharSequence options[] = new CharSequence[]{"Open Profile", "Send Message"};
//
//                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//                                builder.setTitle("Select Options");
//                                builder.setItems(options, new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialogInterface, int position) {
//
//                                        // Clicke Event for each item
//
//                                        if (position == 0){
//
//                                            Intent profileIntent = new Intent(getContext(),ProfileUser.class);
//                                            profileIntent.putExtra("user_uid", userUID);
//                                            startActivity(profileIntent);
//
//                                        }
//
//                                        if (position == 1){
//
//                                            Intent converstionIntent = new Intent(getContext(),ChatRoom.class);
//                                            converstionIntent.putExtra("user_uid", userUID);
//                                            converstionIntent.putExtra("username", username);
//                                            startActivity(converstionIntent);
//
//                                        }
//
//                                    }
//                                });
//
//                                builder.show();
//                            }
//                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };

        recyclerFragmentFriends.setAdapter(firebaseRecyclerAdapter);
    }



    public static class FriendsViewHolder extends RecyclerView.ViewHolder{

        View convertView;
        public FriendsViewHolder(View itemView) {
            super(itemView);

            convertView = itemView;
        }

        public void setUserDate(String date) {
            TextView textStatusMyFriend = convertView.findViewById(R.id.textMyFreiendStatus);
            textStatusMyFriend.setText(date);
        }

        public void setUserName(String name) {
            TextView textNameMyFriend = convertView.findViewById(R.id.textMyFreiendName);
            textNameMyFriend.setText(name);
        }
        public void setUserImage(Context context, String image){
            CircleImageView imgMyFriend = convertView.findViewById(R.id.imageMyFreiend);
            Picasso.with(context).load(image).placeholder(R.drawable.default_avatar2).into(imgMyFriend);
        }

        public void setUserOnline(String onlineStatus){
            ImageView imgOnline = convertView.findViewById(R.id.userOnline_icon);

            if (onlineStatus.equals("true")){
                imgOnline.setVisibility(View.VISIBLE);
            }else {
                imgOnline.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
