package com.keennhoward.inventoryapp.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.versionedparcelable.ParcelField
import kotlinx.android.parcel.Parcelize
import java.sql.Date

@Entity(tableName = "Product")
@Parcelize
data class Product(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name ="product_id")
    var id:Int,

    @ColumnInfo(name = "name")
    var name:String,

    @ColumnInfo(name = "unit")
    var unit:String,

    @ColumnInfo(name = "price")
    var price:Double,

    @ColumnInfo(name = "date")
    var date:String,

    @ColumnInfo(name="qty")
    var qty:Int,

    @ColumnInfo(name = "available_inventory")
    var image:String

) :Parcelable
