package com.ahmet.postphotos.UI.Main.Fragments.Post.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ahmet.postphotos.Adapter.BlogAdapter;
import com.ahmet.postphotos.Model.Blog;
import com.ahmet.postphotos.R;
import com.ahmet.postphotos.UI.Account.Profile.Profile;
import com.ahmet.postphotos.UI.Main.Fragments.Post.ShowPostActivity;
import com.ahmet.postphotos.UI.Main.Home.MainActivity;
import com.ahmet.postphotos.UI.UserData.User.ProfileUser;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class HomeFragment extends Fragment {

    private RecyclerView mBlogRecycler;

    private List<Blog> mListBlog;
    private BlogAdapter blogAdapter;

    private DatabaseReference mRefDatabase;
    private DatabaseReference mDatabaseRefUsers;
    private DatabaseReference mDatabaseLikes;
    private Query mQueryPost;

//    private DatabaseReference mDatabaseCurrentUser;
//    private Query mQueryCurrentUser;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private boolean mProcessLike = false;

    private long countLikes = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        mRefDatabase = FirebaseDatabase.getInstance().getReference().child("blog");
        mRefDatabase.keepSynced(true);
        mDatabaseRefUsers = FirebaseDatabase.getInstance().getReference().child("users");
        mDatabaseRefUsers.keepSynced(true);
        mDatabaseLikes = FirebaseDatabase.getInstance().getReference().child("likes");
        mDatabaseLikes.keepSynced(true);
        mQueryPost = mRefDatabase.orderByChild("time_published");
        mQueryPost.keepSynced(true);

        mListBlog = new ArrayList<>();
        blogAdapter = new BlogAdapter(getActivity(), mListBlog);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View convertView = inflater.inflate(R.layout.fragment_home, container, false);

        mBlogRecycler = convertView.findViewById(R.id.recycler_blog);

        return convertView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mBlogRecycler.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        mBlogRecycler.setHasFixedSize(true);

//        mQueryPost.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                Blog blog = dataSnapshot.getValue(Blog.class);
//                mListBlog.add(blog);
//                blogAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//

        // checkUser();
    }

    @Override
    public void onStart() {
        super.onStart();

        //
        //  mAuth.addAuthStateListener(mAuthStateListener);

        final FirebaseRecyclerAdapter<Blog, BlogViewHolder> blogBlogViewHolderFirebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(
                Blog.class,
                R.layout.raw_blog,
                BlogViewHolder.class,
                mQueryPost
        ) {
            @Override
            protected void populateViewHolder(final BlogViewHolder blogViewHolder, final Blog blog, final int position) {

                final String postKey = getRef(position).getKey();
                final FirebaseUser firebaseUser = mAuth.getCurrentUser();
//                final String mCurrentUser = firebaseUser.getUid();


                mRefDatabase.child(postKey).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        blogViewHolder.getAdapterPosition();



                        final String imageUser = dataSnapshot.child("image_user").getValue().toString();
                        String timePost = dataSnapshot.child("time_post").getValue().toString();
                        final String userUid = dataSnapshot.child("uid").getValue().toString();

                        mDatabaseRefUsers.child(userUid).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                String imageProfile = dataSnapshot.child("image").getValue().toString();
                                String username = dataSnapshot.child("name").getValue().toString();
//                                String lastName = dataSnapshot.child("last_Name").getValue().toString();

                                blogViewHolder.setUserImage(getActivity(),imageProfile);
                                blogViewHolder.setUsername(username);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        blogViewHolder.setTitle(blog.getTitle());
                        blogViewHolder.setDescription(blog.getDescription());
                        blogViewHolder.setImage(getActivity(), blog.getImage());
                        // blogViewHolder.setUsername(blog.getUsername());
                        // blogViewHolder.setUserImage(getActivity(),blog.getImageUser());
                        blogViewHolder.setTimePost(timePost);
                        blogViewHolder.setLike(postKey);



                        blogViewHolder.mRelativeUser.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (!blog.getUid().equals(mAuth.getCurrentUser().getUid())){
                                    Intent intent = new Intent(getActivity(), ProfileUser.class);
                                    intent.putExtra("user_uid", userUid);
                                    Log.d("UID", userUid);
                                    startActivity(intent);
                                    getActivity().finish();
                                }else {
                                    startActivity(new Intent(getActivity(), Profile.class));
                                    getActivity().finish();
                                }
                            }
                        });

                        blogViewHolder.mImagePost.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent postSingleIntent = new Intent(getActivity(),ShowPostActivity.class);
                                postSingleIntent.putExtra("postId", postKey);
                                startActivity(postSingleIntent);
                            }
                        });

                        blogViewHolder.mComments.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //Toast.makeText(MainActivity.this, (position + 1)+"", Toast.LENGTH_SHORT).show();

                                String username = blog.getUsername();

//                                Intent postSingleIntent = new Intent(getActivity(),CommentsActivity.class);
//                                postSingleIntent.putExtra("postId", postKey);
//                                postSingleIntent.putExtra("userUid", userUid);
//                                postSingleIntent.putExtra("username", username);
//                                startActivity(postSingleIntent);
                            }
                        });

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });







                blogViewHolder.mLike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mProcessLike = true;


                        mDatabaseLikes.child(postKey).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {


                                countLikes = dataSnapshot.getChildrenCount();
                                blogViewHolder.setlikeCount(String.valueOf(countLikes));


                                if (mProcessLike) {

                                    if (dataSnapshot.child(postKey).hasChild(mAuth.getCurrentUser().getUid())) {
                                        mDatabaseLikes.child(postKey).child(mAuth.getCurrentUser().getUid()).removeValue();
                                        mProcessLike = false;
                                    } else {
                                        mDatabaseLikes.child(postKey).child(mAuth.getCurrentUser().getUid()).setValue("RandomValues");
                                        mProcessLike = false;
                                    }
                                }
                            }


                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });



                    }
                });

            }
        };

        mBlogRecycler.setAdapter(blogBlogViewHolderFirebaseRecyclerAdapter);


    }

    private void checkUser() {

        if (mAuth.getCurrentUser() != null) {

            final String userUID = mAuth.getCurrentUser().getUid();

            mDatabaseRefUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.hasChild(userUID)) {
                        Intent setupIntent = new Intent(getActivity(), MainActivity.class);
                        setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(setupIntent);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

    protected static class BlogViewHolder extends RecyclerView.ViewHolder{

        View convertView;
        ImageView mImagePost;
        ImageButton mLike;
        ImageButton mComments;
        RelativeLayout mRelativeUser;

        DatabaseReference mDatabaseLike;
        FirebaseAuth mAuth;

        public BlogViewHolder(View itemView) {
            super(itemView);

            convertView = itemView;
            mImagePost = convertView.findViewById(R.id.post_image);
            mLike = convertView.findViewById(R.id.btn_post_like);
            mComments = convertView.findViewById(R.id.btn_post_comment);
            mRelativeUser = convertView.findViewById(R.id.relative_user);

            mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("likes");
            mDatabaseLike.keepSynced(true);

            mAuth = FirebaseAuth.getInstance();
        }

        public void setLike(final String postKey){

            mDatabaseLike.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child(postKey).hasChild(mAuth.getCurrentUser().getUid())){
                        mLike.setImageResource(R.drawable.ic_like_clicked);
                    }else {
                        mLike.setImageResource(R.drawable.ic_action_like);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        public void setTitle(String title){
            TextView mTitle = convertView.findViewById(R.id.post_title);
            mTitle.setText(title);
        }

        public void setDescription(String description){
            TextView mDescription = convertView.findViewById(R.id.post_description);
            mDescription.setText(description);
        }

        public void setTimePost(String timePost){
            TextView mTimePost = convertView.findViewById(R.id.post_time);
            mTimePost.setText(timePost);
        }
        public void setUsername(String username){
            TextView mUsername = convertView.findViewById(R.id.user_name);
            mUsername.setText(username);
        }
        public void setUserImage(Context context, String userimage){
            CircleImageView mUserImage = convertView.findViewById(R.id.user_image);
            Picasso.with(context)
                    .load(userimage)
                    .placeholder(R.drawable.default_avatar2)
                    .into(mUserImage);
        }

        public void setImage(Context context, String image){
            //ImageView mImagePost = convetView.findViewById(R.id.post_image);
            Picasso.with(context)
                    .load(image)
                    .placeholder(R.drawable.default_post)
                    .into(mImagePost);
        }

        public void setlikeCount(String likeCount){
            TextView mLikeCount = convertView.findViewById(R.id.text_like_count);
            mLikeCount.setText(likeCount);
        }


    }
}
