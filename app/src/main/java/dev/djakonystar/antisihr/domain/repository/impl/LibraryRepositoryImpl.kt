package dev.djakonystar.antisihr.domain.repository.impl

import dev.djakonystar.antisihr.data.models.GenericResponse
import dev.djakonystar.antisihr.data.models.ResultData
import dev.djakonystar.antisihr.data.models.library.ArticleResultData
import dev.djakonystar.antisihr.data.models.library.InnerLibraryResultData
import dev.djakonystar.antisihr.data.remote.AntiSihrApi
import dev.djakonystar.antisihr.domain.repository.LibraryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LibraryRepositoryImpl @Inject constructor(
    private val api: AntiSihrApi
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


}