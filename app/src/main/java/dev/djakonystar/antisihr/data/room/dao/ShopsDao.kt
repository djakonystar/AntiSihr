package dev.djakonystar.antisihr.data.room.dao

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.djakonystar.antisihr.data.room.entity.ArticlesBookmarked
import dev.djakonystar.antisihr.data.room.entity.ShopItemBookmarked
import java.nio.DoubleBuffer

@Dao
interface ShopsDao {

    @Query("Select * from bookmarked_shop_items")
    suspend fun getBookmarkedGoods(): List<ShopItemBookmarked>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertGoodToBookmarks(good: ShopItemBookmarked)

    @Delete
    suspend fun deleteGoodFromBookmarks(good: ShopItemBookmarked)

    @Query("SELECT EXISTS(SELECT * FROM bookmarked_shop_items WHERE id=:id and name=:name and image=:image)")
    suspend fun isExistsInBookmarkeds(
        id: Int, name: String,image:String
    ): Boolean
}