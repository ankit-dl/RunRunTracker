package com.ank.runtracker.di

import android.content.Context
import android.provider.DocumentsContract
import androidx.room.Room
import com.ank.runtracker.common.Constants.DB_NAME
import com.ank.runtracker.data.db.TrackerDB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.internal.managers.ApplicationComponentManager
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideTrackerDB(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, TrackerDB::class.java, DB_NAME).build()

    @Provides
    @Singleton
    fun provideDao(db: TrackerDB) = db.getRunDao()

}