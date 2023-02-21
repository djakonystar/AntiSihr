package dev.djakonystar.antisihr.presentation.test.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.djakonystar.antisihr.data.models.ResultData
import dev.djakonystar.antisihr.data.models.ListOfTestsResultData
import dev.djakonystar.antisihr.data.models.TestData
import dev.djakonystar.antisihr.data.models.TestResultData
import dev.djakonystar.antisihr.domain.usecase.TestUseCase
import dev.djakonystar.antisihr.presentation.test.HomeScreenViewModel
import dev.djakonystar.antisihr.presentation.test.TestScreenViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject


@HiltViewModel
class TestScreenViewModelImpl @Inject constructor(
    private val useCase: TestUseCase
) : TestScreenViewModel, ViewModel() {
    override val getTestsSuccessFlow = MutableSharedFlow<TestData>()
    override val getResultForTestsSuccessFlow = MutableSharedFlow<TestResultData>()
    override val messageFlow = MutableSharedFlow<String>()
    override val errorFlow = MutableSharedFlow<Throwable>()

    override suspend fun getTests(id: Int) {
        useCase.getTest(id).onEach {
            when (it) {
                is ResultData.Success -> {
                    getTestsSuccessFlow.emit(it.data)
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

    override suspend fun getResultForTests(id: Int, positive: Int) {
        useCase.getResultForTest(id, positive).onEach {
            when (it) {
                is ResultData.Success -> {
                    getResultForTestsSuccessFlow.emit(it.data)
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


}