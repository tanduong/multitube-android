package com.dnhthoi.tubapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.urbanairship.push.BaseIntentReceiver;
import com.urbanairship.push.PushMessage;

/**
 * Created by ant on 01/03/2016.
 */
public class IntentReceiver extends BaseIntentReceiver {
    private static final String TAG = "PUT_URL";

    @Override
    protected void onChannelRegistrationSucceeded(Context context, String channelId) {
        Log.e(TAG, "Channel registration updated. Channel Id:" + channelId + ".");
    }

    @Override
    protected void onChannelRegistrationFailed(Context context) {
        Log.e(TAG, "Channel registration failed.");
    }

    @Override
    protected void onPushReceived(Context context, PushMessage message, int notificationId) {
        //Log.e(TAG, "Received push notification. Alert: " + message.getAlert() + ". Notification ID: " + notificationId);
        setNotify(context, message);

    }
    public static class switchButtonListener extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String ns = Context.NOTIFICATION_SERVICE;
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(ns);


            notificationManager.cancel(1);

            Intent videoIntent = new Intent(Intent.ACTION_VIEW);
           String strUrl =  intent.getStringExtra(TAG);
            Log.e(TAG, strUrl);
            videoIntent.setData(Uri.parse(strUrl));
            videoIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

          // videoIntent.setClassName("com.google.android.youtube", "com.google.android.youtube.WatchActivity");
          context.startActivity(videoIntent);
            //context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(strUrl)));
        }
    }
    @Override
    protected void onBackgroundPushReceived(Context context, PushMessage message) {
        Log.e(TAG, "Received background push message: " + message);
        setNotify(context, message);
    }

    private void setNotify(Context context, PushMessage message){

        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(ns);


        RemoteViews notificationView = new RemoteViews(context.getPackageName(),
                R.layout.mynotification);

        //the intent that is started when the notification is clicked (works)
        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingNotificationIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, 0);

        // notification.flags |= Notification.F;

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.notify_icon)
                        .setContentTitle("TubApp")
                        .setContentText(message.getAlert())
                        .setContent(notificationView)
                        .setContentIntent(pendingNotificationIntent);
        Notification notification = mBuilder.build();
        notification.flags = Notification.FLAG_INSISTENT | Notification.FLAG_AUTO_CANCEL;
        //this is the intent that is supposed to be called when the
        //button is clicked
        Intent switchIntent = new Intent(context, switchButtonListener.class);

        switchIntent.putExtra(TAG,message.getAlert());

        PendingIntent pendingSwitchIntent = PendingIntent.getBroadcast(context, 0,
                switchIntent, 0);
        notificationView.setOnClickPendingIntent(R.id.closeOnFlash,
                pendingSwitchIntent);
        notificationManager.notify(1, notification);
    }

    @Override
    protected boolean onNotificationOpened(Context context, PushMessage message, int notificationId) {
        Log.e(TAG, "User clicked notification. Alert: " + message.getAlert());

        // Return false to let UA handle launching the launch activity
        return false;
    }

    @Override
    protected boolean onNotificationActionOpened(Context context, PushMessage message, int notificationId, String buttonId, boolean isForeground) {
        Log.e(TAG, "User clicked notification button. Button ID: " + buttonId + " Alert: " + message.getAlert());

        // Return false to let UA handle launching the launch activity
        return false;
    }

    @Override
    protected void onNotificationDismissed(Context context, PushMessage message, int notificationId) {
        Log.e(TAG, "Notification dismissed. Alert: " + message.getAlert() + ". Notification ID: " + notificationId);
    }
}
