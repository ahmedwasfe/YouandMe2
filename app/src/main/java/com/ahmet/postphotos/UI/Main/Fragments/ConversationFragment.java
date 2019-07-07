package com.ahmet.postphotos.UI.Main.Fragments;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmet.postphotos.Model.Conversation;
import com.ahmet.postphotos.Model.Messages;
import com.ahmet.postphotos.R;
import com.ahmet.postphotos.UI.Main.Chat.ChatRoom;
import com.ahmet.postphotos.UI.UserData.Data.FriendsActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConversationFragment extends Fragment {

    private RecyclerView recyclerFragmentConv;

    private FirebaseAuth mAuth;
    private DatabaseReference mRefConversation;
    private DatabaseReference mMessageDatabase;
    private DatabaseReference mDatabaseRefSeen;
    private DatabaseReference mRefUsers;

    private String userCurrentUID;

    private View convertView;



    public ConversationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        convertView = inflater.inflate(R.layout.fragment_conversation, container, false);

        recyclerFragmentConv = convertView.findViewById(R.id.recyclerFragmentConv);
        recyclerFragmentConv.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        recyclerFragmentConv.setHasFixedSize(true);

        mAuth = FirebaseAuth.getInstance();
        userCurrentUID = mAuth.getCurrentUser().getUid();

        mRefConversation = FirebaseDatabase.getInstance().getReference().child("conversation").child(userCurrentUID);
        mRefConversation.keepSynced(true);

        mRefUsers = FirebaseDatabase.getInstance().getReference().child("users");
        mRefUsers.keepSynced(true);

        mMessageDatabase = FirebaseDatabase.getInstance().getReference().child("messages").child(userCurrentUID);
        mDatabaseRefSeen = FirebaseDatabase.getInstance().getReference();



        FloatingActionButton fab = (FloatingActionButton) convertView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                startActivity(new Intent(getContext(), FriendsActivity.class));
            }
        });

        return convertView;

    }

    @Override
    public void onStart() {
        super.onStart();


        Query conversationQuery = mRefConversation.orderByChild("timestamp");

        FirebaseRecyclerAdapter<Conversation, ConversationHolder> conversationHolderFirebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Conversation, ConversationHolder>(

                Conversation.class,
                R.layout.raw_myconvrsaton,
                ConversationHolder.class,
                conversationQuery

        ) {
            @Override
            protected void populateViewHolder(final ConversationHolder conversationHolder, final Conversation conversation, final int position) {


                final String userUiD = getRef(position).getKey();
                final List<Messages> mListMessage = new ArrayList<>();

                final Query lastMessageQuery = mMessageDatabase.child(userUiD).limitToLast(1);

                lastMessageQuery.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        String lastMessage = (String) dataSnapshot.child("message").getValue();
                        String typeMessage = (String) dataSnapshot.child("type").getValue();
                        //final boolean seenMessage = (boolean) dataSnapshot.child("seen").getValue();

                        if (typeMessage.equals("text")) {
                            conversationHolder.setLaseMessage(lastMessage, conversation.isSeen());
                        }else if (typeMessage.equals("image")){
                            conversationHolder.setLaseMessage("Photo", conversation.isSeen());
                        }else {
                            conversationHolder.setLaseMessage("audio", conversation.isSeen());
                        }






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

                mRefUsers.child(userUiD).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

//

                        final String username = dataSnapshot.child("name").getValue().toString();
                        final String userImage = dataSnapshot.child("image").getValue().toString();
                        String userOnline = dataSnapshot.child("online").getValue().toString();

                        conversationHolder.setUserName(username);
                        conversationHolder.setUserImage(getContext(), userImage);
                        conversationHolder.setUserOnline(userOnline);



                        conversationHolder.convertView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(final View view) {

                                Intent converstionIntent = new Intent(getContext(), ChatRoom.class);
                                converstionIntent.putExtra("user_uid", userUiD);
                                converstionIntent.putExtra("username", username);
                                startActivity(converstionIntent);

//                                        if (seenMessage == false) {
//                                            mMessageDatabase.child(userUiD)
//                                                    .child(getRef(position).getKey())
//                                                    .child("seen")
//                                                    .setValue(true)
//                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                        @Override
//                                                        public void onSuccess(Void aVoid) {
//                                                            Toast.makeText(getActivity(), "Success " + seenMessage, Toast.LENGTH_SHORT).show();
//                                                        }
//                                                    });
//                                        }

                            }
                        });

                        conversationHolder.convertView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {

                                final Dialog deleteChatDialog = new Dialog(getContext());
                                deleteChatDialog.setContentView(R.layout.raw_dialog_delete_chat);

                                TextView mDelete = deleteChatDialog.findViewById(R.id.text_done_delete_chat);
                                TextView mCancel = deleteChatDialog.findViewById(R.id.text_cancel_delete_chat);

                                mDelete.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        DatabaseReference mReference = mRefConversation.child(userUiD);
                                        mReference.removeValue(new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                if (isRemoving()){
                                                    Toast.makeText(getContext(), "Success to Remove", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                        deleteChatDialog.dismiss();
                                    }
                                });

                                mCancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        deleteChatDialog.dismiss();
                                    }
                                });

                                deleteChatDialog.show();

                                return false;
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };


        recyclerFragmentConv.setAdapter(conversationHolderFirebaseRecyclerAdapter);

    }



    public static class ConversationHolder extends RecyclerView.ViewHolder{

        View convertView;

        public ConversationHolder(View itemView) {
            super(itemView);

            convertView = itemView;
        }

        public void setLaseMessage(String lastMessage, boolean isSeen) {
            TextView textLastMessage = convertView.findViewById(R.id.textLastMessage);
            textLastMessage.setText(lastMessage);

            if (!isSeen == true){
                textLastMessage.setTypeface(textLastMessage.getTypeface(), Typeface.BOLD);
                textLastMessage.setTextSize(15);
            }else {
                textLastMessage.setTypeface(textLastMessage.getTypeface(), Typeface.NORMAL);
            }
        }

        public void setUserName(String name) {
            TextView textNameMyFriend = convertView.findViewById(R.id.textMyConvUserName);
            textNameMyFriend.setText(name);
        }
        public void setUserImage(Context context, String image){
            CircleImageView imgMyFriend = convertView.findViewById(R.id.imageMyConvUser);
            Picasso.with(context).load(image).placeholder(R.drawable.default_avatar2).into(imgMyFriend);
        }

        private void setCountMessageunRead(String countMessageUnRead){
            TextView messageUnRead = convertView.findViewById(R.id.count_message_unread);
            messageUnRead.setText(countMessageUnRead);
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
}
