package com.hb.tm.navigation

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.hb.tm.ui.detail.DetailActivity
import com.hb.tm.ui.main.MainActivity
import com.hb.tm.ui.main.MainContract
import com.hb.tm.ui.tracking.TrackingActivity


object Navigator {

    @SuppressLint("MissingPermission")
    fun callToNumber(activity: Activity, phoneNumber: String) {
        val intent = Intent(Intent.ACTION_CALL)

        intent.data = Uri.parse("tel:$phoneNumber")
        activity.startActivity(intent)
    }


    fun startBrower(activity: Activity, url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        activity.startActivity(browserIntent)
    }

    fun startMain(activity: Activity) {
        val intent = Intent(activity, MainActivity::class.java)
        activity.startActivity(intent)
    }

    fun startDetail(activity: Activity) {
        val intent = Intent(activity, DetailActivity::class.java)
        activity.startActivity(intent)
    }

    fun startTracking(activity: Activity) {
        val intent = Intent(activity, TrackingActivity::class.java)
        activity.startActivityForResult(intent, MainContract.REQUEST_TRACKING)
    }

}