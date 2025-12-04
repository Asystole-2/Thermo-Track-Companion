package com.example.thermotrackcompanion.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Room database instance definition
@Database(entities = [AlertEntity::class], version = 1, exportSchema = false)
abstract class ThermoTrackDatabase : RoomDatabase() {
    abstract fun alertDao(): AlertDao

    companion object {
        @Volatile
        private var Instance: ThermoTrackDatabase? = null

        fun getDatabase(context: Context): ThermoTrackDatabase {
            // Singleton pattern
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, ThermoTrackDatabase::class.java, "thermotrack_db")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}