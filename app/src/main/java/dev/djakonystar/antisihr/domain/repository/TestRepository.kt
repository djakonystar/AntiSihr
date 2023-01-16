package dev.djakonystar.antisihr.domain.repository

import dev.djakonystar.antisihr.data.models.ResultData
import dev.djakonystar.antisihr.data.models.ListOfTestsData
import dev.djakonystar.antisihr.data.models.TestData
import kotlinx.coroutines.flow.Flow

interface TestRepository {

    suspend fun getListOfTests(): Flow<ResultData<ListOfTestsData>>

    suspend fun getResultForTest()

    suspend fun getTest(id: Int): Flow<ResultData<TestData>>
}