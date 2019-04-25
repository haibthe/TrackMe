package com.hb.tm.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hb.tm.data.entity.TrackingInfo


@Database(entities = [TrackingInfo::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun appDAO(): AppDAO

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context,
                AppDatabase::class.java, "Tracking.db"
            ).build()
    }
}