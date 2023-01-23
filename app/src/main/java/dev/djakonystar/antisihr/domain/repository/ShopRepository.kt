package dev.djakonystar.antisihr.domain.repository

import dev.djakonystar.antisihr.data.models.*
import dev.djakonystar.antisihr.data.models.shop.SellerData
import dev.djakonystar.antisihr.data.models.shop.ShopItemData
import dev.djakonystar.antisihr.data.room.entity.ArticlesBookmarked
import dev.djakonystar.antisihr.data.room.entity.ShopItemBookmarked
import kotlinx.coroutines.flow.Flow

interface ShopRepository {
    suspend fun getBookmarkedProducts(): Flow<ResultData<List<ShopItemBookmarked>>>

    suspend fun getAllProducts(): Flow<ResultData<GenericResponse<List<ShopItemData>>>>

    suspend fun getAllProductsForSeller(id:Int): Flow<ResultData<GenericResponse<List<ShopItemData>>>>

    suspend fun getShopItem(id: Int): Flow<ResultData<GenericResponse<List<ShopItemData>>>>

    suspend fun getSellers(): Flow<ResultData<GenericResponse<List<SellerData>>>>

    suspend fun addProductToBookmarked(item: ShopItemBookmarked): Flow<ResultData<Unit>>
    suspend fun deleteProductFromBookmarked(item: ShopItemBookmarked): Flow<ResultData<Unit>>
}