package dev.djakonystar.antisihr.domain.repository

import dev.djakonystar.antisihr.data.models.*
import dev.djakonystar.antisihr.data.room.entity.AudioBookmarked
import dev.djakonystar.antisihr.data.room.entity.ShopItemBookmarked
import kotlinx.coroutines.flow.Flow

interface AudioRepository {

    suspend fun getListOfAudios(): Flow<ResultData<ListOfAudiosData>>

    suspend fun getListOfBookmarkedAudios(): Flow<ResultData<List<AudioBookmarked>>>

    suspend fun addAudioToBookmarked(item: AudioBookmarked): Flow<ResultData<Unit>>
    suspend fun deleteAudioFromBookmarked(item: AudioBookmarked): Flow<ResultData<Unit>>

    suspend fun isExistsInBookmarkeds(item: AudioBookmarked): Boolean

}