package com.keennhoward.inventoryapp.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keennhoward.inventoryapp.db.MainRepository
import com.keennhoward.inventoryapp.model.Product
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainViewModel(private val repository: MainRepository): ViewModel(){

    val products = repository.products

    fun insert(product: Product): Job = viewModelScope.launch {
        repository.insert(product)
    }

    fun update(product: Product): Job = viewModelScope.launch {
        repository.update(product)
    }

    fun delete(product: Product): Job = viewModelScope.launch {
        repository.delete(product)
    }

    fun deleteAll(): Job = viewModelScope.launch {
        repository.deleteAll()
    }
}