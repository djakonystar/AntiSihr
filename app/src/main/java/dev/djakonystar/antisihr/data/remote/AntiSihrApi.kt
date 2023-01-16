package dev.djakonystar.antisihr.data.remote

import dev.djakonystar.antisihr.data.models.ListOfTestsData
import dev.djakonystar.antisihr.data.models.TestData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface AntiSihrApi {

    @GET("/tests")
    suspend fun getListOfTests(): Response<ListOfTestsData>


    @GET("/tests/{testId}")
    suspend fun getTest(@Path("testId") testId: Int): Response<TestData>


}