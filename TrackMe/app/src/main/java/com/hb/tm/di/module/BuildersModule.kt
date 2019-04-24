package com.hb.tm.di.module

import com.hb.tm.di.module.sub.SystemModule
import com.hb.tm.di.scope.CustomScope
import com.hb.tm.ui.detail.DetailActivity
import com.hb.tm.ui.main.MainActivity
import com.hb.tm.ui.splash.SplashActivity
import com.hb.tm.ui.tracking.TrackingActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class BuildersModule {

    @CustomScope
    @ContributesAndroidInjector(modules = [])
    abstract fun contributeSplashActivity(): SplashActivity

    @CustomScope
    @ContributesAndroidInjector(modules = [SystemModule::class])
    abstract fun contributeMainActivity(): MainActivity

    @CustomScope
    @ContributesAndroidInjector(modules = [SystemModule::class])
    abstract fun contributeDetailActivity(): DetailActivity

    @CustomScope
    @ContributesAndroidInjector(modules = [SystemModule::class])
    abstract fun contributeTrackingActivity(): TrackingActivity
}