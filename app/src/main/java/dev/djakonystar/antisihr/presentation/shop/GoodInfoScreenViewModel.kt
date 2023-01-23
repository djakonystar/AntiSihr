package dev.djakonystar.antisihr.presentation.shop

import dev.djakonystar.antisihr.data.models.GenericResponse
import dev.djakonystar.antisihr.data.models.shop.ShopItemData
import kotlinx.coroutines.flow.Flow

interface GoodInfoScreenViewModel {
    val getProductInfoSuccesFlow: Flow<GenericResponse<List<ShopItemData>>>
    val messageFlow:Flow<String>
    val errorFlow:Flow<Throwable>


    suspend fun getProductInfo(id:Int)
}