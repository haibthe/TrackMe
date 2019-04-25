package com.hb.tm.data

import android.content.Context
import com.hb.tm.data.cache.ICache
import com.hb.tm.data.database.AppDAO
import com.hb.tm.data.entity.TrackingInfo
import com.hb.tm.data.pref.PreferenceHelper
import io.reactivex.Completable
import io.reactivex.Single


class AppDataManager
constructor(
    private val context: Context,
    private val pref: PreferenceHelper,
    private val cache: ICache,
    private val dao: AppDAO
) : DataManager {

    private var mData: String = ""

    override fun setData(data: String) {
        mData = data
    }

    override fun getData(): String {
        return mData
    }

    override fun insertTracking(trackingInfo: TrackingInfo): Completable {
        return dao.insertUser(trackingInfo)
    }

    override fun getAllTrackingInfo(): Single<List<TrackingInfo>> {
        return dao.getAllTrackings()
    }
}