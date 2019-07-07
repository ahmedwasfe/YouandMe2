package com.ahmet.postphotos.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmet.postphotos.Model.Users;
import com.ahmet.postphotos.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ahmet on 3/6/2018.
 */

public class AdapterAddFriends extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<Users> usersArrayList;
    private LayoutInflater layoutInflater;

    public AdapterAddFriends(Context context, ArrayList<Users> usersArrayList) {
        this.context = context;
        this.usersArrayList = usersArrayList;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View convertView = layoutInflater.inflate(R.layout.raw_show_users,parent,false);

        return new AddFriendsHolder(convertView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        Users user = usersArrayList.get(position);
        final AddFriendsHolder friendsHolder = (AddFriendsHolder) holder;

        friendsHolder.userName.setText(user.getName());
        Picasso.with(context)
                .load(user.getImage())
                .placeholder(R.drawable.default_avatar2)
                .into(friendsHolder.userImage);

        friendsHolder.imgAddFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatabaseReference  mRootRef = FirebaseDatabase.getInstance().getReference();
                DatabaseReference mNotificatonDatabase = FirebaseDatabase.getInstance().getReference().child("notifications");

                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                final FirebaseUser firebaseUser = mAuth.getCurrentUser();
                String UID = firebaseUser.getUid();

                final String userUID = mRootRef.getRef().getKey();

                if (friendsHolder.imgAddFriends.isPressed()) {
                    DatabaseReference mNotificationRef = mRootRef.child("notifications").child(userUID).push();
                    String newNotificationId = mNotificationRef.getKey();

                    final HashMap<String, String> notificationData = new HashMap<>();
                    notificationData.put("from", firebaseUser.getUid());
                    notificationData.put("type", "request");

                    Map requestMap = new HashMap();
                    requestMap.put("friend_req/" + firebaseUser.getUid() + "/" + userUID + "/request_type", "sent");
                    requestMap.put("friend_req/" + userUID + "/" + firebaseUser.getUid() + "/request_type", "received");
                    requestMap.put("notifications/" + userUID + "/" + newNotificationId, notificationData);

                    mRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                Toast.makeText(context, "There was some error in sending request", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
//
                    friendsHolder.imgAddFriends.setImageResource(R.drawable.ic_cliked_added_24dp);
                    friendsHolder.imgAddFriends.setPressed(true);

                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class AddFriendsHolder extends RecyclerView.ViewHolder{

        CircleImageView userImage;
        TextView userName;
        public ImageView imgAddFriends;

        public AddFriendsHolder(View itemView) {
            super(itemView);

            userImage = itemView.findViewById(R.id.imageUserShow);
            userName = itemView.findViewById(R.id.textNameShow);
            imgAddFriends = itemView.findViewById(R.id.btnAddFriends);
        }
    }
}
