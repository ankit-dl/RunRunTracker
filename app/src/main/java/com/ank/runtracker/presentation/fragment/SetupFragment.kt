package com.ank.runtracker.presentation.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ank.runtracker.R
import com.ank.runtracker.databinding.FragmentSettingBinding
import com.ank.runtracker.databinding.FragmentSetupBinding

class SetupFragment : Fragment(R.layout.fragment_setup) {

   lateinit  var binding:FragmentSetupBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bind=FragmentSetupBinding.bind(view)
        binding=bind
       binding.tvContinue.setOnClickListener {
           findNavController().navigate(R.id.action_setupFragment_to_runFragment)
       }


    }
}