package dev.djakonystar.antisihr.domain.repository.impl

import android.util.Log
import dev.djakonystar.antisihr.data.models.ResultData
import dev.djakonystar.antisihr.data.models.library.InnerLibraryResultData
import dev.djakonystar.antisihr.data.remote.AntiSihrApi
import dev.djakonystar.antisihr.data.room.LocalRoomDatabase
import dev.djakonystar.antisihr.data.room.entity.ArticlesBookmarked
import dev.djakonystar.antisihr.domain.repository.LibraryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LibraryRepositoryImpl @Inject constructor(
    private val api: AntiSihrApi, private val db: LocalRoomDatabase
) : LibraryRepository {
    override suspend fun getListOfSectionsLibrary() = flow {
        val response = api.getListOfSectionsLibrary()
        if (response.isSuccessful) {
            emit(ResultData.Success(response.body()!!))
        } else {
            emit(ResultData.Message(response.message()))
        }
    }.catch {
        emit(ResultData.Error(it))
    }.flowOn(Dispatchers.IO)

    override suspend fun getListOfArticles(id: Int) = flow {
        val response = api.getArticlesForLibrary(id)
        if (response.isSuccessful) {
            emit(ResultData.Success(response.body()!!))
        } else {
            emit(ResultData.Message(response.message()))
        }
    }.catch {
        emit(ResultData.Error(it))
    }.flowOn(Dispatchers.IO)

    override suspend fun getArticle(id: Int) = flow {
        val response = api.getArticle(id)
        if (response.isSuccessful) {
            emit(ResultData.Success(response.body()!!))
        } else {
            emit(ResultData.Message(response.message()))
        }
    }.catch {
        emit(ResultData.Error(it))
    }.flowOn(Dispatchers.IO)

    override suspend fun addArticleToBookmarked(article: ArticlesBookmarked) = flow {
        emit(ResultData.Success(db.articleDao().insertArticleToBookmarks(article)))
    }.catch {
        Log.d("TTTT", "INSERTING ERROR")
    }.flowOn(Dispatchers.IO)

    override suspend fun deleteArticleFromBookmarked(article: ArticlesBookmarked) = flow {
        emit(ResultData.Success(db.articleDao().deleteArticleFromBookmarks(article)))
    }.catch {
        Log.d("TTTT", "DELETING ERROR")
    }.flowOn(Dispatchers.IO)

    override suspend fun getBookmarkedArticles() = flow {
        emit(ResultData.Success(db.articleDao().getFavouriteArticles()))
    }.catch {
        Log.d("TTTT", "INSERTING ERROR")
    }.flowOn(Dispatchers.IO)


}