package com.ksv.pillsnumberone.presentation

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ksv.pillsnumberone.R
import com.ksv.pillsnumberone.databinding.MedicineViewBinding
import com.ksv.pillsnumberone.entity.MedicineItem
import java.util.Collections

class MedicineCardAdapter(
    private val onTimeClick: (Int) -> Unit,
    private val onMoreClick: (Int, View) -> Unit,
    private val onDataChanged: (List<MedicineItem>) -> Unit
) : RecyclerView.Adapter<MedicineCardAdapter.MedicineViewHolder>() {
    private var medicineList: MutableList<MedicineItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineViewHolder {
        val holder = MedicineViewHolder(
            MedicineViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

        holder.binding.checkFinish.setOnCheckedChangeListener { _, isChecked ->
            setFinishedState(isChecked, holder.binding)
            medicineList[holder.adapterPosition].finished = isChecked
            onDataChanged(medicineList)
        }
        holder.binding.time.setOnClickListener {
            val isChecked = holder.binding.checkFinish.isChecked
            val isEditable = medicineList[holder.adapterPosition].editable
            if (!isChecked && !isEditable) {
                onTimeClick(holder.adapterPosition)
            }
        }
        holder.binding.moreButton.setOnClickListener {
            onMoreClick(holder.adapterPosition, holder.binding.moreButton)
        }

        return holder
    }

    override fun getItemCount(): Int = medicineList.size

    override fun onBindViewHolder(holder: MedicineViewHolder, position: Int) {
        val medicineItem = medicineList.getOrNull(position)
        with(holder.binding) {
            medicineItem?.let {
                title.text = medicineItem.title
                recipe.text = medicineItem.recipe
                time.text = medicineItem.time
                checkFinish.isChecked = medicineItem.finished
                setFinishedState(medicineItem.finished, holder.binding)
                checkFinish.isClickable = !medicineItem.editable
                moreButton.visibility = if (medicineItem.editable) View.VISIBLE else View.GONE
            }
        }
    }

    fun setData(medicineList: List<MedicineItem>) {
        this.medicineList = medicineList.toMutableList()
        onDataChanged(medicineList)
        notifyItemRangeInserted(0, medicineList.size)
    }

    fun getItemAt(index: Int): MedicineItem? {
        return if (index in 0..medicineList.lastIndex)
            medicineList.get(index)
        else null
    }

    fun addItem(medicineItem: MedicineItem) {
        medicineList.add(medicineItem)
        onDataChanged(medicineList)
        notifyItemInserted(medicineList.lastIndex)
    }

    fun removeItemAt(index: Int) {
        if (index in 0..medicineList.lastIndex) {
            medicineList.removeAt(index)
            onDataChanged(medicineList)
            notifyItemRemoved(index)
            notifyItemRangeChanged(index, itemCount - index)
        }
    }

    fun updateItemAt(index: Int, medicine: MedicineItem) {
        if (index in 0..medicineList.lastIndex) {
            medicineList[index] = medicine
            onDataChanged(medicineList)
            notifyItemChanged(index)
        }
    }

    fun isFinished(index: Int): Boolean {
        return if (index in 0..medicineList.lastIndex) {
            medicineList[index].finished
        } else false
    }

    fun finishedChangeItemAt(index: Int) {
        if (index in 0..medicineList.lastIndex) {
            val isChecked = medicineList[index].finished
            medicineList[index].finished = !isChecked
            onDataChanged(medicineList)
            notifyItemChanged(index)
        }
    }

    fun setTime(medicineItem: MedicineItem, time: String) {
        setTimeAt(medicineList.indexOf(medicineItem), time)
    }

    fun setTimeAt(index: Int, time: String) {
        if (index in 0 until medicineList.size) {
            medicineList[index].time = time
            onDataChanged(medicineList)
            notifyItemChanged(index)
        }
    }

    fun getAllItems(): List<MedicineItem> {
        return medicineList
    }

    fun resetAllItems() {
        medicineList.forEach { medicine ->
            medicine.time = "0:00"
            medicine.finished = false
        }
        onDataChanged(medicineList)
        notifyItemRangeChanged(0, medicineList.size)
    }

    fun moveUp(index: Int) {
        if (index in 1 until medicineList.size) {
            Collections.swap(medicineList, index - 1, index)
            onDataChanged(medicineList)
            notifyItemMoved(index, index - 1)
        }
    }

    fun moveDown(index: Int) {
        if (index in 0 until medicineList.size - 1) {
            Collections.swap(medicineList, index, index + 1)
            onDataChanged(medicineList)
            notifyItemMoved(index, index + 1)
        }
    }

    fun switchEditModeForAll(edit: Boolean) {
        medicineList.forEach { item ->
            item.editable = edit
        }
        onDataChanged(medicineList)
        notifyItemRangeChanged(0, medicineList.size)
    }


    private fun setFinishedState(isFinished: Boolean, binding: MedicineViewBinding) {
        if (isFinished) {
            binding.cardLayout.setBackgroundColor(binding.root.context.getColor(R.color.medicine_passive))
        } else {
            binding.cardLayout.setBackgroundColor(binding.root.context.getColor(R.color.medicine_active))
        }
    }


    class MedicineViewHolder(val binding: MedicineViewBinding) :
        RecyclerView.ViewHolder(binding.root)
}