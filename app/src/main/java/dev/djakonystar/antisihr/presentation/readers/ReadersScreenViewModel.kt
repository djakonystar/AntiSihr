package dev.djakonystar.antisihr.presentation.readers

import dev.djakonystar.antisihr.data.models.reader.CityData
import dev.djakonystar.antisihr.data.models.reader.ReaderData
import dev.djakonystar.antisihr.data.models.reader.ReaderDetailData
import kotlinx.coroutines.flow.Flow

interface ReadersScreenViewModel {

    val getReadersSuccessFlow: Flow<List<ReaderData>>
    val getReaderByIdSuccessFlow: Flow<List<ReaderDetailData>>
    val getAllCitiesFlow: Flow<List<CityData>>
    val messageFlow: Flow<String>
    val errorFlow: Flow<Throwable>

    suspend fun getReaders()

    suspend fun getReaderById(id: Int)


    suspend fun getAllCities()
}
