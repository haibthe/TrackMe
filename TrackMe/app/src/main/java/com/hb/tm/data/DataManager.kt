package com.hb.tm.data

import com.hb.lib.data.IDataManager
import com.hb.tm.data.entity.TrackingInfo
import com.hb.tm.data.pref.PreferenceHelper
import io.reactivex.Completable
import io.reactivex.Single

interface DataManager : IDataManager, PreferenceHelper {


    fun setData(data: String)

    fun getData(): String


    fun insertTracking(trackingInfo: TrackingInfo): Completable

    fun getAllTrackingInfo() : Single<List<TrackingInfo>>
}