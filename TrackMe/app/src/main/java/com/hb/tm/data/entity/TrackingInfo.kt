package com.hb.tm.data.entity

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hb.tm.R
import com.mapbox.geojson.utils.PolylineUtils
import timber.log.Timber

@Entity(tableName = "tracking_info")
data class TrackingInfo(
    @ColumnInfo(name = "create_date") val createDate: Long,
    @ColumnInfo(name = "distance") val distance: Float,
    @ColumnInfo(name = "avgSpeed") val avgSpeed: Float,
    @ColumnInfo(name = "duration") val duration: Int,
    @ColumnInfo(name = "path") val path: String
) {
    @PrimaryKey(autoGenerate = true) var tiId: Int = 0

    @ColumnInfo(name = "image_file") var imageFile: String = ""

    fun generateImagePath(context: Context) {
        if (path.isEmpty())
            return

        val points = PolylineUtils.decode(path, 5)

        val size = points.size
        var sp = ""
        var ep = ""
        if (size == 1) {
            val s = points[0]
            sp = "${s.longitude()},${s.latitude()}"
            ep = sp
        } else if (size > 1) {
            val s = points[0]
            val e = points[size - 1]
            sp = "${s.longitude()},${s.latitude()}"
            ep = "${e.longitude()},${e.latitude()}"
        }

        imageFile = context.getString(R.string.map_static)
            .replace("{{START_POINT}}", sp)
            .replace("{{END_POINT}}", ep)
            .replace("{{PATH_ROUTE}}", path)
            .replace("{{TOKEN_MAP}}", context.getString(R.string.map_token))

        Timber.d("Image Map: $imageFile")
    }
}