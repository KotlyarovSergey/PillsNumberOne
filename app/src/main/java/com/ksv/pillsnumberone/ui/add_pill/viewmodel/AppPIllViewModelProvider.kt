package com.ksv.pillsnumberone.ui.add_pill.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ksv.pillsnumberone.model.PillsService

class AppPIllViewModelProvider(private val pillsService: PillsService): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(AddPillViewModel::class.java)){
            return AddPillViewModel(pillsService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}