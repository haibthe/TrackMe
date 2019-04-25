package com.hb.tm.data.store.system

import com.hb.tm.data.entity.TrackingInfo
import io.reactivex.Completable
import io.reactivex.Single

/**
 * Created by buihai on 9/9/17.
 */

interface SystemStore {


    interface LocalStorage {

        fun insertTracking(trackingInfo: TrackingInfo): Completable

        fun getAllTrackingInfo() : Single<List<TrackingInfo>>
    }

    interface RequestService {

    }
}
