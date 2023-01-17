package dev.djakonystar.antisihr.presentation.audio

import dev.djakonystar.antisihr.data.models.*
import kotlinx.coroutines.flow.Flow

interface AudioScreenViewModel {

    val getListOfAudiosSuccessFlow: Flow<List<AudioResultData>>
    val messageFlow: Flow<String>
    val errorFlow: Flow<Throwable>
    suspend fun getListOfTests()
}