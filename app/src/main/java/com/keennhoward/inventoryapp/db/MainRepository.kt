package com.keennhoward.inventoryapp.db

import com.keennhoward.inventoryapp.model.Product

class MainRepository(private val dao: ProductDAO) {

    val products = dao.getAllProducts()

    suspend fun insert(product:Product){
        dao.insert(product)
    }

    suspend fun update(product: Product){
        dao.update(product)
    }

    suspend fun delete(product: Product){
        dao.delete(product)
    }

    suspend fun deleteAll(){
        dao.clearProducts()
    }
}