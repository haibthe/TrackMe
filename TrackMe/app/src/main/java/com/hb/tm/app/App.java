package com.hb.tm.app;


import com.hb.lib.app.HBMvpApp;
import com.hb.tm.BuildConfig;
import com.hb.tm.di.component.AppComponent;
import com.hb.tm.di.component.DaggerAppComponent;
import com.hb.tm.di.module.AppModule;
import com.hb.tm.utils.image.GlideImageHelper;
import com.hb.tm.utils.image.ImageHelper;
import timber.log.Timber;

public class App extends HBMvpApp {

    public static ImageHelper imageHelper;

    private AppComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    protected void init() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        initAllComponent();

        imageHelper = new GlideImageHelper(getBaseContext());
    }

    public void initAllComponent() {
        mAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();

        mAppComponent.inject(this);
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }
}
