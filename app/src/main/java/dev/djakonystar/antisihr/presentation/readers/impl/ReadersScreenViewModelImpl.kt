package dev.djakonystar.antisihr.presentation.readers.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.djakonystar.antisihr.data.models.ResultData
import dev.djakonystar.antisihr.data.models.reader.CityData
import dev.djakonystar.antisihr.data.models.reader.ReaderData
import dev.djakonystar.antisihr.data.models.reader.ReaderDetailData
import dev.djakonystar.antisihr.domain.usecase.ReadersUseCase
import dev.djakonystar.antisihr.presentation.readers.ReadersScreenViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ReadersScreenViewModelImpl @Inject constructor(
    private val useCase: ReadersUseCase
) : ReadersScreenViewModel, ViewModel() {
    override val getReadersSuccessFlow = MutableSharedFlow<List<ReaderData>>()
    override val getReaderByIdSuccessFlow = MutableSharedFlow<List<ReaderDetailData>>()
    override val getAllCitiesFlow = MutableSharedFlow<List<CityData>>()
    override val messageFlow = MutableSharedFlow<String>()
    override val errorFlow = MutableSharedFlow<Throwable>()

    override suspend fun getReaders() {
        useCase.getReaders().onEach {
            when (it) {
                is ResultData.Success -> {
                    getReadersSuccessFlow.emit(it.data.result ?: listOf())
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

    override suspend fun getReaderById(id: Int) {
        useCase.getReaderById(id).onEach {
            when (it) {
                is ResultData.Success -> {
                    getReaderByIdSuccessFlow.emit(it.data.result ?: listOf())
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

    override suspend fun getAllCities() {
        useCase.getAllCities().onEach {
            when (it) {
                is ResultData.Success -> {
                    getAllCitiesFlow.emit(it.data.result ?: listOf())
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
