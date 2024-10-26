package com.ksv.pillsnumberone

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.timepicker.MaterialTimePicker
import com.ksv.pillsnumberone.databinding.FragmentMainBinding
import com.ksv.pillsnumberone.entity.MedicineItem

class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val medicineCardAdapter =
        MedicineCardAdapter { medicineItem -> onTimeClick(medicineItem) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerMorning.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerMorning.adapter = medicineCardAdapter
        medicineCardAdapter.setData(
            listOf(AllPills.ERMITAL.medicine, AllPills.ALMAGEL.medicine, AllPills.BACKSET.medicine)
        )
        medicineCardAdapter.addItem(AllPills.SMECTA.medicine)
        medicineCardAdapter.removeItemAt(0)
//        medicineCardAdapter.checkedChangeItemAt(0)

    }

    private fun onTimeClick(medicine: MedicineItem) {
        if(!medicine.finished) {
            val timePicker = MaterialTimePicker.Builder()
                .setTitleText("Время поедания")
                .build().apply {
                    addOnPositiveButtonClickListener {
                        val hour = this.hour
                        var min = this.minute.toString()
                        if (this.minute < 10) min = "0$min"
                        val timeToDisplay = "$hour:$min"
                        medicineCardAdapter.setTime(medicine, timeToDisplay)
                    }
                }
            timePicker.show(parentFragmentManager, timePicker::class.java.name)
        }
    }

//    private fun setMorningPills() {
//        binding.morningAlmagel.setMedicine(AllPills.ALMAGEL.medicine)
//        setTimeListener(binding.morningAlmagel)
//        binding.morningBackset.setMedicine(AllPills.BACKSET.medicine)
//        setTimeListener(binding.morningBackset)
//        binding.morningErmital.apply {
//            setMedicine(AllPills.ERMITAL.medicine)
//            setTimeListener(this)
//        }
//
//    }

//    private fun setTimeListener(view: MedicineView){
//        view.timeClicked = {
//            val timePicker = MaterialTimePicker.Builder()
//                .setTitleText("Время поедания")
//                .build().apply {
//                    addOnPositiveButtonClickListener {
//                        val hour = this.hour
//                        var min = this.minute.toString()
//                        if (this.minute < 10) min = "0$min"
//                        val time = "$hour:$min"
//                        view.setTime(time)
//                    }
//                }
//            timePicker.show(parentFragmentManager, timePicker::class.java.name)
//        }
//    }

    companion object {
        enum class AllPills(val medicine: MedicineItem) {
            SMECTA(MedicineItem("Смекта", "За час до еды")),
            MEBEVETIN(MedicineItem("Мебиверин", "За 20 мин. до еды")),
            OMEPRAZOL(MedicineItem("Омепразол", "За 30 мин. до еды")),
            BACKSET(MedicineItem("Бак-Сет", "Во время еды")),
            ERMITAL(MedicineItem("Эрмиталь", "Во время еды")),
            LEVOFLOCSACIN(MedicineItem("Левофлоксацин", "После еды")),
            ALMAGEL(MedicineItem("Алмагель", "Через час после еды"))
        }

    }
}
