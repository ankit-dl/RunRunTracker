package com.ank.runtracker.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RunDao {
    @Insert
    suspend fun insert(run: Run)

    @Delete
    suspend fun delete(run: Run)

    @Query("SELECT * FROM TRACKER_TABLE ORDER BY timeStamp DESC ")
    fun getAllRunByDate(): LiveData<List<Run>>

    @Query("SELECT * FROM TRACKER_TABLE ORDER BY averageSpeed DESC ")
    fun getAllRunByAverageSpeed(): LiveData<List<Run>>

    @Query("SELECT * FROM TRACKER_TABLE ORDER BY distanceInMeter DESC ")
    fun getAllRunByDistanceInMeter(): LiveData<List<Run>>

    @Query("SELECT * FROM TRACKER_TABLE ORDER BY timeInMills DESC ")
    fun getAllRunByTimeInMills(): LiveData<List<Run>>

    @Query("SELECT * FROM TRACKER_TABLE ORDER BY caloriesBurned DESC ")
    fun getAllRunByCaloriesBurned(): LiveData<List<Run>>

    @Query("SELECT SUM(timeInMills) FROM TRACKER_TABLE")
    fun getTotalTimeInMillis(): LiveData<Long>

    @Query("SELECT SUM(caloriesBurned) FROM TRACKER_TABLE")
    fun getTotalCaloriesBurned(): LiveData<Int>

    @Query("SELECT SUM(distanceInMeter) FROM TRACKER_TABLE")
    fun getTotalDistance(): LiveData<Int>

    @Query("SELECT AVG(averageSpeed) FROM TRACKER_TABLE")
    fun getTotalAvgSpeed(): LiveData<Float>
}