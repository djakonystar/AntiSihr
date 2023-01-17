package dev.djakonystar.antisihr.presentation.audio

import dev.djakonystar.antisihr.data.models.*
import kotlinx.coroutines.flow.Flow

interface AudioPlayerScreenViewModel {

    val messageFlow: Flow<String>
    val errorFlow: Flow<Throwable>
}