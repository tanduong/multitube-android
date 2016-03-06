package com.dnhthoi.tubapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dnhthoi.tubapp.data.YouTubeData;
import com.urbanairship.UAirship;
import com.urbanairship.push.BaseIntentReceiver;
import com.urbanairship.push.PushMessage;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.realm.Realm;

/**
 * Created by ant on 01/03/2016.
 */
public class IntentReceiver extends BaseIntentReceiver {
    private static final String TAG = "PUT_URL";
    static final String mainUrl = "";

    @Override
    protected void onChannelRegistrationSucceeded(Context context, String channelId) {
        Log.e(TAG, "Channel registration updated. Channel Id:" + channelId + ".");
        UAirship.shared().getPushManager().getNamedUser().setId("NamedUserID");
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
            //-----> clear notify
            String ns = Context.NOTIFICATION_SERVICE;
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(ns);

            notificationManager.cancel(1);
            //<------end
            //---> set up to save to clipboard
            String url = intent.getStringExtra(TAG);
            Log.e(TAG,url);
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Message",url);
            clipboard.setPrimaryClip(clip);
        }
    }
    @Override
    protected void onBackgroundPushReceived(Context context, PushMessage message) {
        Log.e(TAG, "Received background push message: " + message);
        setNotify(context, message);
    }

    private void setNotify(Context context, PushMessage message){

        getYouTubeData(message.getAlert(), context);
        //get Mode
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        //if Mode open with youtube immediately is FALSE then do:
        if (!prefs.getBoolean(MainActivity.MODE, true)) {

            //get system notify service
            String ns = Context.NOTIFICATION_SERVICE;
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(ns);

            // get Notify layout
            RemoteViews notificationView = new RemoteViews(context.getPackageName(),
                    R.layout.mynotification);
            //----->set up Intent to play video when tap on notifycation
            Intent videoIntent = new Intent(Intent.ACTION_VIEW);
            videoIntent.setData(Uri.parse(message.getAlert()));
            videoIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingNotificationIntent = PendingIntent.getActivity(context, 0,
                    videoIntent, 0);
            //<----end set up
            Log.e("URL current::",message.getAlert());
            //--->set up notifycation
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.notify_icon)
                            .setContentTitle("TubApp")
                            .setContentText(message.getAlert())
                            .setContent(notificationView)
                            .setContentIntent(pendingNotificationIntent)
                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
            Notification notification = mBuilder.build();
            //<---- end set up
            //set auto clear notify when tap on it
            notification.flags = Notification.FLAG_INSISTENT | Notification.FLAG_AUTO_CANCEL;
            //----->set up intent for liston ation button coppy
            Intent switchIntent = new Intent(context, switchButtonListener.class);

            switchIntent.putExtra(TAG, message.getAlert());
            Log.e("Why won here", switchIntent.getStringExtra(TAG));
            PendingIntent pendingSwitchIntent = PendingIntent.getBroadcast(context, 0,
                    switchIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            notificationView.setOnClickPendingIntent(R.id.closeOnFlash,
                    pendingSwitchIntent);
            //<----- end set up
            //notify
            notificationManager.notify(1, notification);
        }else {
            //set up intent to open youtube
            Intent videoIntent = new Intent(Intent.ACTION_VIEW);
            videoIntent.setData(Uri.parse(message.getAlert()));
            videoIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(videoIntent);
        }
    }


    private void getYouTubeData(final String url,final Context context){
        RequestQueue queue = Volley.newRequestQueue(context);

// Request a string response from the provided URL.
        final String baseUrl = "https://www.youtube.com/list_ajax?style=json&action_get_templist=1&video_ids=";

        String urlFecthInfo = baseUrl + url.split("v=")[1];
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlFecthInfo,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        //save youtube url
                        try {
                            JSONObject jsonResponse = new JSONObject(response);

                            //JSONObject jsonResponse = new JSONObject(response);

                            JSONArray videoDataArr = jsonResponse.getJSONArray("video");

                            JSONObject videoData = videoDataArr.getJSONObject(0);

                            String title = videoData.getString("title");
                            String thumbnails = videoData.getString("thumbnail");

                            Realm realm = Realm.getInstance(context);
                            realm.beginTransaction();
                            YouTubeData data = realm.createObject(YouTubeData.class);
                            data.setUrl(url);
                            data.setThumbnails(thumbnails);
                            data.setTitle(title);
                            realm.commitTransaction();

                            Log.e("Done::: ", thumbnails);
                        }catch (JSONException jEx){
                            Log.e("Json err", jEx.toString());
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
// Add the request to the RequestQueue.
        queue.add(stringRequest);
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
