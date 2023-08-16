package com.u1tramarinet.youtubemusicsharehelper;

import android.app.Application;
import android.os.Build;

import com.google.android.material.color.DynamicColors;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            DynamicColors.applyToActivitiesIfAvailable(this);
        }
    }
}
