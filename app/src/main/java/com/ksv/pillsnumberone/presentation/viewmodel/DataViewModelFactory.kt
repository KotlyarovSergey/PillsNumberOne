package com.ksv.pillsnumberone.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ksv.pillsnumberone.model.DataItemService

class DataViewModelFactory(private val service2: DataItemService): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(DataViewModel::class.java)) {
            return DataViewModel(service2) as T
        } else
            throw IllegalArgumentException("Unknown ViewModel class")
    }
}


//class TestViewModelFactory(private val pillsDao: PillsDao): ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if(modelClass.isAssignableFrom(TestViewModel::class.java)) {
//            return TestViewModel(pillsDao) as T
//        } else
//            throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}