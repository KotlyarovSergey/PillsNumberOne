package com.ksv.pillsnumberone.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ksv.pillsnumberone.model.DataItemService

class AppPIllViewModelProvider(private val dataItemService: DataItemService): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(AddPillViewModel::class.java)){
            return AddPillViewModel(dataItemService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}