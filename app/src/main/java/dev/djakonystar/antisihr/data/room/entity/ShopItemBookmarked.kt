package dev.djakonystar.antisihr.data.room.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.djakonystar.antisihr.data.models.shop.SellerData


@Entity(tableName = "bookmarked_shop_items")
data class ShopItemBookmarked(
    @PrimaryKey(autoGenerate = false) val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val image: String,
    val weight: String,
    val isFavourite: Boolean,
    val sellerId:Int,
    val sellerName:String,
    val sellerUrl:String
)