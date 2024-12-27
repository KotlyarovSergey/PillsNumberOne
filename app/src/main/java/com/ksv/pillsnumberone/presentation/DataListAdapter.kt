package com.ksv.pillsnumberone.presentation

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.ksv.pillsnumberone.entity.DataItem
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.ksv.pillsnumberone.R
import com.ksv.pillsnumberone.databinding.CaptionViewBinding
import com.ksv.pillsnumberone.databinding.PillViewBinding

class DataListAdapter : RecyclerView.Adapter<DataListAdapter.DataItemViewHolder>() {

    var data = listOf<DataItem>()
        set(value) {
            field = value
            notifyItemRangeChanged(0, value.size)
            Log.d("ksvlog", "Adapter.data: $data")
        }

    sealed class DataItemViewHolder(open val binding: ViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        class PillItemViewHolder(override val binding: PillViewBinding) :
            DataItemViewHolder(binding) {
            fun bind(pill: DataItem.Pill) {
                binding.time.text = pill.time
                binding.recipe.text = pill.recipe
                binding.title.text = pill.title
            }
        }

        class CaptionItemViewHolder(override val binding: CaptionViewBinding) :
            DataItemViewHolder(binding) {
            fun bind(caption: DataItem.Caption) {
                binding.caption.text = caption.caption
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataItemViewHolder {
        return when (viewType) {
            R.layout.pill_view -> {
                DataItemViewHolder.PillItemViewHolder(
                    PillViewBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            R.layout.caption_view -> {
                DataItemViewHolder.CaptionItemViewHolder(
                    CaptionViewBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            else -> throw IllegalArgumentException("Invalid ViewType Provided")
        }
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: DataItemViewHolder, position: Int) {
        when(holder){
            is DataItemViewHolder.CaptionItemViewHolder -> holder.bind(data[position] as DataItem.Caption)
            is DataItemViewHolder.PillItemViewHolder -> holder.bind(data[position] as DataItem.Pill)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(data[position]){
            is DataItem.Caption -> R.layout.caption_view
            is DataItem.Pill -> R.layout.pill_view
        }
    }
}