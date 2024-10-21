package com.ksv.pillsnumberone

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TimePicker
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColor
import androidx.core.graphics.toColorLong
import com.google.android.material.timepicker.MaterialTimePicker
import com.ksv.pillsnumberone.databinding.MedicineViewBinding
import java.util.Calendar

class MedicineView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
): LinearLayout (context, attrs) {
    private val binding: MedicineViewBinding
    var timeClicked: (() -> Unit)? = null

    init {
        val inflatedView = inflate(context, R.layout.medicine_view, this)
        binding = MedicineViewBinding.bind(inflatedView)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        binding.main.setBackgroundColor(context.getColor(R.color.medicine_active))
        binding.check.setOnCheckedChangeListener { _, isChecked ->  checkClick(isChecked)}
        binding.time.setOnClickListener { timeClicked?.invoke() }
    }

    fun setTitle(title: String){
        binding.title.text = title
    }
    fun setReceipt(receipt: String) {
        binding.receipt.text = receipt
    }

    fun setTime(time: String){
        binding.time.text = time
    }

    private fun checkClick(isChecked: Boolean){
        if(isChecked){
            binding.main.setBackgroundColor(context.getColor(R.color.medicine_active))

        } else {
            binding.main.setBackgroundColor(context.getColor(R.color.medicine_passive))
        }

    }


}