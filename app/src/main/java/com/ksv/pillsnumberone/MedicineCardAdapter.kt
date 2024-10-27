package com.ksv.pillsnumberone

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ksv.pillsnumberone.databinding.MedicineViewBinding
import com.ksv.pillsnumberone.entity.MedicineItem

class MedicineCardAdapter(
    private val onTimeClick: (Int) -> Unit
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
                checkFinish.isChecked = medicineItem.finished
                setFinishedState(medicineItem.finished, holder.binding)

                checkFinish.setOnCheckedChangeListener { _, isChecked ->
                    setFinishedState(isChecked, holder.binding)
                    medicineItem.finished = isChecked
                }
            }
            time.setOnClickListener {
                if(!checkFinish.isChecked) onTimeClick(position)
            }
        }
    }

    fun setData(medicineList: List<MedicineItem>) {
        this.medicineList = medicineList.toMutableList()
//        notifyDataSetChanged()
//        notifyItemRangeChanged(0, medicineList.lastIndex)
        notifyItemRangeInserted(0, medicineList.lastIndex)
    }

    fun addItem(medicineItem: MedicineItem) {
        medicineList.add(medicineItem)
//        notifyDataSetChanged()
//        notifyItemChanged(medicineList.lastIndex)
        notifyItemInserted(medicineList.lastIndex)
    }

    fun removeItemAt(index: Int) {
        if (index in 0 until medicineList.size) {
            medicineList.removeAt(index)
//            notifyDataSetChanged()
            notifyItemRemoved(index)
        }
    }

    fun updateItemAt(index: Int, medicine: MedicineItem){
        if (index in 0 until medicineList.size) {
            medicineList[index] = medicine
            notifyItemChanged(index)
        }
    }

    fun isFinished(index: Int): Boolean{
        return if (index in 0 until medicineList.size) {
            medicineList[index].finished
        } else false
    }

    fun checkedChangeItemAt(index: Int) {
        if (index in 0 until medicineList.size) {
            val isChecked = medicineList[index].finished
            medicineList[index].finished = !isChecked
//            notifyDataSetChanged()
            notifyItemChanged(index)
        }
    }

    fun setTime(medicineItem: MedicineItem, time: String) {
        setTimeAt(medicineList.indexOf(medicineItem), time)
    }

    fun setTimeAt(index: Int, time: String) {
        if (index in 0 until medicineList.size) {
            medicineList[index].time = time
//            notifyDataSetChanged()
            notifyItemChanged(index)
        }
    }

    fun getItems(): List<MedicineItem>{
        return medicineList
    }

    fun resetAllItems(){
        medicineList.forEachIndexed { index, medicine ->
            medicine.time = "0:00"
            medicine.finished = false
            notifyItemChanged(index)
        }
    }

    private fun setFinishedState(isFinished: Boolean, binding: MedicineViewBinding){
        if (isFinished) {
            binding.cardLayout.setBackgroundColor(binding.root.context.getColor(R.color.medicine_passive))
        } else {
            binding.cardLayout.setBackgroundColor(binding.root.context.getColor(R.color.medicine_active))
        }
    }


    class MedicineViewHolder(val binding: MedicineViewBinding) :
        RecyclerView.ViewHolder(binding.root)
}