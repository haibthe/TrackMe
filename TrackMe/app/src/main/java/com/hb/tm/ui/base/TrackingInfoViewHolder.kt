package com.hb.tm.ui.base

import android.view.View
import android.widget.TextView
import butterknife.BindView
import com.hb.tm.R
import com.hb.tm.data.entity.TrackingInfo
import com.hb.uiwidget.recyclerview.BaseViewHolder
import java.util.*

open class TrackingInfoViewHolder(itemView: View) : BaseViewHolder<TrackingInfo>(itemView) {

    @BindView(R.id.tv_distance)
    lateinit var distance: TextView
    @BindView(R.id.tv_speed)
    lateinit var speed: TextView
    @BindView(R.id.tv_time)
    lateinit var time: TextView

    override fun bindData(data: TrackingInfo) {
    }

    fun updateDuration(duration: Int) {
        time.text = formatDuration(duration)
    }

    fun updateSpeed(speed: Float) {
        val msg = String.format("%.2f m/s", speed)
        this.speed.text = msg
    }

    fun updateDistance(dis: Float) {
        val msg = if (dis > 1000) {
            String.format("%.02f km", dis / 1000)
        } else {
            String.format("%.0f m", dis)
        }
        this.distance.text = msg
    }

    private fun formatDuration(duration: Int): String {
        var time = duration
        val h = time / 3600
        time %= 3600
        val m = time / 60
        time %= 60
        val s = time
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", h, m, s)
    }
}