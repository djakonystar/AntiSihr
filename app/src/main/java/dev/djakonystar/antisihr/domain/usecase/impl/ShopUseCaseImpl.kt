package dev.djakonystar.antisihr.domain.usecase.impl

import dev.djakonystar.antisihr.data.models.GenericResponse
import dev.djakonystar.antisihr.data.models.ResultData
import dev.djakonystar.antisihr.data.models.shop.SellerData
import dev.djakonystar.antisihr.data.room.LocalRoomDatabase
import dev.djakonystar.antisihr.data.room.entity.ShopItemBookmarked
import dev.djakonystar.antisihr.domain.repository.ShopRepository
import dev.djakonystar.antisihr.domain.usecase.ShopUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShopUseCaseImpl @Inject constructor(
    private val repo: ShopRepository, private val db: LocalRoomDatabase
) : ShopUseCase {
    override suspend fun getAllProducts() = flow {
        repo.getAllProducts().collect {
            if (it is ResultData.Success) {
                val list = mutableListOf<ShopItemBookmarked>()
                it.data.result?.forEach {
                    list.add(
                        ShopItemBookmarked(
                            it.id,
                            it.name,
                            it.description,
                            it.price,
                            it.image,
                            it.weight?:"",
                            db.goodsDao().isExistsInBookmarkeds(
                                it.id, it.name, it.image
                            ),
                            it.seller?.id,
                            it.seller?.name,
                            it.seller?.url
                        )
                    )
                }
                emit(ResultData.Success(list))
            }
        }
    }


    override suspend fun getAllProductsForSeller(id: Int) = flow {
        repo.getAllProductsForSeller(id).collect {
            if (it is ResultData.Success) {
                val list = mutableListOf<ShopItemBookmarked>()
                it.data.result?.forEach {
                    list.add(
                        ShopItemBookmarked(
                            it.id,
                            it.name,
                            it.description,
                            it.price,
                            it.image,
                            it.weight ?: "",
                            db.goodsDao().isExistsInBookmarkeds(
                                it.id, it.name, it.image
                            ),
                            it.seller?.id,
                            it.seller?.name,
                            it.seller?.url
                        )
                    )
                }
                emit(ResultData.Success(list))
            }
        }
    }

    override suspend fun getShopItem(id: Int) = repo.getShopItem(id)
    override suspend fun getSellers() = repo.getSellers()

    override suspend fun addProductToBookmarked(item: ShopItemBookmarked) =
        repo.addProductToBookmarked(item)

    override suspend fun deleteProductFromBookmarked(item: ShopItemBookmarked) =
        repo.deleteProductFromBookmarked(item)

    override suspend fun getBookmarkedProducts() = repo.getBookmarkedProducts()
}