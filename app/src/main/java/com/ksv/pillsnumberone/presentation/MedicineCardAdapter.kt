package com.ksv.pillsnumberone.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ksv.pillsnumberone.MyApp
import com.ksv.pillsnumberone.R
import com.ksv.pillsnumberone.databinding.MedicineViewBinding
import com.ksv.pillsnumberone.entity.MedicineItem
import java.util.Collections

class MedicineCardAdapter(
    private val onTimeClick: (Int) -> Unit,
    private val onItemLongClick: () -> Unit,
    private val onItemClick: (Int) -> Unit,
    private val onDataChanged: (List<MedicineItem>) -> Unit
) : RecyclerView.Adapter<MedicineCardAdapter.MedicineViewHolder>() {

    private var medicineList: MutableList<MedicineItem> = mutableListOf()
    private var editableItemPosition: Int = -1
    private var permissionToEdit = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineViewHolder {
        val holder = MedicineViewHolder(
            MedicineViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

        holder.binding.time.setOnClickListener {
            val isChecked = medicineList[holder.adapterPosition].finished
            val isEditable = medicineList[holder.adapterPosition].editable
            if (!isChecked && !isEditable) {
                onTimeClick(holder.adapterPosition)
            }
        }
        holder.binding.removeButton.setOnClickListener {
            removeItemAt(holder.adapterPosition)
        }

        holder.binding.cardLayout.setOnLongClickListener {
            if (editableItemPosition == -1 && permissionToEdit) {
                setOnEditModeAt(holder.adapterPosition)
                onItemLongClick()
            }
            return@setOnLongClickListener true
        }
        holder.binding.moveUpButton.setOnClickListener {
            moveUp(holder.adapterPosition)
        }
        holder.binding.moveDownButton.setOnClickListener {
            moveDown(holder.adapterPosition)
        }

        holder.binding.cardLayout.setOnClickListener {
            if(permissionToEdit){
                if(holder.adapterPosition == editableItemPosition){
                    onItemClick(holder.adapterPosition)
                }
                if(editableItemPosition == -1){
                    val setFinished = !medicineList[holder.adapterPosition].finished
                    holder.binding.mainLayout.setBackgroundColor(getBackgroundColor(setFinished))
                    medicineList[holder.adapterPosition].finished = setFinished
                    onDataChanged(medicineList)
                }
            }
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
                time.visibility = if (medicineItem.editable) View.GONE else View.VISIBLE
                mainLayout.setBackgroundColor(getBackgroundColor(medicineItem.finished))
                removeButton.visibility = if (medicineItem.editable) View.VISIBLE else View.GONE
                moveLayout.visibility = if (medicineItem.editable) View.VISIBLE else View.GONE
                if (medicineItem.editable) editableItemPosition = holder.adapterPosition
            }
        }
    }


    //      CREATE
    fun setData(medicineList: List<MedicineItem>) {
        this.medicineList = medicineList.toMutableList()
        onDataChanged(medicineList)
        notifyItemRangeInserted(0, medicineList.size)
    }

    fun addItem(medicineItem: MedicineItem) {
        medicineList.add(medicineItem)
        onDataChanged(medicineList)
        notifyItemInserted(medicineList.lastIndex)
    }

    //      READ
    fun getItemAt(index: Int): MedicineItem? {
        return if (index in 0..medicineList.lastIndex)
            medicineList[index]
        else null
    }

    fun getAllItems(): List<MedicineItem> {
        return medicineList
    }

    //      UPDATE
    fun updateItemAt(index: Int, medicine: MedicineItem) {
        if (index in 0..medicineList.lastIndex) {
            medicineList[index] = medicine
            onDataChanged(medicineList)
            notifyItemChanged(index)
        }
    }

    fun setTimeAt(index: Int, time: String) {
        if (index in 0 until medicineList.size) {
            medicineList[index].time = time
            onDataChanged(medicineList)
            notifyItemChanged(index)
        }
    }

    fun resetAllItems() {
        medicineList.forEach { medicine ->
            medicine.time = "0:00"
            medicine.finished = false
        }
        onDataChanged(medicineList)
        notifyItemRangeChanged(0, medicineList.size)
    }

    private fun moveUp(index: Int) {
        if (index in 1 until medicineList.size) {
            Collections.swap(medicineList, index - 1, index)
            onDataChanged(medicineList)
            notifyItemMoved(index, index - 1)
            editableItemPosition = index - 1
        }
    }

    private fun moveDown(index: Int) {
        if (index in 0 until medicineList.size - 1) {
            Collections.swap(medicineList, index, index + 1)
            onDataChanged(medicineList)
            notifyItemMoved(index, index + 1)
            editableItemPosition = index + 1
        }
    }

    private fun setOnEditModeAt(position: Int) {
        if (position in 0..medicineList.lastIndex) {
            medicineList[position].editable = true
            onDataChanged(medicineList)
            notifyItemChanged(position, medicineList.size)
            editableItemPosition = position
        }
    }

    fun finishEdition() {
        permissionToEdit = true
        if (editableItemPosition >= 0) {
            medicineList[editableItemPosition].editable = false
            onDataChanged(medicineList)
            notifyItemChanged(editableItemPosition, medicineList.size)
            editableItemPosition = -1
        }
    }

    //      DELETE
    private fun removeItemAt(index: Int) {
        if (index in 0..medicineList.lastIndex) {
            medicineList.removeAt(index)
            notifyItemRemoved(index)
            notifyItemRangeChanged(index, itemCount - index)
            editableItemPosition = -1
            onDataChanged(medicineList)
        }
    }

    //      SUPPORT
    private fun getBackgroundColor(isFinished:Boolean): Int {
        return if(isFinished) MyApp.applicationContext.getColor(R.color.medicine_passive)
        else MyApp.applicationContext.getColor(R.color.medicine_active)
    }

    fun denyPermissionToEdit(){
        permissionToEdit = false
    }


    class MedicineViewHolder(val binding: MedicineViewBinding) :
        RecyclerView.ViewHolder(binding.root)
}