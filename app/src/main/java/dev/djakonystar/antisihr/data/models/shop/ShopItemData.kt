package dev.djakonystar.antisihr.data.models.shop

data class ShopItemData(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val seller: SellerData?,
    val image: String,
    val weight: String?
)
