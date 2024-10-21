package com.ksv.pillsnumberone

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.timepicker.MaterialTimePicker
import com.ksv.pillsnumberone.databinding.FragmentMainBinding
import java.util.Calendar

class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.medicineOne.setTitle("Омепразол")
        binding.medicineOne.setReceipt("за 30 мин. до еды")
        binding.medicineOne.timeClicked = {
            val timePicker = MaterialTimePicker.Builder()
                .setTitleText("Время поедания")
                .build().apply {
                    addOnPositiveButtonClickListener {
                        val hour = this.hour
                        var min = this.minute.toString()
                        if (this.minute < 10) min = "0$min"
                        val time = "$hour:$min"
                        binding.medicineOne.setTime(time)
                    }
                }
            timePicker.show(parentFragmentManager, timePicker::class.java.name)
        }
    }
}
