package com.ank.runtracker.data.db

import android.content.Context
import androidx.room.*
import com.ank.runtracker.common.Constants.DB_VERSION
import java.security.AccessControlContext

@Database(entities = [Run::class], version = DB_VERSION)
@TypeConverters(Converters::class)
abstract class TrackerDB : RoomDatabase() {

    abstract fun getRunDao(): RunDao



}