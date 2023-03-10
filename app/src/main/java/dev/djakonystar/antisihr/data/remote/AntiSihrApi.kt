package dev.djakonystar.antisihr.data.remote

import dev.djakonystar.antisihr.data.models.*
import dev.djakonystar.antisihr.data.models.drawerlayout.AboutAppData
import dev.djakonystar.antisihr.data.models.drawerlayout.AddFeedbackData
import dev.djakonystar.antisihr.data.models.drawerlayout.LanguageData
import dev.djakonystar.antisihr.data.models.library.ArticleResultData
import dev.djakonystar.antisihr.data.models.library.InnerLibraryResultData
import dev.djakonystar.antisihr.data.models.library.LibraryResultData
import dev.djakonystar.antisihr.data.models.reader.CityData
import dev.djakonystar.antisihr.data.models.reader.ReaderData
import dev.djakonystar.antisihr.data.models.reader.ReaderDetailData
import dev.djakonystar.antisihr.data.models.shop.SellerData
import dev.djakonystar.antisihr.data.models.shop.ShopItemData
import retrofit2.Response
import retrofit2.http.*

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

    @GET("/library")
    suspend fun getListOfSectionsLibrary(): Response<GenericResponse<List<LibraryResultData>>>

    @GET("/articles/list/{id}")
    suspend fun getArticlesForLibrary(@Path("id") id: Int): Response<GenericResponse<List<InnerLibraryResultData>>>

    @GET("/articles/{id}")
    suspend fun getArticle(@Path("id") id: Int): Response<GenericResponse<List<ArticleResultData>>>


    @Headers("Content-Type:application/json")
    @POST("/feedback")
    suspend fun addFeedback(@Body body: AddFeedbackData): Response<GenericResponse<String>>

    @GET("/languages")
    suspend fun getLanguages(): Response<GenericResponse<List<LanguageData>>>

    @GET("/about")
    suspend fun getInfoAboutApp(): Response<GenericResponse<List<AboutAppData>>>

    @GET("/products")
    suspend fun getAllProducts(): Response<GenericResponse<List<ShopItemData>>>

    @GET("/products/seller/{id}")
    suspend fun getAllProductsForSeller(@Path("id") sellerId: Int): Response<GenericResponse<List<ShopItemData>>>

    @GET("/products/{id}")
    suspend fun getProductInfo(@Path("id") goodId: Int): Response<GenericResponse<List<ShopItemData>>>


    @GET("/sellers")
    suspend fun getAllSellers(): Response<GenericResponse<List<SellerData>>>


    @GET("/cities")
    suspend fun getAllCities(): Response<GenericResponse<List<CityData>>>

}
