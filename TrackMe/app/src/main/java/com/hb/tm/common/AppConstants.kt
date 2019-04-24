package com.hb.tm.common

import android.Manifest
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by buihai on 7/29/17.
 */
object AppConstants {

    val PERMISSIONS_IN_APP = arrayOf(
        Manifest.permission.INTERNET,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_NETWORK_STATE,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )


    val formatDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

}
