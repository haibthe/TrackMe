package com.hb.tm.ui.tracking

import android.location.Location
import com.hb.lib.mvp.impl.HBMvpPresenter
import com.hb.tm.data.entity.TrackingInfo
import com.hb.tm.data.repository.SystemRepository
import com.mapbox.geojson.Point
import com.mapbox.geojson.utils.PolylineUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class TrackingPresenter
@Inject constructor(
    private val repository: SystemRepository
) : HBMvpPresenter<TrackingActivity>(), TrackingContract.Presenter {

    companion object {
        const val DELTA_DISTANCE = 2
    }


    private var isTracking = true

    private var mPath = ArrayList<Point>()
    private var mCurLocation: Location? = null

    private var mDuration: Int = 0
    private var mDistance = 0f
    private var mAvgSpeed = 0f

    override fun resume() {
        super.resume()
        Timber.d("resume")
    }

    override fun pause() {
        super.pause()
        Timber.d("pause")
    }

    override fun destroy() {
        super.destroy()
        Timber.d("destroy")
    }

    override fun saveTracking() {

        val pathEncode = PolylineUtils.encode(mPath, 5)
        val data = TrackingInfo(
            createDate = System.currentTimeMillis(),
            distance = mDistance,
            avgSpeed = mAvgSpeed / mPath.size,
            duration = mDuration,
            path = pathEncode
        )

        data.generateImagePath(getView())

        if (isViewAttached()) {
            getView().showLoading()
        }

        disposable.add(
            repository.insertTracking(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (isViewAttached()) {
                        getView().saveCompleted()
                    }
                }, {
                    if (isViewAttached()) {
                        getView().saveFailed(it.localizedMessage)
                    }
                })
        )


    }

    override fun startTracking() {
        isTracking = true
        runDuration()
    }

    private fun runDuration() {
        if (!isTracking)
            return

        disposable.add(
            Observable.just(mDuration++)
                .delay(1000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (isViewAttached()) {
                        getView().updateDuration(it)
                    }
                    runDuration()
                }
        )

    }

    override fun pauseTracking() {
        isTracking = false
    }

    override fun tracking(location: Location) {

        if (!isTracking) {
            return
        }
//        if (location.speed <= 0)
//            return
        if (mCurLocation != null) {
            val dis = mCurLocation!!.distanceTo(location)
            mDistance += dis
        }

        mCurLocation = location
        mPath.add(Point.fromLngLat(location.longitude, location.latitude))
        mAvgSpeed += location.speed

        disposable.add(
            Observable.just(location)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (isViewAttached()) {
                        getView().updatePath(mPath)
                        getView().updateSpeed(it.speed)
                        getView().updateDistance(mDistance)
                    }
                }
        )
    }

    override fun getPath(): List<Point> {
        return mPath
    }

}