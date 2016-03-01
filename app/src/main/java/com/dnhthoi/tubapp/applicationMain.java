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
                //shut up default notify when mess come
                airship.getPushManager().setUserNotificationsEnabled(false);

            }
        });
    }
}
