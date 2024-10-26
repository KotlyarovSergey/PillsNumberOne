package com.ksv.pillsnumberone

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.timepicker.MaterialTimePicker
import com.ksv.pillsnumberone.databinding.MedicineViewBinding
import com.ksv.pillsnumberone.entity.MedicineItem

class MedicineCardAdapter(
    private val onClick: (MedicineItem) -> Unit
) : RecyclerView.Adapter<MedicineCardAdapter.MedicineViewHolder>() {
    private var medicineList: MutableList<MedicineItem> = mutableListOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineViewHolder {
        return MedicineViewHolder(
            MedicineViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = medicineList.size

    override fun onBindViewHolder(holder: MedicineViewHolder, position: Int) {
        val medicineItem = medicineList.getOrNull(position)
        with(holder.binding) {
            medicineItem?.let {
                title.text = medicineItem.title
                recipe.text = medicineItem.recipe
                time.text = medicineItem.time
                val isChecked = medicineItem.finished
                checkFinish.isChecked = isChecked
                if (isChecked) {
                    cardLayout.setBackgroundColor(root.context.getColor(R.color.medicine_passive))
                } else {
                    cardLayout.setBackgroundColor(root.context.getColor(R.color.medicine_active))
                }

                checkFinish.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        cardLayout.setBackgroundColor(root.context.getColor(R.color.medicine_passive))
                    } else {
                        cardLayout.setBackgroundColor(root.context.getColor(R.color.medicine_active))
                    }
                    medicineItem.finished = isChecked
                }
            }
        }
        holder.binding.time.setOnClickListener {
            medicineItem?.let(onClick)
        }
    }

    fun setData(medicineList: List<MedicineItem>) {
        this.medicineList = medicineList.toMutableList()
        notifyDataSetChanged()
    }

    fun addItem(medicineItem: MedicineItem) {
        medicineList.add(medicineItem)
        notifyDataSetChanged()
    }

    fun removeItemAt(index: Int) {
        if (index in 0 until medicineList.size) {
            medicineList.removeAt(index)
            notifyDataSetChanged()
        }
    }

    fun checkedChangeItemAt(index: Int) {
        if (index in 0 until medicineList.size) {
            val isChecked = medicineList[index].finished
            medicineList[index].finished = !isChecked
            notifyDataSetChanged()
        }
    }

    fun setTime(medicineItem: MedicineItem, time: String) {
        setTimeAt(medicineList.indexOf(medicineItem), time)
    }

    fun setTimeAt(index: Int, time: String) {
        if (index in 0 until medicineList.size) {
            medicineList[index].time = time
            notifyDataSetChanged()
        }
    }


    class MedicineViewHolder(val binding: MedicineViewBinding) :
        RecyclerView.ViewHolder(binding.root)
}