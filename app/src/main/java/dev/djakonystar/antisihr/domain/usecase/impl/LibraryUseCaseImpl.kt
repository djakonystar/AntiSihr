package dev.djakonystar.antisihr.domain.usecase.impl

import dev.djakonystar.antisihr.data.models.ResultData
import dev.djakonystar.antisihr.data.models.library.InnerLibraryBookmarkData
import dev.djakonystar.antisihr.data.models.library.InnerLibraryResultData
import dev.djakonystar.antisihr.data.room.LocalRoomDatabase
import dev.djakonystar.antisihr.data.room.entity.ArticlesBookmarked
import dev.djakonystar.antisihr.domain.repository.LibraryRepository
import dev.djakonystar.antisihr.domain.usecase.LibraryUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LibraryUseCaseImpl @Inject constructor(
    private val repo: LibraryRepository, private val db: LocalRoomDatabase
) : LibraryUseCase {
    override suspend fun getLibrarySectionList() = repo.getListOfSectionsLibrary()


    override suspend fun getListOfArticles(id: Int) = flow {
        repo.getListOfArticles(id).collect {
            if (it is ResultData.Success) {
                val list = mutableListOf<InnerLibraryBookmarkData>()
                it.data.result?.forEach {
                    list.add(
                        InnerLibraryBookmarkData(
                            it.id,
                            it.lead,
                            it.title,
                            db.articleDao().isExistsInBookmarkeds(it.id, it.lead, it.title)
                        )
                    )
                }
                emit(ResultData.Success(list))
            }
        }
    }

    override suspend fun getArticle(id: Int) = repo.getArticle(id)
    override suspend fun addArticleToBookmarked(article: ArticlesBookmarked) =
        repo.addArticleToBookmarked(article)

    override suspend fun deleteArticleFromBookmarked(article: ArticlesBookmarked) =
        repo.deleteArticleFromBookmarked(article)

    override suspend fun getBookmarkedArticles() = flow {
        repo.getBookmarkedArticles().collect {
            if (it is ResultData.Success) {
                val list = mutableListOf<InnerLibraryBookmarkData>()
                it.data.forEach {
                    list.add(InnerLibraryBookmarkData(it.id, it.lead, it.title, true))
                }
                emit(ResultData.Success(list))
            }
        }
    }

}