package com.ahmet.postphotos.UI.Main.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ahmet.postphotos.Model.Friends;
import com.ahmet.postphotos.Model.Notifications;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {

    private View convertView;
    private RecyclerView mRecyclerNotification;

    private DatabaseReference mReferenceNotification;
    private DatabaseReference mReferenceUsers;

    private FirebaseAuth mAuth;

    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        convertView =  inflater.inflate(R.layout.fragment_notification, container, false);

        mRecyclerNotification = convertView.findViewById(R.id.recycler_notification);
        mRecyclerNotification.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mRecyclerNotification.setHasFixedSize(true);

        mAuth = FirebaseAuth.getInstance();
        String mCurrentUser = mAuth.getCurrentUser().getUid();

        mReferenceNotification = FirebaseDatabase.getInstance().getReference().child("notifications").child(mCurrentUser);
        mReferenceNotification.keepSynced(true);
        mReferenceUsers = FirebaseDatabase.getInstance().getReference().child("users");
        mReferenceUsers.keepSynced(true);



        return convertView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Notifications, NotificationHolder> notificationnotificationHolderFirebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Notifications, NotificationHolder>(
                Notifications.class,
                R.layout.raw_notification,
                NotificationHolder.class,
                mReferenceNotification
        ) {
            @Override
            protected void populateViewHolder(final NotificationHolder notificationHolder, Notifications notifications, int position) {


                String key = getRef(position).getKey();

                mReferenceUsers.child(mAuth.getCurrentUser().getUid())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                String name = dataSnapshot.child("name").getValue().toString();
                                String image = dataSnapshot.child("image").getValue().toString();

                                notificationHolder.setUsername(name);
                                notificationHolder.setUserImage(getContext(), image);

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }

        };

        mRecyclerNotification.setAdapter(notificationnotificationHolderFirebaseRecyclerAdapter);
    }


    public static class NotificationHolder extends RecyclerView.ViewHolder{

        private View convertView;

        public NotificationHolder(View itemView) {
            super(itemView);

            convertView = itemView;
        }

        public void setUsername(String name){
            TextView textName = convertView.findViewById(R.id.name_user_notification);
            textName.setText(name);
        }

        public void setUserImage(Context mContext, String image){
            CircleImageView imageUser = convertView.findViewById(R.id.image_user_notification);
            Picasso.with(mContext)
                    .load(image)
                    .placeholder(R.drawable.default_avatar2)
                    .into(imageUser);
        }
    }
}
