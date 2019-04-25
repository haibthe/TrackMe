package com.hb.tm.ui.tracking.service

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Binder
import android.os.Build
import android.os.IBinder
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.hb.tm.R
import com.hb.tm.ui.tracking.TrackingActivity
import com.mapbox.mapboxsdk.maps.MapboxMap


class LocalService : Service() {

    companion object {
        const val NOTIFICATION = R.string.local_service_started
        const val CHANNEL_ID = "1001001"
    }

    private lateinit var mNotificationManager: NotificationManager

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    inner class LocalBinder : Binder() {
        fun getService(): LocalService {
            return this@LocalService
        }
    }

    private val mBinder = LocalBinder()

    fun setupLocationService(mapboxMap: MapboxMap) {
        val locationRequestFormMapBox = mapboxMap.locationComponent.locationEngineRequest
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.create()
        locationRequest.apply {
            interval = locationRequestFormMapBox.interval
            fastestInterval = locationRequestFormMapBox.fastestInterval
        }
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                for (location in locationResult.locations) {
                    mOnLocationChangeListener?.onLocation(location)
                    break
                }
            }
        }
        startLocationUpdates()
    }

    @SuppressLint("MissingPermission")
    fun getLocation(): Task<Location> {
        return fusedLocationClient.lastLocation
    }

    override fun onCreate() {
        super.onCreate()

        mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        showNotification()

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        mNotificationManager.cancel(NOTIFICATION)
        stopLocationUpdates()

    }

    override fun onBind(intent: Intent?): IBinder? {
        return mBinder
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null
        )
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun showNotification() {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        val text = "Service: "+ getText(R.string.local_service_started)



        // The PendingIntent to launch our activity if the user selects this notification
        val contentIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, TrackingActivity::class.java), 0
        )

        // Set the info for the views that show in the notification panel.
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, getString(R.string.app_name),
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            service.createNotificationChannel(channel)
            Notification.Builder(this, CHANNEL_ID)
        } else {
            Notification.Builder(this)
        }
        val notification = builder
            .setSmallIcon(R.mipmap.ic_launcher)  // the status icon
            .setTicker(text)  // the status text
            .setWhen(System.currentTimeMillis())  // the time stamp
            .setContentTitle(getText(R.string.app_name))  // the label of the entry
            .setContentText(text)  // the contents of the entry
            .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
            .build()

        // Send the notification.
        startForeground(NOTIFICATION, notification)
    }

    private var mOnLocationChangeListener: OnLocationChangeListener? = null

    fun setOnLocationChangeListener(listener: OnLocationChangeListener) {
        mOnLocationChangeListener = listener
    }

    interface OnLocationChangeListener {
        fun onLocation(location: Location)
    }
}