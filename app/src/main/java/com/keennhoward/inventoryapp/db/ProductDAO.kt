package com.keennhoward.inventoryapp.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.keennhoward.inventoryapp.model.Product

@Dao
interface ProductDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: Product)

    @Update
    suspend fun update(product: Product)

    @Delete
    suspend fun delete(product: Product)

    @Query("SELECT * FROM Product")
    fun getAllProducts(): LiveData<List<Product>>

    @Query("DELETE FROM Product")
    suspend fun clearProducts()
}