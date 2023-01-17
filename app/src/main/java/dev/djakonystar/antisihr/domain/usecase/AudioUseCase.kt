package dev.djakonystar.antisihr.domain.usecase

import dev.djakonystar.antisihr.data.models.*
import kotlinx.coroutines.flow.Flow

interface AudioUseCase {

    suspend fun getListOfAudios(): Flow<ResultData<ListOfAudiosData>>

}