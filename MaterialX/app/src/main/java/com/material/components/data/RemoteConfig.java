package com.material.components.data;

import android.app.Activity;

import com.material.components.BuildConfig;

public class RemoteConfig {

    public RemoteConfig() {

    }

    public void fetchData(Activity activity) {
    }

    public String getBannerUnitId() {
        return "ca-app-pub-3239677920600357/9667976501";
    }

    public Long getAppVersion() {
        return Long.valueOf(BuildConfig.VERSION_CODE);
    }
}
