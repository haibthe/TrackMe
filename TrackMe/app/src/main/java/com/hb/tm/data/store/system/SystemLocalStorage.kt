package com.hb.tm.data.store.system


import com.hb.tm.data.DataManager
import com.hb.tm.data.entity.TrackingInfo
import io.reactivex.Completable
import io.reactivex.Single

/**
 * Created by buihai on 9/9/17.
 */

class SystemLocalStorage(
    private val dm: DataManager
) : SystemStore.LocalStorage {

    override fun insertTracking(trackingInfo: TrackingInfo): Completable {
        return dm.insertTracking(trackingInfo)
    }

    override fun getAllTrackingInfo(): Single<List<TrackingInfo>> {
        return dm.getAllTrackingInfo()
    }
}
