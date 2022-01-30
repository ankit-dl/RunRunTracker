package com.ank.runtracker.presentation.fragment



import android.Manifest
import android.os.Bundle

import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ank.runtracker.R

import com.ank.runtracker.common.TrackingUtils
import com.ank.runtracker.databinding.FragmentRunBinding

import com.ank.runtracker.presentation.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog


@AndroidEntryPoint
class RunFragment : Fragment(R.layout.fragment_run) {
    private lateinit var binding: FragmentRunBinding
    private  val TAG = "RunFragment"
    val viewModel: MainViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRunBinding.bind(view)

        requestPermissions()
        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_runFragment_to_trackerFragment)
        }
    }

    private fun requestPermissions() {
        if (TrackingUtils.hasLocationPermissions(requireContext())) {
            return
        }


    locationPermissionRequest().launch(
            TrackingUtils.getLocationPermission(),
        )
    }

    private fun locationPermissionRequest() = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->

        var isAllPermissionGranted=true
        permissions.entries.forEach {
            if (!it.value) isAllPermissionGranted=false
        }
        if (!isAllPermissionGranted)  AppSettingsDialog.Builder(this).build().show()



    }




}