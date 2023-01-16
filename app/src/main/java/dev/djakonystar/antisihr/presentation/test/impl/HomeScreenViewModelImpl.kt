package dev.djakonystar.antisihr.presentation.test.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.djakonystar.antisihr.data.models.ResultData
import dev.djakonystar.antisihr.data.models.ListOfTestsResultData
import dev.djakonystar.antisihr.data.models.TestData
import dev.djakonystar.antisihr.domain.usecase.TestUseCase
import dev.djakonystar.antisihr.presentation.test.HomeScreenViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeScreenViewModelImpl @Inject constructor(
    private val useCase: TestUseCase
) : HomeScreenViewModel, ViewModel() {
    override val getListOfTestsSuccessFlow = MutableSharedFlow<List<ListOfTestsResultData>>()
    override val messageFlow = MutableSharedFlow<String>()
    override val errorFlow = MutableSharedFlow<Throwable>()

    override suspend fun getListOfTests() {
        useCase.getListOfTests().onEach {
            when (it) {
                is ResultData.Success -> {
                    getListOfTestsSuccessFlow.emit(it.data)
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
            getListOfTests()
        }
    }

}