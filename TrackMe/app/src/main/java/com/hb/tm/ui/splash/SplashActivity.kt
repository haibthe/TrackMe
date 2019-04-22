package com.hb.tm.ui.splash

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import butterknife.BindView
import com.google.android.material.snackbar.Snackbar
import com.hb.lib.mvp.impl.HBMvpActivity
import com.hb.lib.utils.NetworkUtils
import com.hb.lib.utils.Utils
import com.hb.tm.R
import com.hb.tm.common.AppConstants
import com.hb.tm.navigation.Navigator


/**
 * Created by buihai on 7/13/17.
 */

class SplashActivity : HBMvpActivity<SplashPresenter>(), SplashContract.View {


    override fun getResLayoutId(): Int = R.layout.activity_splash

    @BindView(R.id.viewgroup_logo)
    lateinit var logoViewGroup: View

    @BindView(R.id.logo)
    lateinit var logoView: View

    @BindView(R.id.text_view_version)
    lateinit var versionView: TextView
    @BindView(R.id.viewgroup_splash_container)
    lateinit var background: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val version = "Version ${Utils.getVersionName(this)} (${Utils.getVersionCode(this)})"
        versionView.text = version

        showAlertPermissions()

    }


    private fun registerAppPermission() {
        for (permission in AppConstants.PERMISSIONS_IN_APP) {
            if (!mayRequestPermission(permission))
                return
        }

        logoViewGroup.animate()
            .alpha(1.0f)
            .setDuration(1000)
            .withEndAction {
                this.loadData()
            }
            .start()
    }

    private fun showAlertPermissions() {
        statusCheck()
    }

    override fun showErrorDialog(msg: String, listener: View.OnClickListener?) {
        val popup = Snackbar.make(getView(), msg, Snackbar.LENGTH_INDEFINITE)
            .setAction("Đóng") {
                loadData()
            }
        popup.show()
    }


    @SuppressLint("MissingPermission")
    override fun loadData() {

        if (!NetworkUtils.isNetworkConnected(this)) {
            val msg = "Internet của bạn đang chập chờn hoặc không vào được"
            showErrorDialog(msg)
            return
        }

        mPresenter.loadData()
    }

    override fun showUpdateDialog(isForce: Boolean) {
        val builder = AlertDialog.Builder(this)
            .setCancelable(false)
            .setTitle("Thông báo cập nhật")
            .setMessage("Hiện nay đang có phiên bản mới. Cập nhật để sử dụng tốt hơn.")
            .setPositiveButton("Cập nhật") { dialog, _ ->
                val appPackageName = packageName // getPackageName() from Context or Activity object
                try {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
                } catch (anfe: android.content.ActivityNotFoundException) {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                        )
                    )
                }

                dialog.dismiss()
                finish()
            }
            .setNegativeButton("Đóng") { dialog, _ ->
                dialog.dismiss()
                finish()
            }

        if (!isForce) {
            builder.setNegativeButton("Bỏ qua") { dialog, _ ->
                dialog.dismiss()
                mPresenter.loadData()
            }
        }


        builder.show()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_GPS_MANAGER) {
            statusCheck()
        } else if (requestCode == LOCATION_SETTINGS_REQUEST) {
            statusCheck()
        }
    }


    private fun mayRequestPermission(permission: String): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true
        }
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            return true
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            Snackbar.make(
                getView(), "Permissions are need for application.",
                Snackbar.LENGTH_INDEFINITE
            ).setAction(
                android.R.string.ok
            ) { ActivityCompat.requestPermissions(this, arrayOf(permission), REQUEST_ALL_PERMISSION) }
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(permission), REQUEST_ALL_PERMISSION)
        }
        return false
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {

        if (requestCode == REQUEST_ALL_PERMISSION) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                registerAppPermission()
            }
        }
    }


    override fun openTestActivity() {
//        Navigator.startTest(this)
        this.finish()
    }

    override fun openMainActivity() {
        Navigator.startMain(this)
        this.finish()
    }


    private fun statusCheck() {
        val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps()
        } else {
            registerAppPermission()
        }
    }

    private fun buildAlertMessageNoGps() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Ứng dụng cần phải bật định vị. Bạn có muốn bật không?")
            .setCancelable(false)
            .setPositiveButton("Muốn") { _, _ ->
                startActivityForResult(
                    Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                    REQUEST_GPS_MANAGER
                )
            }
            .setNegativeButton("Không") { dialog, _ ->
                dialog.cancel()
                finish()
            }
        val alert = builder.create()
        alert.show()
    }


    companion object {
        const val REQUEST_ALL_PERMISSION = 0
        const val REQUEST_GPS_MANAGER = 1
        const val LOCATION_SETTINGS_REQUEST = 2
    }
}
