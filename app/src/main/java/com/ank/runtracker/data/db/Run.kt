package com.ank.runtracker.data.db

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracker_table")
data class Run(
    val bitmap: Bitmap? = null,
    val timeStamp: Long = 0L,
    val averageSpeed: Long = 0,
    val distanceInMeter: Int = 0,
    val timeInMills: Long = 0L,
    val caloriesBurned: Int = 0


) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}
