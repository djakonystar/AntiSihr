package dev.djakonystar.antisihr.domain.usecase

import dev.djakonystar.antisihr.data.models.ResultData
import dev.djakonystar.antisihr.data.models.ListOfTestsResultData
import dev.djakonystar.antisihr.data.models.TestData
import dev.djakonystar.antisihr.data.models.TestResultData
import kotlinx.coroutines.flow.Flow

interface TestUseCase {

    suspend fun getListOfTests(): Flow<ResultData<List<ListOfTestsResultData>>>

    suspend fun getTest(id: Int): Flow<ResultData<TestData>>

    suspend fun getResultForTest(id:Int, positive:Int):Flow<ResultData<TestResultData>>
}