package dev.djakonystar.antisihr.data.models.shop

data class ShopItemData(
    val id: Int,
    val name: String,
    val image: String,
    val price: Double,
    val seller: SellerData?,
    val weight: String?,
    val description: String
)
