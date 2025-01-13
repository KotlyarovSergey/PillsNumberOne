package com.ksv.pillsnumberone.presentation.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.ksv.pillsnumberone.entity.DataItem
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.ksv.pillsnumberone.R
import com.ksv.pillsnumberone.databinding.CaptionViewBinding
import com.ksv.pillsnumberone.databinding.PillViewBinding
import com.ksv.pillsnumberone.entity.Interaction


class DataListAdapter(private val interaction: Interaction) :
    ListAdapter<DataItem, DataListAdapter.DataItemViewHolder>(DiffUtilItemCallback()) {

    sealed class DataItemViewHolder(open val binding: ViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        class CaptionItemViewHolder(override val binding: CaptionViewBinding) :
            DataItemViewHolder(binding)

        class PillItemViewHolder(override val binding: PillViewBinding) :
            DataItemViewHolder(binding)

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

    override fun onBindViewHolder(holder: DataItemViewHolder, position: Int) {
        when (holder) {
            is DataItemViewHolder.CaptionItemViewHolder -> {
                holder.binding.periodCaption = currentList[position] as DataItem.PeriodCaption
            }

            is DataItemViewHolder.PillItemViewHolder -> {
                holder.binding.pill = currentList[position] as DataItem.Pill
                holder.binding.interaction = interaction
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (currentList[position]) {
            is DataItem.PeriodCaption -> R.layout.caption_view
            is DataItem.Pill -> R.layout.pill_view
        }
    }

    class DiffUtilItemCallback : DiffUtil.ItemCallback<DataItem>() {
        override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean =
            if (oldItem::class == newItem::class) {
                when (oldItem) {
                    is DataItem.Pill -> oldItem.id == (newItem as DataItem.Pill).id
                    is DataItem.PeriodCaption -> oldItem.id == (newItem as DataItem.PeriodCaption).id
                }
            } else false

        override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean =
            oldItem == newItem
    }

}