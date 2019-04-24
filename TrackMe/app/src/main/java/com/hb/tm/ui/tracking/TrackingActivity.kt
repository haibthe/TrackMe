package com.hb.tm.ui.tracking

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.os.StrictMode
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import butterknife.BindView
import butterknife.OnClick
import com.hb.lib.mvp.impl.HBMvpActivity
import com.hb.lib.utils.ui.ThemeUtils
import com.hb.tm.BuildConfig
import com.hb.tm.R
import com.hb.tm.app.App
import com.hb.tm.ui.base.TrackingInfoViewHolder
import com.hb.tm.ui.tracking.service.LocalService
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.snapshotter.MapSnapshotter
import com.mapbox.mapboxsdk.style.layers.LineLayer
import com.mapbox.mapboxsdk.style.layers.Property
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import timber.log.Timber
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class TrackingActivity : HBMvpActivity<TrackingPresenter>(), TrackingContract.View,
    PermissionsListener, LocalService.OnLocationChangeListener {

    override fun getResLayoutId(): Int = R.layout.activity_tracking

    @BindView(R.id.mapview)
    lateinit var mMapView: MapView
    lateinit var mMapboxMap: MapboxMap
    lateinit var mStyle: Style

    private var permissionsManager: PermissionsManager = PermissionsManager(this)


    @BindView(R.id.image_view_pause)
    lateinit var mPauseView: View
    @BindView(R.id.image_view_resume)
    lateinit var mResumeView: View
    @BindView(R.id.image_view_stop)
    lateinit var mStopView: View

    @BindView(R.id.viewgroup_tracking_info)
    lateinit var mTrackingInfoView: View
    lateinit var mTrackingInfoViewHolder: TrackingInfoViewHolder

    private var mShouldUnbind = false
    private var mBoundService: LocalService? = null

    private val mConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName) {
            mBoundService = null
        }

        override fun onServiceConnected(name: ComponentName, service: IBinder?) {
            mBoundService = (service as LocalService.LocalBinder).getService()
            mBoundService?.apply {
                setOnLocationChangeListener(this@TrackingActivity)
                setupLocationService(mMapboxMap)
            }

        }
    }

    override fun onLocation(location: Location) {
        mPresenter.tracking(location)

        updateStartMarker(location)
    }

    private fun doBindService() {
        if (bindService(
                Intent(this, LocalService::class.java),
                mConnection, Context.BIND_AUTO_CREATE
            )
        ) {
            mShouldUnbind = true
        } else {
            Timber.e("Error service")
        }
    }

    private fun doUnbindService() {
        if (mShouldUnbind) {
            unbindService(mConnection)
            mShouldUnbind = false
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mTrackingInfoViewHolder = TrackingInfoViewHolder(mTrackingInfoView)

        mMapView.onCreate(savedInstanceState)
        mMapView.getMapAsync {
            mMapboxMap = it
            val uiSettings = mMapboxMap.uiSettings

            uiSettings.isLogoEnabled = false
            uiSettings.isAttributionEnabled = false

            val styleUrl = "https://images.vietbando.com/Style/vt_vbddefault/${getToken()}"
            mMapboxMap.setStyle(styleUrl) { style ->
                mStyle = style
                enableLocationComponent(style)

                setupLineLayer(style)
                setupMarkerLayer(style)

                onTrackingResume()
            }

            if (BuildConfig.DEBUG) {
                mMapboxMap.addOnMapClickListener { pointOnMap ->
                    val mockupLocation = Location("Test")
                    mockupLocation.latitude = pointOnMap.latitude
                    mockupLocation.longitude = pointOnMap.longitude
                    mPresenter.tracking(mockupLocation)
                    true
                }
            }
        }

        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
    }

    @SuppressLint("MissingPermission")
    @OnClick(R.id.fab_location)
    fun onLocation() {
        val location = mMapboxMap.locationComponent.lastKnownLocation ?: return

        mMapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location), 17.0), 500)
    }

    @SuppressLint("MissingPermission")
    override fun getLocation(): Location? {
        return mMapboxMap.locationComponent.lastKnownLocation
    }

    companion object {
        const val MARKER_SOURCE = "marker-source"
        const val MARKER_LAYER = "marker-layer"
        const val MARKER_IMAGE_START = "marker-start"

        const val LINE_SOURCE = "line-source"
        const val LINE_LAYER_BK = "line-layer-background"
        const val LINE_LAYER_FK = "line-layer-foreground"
    }

    private val mLineSource = GeoJsonSource(LINE_SOURCE)
    private val mMarkerSource = GeoJsonSource(MARKER_SOURCE)
    private var isFistLoad = true

    private fun setupLineLayer(style: Style) {
        style.addSource(mLineSource)


        val lineLayer1 = LineLayer(LINE_LAYER_BK, LINE_SOURCE)
            .withProperties(
                PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                PropertyFactory.lineWidth(7f),
                PropertyFactory.lineColor(Color.parseColor("#ffffff"))
            )
        style.addLayer(lineLayer1)


        val lineLayer = LineLayer(LINE_LAYER_FK, LINE_SOURCE)
            .withProperties(
                PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                PropertyFactory.lineWidth(5f),
                PropertyFactory.lineColor(Color.parseColor("#e55e5e"))
            )
        style.addLayer(lineLayer)
    }

    override fun updatePath(path: List<Point>) {
        val ls = LineString.fromLngLats(path)
        ls.toString()
        mLineSource.setGeoJson(ls)
    }

    @SuppressLint("MissingPermission")
    private fun setupMarkerLayer(style: Style) {
        style.addImage(
            MARKER_IMAGE_START,
            BitmapFactory.decodeResource(
                this@TrackingActivity.resources, R.drawable.custom_marker
            )
        )

        style.addSource(mMarkerSource)

        val layer = SymbolLayer(MARKER_LAYER, MARKER_SOURCE)
            .withProperties(
                PropertyFactory.iconAllowOverlap(true),
                PropertyFactory.iconIgnorePlacement(true),
                PropertyFactory.iconImage(MARKER_IMAGE_START),
                PropertyFactory.iconOffset(arrayOf(0f, -20f))
            )
        style.addLayer(layer)
    }

    override fun updateStartMarker(location: Location) {
        if (isFistLoad) {
            mMarkerSource.setGeoJson(Point.fromLngLat(location.longitude, location.latitude))
            isFistLoad = false
        }
    }

    private fun enableLocationComponent(style: Style) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            val customLocationComponentOptions = LocationComponentOptions.builder(this)
                .trackingGesturesManagement(true)
                .accuracyColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .build()

            val locationComponentActivationOptions = LocationComponentActivationOptions.builder(this, style)
                .locationComponentOptions(customLocationComponentOptions)
                .build()


            mMapboxMap.locationComponent.apply {
                activateLocationComponent(locationComponentActivationOptions)
                isLocationComponentEnabled = true
                cameraMode = CameraMode.TRACKING
                renderMode = RenderMode.COMPASS
                zoomWhileTracking(16.0)

            }

            doBindService()
        } else {
            permissionsManager = PermissionsManager(this)
            permissionsManager.requestLocationPermissions(this)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onExplanationNeeded(permissionsToExplain: List<String>) {
        Toast.makeText(this, "Location Explanation", Toast.LENGTH_LONG).show()
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            enableLocationComponent(mMapboxMap.style!!)
        } else {
            Toast.makeText(this, "Permission not granted", Toast.LENGTH_LONG).show()
            finish()
        }
    }


    @OnClick(R.id.image_view_resume)
    fun onTrackingResume() {
        //  Tracking Resume
        mPauseView.visibility = View.VISIBLE
        mResumeView.visibility = View.GONE
        mStopView.visibility = View.GONE

        mPresenter.startTracking()

        onLocation()
    }

    @OnClick(R.id.image_view_pause)
    fun onTrackingPause() {
        // Tracking Pause
        mPauseView.visibility = View.GONE
        mResumeView.visibility = View.VISIBLE
        mStopView.visibility = View.VISIBLE

        mPresenter.pauseTracking()

        val path = mPresenter.getPath()

        if (path.size > 1) {
            val bounds = LatLngBounds.Builder().includes(path.map {
                LatLng(it.latitude(), it.longitude())
            }).build()

            val padding = ThemeUtils.dpToPx(this, 32)
            mMapboxMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(bounds, padding),
                500
            )
        } else {
            onLocation()
        }
    }

    @OnClick(R.id.image_view_stop)
    fun onTrackingStop() {

        startSnapShot(
            mMapboxMap.projection.visibleRegion.latLngBounds,
            mMapView.measuredHeight,
            mMapView.measuredWidth
        )


    }

    override fun saveCompleted() {
        finish()
    }

    override fun saveFailed(error: String) {
        showErrorDialog(error, View.OnClickListener {

        })
    }

    override fun updateDuration(duration: Int) {
        mTrackingInfoViewHolder.updateDuration(duration)
    }

    override fun updateDistance(distance: Float) {
        mTrackingInfoViewHolder.updateDistance(distance)
    }

    override fun updateSpeed(speed: Float) {
        mTrackingInfoViewHolder.updateSpeed(speed)
    }

    private fun getToken(): String {
        return getString(R.string.vbd_token)
    }

    override fun onBackPressed() {
    }

    public override fun onStart() {
        super.onStart()
        mMapView.onStart()
        Timber.d("onStart")

    }

    public override fun onResume() {
        super.onResume()
        mMapView.onResume()
        Timber.d("onResume")
    }


    public override fun onPause() {
        super.onPause()
        mMapView.onPause()
        Timber.d("onPause")
    }

    public override fun onStop() {
        super.onStop()
        mMapView.onStop()
        Timber.d("onStop")
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mMapView.onDestroy()
        doUnbindService()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mMapView.onSaveInstanceState(outState)
    }


    private var mapSnapshotter: MapSnapshotter? = null

    private fun startSnapShot(latLngBounds: LatLngBounds, width: Int, height: Int) {
        if (!mStyle.isFullyLoaded) {
            return
        }

        val style = mStyle

        if (mapSnapshotter == null) {
            val options = MapSnapshotter.Options(width, height)
                .withRegion(latLngBounds).withStyle(style.url)

            mapSnapshotter = MapSnapshotter(this, options)
        } else {
            mapSnapshotter!!.apply {
                setSize(width, height)
                setRegion(latLngBounds)
                setRegion(latLngBounds)
            }
        }

        mapSnapshotter!!.start { snapshot ->
            val bitmapOfMapSnapshotImage = snapshot.bitmap
            val bmpUri = getLocalBitmapUri(bitmapOfMapSnapshotImage)

            mPresenter.saveTracking()
        }
    }

    private fun getLocalBitmapUri(bmp: Bitmap): Uri? {
        var bmpUri: Uri? = null
        val fileName = App.folder + "image_${System.currentTimeMillis()}.png"
        val file = File(fileName)
        try {
            val out = FileOutputStream(file)
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out)
            try {
                out.close()
            } catch (ioe: IOException) {
                ioe.printStackTrace()
            }
            bmpUri = Uri.fromFile(file)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return bmpUri
    }
}