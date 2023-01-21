package dev.djakonystar.antisihr.presentation.library

import dev.djakonystar.antisihr.data.models.GenericResponse
import dev.djakonystar.antisihr.data.models.library.ArticleResultData
import dev.djakonystar.antisihr.data.models.library.InnerLibraryResultData
import kotlinx.coroutines.flow.Flow

interface InnerLibraryScreenViewModel {
    val getListOfArticlesFlow: Flow<GenericResponse<List<InnerLibraryResultData>>>
    val getArticleSuccessFlow: Flow<GenericResponse<List<ArticleResultData>>>
    val messageFlow:Flow<String>

    val errorFlow:Flow<Throwable>
    suspend fun getListOfArticles(id:Int)

    suspend fun getArticle(id:Int)
}