package com.ank.runtracker.presentation.fragment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ank.runtracker.R
import com.ank.runtracker.common.Constants
import com.ank.runtracker.common.TrackingUtils
import com.ank.runtracker.databinding.FragmentTrackerBinding
import com.ank.runtracker.presentation.service.TrackerService
import com.ank.runtracker.presentation.service.pathline
import com.ank.runtracker.presentation.viewmodel.MainViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrackerFragment : Fragment(R.layout.fragment_tracker) {

    private var menu: Menu?=null
    private var currentTimeInMillis = 0L
    val viewModel: MainViewModel by viewModels()
    private var gmap: GoogleMap? = null
    private var isTracking: Boolean = false
    private var polyPaths = mutableListOf<pathline>()
    lateinit var binding: FragmentTrackerBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTrackerBinding.bind(view)
        // binding = bind
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync {
            gmap = it
            addAllPolyLines()

        }
        subscribeToObserver()
        binding.btnToggleRun.setOnClickListener {
            toggleRun()
        }

    }

    private fun subscribeToObserver() {
        TrackerService.isTracking.observe(viewLifecycleOwner) {

            updateTracking(it)
        }
        TrackerService.paths.observe(viewLifecycleOwner) {
            polyPaths = it
            if (polyPaths.isNotEmpty()) {
                updatePolyLines()
                moveCameraToUser()
            }

        }
        TrackerService.timeRunInMilli.observe(viewLifecycleOwner) {
            currentTimeInMillis = it
            var formattedTime = TrackingUtils.getFormattedTime(currentTimeInMillis, true)
            binding.tvTimer.text = formattedTime

        }
    }

    private fun toggleRun() {
        if (!isTracking) {
            sendCommandService(Constants.START_RESUME_SERVICE)
            menu?.getItem(0)?.isVisible = true
        } else sendCommandService(Constants.PAUSE_SERVICE)
    }

    private fun updateTracking(isTracking: Boolean) {
        this.isTracking = isTracking
        if (isTracking) {
            binding.btnToggleRun.text = getString(R.string.stop)
            binding.btnFinishRun.visibility = View.GONE
        } else {
            binding.btnToggleRun.text = getString(R.string.start)
            binding.btnFinishRun.visibility = View.VISIBLE
            menu?.getItem(0)?.isVisible = true
        }

    }

    private fun addAllPolyLines() {
        for (polyline in polyPaths) {
            val polylines = PolylineOptions()
                .color(Color.RED)
                .width(8f)
                .addAll(polyline)
            gmap?.addPolyline(polylines)
        }
    }

    private fun moveCameraToUser() {
        if (polyPaths.isNotEmpty() && polyPaths.last().isNotEmpty()) {
            gmap?.animateCamera(CameraUpdateFactory.newLatLngZoom(polyPaths.last().last(), 15f))

        }
    }

    private fun updatePolyLines() {
        if (polyPaths.last().size > 1) {
            val s = polyPaths.last()[polyPaths.last().size - 2]
            val d = polyPaths.last().last()
            val polylines = PolylineOptions()
                .color(Color.RED)
                .width(8f)
                .add(s)
                .add(d)
            gmap?.addPolyline(polylines)
        }
    }

    private fun sendCommandService(action: String) {
        Intent(requireContext(), TrackerService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar_tracking_menu, menu)
        this.menu = menu
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if(currentTimeInMillis > 0L) {
            this.menu?.getItem(0)?.isVisible = true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.miCancelTracking -> {
               // showCancelTrackingDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun showCancelTrackingDialog() {
        val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle("Cancel the Run?")
            .setMessage("Are you sure to cancel the current run and delete all its data?")
            .setIcon(R.drawable.ic_delete)
            .setPositiveButton("Yes") { _, _ ->
                stopRun()
            }
            .setNegativeButton("No") { dialogInterface, _ ->
                dialogInterface.cancel()
            }
            .create()
        dialog.show()
    }
    private fun stopRun() {
        sendCommandService(Constants.STOP_SERVICE)

        findNavController().navigate(R.id.action_trackerFragment_to_runFragment)
    }
    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
        sendCommandService(Constants.STOP_SERVICE)
    }


}