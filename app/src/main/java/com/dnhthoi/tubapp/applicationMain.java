package com.dnhthoi.tubapp;

import android.app.Application;
import android.util.Log;

import com.urbanairship.UAirship;

/**
 * Created by ant on 29/02/2016.
 */
public class applicationMain extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        UAirship.takeOff(this, new UAirship.OnReadyCallback() {
            @Override
            public void onAirshipReady(UAirship airship) {

                // Enable user notifications
                airship.getPushManager().setUserNotificationsEnabled(false);
                //String str = airship.getPushManager().getLastReceivedMetadata();
                //Log.e("The Metatdata:: ", str);
            }
        });
    }
}
