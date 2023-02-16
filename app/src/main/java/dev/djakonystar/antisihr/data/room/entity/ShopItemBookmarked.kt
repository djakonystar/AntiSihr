package dev.djakonystar.antisihr.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "bookmarked_shop_items")
data class ShopItemBookmarked(
    @PrimaryKey(autoGenerate = false) val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val image: String,
    val weight: Double?,
    var isFavourite: Boolean,
    val sellerId:Int,
    val sellerName:String,
    val sellerUrl:String
)