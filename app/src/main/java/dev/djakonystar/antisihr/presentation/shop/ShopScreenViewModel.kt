package dev.djakonystar.antisihr.presentation.shop

import dev.djakonystar.antisihr.data.models.GenericResponse
import dev.djakonystar.antisihr.data.models.shop.SellerData
import dev.djakonystar.antisihr.data.models.shop.ShopItemData
import dev.djakonystar.antisihr.data.room.entity.ShopItemBookmarked
import kotlinx.coroutines.flow.Flow

interface ShopScreenViewModel {
    val getGoodsSuccessFlow: Flow<List<ShopItemBookmarked>>
    val deleteFromBookmarkedFlow: Flow<ShopItemBookmarked>
    val getBookmarkedGoodsSuccessFlow: Flow<List<ShopItemBookmarked>>
    val messageFlow: Flow<String>
    val errorFlow: Flow<Throwable>

    suspend fun getAllProducts()

    suspend fun getAllProductsOfSeller(id:Int)

    val getAllSellersSuccessFlow: Flow<GenericResponse<List<SellerData>>>
    suspend fun getAllSellers()




    suspend fun getAllBookmarkedProducts()

    suspend fun deleteFromBookmarked(item:ShopItemBookmarked)
    suspend fun addToBookmarked(item:ShopItemBookmarked)
}