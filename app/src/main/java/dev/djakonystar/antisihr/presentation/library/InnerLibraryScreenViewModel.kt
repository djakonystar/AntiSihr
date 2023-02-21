package dev.djakonystar.antisihr.presentation.library

import dev.djakonystar.antisihr.data.models.GenericResponse
import dev.djakonystar.antisihr.data.models.library.ArticleResultData
import dev.djakonystar.antisihr.data.models.library.InnerLibraryBookmarkData
import dev.djakonystar.antisihr.data.models.library.InnerLibraryResultData
import dev.djakonystar.antisihr.data.room.entity.ArticlesBookmarked
import kotlinx.coroutines.flow.Flow

interface InnerLibraryScreenViewModel {
    val getListOfArticlesFlow: Flow<List<InnerLibraryBookmarkData>>
    val getArticleSuccessFlow: Flow<GenericResponse<List<ArticleResultData>>>
    val messageFlow: Flow<String>

    val successDeleteFromBookmarkFlow: Flow<Unit>
    val successAddToBookmarkFlow: Flow<Unit>
    val errorFlow: Flow<Throwable>

    suspend fun getListOfArticles(id: Int)


    suspend fun getFavouriteListOfArticles()

    suspend fun getArticle(id: Int)

    suspend fun deleteArticleFromBookmarkeds(article: ArticlesBookmarked)
    suspend fun addArticleToBookmarkeds(article: ArticlesBookmarked)
}