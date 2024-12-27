package com.ksv.pillsnumberone.entity

sealed class DataItem(
    var index: Int
) {
    class Caption(
        indexd: Int,
        val caption: String,
        val period: Period
    ): DataItem(indexd){
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as Caption
            if(index != other.index) return false
            if(caption != other.caption) return false
            if(period != other.period) return false
            return true
        }
        override fun hashCode(): Int {
            var result = index.hashCode()
            result = 31 * result + caption.hashCode()
            result = 31 * result + period.hashCode()
            return result
        }
    }

    class Pill(
        index: Int,
        val title: String,
        val recipe: String,
        val period: Period,
        val position: Int,
        val time: String = "0:00",
        val finished: Boolean = false,
        val editable: Boolean = false,
    ): DataItem(index){
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as Pill
            if (index != other.index) return false
            if (title != other.title) return false
            if (recipe != other.recipe) return false
            if (period != other.period) return false
            if (position != other.position) return false
            if (time != other.time) return false
            if (finished != other.finished) return false
            if (editable != other.editable) return false

            return true
        }

        override fun hashCode(): Int {
            var result = index.hashCode()
            result = 31 * result + title.hashCode()
            result = 31 * result + recipe.hashCode()
            result = 31 * result + period.hashCode()
            result = 31 * result + position
            result = 31 * result + time.hashCode()
            result = 31 * result + finished.hashCode()
            result = 31 * result + editable.hashCode()
            return result
        }
    }
}