package com.ahmet.postphotos.Adapter;

import android.app.LauncherActivity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ahmet.postphotos.Model.Blog;
import com.ahmet.postphotos.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.BlogHolder> {

    private Context mContext;
    private List<Blog> mListBlog;
    private LayoutInflater layoutInflater;

    public BlogAdapter(Context mContext, List<Blog> mListBlog) {
        this.mContext = mContext;
        this.mListBlog = mListBlog;
        layoutInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public BlogHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View convertView = layoutInflater.inflate(R.layout.raw_blog, parent, false);

        return new BlogHolder(convertView);
    }

    @Override
    public void onBindViewHolder(@NonNull BlogHolder holder, int position) {

        Blog blog = mListBlog.get(position);

        holder.setImage(mContext, blog.getImage());
        holder.setTitle(blog.getTitle());
        holder.setDescription(blog.getDescription());
        // holder.setImage(getActivity(), blog.getImage());
        // blogViewHolder.setUsername(blog.getUsername());
        // blogViewHolder.setUserImage(getActivity(),blog.getImageUser());
        //holder.setTimePost(timePost);
        //holder.setLike(postKey);

    }

    @Override
    public int getItemCount() {
        return mListBlog.size();
    }

    protected static class BlogHolder extends RecyclerView.ViewHolder{

        View convertView;
        ImageView mImagePost;
        ImageButton mLike;
        ImageButton mComments;
        RelativeLayout mRelativeUser;

        DatabaseReference mDatabaseLike;
        FirebaseAuth mAuth;

        public BlogHolder(View itemView) {
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
