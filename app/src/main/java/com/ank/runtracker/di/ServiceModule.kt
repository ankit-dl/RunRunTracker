package com.ank.runtracker.di

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.ank.runtracker.R
import com.ank.runtracker.common.Constants
import com.ank.runtracker.presentation.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {

    @SuppressLint("VisibleForTests")
    @ServiceScoped
    @Provides
    fun provideFusedLocation(@ApplicationContext context: Context) =
        FusedLocationProviderClient(context)

    @Provides
    @ServiceScoped
    fun providePendingIntent(@ApplicationContext context: Context) =
        PendingIntent.getActivity(
            context, 0,
            Intent(context, MainActivity::class.java).also {

                it.action = Constants.ACTION_SHOW_TRACKING_FRAGMENT
            }, PendingIntent.FLAG_IMMUTABLE
        )

    @Provides
    @ServiceScoped
    fun provideNotificationProvider(
        @ApplicationContext context: Context,
        pendingIntent: PendingIntent
    ) = NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
        .setAutoCancel(false)
        .setOngoing(true)
        .setSmallIcon(R.drawable.ic_directions_run_black_24dp)
        .setContentInfo("Running...")
        .setContentText("00:00:00")
        .setContentIntent(pendingIntent)

    @Provides
    @ServiceScoped
    fun providenNotificationManager(@ApplicationContext context: Context) =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
}

