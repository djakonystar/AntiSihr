package dev.djakonystar.antisihr.presentation.test

import dev.djakonystar.antisihr.data.models.ListOfTestsResultData
import dev.djakonystar.antisihr.data.models.TestData
import kotlinx.coroutines.flow.Flow

interface HomeScreenViewModel {

    val getListOfTestsSuccessFlow: Flow<List<ListOfTestsResultData>>
    val messageFlow: Flow<String>
    val errorFlow: Flow<Throwable>
    suspend fun getListOfTests()
}