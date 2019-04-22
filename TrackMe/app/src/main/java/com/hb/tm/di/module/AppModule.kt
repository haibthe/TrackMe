package com.hb.tm.di.module

import android.app.Application
import android.content.Context
import com.google.gson.Gson
import com.hb.lib.data.IDataManager
import com.hb.lib.utils.RxBus
import com.hb.tm.data.AppDataManager
import com.hb.tm.data.cache.AppCache
import com.hb.tm.data.cache.ICache
import com.hb.tm.data.pref.AppPreferenceHelper
import com.hb.tm.data.pref.PreferenceHelper
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import java.io.File
import javax.inject.Singleton


@Module
class AppModule(val app: Application) {

    @Singleton
    @Provides
    fun providesContext(): Context = app

    @Singleton
    @Provides
    fun providesCompositeDisposable(): CompositeDisposable = CompositeDisposable()

    @Singleton
    @Provides
    fun providesCacheDir(): File = app.cacheDir

    @Singleton
    @Provides
    fun providesRxBus(): RxBus = RxBus()

    @Singleton
    @Provides
    fun providesCache(context: Context): ICache = AppCache(context)

    @Singleton
    @Provides
    fun providesPreferences(context: Context, gson: Gson): PreferenceHelper = AppPreferenceHelper(context, gson)

    @Singleton
    @Provides
    fun providesDataManager(context: Context, preferenceHelper: PreferenceHelper, cache: ICache): IDataManager =
        AppDataManager(context, preferenceHelper, cache)



}