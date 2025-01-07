package com.ksv.pillsnumberone.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ksv.pillsnumberone.data.PillsDao
import com.ksv.pillsnumberone.model.DataItemService2

class TestViewModelFactory2(private val pillsDao: PillsDao): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(TestViewModel2::class.java)) {
            return TestViewModel2(pillsDao) as T
        } else
            throw IllegalArgumentException("Unknown ViewModel class")
    }
}