package com.ahmet.postphotos.UI.Main.Fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmet.postphotos.Model.Friends;
import com.ahmet.postphotos.R;
import com.ahmet.postphotos.UI.UserData.User.ProfileUser;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment {

    private RecyclerView recyclerFragmentRequest;

    private FirebaseAuth mAuth;
    private DatabaseReference mRefRequest;
    private DatabaseReference mReferenceRequest;
    private DatabaseReference mRefAcceptFriends;
    private DatabaseReference mRefUsers;

    private String userCurrentUID;
    private String currentRequestState;

    private View convertView;



    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        convertView =  inflater.inflate(R.layout.fragment_requests, container, false);

        recyclerFragmentRequest = convertView.findViewById(R.id.recyclerFragmentRequest);
        recyclerFragmentRequest.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        recyclerFragmentRequest.setHasFixedSize(true);

        currentRequestState = "not_friends";

        mAuth = FirebaseAuth.getInstance();
        userCurrentUID = mAuth.getCurrentUser().getUid();

        mRefRequest = FirebaseDatabase.getInstance().getReference().child("friend_req").child(userCurrentUID);
        mRefRequest.keepSynced(true);
        mRefUsers = FirebaseDatabase.getInstance().getReference().child("users");
        mRefUsers.keepSynced(true);
        mReferenceRequest = FirebaseDatabase.getInstance().getReference();
        mRefAcceptFriends = FirebaseDatabase.getInstance().getReference().child("friends");


        return convertView;
    }

    @Override
    public void onStart() {
        super.onStart();


        final FirebaseRecyclerAdapter<Friends, RequestHolder> requestHolderFirebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Friends, RequestHolder>(

                Friends.class,
                R.layout.raw_myrequest,
                RequestHolder.class,
                mRefRequest
        ) {
            @Override
            protected void populateViewHolder(final RequestHolder requestHolder, Friends friends, int position) {

                final String userUID = getRef(position).getKey();

                mRefUsers.child(userUID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String username = dataSnapshot.child("name").getValue().toString();
                        String userImage = dataSnapshot.child("image").getValue().toString();
//                        String userOnline = dataSnapshot.child("online").getValue().toString();

                        requestHolder.setUserName(username);
                        requestHolder.setUserImage(getContext(), userImage);


                        requestHolder.convertView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Intent profileIntent = new Intent(getContext(),ProfileUser.class);
                                profileIntent.putExtra("user_uid", userUID);
                                startActivity(profileIntent);

                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                mRefRequest.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild(userUID)){
                            String requestType = dataSnapshot.child(userUID).child("request_type").getValue().toString();

                            if (requestType.equals("received")){
                                currentRequestState = "request_received";
                            }else if (requestType.equals("sent")){
                                currentRequestState = "request_sent";
                                requestHolder.mAcceptRequest.setVisibility(View.INVISIBLE);
                                requestHolder.mDeclineRequest.setText(R.string.cancel_request);
                            }
                        }else{
                            mRefAcceptFriends.child(userCurrentUID)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.hasChild(userUID)){
                                                currentRequestState = "friends";
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                requestHolder.mAcceptRequest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Toast.makeText(getContext(), R.string.accept, Toast.LENGTH_SHORT).show();
                        //  if (currentRequestState.equals("request_received")) {

                        final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                        Map friendsMap = new HashMap();
                        friendsMap.put("friends/" + userCurrentUID + "/" + userUID + "/date", currentDate);
                        friendsMap.put("friends/" + userUID + "/" + userCurrentUID + "/date", currentDate);

                        friendsMap.put("friend_req/" + userCurrentUID + "/" + userUID + "/request_type", null);
                        friendsMap.put("friend_req/" + userUID + "/" + userCurrentUID + "/request_type", null);

                        mReferenceRequest.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                                if(databaseError == null){

                                    currentRequestState = "friends";
                                    Toast.makeText(getContext(), R.string.accept, Toast.LENGTH_SHORT).show();
                                } else {

                                    String error = databaseError.getMessage();

                                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        //  }

                    }
                });
//
                requestHolder.mDeclineRequest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // - -------------- CANCEL REQUEST STATE ------------ إلغاء طلب لبصداقة   Cancel Request Friendly

                        if (currentRequestState.equals("request_sent")){

                            final DatabaseReference mFriendsReqDatabase = FirebaseDatabase.getInstance().getReference().child("friend_req");

                            mFriendsReqDatabase.child(userCurrentUID).child(userUID)
                                    .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    mFriendsReqDatabase.child(userUID).child(userCurrentUID)
                                            .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getActivity(), "" + R.string.delete, Toast.LENGTH_SHORT).show();

                                        }
                                    });

                                }
                            });

                        }

                        notifyDataSetChanged();
                        // recyclerFragmentRequest.setAdapter(requestHolderFirebaseRecyclerAdapter);

                    }
                });



                if (currentRequestState.equals("request_sent")){
                    requestHolder.mAcceptRequest.setVisibility(View.INVISIBLE);
                    requestHolder.mDeclineRequest.setText("Cancel");
                }

            }


        };

        recyclerFragmentRequest.setAdapter(requestHolderFirebaseRecyclerAdapter);

    }
    public static class RequestHolder extends RecyclerView.ViewHolder{

        View convertView;

        Button mAcceptRequest, mDeclineRequest;

        public RequestHolder(View itemView) {
            super(itemView);

            convertView = itemView;
            mAcceptRequest = itemView.findViewById(R.id.btn_accept);
            mDeclineRequest = itemView.findViewById(R.id.btn_decline);


        }



        public void setUserName(String name) {
            TextView textMyRequestUserName = convertView.findViewById(R.id.textMyRequestUserName);
            textMyRequestUserName.setText(name);
        }
        public void setUserImage(Context context, String image){
            CircleImageView imageMyRequestUser = convertView.findViewById(R.id.imageMyRequestUser);
            Picasso.with(context)
                    .load(image)
                    .placeholder(R.drawable.default_avatar2)
                    .into(imageMyRequestUser);
        }

    }
}
