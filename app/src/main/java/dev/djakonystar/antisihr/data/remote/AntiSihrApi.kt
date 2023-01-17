package dev.djakonystar.antisihr.data.remote

import dev.djakonystar.antisihr.data.models.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AntiSihrApi {

    @GET("/tests")
    suspend fun getListOfTests(): Response<ListOfTestsData>


    @GET("/tests/{testId}")
    suspend fun getTest(@Path("testId") testId: Int): Response<TestData>


    @GET("/tests/result/")
    suspend fun getResultForTest(
        @Query("id") id: Int, @Query("positive") positive: Int
    ): Response<TestResultData>

    @GET("/audio")
    suspend fun getListOfAudios(): Response<ListOfAudiosData>

    @GET("/audio/{id}")
    suspend fun getAudioById(@Path("id") id:Int): Response<AudioSingleData>


}