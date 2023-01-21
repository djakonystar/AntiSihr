package dev.djakonystar.antisihr.data.room.dao

import androidx.core.view.WindowInsetsCompat.Type.InsetsType
import androidx.room.*
import dev.djakonystar.antisihr.data.models.library.ArticleResultData
import dev.djakonystar.antisihr.data.models.library.InnerLibraryResultData
import dev.djakonystar.antisihr.data.room.entity.ArticlesBookmarked


@Dao
interface ArticlesDao {

    @Query("Select * from bookmarked_articles")
    suspend fun getFavouriteArticles(): List<ArticlesBookmarked>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertArticleToBookmarks(article: ArticlesBookmarked)

    @Delete
    suspend fun deleteArticleFromBookmarks(article: ArticlesBookmarked)

    @Query("SELECT EXISTS(SELECT * FROM bookmarked_articles WHERE id=:id and lead=:lead and title=:title)")
    suspend fun isExistsInBookmarkeds(id: Int, lead: String, title: String): Boolean
}