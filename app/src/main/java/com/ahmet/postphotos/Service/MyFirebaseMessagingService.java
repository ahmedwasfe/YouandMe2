package com.ahmet.postphotos.Service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.ahmet.postphotos.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by ahmet on 2/8/2018.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String notificationTitle = remoteMessage.getNotification().getTitle();
        String notificationBody = remoteMessage.getNotification().getBody();
        String clickAction = remoteMessage.getNotification().getClickAction();
        String fromUserUID = remoteMessage.getData().get("from_user_id");


        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Bitmap bitmapIcon = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.icon_notification);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.icon_notification)
                        .setLargeIcon(bitmapIcon)
                        .setContentTitle(notificationTitle)
                        .setContentText(notificationBody)
                        .setSound(alarmSound);
//                         .setSound(Uri.parse("android.resource://"
//                                 + this.getPackageName() + "/" + R.raw.opoo_notification));

//        Notification notification = mBuilder.build();
//        notification.sound = Uri.parse("android.resource://"
//                + this.getPackageName() + "/" + R.raw.aa);

        Intent profileUserIntent = new Intent(clickAction);
        profileUserIntent.putExtra("user_uid", fromUserUID);

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        profileUserIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);


        int mNotificationId = (int) System.currentTimeMillis();

        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mNotifyMgr.notify(mNotificationId, mBuilder.build());


    }

}