package com.ksv.pillsnumberone.entity

interface Interaction {
    fun onRemoveClick(item: DataItem)
    fun onUpClick(item: DataItem)
    fun onDownClick(item: DataItem)
    fun onItemClick(item: DataItem)
    fun onItemLongClick(item: DataItem): Boolean
    fun onTimeClick(item: DataItem)
}