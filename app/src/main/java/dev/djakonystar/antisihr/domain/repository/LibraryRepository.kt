package dev.djakonystar.antisihr.domain.repository

import dev.djakonystar.antisihr.data.models.GenericResponse
import dev.djakonystar.antisihr.data.models.ResultData
import dev.djakonystar.antisihr.data.models.library.ArticleResultData
import dev.djakonystar.antisihr.data.models.library.InnerLibraryResultData
import dev.djakonystar.antisihr.data.models.library.LibraryResultData
import kotlinx.coroutines.flow.Flow

interface LibraryRepository {

    suspend fun getListOfSectionsLibrary(): Flow<ResultData<GenericResponse<List<LibraryResultData>>>>

    suspend fun getListOfArticles(id:Int):Flow<ResultData<GenericResponse<List<InnerLibraryResultData>>>>

    suspend fun getArticle(id:Int):Flow<ResultData<GenericResponse<List<ArticleResultData>>>>
}