package com.ksv.pillsnumberone.presentation

import android.util.Log
import android.view.LayoutInflater
import android.view.View
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
            DataItemViewHolder(binding) {
            fun bind(caption: DataItem.Caption) {
                binding.caption.text = caption.caption
            }

        }

        class PillItemViewHolder(override val binding: PillViewBinding) :
            DataItemViewHolder(binding) {
            fun bind(
                pill: DataItem.Pill,
                interaction: Interaction
            ) {
//                Log.d("ksvlog", "bind: $pill")
                binding.title.text = pill.title
//                binding.recipe.text = pill.recipe
                val recipe = "id: ${pill.id}, pos: ${pill.position} \n${pill.recipe}"
                binding.recipe.text = recipe
                binding.time.text = pill.time
                binding.root.setOnClickListener { interaction.onItemClick(pill) }
                binding.root.setOnLongClickListener {
                    interaction.onItemLongClick(pill)
                    true
                }
                binding.moveUpButton.setOnClickListener { interaction.onUpClick(pill) }
                binding.moveDownButton.setOnClickListener { interaction.onDownClick(pill) }
                binding.removeButton.setOnClickListener { interaction.onRemoveClick(pill) }
                binding.time.setOnClickListener { interaction.onTimeClick(pill) }
                binding.time.isClickable = true
                if (pill.finished) {
                    binding.time.isClickable = false
                    binding.mainLayout.setBackgroundColor(binding.root.context.getColor(R.color.medicine_passive))
                } else {
                    binding.mainLayout.setBackgroundColor(binding.root.context.getColor(R.color.medicine_active))
                }
                if (pill.editable){
                    binding.time.isClickable = false
                    binding.removeLayout.visibility = View.VISIBLE
                    binding.moveLayout.visibility = View.VISIBLE
                } else {
                    binding.removeLayout.visibility = View.GONE
                    binding.moveLayout.visibility = View.GONE
                }
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

    override fun onBindViewHolder(holder: DataItemViewHolder, position: Int) {
        when (holder) {
            is DataItemViewHolder.CaptionItemViewHolder -> {
                holder.bind(currentList[position] as DataItem.Caption)
            }

            is DataItemViewHolder.PillItemViewHolder -> {
                holder.bind(
                    currentList[position] as DataItem.Pill,
                    interaction
                )
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (currentList[position]) {
            is DataItem.Caption -> R.layout.caption_view
            is DataItem.Pill -> R.layout.pill_view
        }
    }

    class DiffUtilItemCallback : DiffUtil.ItemCallback<DataItem>() {
        override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean =
            if (oldItem::class == newItem::class) {
                when (oldItem) {
                    is DataItem.Pill -> {
                        oldItem.id == (newItem as DataItem.Pill).id
                    }

                    is DataItem.Caption -> {
                        oldItem.id == (newItem as DataItem.Caption).id
                    }
                }
            } else false

        override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean =
            oldItem == newItem
    }
}