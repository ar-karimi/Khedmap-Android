package com.khedmap.khedmap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;
import com.khedmap.khedmap.di.component.AppComponent;
import com.khedmap.khedmap.di.component.DaggerAppComponent;
import com.khedmap.khedmap.di.module.AppModule;
import com.khedmap.khedmap.di.module.ContextModule;
import com.khedmap.khedmap.di.module.DataModule;
import com.pusher.pushnotifications.PushNotifications;


public class InitApplication extends MultiDexApplication {

    private AppComponent component;

    public static InitApplication get(Context context) {
        return (InitApplication) context.getApplicationContext();
    }

    @Override
    protected void attachBaseContext(Context base) {
        MultiDex.install(base); //for support MultiDex in Android 9
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        component = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .contextModule(new ContextModule(this))
                .dataModule(new DataModule())
                .build();

        //to Fix vector Drawable Problem in Older Android (usually <5)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        //to Fix Protocol Problem in Older Android (usually <5)
        try {
            ProviderInstaller.installIfNeeded(this);
        } catch (GooglePlayServicesRepairableException e) {
            // Thrown when Google Play Services is not installed, up-to-date, or enabled
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e("SecurityException", "Google Play Services not available.");
        }

        //Crashlytics initializing with user UUID
        //to get Android ID
        @SuppressLint("HardwareIds")
        String androidId = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        Crashlytics.setUserIdentifier(androidId);
        //till here

        //Pusher initializing
        PushNotifications.start(getApplicationContext(), "ad9d4890-2a5f-4365-85da-a9c44b5a0fec");

    }

    public AppComponent component() {
        return component;
    }
}