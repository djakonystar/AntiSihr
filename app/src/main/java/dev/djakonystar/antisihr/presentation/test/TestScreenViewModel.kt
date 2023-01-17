package dev.djakonystar.antisihr.presentation.test

import dev.djakonystar.antisihr.data.models.ListOfTestsResultData
import dev.djakonystar.antisihr.data.models.TestData
import dev.djakonystar.antisihr.data.models.TestResultData
import kotlinx.coroutines.flow.Flow

interface TestScreenViewModel {

    val getTestsSuccessFlow: Flow<TestData>
    val getResultForTestsSuccessFlow: Flow<TestResultData>
    val messageFlow: Flow<String>
    val errorFlow: Flow<Throwable>
    suspend fun getTests(id: Int)
    suspend fun getResultForTests(id: Int, positive:Int)
}