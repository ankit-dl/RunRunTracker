package com.ank.runtracker.presentation.fragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ank.runtracker.R
import com.ank.runtracker.presentation.viewmodel.StatisticsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StatsticsFragment : Fragment(R.layout.fragment_stats) {
    val viewModel: StatisticsViewModel by viewModels()
}