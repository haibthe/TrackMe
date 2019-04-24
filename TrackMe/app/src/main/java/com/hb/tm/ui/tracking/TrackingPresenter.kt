package com.hb.tm.ui.tracking

import android.location.Location
import com.hb.lib.mvp.impl.HBMvpPresenter
import com.hb.tm.data.repository.SystemRepository
import com.mapbox.geojson.Point
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


        if (isViewAttached()) {
            getView().saveCompleted()
        }
    }

    override fun startTracking() {
        isTracking = true
        runDuration()
    }

    private fun runDuration() {
        if (!isTracking)
            return
        val location = if (isViewAttached()) getView().getLocation() else null
        if (location != null) tracking(location)

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
        if (mCurLocation != null) {
            val dis = mCurLocation!!.distanceTo(location)
            Timber.d("Distance: $dis m")
            if (dis < DELTA_DISTANCE) {
                return
            }
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