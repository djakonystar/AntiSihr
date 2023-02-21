package dev.djakonystar.antisihr.data.room.dao

import androidx.room.*
import dev.djakonystar.antisihr.data.models.AudioResultData
import dev.djakonystar.antisihr.data.room.entity.AudioBookmarked
import dev.djakonystar.antisihr.data.room.entity.ShopItemBookmarked


@Dao
interface AudioDao {
    @Query("Select * from audio_articles")
    suspend fun getBookmarkedAudio(): List<AudioBookmarked>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAudioToBookmarks(audio: AudioBookmarked)

    @Delete
    suspend fun deleteAudioFromBookmarks(audio: AudioBookmarked)

    @Query("SELECT EXISTS(SELECT * FROM audio_articles WHERE id=:id and author=:author and name=:name)")
    suspend fun isExistsInBookmarkeds(
        id: Int, author: String,name:String
    ): Boolean
}