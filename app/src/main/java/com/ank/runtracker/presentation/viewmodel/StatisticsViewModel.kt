package com.ank.runtracker.presentation.viewmodel

import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import com.ank.runtracker.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(val repository: MainRepository) : ViewModel() {


}