package dev.djakonystar.antisihr.domain.usecase.impl

import dev.djakonystar.antisihr.data.models.GenericResponse
import dev.djakonystar.antisihr.data.models.ResultData
import dev.djakonystar.antisihr.data.models.reader.ReaderDetailData
import dev.djakonystar.antisihr.domain.repository.ReadersRepository
import dev.djakonystar.antisihr.domain.usecase.ReadersUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReadersUseCaseImpl @Inject constructor(
    private val repository: ReadersRepository
): ReadersUseCase {

    override suspend fun getReaders() = repository.getReaders()

    override suspend fun getReaderById(id: Int) = repository.getReaderById(id)
}
