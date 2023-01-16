package dev.djakonystar.antisihr.domain.usecase.impl

import dev.djakonystar.antisihr.data.models.ResultData
import dev.djakonystar.antisihr.data.models.TestData
import dev.djakonystar.antisihr.domain.repository.TestRepository
import dev.djakonystar.antisihr.domain.usecase.TestUseCase
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class TestUseCaseImpl @Inject constructor(
    private val repo: TestRepository
) : TestUseCase {

    override suspend fun getListOfTests() = flow {
        repo.getListOfTests().collect { data ->
            if (data is ResultData.Success) {
                emit(ResultData.Success(data.data.result.sortedBy { it.id }))
            }
        }
    }

    override suspend fun getTest(id: Int) = repo.getTest(id)
}