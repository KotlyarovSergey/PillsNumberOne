package com.ksv.pillsnumberone.presentation

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

typealias OnClick = (DataItem) -> Unit
//typealias OnClick = (Int) -> Unit

class DataListAdapter(
    private val onUpClick: OnClick,
    private val onDownClick: OnClick,
    private val onRemoveClick: OnClick
) :
    ListAdapter<DataItem, DataListAdapter.DataItemViewHolder>(DiffUtilItemCallback()) {

//    var data = listOf<DataItem>()
//        set(value) {
//            field = value
//            notifyItemRangeChanged(0, value.size)
//            Log.d("ksvlog", "Adapter.data: $data")
//        }

    sealed class DataItemViewHolder(open val binding: ViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        class PillItemViewHolder(override val binding: PillViewBinding) :
            DataItemViewHolder(binding) {
            fun bind(pill: DataItem.Pill, onUpClick: OnClick, onDownClick: OnClick, onRemoveClick: OnClick) {
//            fun bind(pill: DataItem.Pill, position: Int,  onUpClick: OnClick, onDownClick: OnClick, onRemoveClick: OnClick) {
                binding.time.text = pill.time
                binding.recipe.text = pill.recipe
                binding.title.text = pill.title
                binding.moveUpButton.setOnClickListener { onUpClick(pill) }
                binding.moveDownButton.setOnClickListener { onDownClick(pill) }
                binding.removeButton.setOnClickListener { onRemoveClick(pill) }
//                binding.moveUpButton.setOnClickListener { onUpClick(position) }
//                binding.moveDownButton.setOnClickListener { onDownClick(position) }
//                binding.removeButton.setOnClickListener { onRemoveClick(position) }
            }
        }

        class CaptionItemViewHolder(override val binding: CaptionViewBinding) :
            DataItemViewHolder(binding) {
            fun bind(caption: DataItem.Caption,  onUpClick: OnClick, onDownClick: OnClick, onRemoveClick: OnClick) {
//            fun bind(caption: DataItem.Caption, position: Int,  onUpClick: OnClick, onDownClick: OnClick, onRemoveClick: OnClick) {
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

//    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: DataItemViewHolder, position: Int) {
        when(holder){
            is DataItemViewHolder.CaptionItemViewHolder -> {
//                holder.bind(data[position] as DataItem.Caption, onUpClick, onDownClick, onRemoveClick)
                holder.bind(currentList[position] as DataItem.Caption, onUpClick, onDownClick, onRemoveClick)
//                holder.bind(currentList[position] as DataItem.Caption, position, onUpClick, onDownClick, onRemoveClick)
            }
            is DataItemViewHolder.PillItemViewHolder -> {
//                holder.bind(data[position] as DataItem.Pill, onUpClick, onDownClick, onRemoveClick)
                holder.bind(currentList[position] as DataItem.Pill, onUpClick, onDownClick, onRemoveClick)
//                holder.bind(currentList[position] as DataItem.Pill, position, onUpClick, onDownClick, onRemoveClick)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
//        return when(data[position]){
        return when(currentList[position]){
            is DataItem.Caption -> R.layout.caption_view
            is DataItem.Pill -> R.layout.pill_view
        }
    }

    class DiffUtilItemCallback: DiffUtil.ItemCallback<DataItem>(){
        override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean =
            oldItem.index == newItem.index

        override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean =
            oldItem == newItem

    }
}