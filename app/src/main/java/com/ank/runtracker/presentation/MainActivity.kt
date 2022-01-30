package com.ank.runtracker.presentation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.ank.runtracker.R
import com.ank.runtracker.common.Constants
import com.ank.runtracker.data.db.RunDao
import com.ank.runtracker.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var runDao: RunDao
    lateinit var binding: ActivityMainBinding
    lateinit var navhost: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupBottomBar()
        navigateToTrackingFragment(intent)
        navhost.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.setupFragment, R.id.trackerFragment -> binding.bottomNavigationView.visibility =
                    View.GONE

                else -> binding.bottomNavigationView.visibility = View.VISIBLE
            }

        }

    }

    private fun setupBottomBar() {
        binding.apply {
            setSupportActionBar(toolbar)
            navhost =
                (supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment).navController
            binding.bottomNavigationView.setupWithNavController(navhost)
        }

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToTrackingFragment(intent)

    }

    private fun navigateToTrackingFragment(intent: Intent?) {
        intent?.let {
            if (it.action == Constants.ACTION_SHOW_TRACKING_FRAGMENT) {
                navhost.navigate(R.id.action_global_tracking_fragment)
            }
        }
    }
}