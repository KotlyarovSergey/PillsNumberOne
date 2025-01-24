package com.ksv.pillsnumberone.ui.home.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ksv.pillsnumberone.model.PillsService

class HomeViewModelFactory(private val service: PillsService): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(service) as T
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