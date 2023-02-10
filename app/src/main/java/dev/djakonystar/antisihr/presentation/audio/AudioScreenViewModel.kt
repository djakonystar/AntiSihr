package dev.djakonystar.antisihr.presentation.audio

import dev.djakonystar.antisihr.data.models.*
import dev.djakonystar.antisihr.data.room.entity.AudioBookmarked
import kotlinx.coroutines.flow.Flow

interface AudioScreenViewModel {

    val getListOfAudiosSuccessFlow: Flow<List<AudioBookmarked>>
    val messageFlow: Flow<String>
    val errorFlow: Flow<Throwable>
    suspend fun getListOfAudios()

    val isExistsInBookmarkedsFlow: Flow<Boolean>


    suspend fun getIsExistsInBookmarkeds(item:AudioBookmarked)
    suspend fun getBookmarkedAudios()
    suspend fun addToBookmarkeds(audioBookmarked: AudioBookmarked)
    suspend fun deleteFromBookmarkeds(audioBookmarked: AudioBookmarked)
}