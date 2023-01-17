package dev.djakonystar.antisihr.domain.repository

import dev.djakonystar.antisihr.data.models.*
import kotlinx.coroutines.flow.Flow

interface AudioRepository {

    suspend fun getListOfAudios(): Flow<ResultData<ListOfAudiosData>>

}