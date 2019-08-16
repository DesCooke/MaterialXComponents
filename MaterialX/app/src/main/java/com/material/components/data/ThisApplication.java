package com.material.components.data;

import android.app.Application;

import com.google.android.gms.ads.MobileAds;
import com.material.components.R;

public class ThisApplication extends Application {

    private SharedPref sharedPref;
    private RemoteConfig remoteConfig;

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPref = new SharedPref(this);
        remoteConfig = new RemoteConfig();

        // initialize firebase ads
        MobileAds.initialize(getApplicationContext(), getResources().getString(R.string.admob_app_id));
    }

}
