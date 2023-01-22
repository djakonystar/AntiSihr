package dev.djakonystar.antisihr.presentation.library.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.djakonystar.antisihr.data.models.GenericResponse
import dev.djakonystar.antisihr.data.models.ResultData
import dev.djakonystar.antisihr.data.models.library.LibraryResultData
import dev.djakonystar.antisihr.domain.usecase.LibraryUseCase
import dev.djakonystar.antisihr.presentation.library.LibraryScreenViewModel
import dev.djakonystar.antisihr.utils.visibilityOfLoadingAnimationView
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LibraryScreenViewModelImpl @Inject constructor(
    private val useCase: LibraryUseCase
) : ViewModel(), LibraryScreenViewModel {
    override val getListOfSectionsLibraryFlow =
        MutableSharedFlow<GenericResponse<List<LibraryResultData>>>()
    override val messageFlow = MutableSharedFlow<String>()
    override val errorFlow = MutableSharedFlow<Throwable>()

    override suspend fun getListOfSectionsLibrary() {
        useCase.getLibrarySectionList().onEach {
            when (it) {
                is ResultData.Success -> {
                    getListOfSectionsLibraryFlow.emit(it.data)
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
            getListOfSectionsLibrary()
            visibilityOfLoadingAnimationView.emit(true)
        }
    }


}