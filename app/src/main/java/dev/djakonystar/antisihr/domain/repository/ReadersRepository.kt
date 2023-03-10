package dev.djakonystar.antisihr.domain.repository

import dev.djakonystar.antisihr.data.models.GenericResponse
import dev.djakonystar.antisihr.data.models.ResultData
import dev.djakonystar.antisihr.data.models.reader.CityData
import dev.djakonystar.antisihr.data.models.reader.ReaderData
import dev.djakonystar.antisihr.data.models.reader.ReaderDetailData
import kotlinx.coroutines.flow.Flow

interface ReadersRepository {

    suspend fun getReaders(): Flow<ResultData<GenericResponse<List<ReaderData>>>>

    suspend fun getReaderById(id: Int): Flow<ResultData<GenericResponse<List<ReaderDetailData>>>>

    suspend fun getAllCities(): Flow<ResultData<GenericResponse<List<CityData>>>>
}
