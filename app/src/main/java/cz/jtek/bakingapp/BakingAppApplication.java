package cz.jtek.bakingapp;

import android.app.Application;

import com.facebook.stetho.Stetho;

public class BakingAppApplication extends Application {

    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            // Initialize Stetho only for debug builds
            Stetho.initializeWithDefaults(this);
        }
    }
}
