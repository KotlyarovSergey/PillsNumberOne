package com.ksv.pillsnumberone.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ksv.pillsnumberone.data.PillsDao
import com.ksv.pillsnumberone.model.DataItemService2

class TestViewModelFactory(private val service2: DataItemService2): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(TestViewModel::class.java)) {
            return TestViewModel(service2) as T
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