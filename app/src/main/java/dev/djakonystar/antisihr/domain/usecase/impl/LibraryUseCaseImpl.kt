package dev.djakonystar.antisihr.domain.usecase.impl

import dev.djakonystar.antisihr.data.models.GenericResponse
import dev.djakonystar.antisihr.data.models.ResultData
import dev.djakonystar.antisihr.data.models.library.ArticleResultData
import dev.djakonystar.antisihr.data.models.library.InnerLibraryResultData
import dev.djakonystar.antisihr.domain.repository.LibraryRepository
import dev.djakonystar.antisihr.domain.usecase.LibraryUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LibraryUseCaseImpl @Inject constructor(
    private val repo: LibraryRepository
) : LibraryUseCase {
    override suspend fun getLibrarySectionList() = repo.getListOfSectionsLibrary()
    override suspend fun getListOfArticles(id: Int) = repo.getListOfArticles(id)
    override suspend fun getArticle(id: Int) = repo.getArticle(id)

}