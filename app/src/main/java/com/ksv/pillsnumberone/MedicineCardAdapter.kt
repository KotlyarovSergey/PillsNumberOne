package com.ksv.pillsnumberone

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ksv.pillsnumberone.databinding.MedicineViewBinding
import com.ksv.pillsnumberone.entity.MedicineItem

class MedicineCardAdapter : RecyclerView.Adapter<MedicineCardAdapter.MedicineViewHolder>() {
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
        with(holder.binding){
            medicineItem?.let {
                title.text = medicineItem.title
                recipe.text = medicineItem.recipe
                checkFinish.isChecked = medicineItem.finished
                time.text = medicineItem.time
            }
        }
    }

    fun setData(medicineList: List<MedicineItem>){
        this.medicineList = medicineList.toMutableList()
        notifyDataSetChanged()
    }

    fun addItem(medicineItem: MedicineItem){
        medicineList.add(medicineItem)
        notifyDataSetChanged()
    }




    //    class MedicineViewHolder(medicineView: MedicineView) : RecyclerView.ViewHolder(medicineView)
    class MedicineViewHolder(val binding: MedicineViewBinding) :
        RecyclerView.ViewHolder(binding.root)
}