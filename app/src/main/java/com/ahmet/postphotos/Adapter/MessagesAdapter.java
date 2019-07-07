package com.ahmet.postphotos.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmet.postphotos.Config.MyClipboardManager;
import com.ahmet.postphotos.Model.Messages;
import com.ahmet.postphotos.R;
import com.ahmet.postphotos.UI.Main.Chat.ReplayMessageFriends;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

import static android.view.View.GONE;

/**
 * Created by ahmet on 3/8/2018.
 */

public class MessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Messages> messagesList;
    private LayoutInflater layoutInflater;

   // public static MediaPlayer mMediaPlayer;

    private DatabaseReference mRefUsers;


    private static final int VIEW_MESSAGE_IMAGE_ME = 1;
    private static final int VIEW_MESSAGE_IMAGE_OTHER = 2;

    private int audioStatus;

    public MessagesAdapter(Context context, List<Messages> messagesList) {
        this.context = context;
        this.messagesList = messagesList;
        layoutInflater = LayoutInflater.from(context);
//        mMediaPlayer = new MediaPlayer();
//        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType){

            case VIEW_MESSAGE_IMAGE_ME:
                View viewMessageImageMe = layoutInflater.inflate(R.layout.raw_my_image_message, parent, false);
                viewHolder = new MyMessageImageHolder(viewMessageImageMe, context);
                break;
            case VIEW_MESSAGE_IMAGE_OTHER:
                View viewMessageImageOther = layoutInflater.inflate(R.layout.raw_other_image_message, parent, false);
                viewHolder = new OtherMessageImageHolder(viewMessageImageOther, context);
                break;
        }

        return viewHolder;
    }
//

    private void mymessageImage(final MyMessageImageHolder myMessageImageHolder, int position){

        final Messages messages = messagesList.get(position);
        final MediaPlayer mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        audioStatus = R.drawable.ic_play_audio;


        if (messages.getType().equals("text")){
            myMessageImageHolder.mTextMyMessage.setText(messages.getMessage());
            myMessageImageHolder.mMyTextTime.setText(messages.getCurrentTime());
            myMessageImageHolder.mMyImageTime.setVisibility(GONE);
            myMessageImageHolder.mImageMyMessage.setVisibility(GONE);
            myMessageImageHolder.mMyAudioMessagePlay.setVisibility(GONE);
            myMessageImageHolder.mSeekBarMy.setVisibility(GONE);
            myMessageImageHolder.mTextTimeProgress.setVisibility(GONE);
            myMessageImageHolder.mTextTimeMessage.setVisibility(GONE);
        }else if (messages.getType().equals("image")) {
            Picasso.with(context)
                    .load(messages.getMessage())
                    .into(myMessageImageHolder.mImageMyMessage);
            myMessageImageHolder.mTextMyMessage.setVisibility(GONE);
            myMessageImageHolder.mMyImageTime.setText(messages.getCurrentTime());
            myMessageImageHolder.mConstrainMyMessageText.setVisibility(GONE);
            myMessageImageHolder.mMyAudioMessagePlay.setVisibility(GONE);
            myMessageImageHolder.mSeekBarMy.setVisibility(GONE);
            myMessageImageHolder.mTextTimeProgress.setVisibility(GONE);
            myMessageImageHolder.mTextTimeMessage.setVisibility(GONE);
        } else {

            myMessageImageHolder.mTextTimeMessage.setText(messages.getCurrentTime());
            if (audioStatus == R.drawable.ic_play_audio){
                myMessageImageHolder.mMyAudioMessagePlay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        myMessageImageHolder.mMyAudioMessagePlay.setImageResource(R.drawable.ic_pause_audio);


                        if (!mMediaPlayer.isPlaying()){
                            // btnPlayAudio.setBackgroundResource(R.drawable.ic_play_audio_white);
                            Thread updateSeekBar;
                            updateSeekBar = new Thread(){
                                @Override
                                public void run() {
                                    int audioDuration = mMediaPlayer.getDuration();
                                    int currentPosition = 0;
                                    myMessageImageHolder.mSeekBarMy.setMax(audioDuration);
                                    while (currentPosition < audioDuration){
                                        try {
                                            sleep(100);
                                            currentPosition = mMediaPlayer.getCurrentPosition();
                                            myMessageImageHolder.mSeekBarMy.setProgress(currentPosition);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            };
                            mMediaPlayer.start();
                            updateSeekBar.start();
                        } else {
                            mMediaPlayer.stop();
                            mMediaPlayer.release();
                        }

                        try {
                            mMediaPlayer.setDataSource(messages.getMessage());
//                        mMediaPlayer.setOnPreparedListener();
                            mMediaPlayer.prepare();
                            mMediaPlayer.start();
                            Toast.makeText(context, "Audio Play", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.d("NOTPLAY", e.getMessage());
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        // mMediaPlayer.start();

                    }
                });

            }

            myMessageImageHolder.mSeekBarMy.setMax(mMediaPlayer.getDuration());

            int timeMax = (myMessageImageHolder.mSeekBarMy.getMax() / 1000);
            int minutsMax = timeMax / 60;
            int secoundMax = timeMax % 60;

            int timeProgress = (myMessageImageHolder.mSeekBarMy.getProgress() / 1000);
            int minutsProgress = timeProgress / 60;
            int secoundProgress= timeProgress % 60;

            // mTextCurrentTime.setText(minutsMax + " : " + secoundMax);
            myMessageImageHolder.mTextTimeProgress.setText
                    (minutsMax + " : " + secoundMax + "  " + minutsProgress + " : " + secoundProgress);

            myMessageImageHolder.mSeekBarMy.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) mMediaPlayer.seekTo(progress);
//
                    myMessageImageHolder.mSeekBarMy.setMax(mMediaPlayer.getDuration());

                    int timeMax = (myMessageImageHolder.mSeekBarMy.getMax() / 1000);
                    int minutsMax = timeMax / 60;
                    int secoundMax = timeMax % 60;

                    int timeProgress = (myMessageImageHolder.mSeekBarMy.getProgress() / 1000);
                    int minutsProgress = timeProgress / 60;
                    int secoundProgress= timeProgress % 60;

                    // mTextCurrentTime.setText(minutsMax + " : " + secoundMax);
                    myMessageImageHolder.mTextTimeProgress.setText
                            (minutsMax + " : " + secoundMax + "  " + minutsProgress + " : " + secoundProgress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            myMessageImageHolder.mConstrainMyMessageText.setVisibility(GONE);
            // myMessageImageHolder.mMyTextTime.setText(messages.getCurrentTime());
            myMessageImageHolder.mMyImageTime.setVisibility(GONE);
            myMessageImageHolder.mImageMyMessage.setVisibility(GONE);

        }


    }

    private void othermessageImage(final OtherMessageImageHolder otherMessageImageHolder, int position){

        final Messages messages = messagesList.get(position);
        final MediaPlayer mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

       // progressSeekBar(otherMessageImageHolder);

        audioStatus = R.drawable.ic_play_audio_white;

        if (messages.getType().equals("text")) {
            otherMessageImageHolder.mTextOtherMessage.setText(messages.getMessage());
            otherMessageImageHolder.mOtherTextTime.setText(messages.getCurrentTime());
            otherMessageImageHolder.mOtherImageTime.setVisibility(GONE);
            otherMessageImageHolder.mImageOtherMessage.setVisibility(GONE);
            otherMessageImageHolder.mOtherAudioMessagePlay.setVisibility(GONE);
            otherMessageImageHolder.mOtherSeekBar.setVisibility(GONE);
            otherMessageImageHolder.mOtherTextProgress.setVisibility(GONE);
            otherMessageImageHolder.mOtherTextTimeAudio.setVisibility(GONE);

        }else if (messages.getType().equals("image")) {
            Picasso.with(context)
                    .load(messages.getMessage())
                    .into(otherMessageImageHolder.mImageOtherMessage);
            otherMessageImageHolder.mOtherImageTime.setText(messages.getCurrentTime());
            otherMessageImageHolder.mTextOtherMessage.setVisibility(GONE);
            otherMessageImageHolder.mOtherTextTime.setVisibility(GONE);
            otherMessageImageHolder.mOtherAudioMessagePlay.setVisibility(GONE);
            otherMessageImageHolder.mOtherSeekBar.setVisibility(GONE);
            otherMessageImageHolder.mOtherTextProgress.setVisibility(GONE);
            otherMessageImageHolder.mOtherTextTimeAudio.setVisibility(GONE);

        }else {

            otherMessageImageHolder.mOtherTextTimeAudio.setText(messages.getCurrentTime());

            if (audioStatus == R.drawable.ic_play_audio_white){
                otherMessageImageHolder.mOtherAudioMessagePlay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        otherMessageImageHolder.mOtherAudioMessagePlay.setImageResource(R.drawable.ic_pause_audio_white);


                        if (!mMediaPlayer.isPlaying()){
                            // btnPlayAudio.setBackgroundResource(R.drawable.ic_play_audio_white);
                            Thread updateSeekBar;
                            updateSeekBar = new Thread(){
                                @Override
                                public void run() {
                                    int audioDuration = mMediaPlayer.getDuration();
                                    int currentPosition = 0;
                                    otherMessageImageHolder.mOtherSeekBar.setMax(audioDuration);
                                    while (currentPosition < audioDuration){
                                        try {
                                            sleep(100);
                                            currentPosition = mMediaPlayer.getCurrentPosition();
                                            otherMessageImageHolder.mOtherSeekBar.setProgress(currentPosition);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            };
                            mMediaPlayer.start();
                            updateSeekBar.start();
                        } else {
                            mMediaPlayer.stop();
                            mMediaPlayer.release();
                        }

                        try {
                            mMediaPlayer.setDataSource(messages.getMessage());
//                        mMediaPlayer.setOnPreparedListener();
                            mMediaPlayer.prepare();
                            mMediaPlayer.start();
                            Toast.makeText(context, "Audio Play", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.d("NOTPLAY", e.getMessage());
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        //playAudio(otherMessageImageHolder.mOtherSeekBar);
                        //mMediaPlayer.start();

                    }
                });
            }

            otherMessageImageHolder.mOtherSeekBar.setMax(mMediaPlayer.getDuration());

            int timeMax = (otherMessageImageHolder.mOtherSeekBar.getMax() / 1000);
            int minutsMax = timeMax / 60;
            int secoundMax = timeMax % 60;

            int timeProgress = (otherMessageImageHolder.mOtherSeekBar.getProgress() / 1000);
            int minutsProgress = timeProgress / 60;
            int secoundProgress= timeProgress % 60;

            // mTextCurrentTime.setText(minutsMax + " : " + secoundMax);
            otherMessageImageHolder.mOtherTextProgress.setText
                    (minutsMax + " : " + secoundMax + "  " + minutsProgress + " : " + secoundProgress);

            otherMessageImageHolder.mOtherSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) mMediaPlayer.seekTo(progress);

                    otherMessageImageHolder.mOtherSeekBar.setMax(mMediaPlayer.getDuration());

                    int timeMax = (otherMessageImageHolder.mOtherSeekBar.getMax() / 1000);
                    int minutsMax = timeMax / 60;
                    int secoundMax = timeMax % 60;

                    int timeProgress = (otherMessageImageHolder.mOtherSeekBar.getProgress() / 1000);
                    int minutsProgress = timeProgress / 60;
                    int secoundProgress= timeProgress % 60;

                    // mTextCurrentTime.setText(minutsMax + " : " + secoundMax);
                    otherMessageImageHolder.mOtherTextProgress.setText
                            (minutsMax + " : " + secoundMax + "  " + minutsProgress + " : " + secoundProgress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });


            otherMessageImageHolder.mTextOtherMessage.setVisibility(GONE);
            otherMessageImageHolder.mOtherTextTime.setVisibility(GONE);
           // otherMessageImageHolder.mConstrainOtherMessageText.setVisibility(GONE);
            // myMessageImageHolder.mMyTextTime.setText(messages.getCurrentTime());
            otherMessageImageHolder.mOtherImageTime.setVisibility(GONE);
            otherMessageImageHolder.mImageOtherMessage.setVisibility(GONE);
        }
        
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        if (TextUtils.equals(messagesList.get(position).getFrom(),
                FirebaseAuth.getInstance().getCurrentUser().getUid())){

           // myMessage((MyMessageHolder) holder, position);
            mymessageImage((MyMessageImageHolder) holder, position);
        } else {
          //  otherMessage((OtherMessageHolder) holder, position);
            othermessageImage((OtherMessageImageHolder) holder, position);
        }



    }


    @Override
    public int getItemCount() {
        if (messagesList != null) {
            return messagesList.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {

        Messages messages = messagesList.get(position);

        if (TextUtils.equals(messagesList.get(position).getFrom(),
                FirebaseAuth.getInstance().getCurrentUser().getUid())){

               // return VIEW_MESSAGE_TEXT_ME;

                return VIEW_MESSAGE_IMAGE_ME;

        } else {

               // return VIEW_MESSAGE_TEXT_OTHER;

               return VIEW_MESSAGE_IMAGE_OTHER;

        }
       // return 0;

    }

    public static class MyMessageImageHolder extends RecyclerView.ViewHolder{

        ImageView mImageMyMessage;
        TextView mTextMyMessage;
        TextView mMyImageTime, mMyTextTime;
        ConstraintLayout mConstrainMyMessageText;
        ImageView mMyAudioMessagePlay;
        SeekBar mSeekBarMy;
        TextView mTextTimeProgress, mTextTimeMessage;
        private Context mContext;

        public MyMessageImageHolder(View itemView, Context mContext) {
            super(itemView);
            this.mContext = mContext;
            mImageMyMessage = itemView.findViewById(R.id.image_message_my);
            mTextMyMessage = itemView.findViewById(R.id.text_message_my);
            mMyImageTime = itemView.findViewById(R.id.text_my_message_image_time);
            mMyTextTime = itemView.findViewById(R.id.text_my_message_text_time);
            mConstrainMyMessageText = itemView.findViewById(R.id.constrain_my_message_text);
            mMyAudioMessagePlay = itemView.findViewById(R.id.my_message_audio);
            mSeekBarMy = itemView.findViewById(R.id.seekbar_my_message);
            mTextTimeProgress = (TextView) itemView.findViewById(R.id.text_progress_audio2);
            mTextTimeMessage = (TextView) itemView.findViewById(R.id.text_my_message_audio_time);

            optionMessage(mContext,mTextMyMessage);
        }
    }

    public static class OtherMessageImageHolder extends RecyclerView.ViewHolder{

        ImageView mImageOtherMessage;
        TextView mTextOtherMessage;
        TextView mOtherImageTime,mOtherTextTime;
        RelativeLayout mConstrainOtherMessageText;
        ImageView mOtherAudioMessagePlay;
        SeekBar mOtherSeekBar;
        TextView mOtherTextProgress, mOtherTextTimeAudio;
        private Context mContext;

        public OtherMessageImageHolder(View itemView, Context mContext) {
            super(itemView);
            this.mContext = mContext;
            mImageOtherMessage = itemView.findViewById(R.id.image_message_other);
            mTextOtherMessage = itemView.findViewById(R.id.text_message_other);
            mOtherImageTime = itemView.findViewById(R.id.text_other_message_image_time);
            mOtherTextTime = itemView.findViewById(R.id.text_other_message_text_time);
            //mConstrainOtherMessageText = itemView.findViewById(R.id.relative_other_message_text);
            mOtherAudioMessagePlay = itemView.findViewById(R.id.other_message_audio);
            mOtherSeekBar = itemView.findViewById(R.id.seekbar_other_message);
            mOtherTextProgress = itemView.findViewById(R.id.text_progress_audio);
            mOtherTextTimeAudio = itemView.findViewById(R.id.text_other_message_audio_time);

            //playAudio(mOtherSeekBar);
           // progressSeekBar(mOtherSeekBar, mOtherTextProgress);

            optionMessage(mContext, mTextOtherMessage);
        }
    }

    public static void optionMessage(final Context mContext, final TextView textMessafe){

        textMessafe.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    final Dialog dialogOPtionMessage = new Dialog(mContext);
                    dialogOPtionMessage.setContentView(R.layout.raw_dialog_option_message);
                    ImageView mCopy = dialogOPtionMessage.findViewById(R.id.img_copy);
                    ImageView mReplay = dialogOPtionMessage.findViewById(R.id.img_replay);
                    ImageView mDelete = dialogOPtionMessage.findViewById(R.id.img_delete);
                    ImageView mForward = dialogOPtionMessage.findViewById(R.id.img_forward);
                    ImageView mShare = dialogOPtionMessage.findViewById(R.id.img_share);

                    mCopy.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            MyClipboardManager.copyToClipboard(mContext,textMessafe.getText().toString());
                            Toast.makeText(mContext, "Copy Text to Clibborder", Toast.LENGTH_SHORT).show();
                            dialogOPtionMessage.dismiss();
                        }
                    });

                    mReplay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent intent = new Intent(mContext, ReplayMessageFriends.class);
                            intent.putExtra("messageReplay", textMessafe.getText().toString());
                            mContext.startActivity(intent);
                            dialogOPtionMessage.dismiss();

                        }

                    });

                    mDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Messages messages = new Messages();
                            //DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
                            DatabaseReference mUserMessageRef = FirebaseDatabase.getInstance().getReference();
                            String pushID = mUserMessageRef.getKey();
                            mUserMessageRef.child("messages")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(messages.getTo()).child(pushID);


                            mUserMessageRef.removeValue();
                            Toast.makeText(mContext, "Delete Message", Toast.LENGTH_SHORT).show();
                            dialogOPtionMessage.dismiss();
                        }
                    });

                    mForward.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(mContext, "Forward", Toast.LENGTH_SHORT).show();
                            dialogOPtionMessage.dismiss();
                        }
                    });

                    mShare.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent shareMessage = new Intent(Intent.ACTION_SEND);
                            shareMessage.setType("text/plain");
                            shareMessage.putExtra(Intent.EXTRA_TEXT, textMessafe.getText().toString());
                            mContext.startActivity(shareMessage);
                            dialogOPtionMessage.dismiss();
                        }
                    });

                    dialogOPtionMessage.show();
                    return true;
                }
            });
    }

//    private static void playAudio(final SeekBar mSeekBar){
//        if (!mMediaPlayer.isPlaying()){
//            // btnPlayAudio.setBackgroundResource(R.drawable.ic_play_audio_white);
//            Thread updateSeekBar;
//            updateSeekBar = new Thread(){
//                @Override
//                public void run() {
//                    int audioDuration = mMediaPlayer.getDuration();
//                    int currentPosition = 0;
//                    mSeekBar.setMax(audioDuration);
//                    while (currentPosition < audioDuration){
//                        try {
//                            sleep(100);
//                            currentPosition = mMediaPlayer.getCurrentPosition();
//                            mSeekBar.setProgress(currentPosition);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            };
//            mMediaPlayer.start();
//            updateSeekBar.start();
//        }
//    }

//    private static void progressSeekBar(SeekBar mSeekBar, TextView mTextCurrentTime){
//
//
//
//        mSeekBar.setMax(mMediaPlayer.getDuration());
//
//        int timeMax = (mSeekBar.getMax() / 1000);
//        int minutsMax = timeMax / 60;
//        int secoundMax = timeMax % 60;
//
//        int timeProgress = (mSeekBar.getProgress() / 1000);
//        int minutsProgress = timeProgress / 60;
//        int secoundProgress= timeProgress % 60;
//
//       // mTextCurrentTime.setText(minutsMax + " : " + secoundMax);
//        mTextCurrentTime.setText(minutsProgress + " : " + secoundProgress);
//    }

}


