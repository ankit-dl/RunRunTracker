package com.ank.runtracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.ank.runtracker.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(val repository: MainRepository) : ViewModel() {


}