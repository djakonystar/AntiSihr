package dev.djakonystar.antisihr.presentation.shop.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.djakonystar.antisihr.data.models.GenericResponse
import dev.djakonystar.antisihr.data.models.ResultData
import dev.djakonystar.antisihr.data.models.shop.SellerData
import dev.djakonystar.antisihr.data.models.shop.ShopItemData
import dev.djakonystar.antisihr.data.room.entity.ShopItemBookmarked
import dev.djakonystar.antisihr.domain.usecase.ShopUseCase
import dev.djakonystar.antisihr.presentation.shop.ShopScreenViewModel
import dev.djakonystar.antisihr.utils.showBottomNavigationView
import dev.djakonystar.antisihr.utils.visibilityOfLoadingAnimationView
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShopScreenViewModelImpl @Inject constructor(
    private val useCase: ShopUseCase
) : ShopScreenViewModel, ViewModel() {
    override val getGoodsSuccessFlow = MutableSharedFlow<List<ShopItemBookmarked>>()
    override val getBookmarkedGoodsSuccessFlow = MutableSharedFlow<List<ShopItemBookmarked>>()
    override val messageFlow = MutableSharedFlow<String>()
    override val errorFlow = MutableSharedFlow<Throwable>()

    override suspend fun getAllProducts() {
        useCase.getAllProducts().onEach {
            when (it) {
                is ResultData.Success -> {
                    getGoodsSuccessFlow.emit(it.data)
                }
                is ResultData.Message -> {
                    messageFlow.emit(it.message)
                }
                is ResultData.Error -> {
                    errorFlow.emit(it.error)
                }
            }
        }.launchIn(viewModelScope)
    }

    init {
        viewModelScope.launch {
            getAllProducts()
            visibilityOfLoadingAnimationView.emit(true)
        }
    }

    override val getAllSellersSuccessFlow = MutableSharedFlow<GenericResponse<List<SellerData>>>()

    override suspend fun getAllSellers() {
        useCase.getSellers().onEach {
            when (it) {
                is ResultData.Success -> {
                    getAllSellersSuccessFlow.emit(it.data)
                }
                is ResultData.Message -> {
                    messageFlow.emit(it.message)
                }
                is ResultData.Error -> {
                    errorFlow.emit(it.error)
                }
            }
        }.launchIn(viewModelScope)
    }

    override suspend fun getAllProductsOfSeller(id: Int) {
        useCase.getAllProductsForSeller(id).onEach {
            when (it) {
                is ResultData.Success -> {
                    getGoodsSuccessFlow.emit(it.data)
                }
                is ResultData.Message -> {
                    messageFlow.emit(it.message)
                }
                is ResultData.Error -> {
                    errorFlow.emit(it.error)
                }
            }
        }.launchIn(viewModelScope)
    }

    override suspend fun getAllBookmarkedProducts() {
        useCase.getBookmarkedProducts().onEach {
            when (it) {
                is ResultData.Success -> {
                    getBookmarkedGoodsSuccessFlow.emit(it.data)
                }
                is ResultData.Message -> {
                    messageFlow.emit(it.message)
                }
                is ResultData.Error -> {
                    errorFlow.emit(it.error)
                }
            }
        }.launchIn(viewModelScope)
    }

    override suspend fun deleteFromBookmarked(item: ShopItemBookmarked) {
        useCase.deleteProductFromBookmarked(item)
    }

    override suspend fun addToBookmarked(item: ShopItemBookmarked) {
        useCase.addProductToBookmarked(item)
    }
}