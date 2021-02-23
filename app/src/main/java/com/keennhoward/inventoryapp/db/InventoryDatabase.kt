package com.keennhoward.inventoryapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.keennhoward.inventoryapp.model.Product

@Database(entities = [Product::class], version = 1, exportSchema = false)
abstract class InventoryDatabase: RoomDatabase() {

    abstract fun productDao():ProductDAO

    companion object{

        @Volatile
        private var INSTANCE: InventoryDatabase? = null

        fun getInstance(context : Context): InventoryDatabase{
            if (INSTANCE!=null) return INSTANCE!!

            synchronized(this){
                INSTANCE = Room
                    .databaseBuilder(context,InventoryDatabase::class.java, "INVENTORY_DATABASE")
                    .fallbackToDestructiveMigration()
                    .build()
                return INSTANCE!!
            }
        }
    }

}