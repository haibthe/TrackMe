package com.hb.tm.ui.tracking

import android.location.Location
import com.mapbox.geojson.Point

interface TrackingContract {
    interface View {

        fun saveCompleted()

        fun saveFailed(error: String)

        fun updatePath(path: List<Point>)

        fun updateStartMarker(location: Location)

        fun updateDistance(distance: Float)

        fun updateSpeed(speed: Float)

        fun updateDuration(duration: Int)


        fun getLocation(): Location?
    }

    interface Presenter {


        fun saveTracking()

        fun startTracking()

        fun pauseTracking()

        fun tracking(location: Location)

        fun getPath(): List<Point>
    }
}