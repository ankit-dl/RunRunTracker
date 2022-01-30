package com.ank.runtracker.repository

import androidx.lifecycle.LiveData
import androidx.room.Query
import com.ank.runtracker.data.db.Run
import com.ank.runtracker.data.db.RunDao
import javax.inject.Inject

class MainRepository @Inject constructor(private val runDao: RunDao) {


    suspend fun insert(run: Run) = runDao.insert(run)
    suspend fun delete(run: Run) = runDao.delete(run)

    fun getAllRunByDate() = runDao.getAllRunByDate()


    fun getAllRunByAverageSpeed() = runDao.getAllRunByAverageSpeed()


    fun getAllRunByDistanceInMeter() = runDao.getAllRunByDistanceInMeter()


    fun getAllRunByTimeInMills() = runDao.getAllRunByTimeInMills()


    fun getAllRunByCaloriesBurned() = runDao.getAllRunByCaloriesBurned()


    fun getTotalTimeInMillis() = runDao.getTotalTimeInMillis()


    fun getTotalCaloriesBurned() = runDao.getTotalCaloriesBurned()


    fun getTotalDistance() = runDao.getTotalDistance()


    fun getTotalAvgSpeed() = runDao.getTotalAvgSpeed()
}