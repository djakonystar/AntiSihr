package dev.djakonystar.antisihr.domain.usecase

import dev.djakonystar.antisihr.data.models.*
import dev.djakonystar.antisihr.data.room.entity.AudioBookmarked
import kotlinx.coroutines.flow.Flow

interface AudioUseCase {

    suspend fun getListOfAudios(): Flow<ResultData<List<AudioBookmarked>>>

    suspend fun getListOfBookmarkedAudios(): Flow<ResultData<List<AudioBookmarked>>>

    suspend fun addAudioToBookmarked(item: AudioBookmarked): Flow<ResultData<Unit>>
    suspend fun deleteAudioFromBookmarked(item: AudioBookmarked): Flow<ResultData<Unit>>

    suspend fun isExistsInBookmarkeds(item: AudioBookmarked):Boolean

}