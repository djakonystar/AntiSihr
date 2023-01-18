package dev.djakonystar.antisihr.data.remote

import dev.djakonystar.antisihr.data.models.*
import dev.djakonystar.antisihr.data.models.reader.ReaderData
import dev.djakonystar.antisihr.data.models.reader.ReaderDetailData
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

    /**
     * Get all readers
     */
    @GET("/readers")
    suspend fun getReaders(): Response<GenericResponse<List<ReaderData>>>

    /**
     * Get reader by [id]
     */
    @GET("/readers/{id}")
    suspend fun getReaderById(
        @Path("id") id: Int
    ): Response<GenericResponse<List<ReaderDetailData>>>

}
