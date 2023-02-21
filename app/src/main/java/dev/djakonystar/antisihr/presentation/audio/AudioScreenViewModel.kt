package dev.djakonystar.antisihr.presentation.audio

import dev.djakonystar.antisihr.data.models.*
import dev.djakonystar.antisihr.data.room.entity.AudioBookmarked
import kotlinx.coroutines.flow.Flow

interface AudioScreenViewModel {

    val getListOfAudiosSuccessFlow: Flow<List<AudioBookmarked>>
    val messageFlow: Flow<String>
    val errorFlow: Flow<Throwable>
    suspend fun getListOfAudios()



    suspend fun getBookmarkedAudios()

}