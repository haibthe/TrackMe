package com.hb.tm.data.repository

import com.hb.tm.data.entity.DataWrapper
import com.hb.tm.data.entity.TrackingInfo
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

/**
 * Created by buihai on 9/9/17.
 */

interface SystemRepository {

    fun getDataTest(): Observable<List<DataWrapper<*>>>

    fun insertTracking(data: TrackingInfo): Completable

    fun getAllTrackings(): Single<List<TrackingInfo>>

}
