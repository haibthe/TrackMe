package com.hb.tm.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hb.tm.data.entity.TrackingInfo
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface AppDAO {

    @Query("SELECT * FROM tracking_info")
    fun getAllTrackings(): Single<List<TrackingInfo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(ti: TrackingInfo): Completable
}