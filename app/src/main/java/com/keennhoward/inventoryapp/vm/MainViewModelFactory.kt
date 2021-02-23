package com.keennhoward.inventoryapp.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.keennhoward.inventoryapp.db.MainRepository

class MainViewModelFactory(private val repository: MainRepository):ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MainViewModel::class.java)){
            return MainViewModel(repository = repository) as T
        }
        throw IllegalArgumentException("Unknown View Model Class")
    }
}