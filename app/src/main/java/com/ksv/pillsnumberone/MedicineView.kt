package com.ksv.pillsnumberone

import android.content.Context
import android.util.AttributeSet
import androidx.cardview.widget.CardView
import com.ksv.pillsnumberone.databinding.MedicineViewBinding
import com.ksv.pillsnumberone.entity.MedicineItem

class MedicineView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
): CardView (context, attrs) {
//    private val binding: MedicineViewBinding
//    var timeClicked: (() -> Unit)? = null
//
//    init {
//        val inflatedView = inflate(context, R.layout.medicine_view, this)
//        binding = MedicineViewBinding.bind(inflatedView)
//    }
//
//    override fun onAttachedToWindow() {
//        super.onAttachedToWindow()
//        binding.cardLayout.setBackgroundColor(context.getColor(R.color.medicine_active))
//        binding.checkFinish.setOnCheckedChangeListener { _, isChecked ->  checkClick(isChecked)}
//        binding.time.setOnClickListener { timeClicked?.invoke() }
//    }
//
//    fun setMedicine(medicine: MedicineItem){
//        binding.title.text = medicine.title
//        binding.recipe.text = medicine.recipe
//        binding.checkFinish.isChecked = medicine.finished
//        binding.time.text = medicine.time
//    }
//    fun setTitle(title: String){
//        binding.title.text = title
//    }
//    fun setReceipt(receipt: String) {
//        binding.recipe.text = receipt
//    }
//
//    fun setTime(time: String){
//        binding.time.text = time
//    }
//
//    private fun checkClick(isChecked: Boolean){
//        if(isChecked){
//            binding.cardLayout.setBackgroundColor(context.getColor(R.color.medicine_passive))
//        } else {
//            binding.cardLayout.setBackgroundColor(context.getColor(R.color.medicine_active))
//        }
//    }


}