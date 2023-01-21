package dev.djakonystar.antisihr.presentation.library.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.djakonystar.antisihr.data.models.GenericResponse
import dev.djakonystar.antisihr.data.models.ResultData
import dev.djakonystar.antisihr.data.models.library.ArticleResultData
import dev.djakonystar.antisihr.data.models.library.InnerLibraryBookmarkData
import dev.djakonystar.antisihr.data.models.library.InnerLibraryResultData
import dev.djakonystar.antisihr.data.room.entity.ArticlesBookmarked
import dev.djakonystar.antisihr.domain.usecase.LibraryUseCase
import dev.djakonystar.antisihr.presentation.library.InnerLibraryScreenViewModel
import dev.djakonystar.antisihr.presentation.library.LibraryScreenViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject


@HiltViewModel
class InnerLibraryScreenViewModelImpl @Inject constructor(
    private val useCase: LibraryUseCase
) : ViewModel(), InnerLibraryScreenViewModel {
    override val getListOfArticlesFlow = MutableSharedFlow<List<InnerLibraryBookmarkData>>()
    override val getArticleSuccessFlow =
        MutableSharedFlow<GenericResponse<List<ArticleResultData>>>()
    override val messageFlow = MutableSharedFlow<String>()
    override val successDeleteFromBookmarkFlow = MutableSharedFlow<Unit>()
    override val successAddToBookmarkFlow = MutableSharedFlow<Unit>()
    override val errorFlow = MutableSharedFlow<Throwable>()
    override suspend fun getListOfArticles(id: Int) {
        useCase.getListOfArticles(id).onEach {
            when (it) {
                is ResultData.Success -> {
                    getListOfArticlesFlow.emit(it.data)
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

    override suspend fun getFavouriteListOfArticles() {
        useCase.getBookmarkedArticles().onEach {
            when (it) {
                is ResultData.Success -> {
                    getListOfArticlesFlow.emit(it.data)
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

    override suspend fun getArticle(id: Int) {
        useCase.getArticle(id).onEach {
            when (it) {
                is ResultData.Success -> {
                    getArticleSuccessFlow.emit(it.data)
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

    override suspend fun deleteArticleFromBookmarkeds(article: ArticlesBookmarked) {
        useCase.deleteArticleFromBookmarked(article).onEach {
            when (it) {
                is ResultData.Success -> {
                    successDeleteFromBookmarkFlow.emit(it.data)
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

    override suspend fun addArticleToBookmarkeds(article: ArticlesBookmarked) {
        useCase.addArticleToBookmarked(article).onEach {
            when (it) {
                is ResultData.Success -> {
                    successAddToBookmarkFlow.emit(it.data)
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