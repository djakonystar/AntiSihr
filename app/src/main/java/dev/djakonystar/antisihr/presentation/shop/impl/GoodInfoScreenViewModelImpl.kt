package dev.djakonystar.antisihr.presentation.shop.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.djakonystar.antisihr.data.models.GenericResponse
import dev.djakonystar.antisihr.data.models.ResultData
import dev.djakonystar.antisihr.data.models.shop.ShopItemData
import dev.djakonystar.antisihr.domain.usecase.ShopUseCase
import dev.djakonystar.antisihr.presentation.shop.GoodInfoScreenViewModel
import dev.djakonystar.antisihr.presentation.shop.ShopScreenViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class GoodInfoScreenViewModelImpl @Inject constructor(
    private val useCase: ShopUseCase
) : GoodInfoScreenViewModel, ViewModel() {
    override val getProductInfoSuccesFlow = MutableSharedFlow<GenericResponse<List<ShopItemData>>>()
    override val messageFlow = MutableSharedFlow<String>()
    override val errorFlow = MutableSharedFlow<Throwable>()

    override suspend fun getProductInfo(id: Int) {
        useCase.getShopItem(id).onEach {
            when(it){
                is ResultData.Success ->{
                    getProductInfoSuccesFlow.emit(it.data)
                }
                is ResultData.Message->{
                    messageFlow.emit(it.message)
                }
                is ResultData.Error->{
                    errorFlow.emit(it.error)
                }
            }
        }.launchIn(viewModelScope)
    }

}