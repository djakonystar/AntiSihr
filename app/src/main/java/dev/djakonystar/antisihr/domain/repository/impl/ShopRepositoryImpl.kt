package dev.djakonystar.antisihr.domain.repository.impl

import android.util.Log
import dev.djakonystar.antisihr.data.models.GenericResponse
import dev.djakonystar.antisihr.data.models.ResultData
import dev.djakonystar.antisihr.data.models.shop.SellerData
import dev.djakonystar.antisihr.data.remote.AntiSihrApi
import dev.djakonystar.antisihr.data.room.LocalRoomDatabase
import dev.djakonystar.antisihr.data.room.entity.ArticlesBookmarked
import dev.djakonystar.antisihr.data.room.entity.ShopItemBookmarked
import dev.djakonystar.antisihr.domain.repository.ShopRepository
import dev.djakonystar.antisihr.domain.repository.TestRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ShopRepositoryImpl @Inject constructor(
    private val antiSihrApi: AntiSihrApi,
    private val db: LocalRoomDatabase
) : ShopRepository {

    override suspend fun addProductToBookmarked(item: ShopItemBookmarked) = flow {
        emit(ResultData.Success(db.goodsDao().insertGoodToBookmarks(item)))
    }.catch {
        Log.d("TTTT", "INSERTING ERROR")
    }.flowOn(Dispatchers.IO)

    override suspend fun deleteProductFromBookmarked(item: ShopItemBookmarked) = flow {
        emit(ResultData.Success(db.goodsDao().deleteGoodFromBookmarks(item)))
    }.catch {
        Log.d("TTTT", "DELETING ERROR")
    }.flowOn(Dispatchers.IO)

    override suspend fun getBookmarkedProducts() = flow {
        emit(ResultData.Success(db.goodsDao().getBookmarkedGoods()))
    }.catch {
        Log.d("TTTT", "GETTING BOOKMARKEDs ERROR")
    }.flowOn(Dispatchers.IO)

    override suspend fun getAllProducts() = flow {
        val request = antiSihrApi.getAllProducts()
        if (request.isSuccessful) {
            emit(ResultData.Success(request.body()!!))
        } else {
            emit(ResultData.Message(request.message()))
        }
    }.catch {
        emit(ResultData.Error(it))
    }.flowOn(Dispatchers.IO)

    override suspend fun getAllProductsForSeller(id: Int) = flow {
        val request = antiSihrApi.getAllProductsForSeller(id)
        if (request.isSuccessful) {
            emit(ResultData.Success(request.body()!!))
        } else {
            emit(ResultData.Message(request.message()))
        }
    }.catch {
        emit(ResultData.Error(it))
    }.flowOn(Dispatchers.IO)

    override suspend fun getShopItem(id: Int) = flow {
        val request = antiSihrApi.getProductInfo(id)
        if (request.isSuccessful) {
            emit(ResultData.Success(request.body()!!))
        } else {
            emit(ResultData.Message(request.message()))
        }
    }.catch {
        emit(ResultData.Error(it))
    }.flowOn(Dispatchers.IO)

    override suspend fun getSellers() = flow {
        val request = antiSihrApi.getAllSellers()
        if (request.isSuccessful) {
            emit(ResultData.Success(request.body()!!))
        } else {
            emit(ResultData.Message(request.message()))
        }
    }.catch {
        emit(ResultData.Error(it))
    }.flowOn(Dispatchers.IO)
}