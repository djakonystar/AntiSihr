package dev.djakonystar.antisihr.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.djakonystar.antisihr.data.remote.AntiSihrApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
    @[Provides Singleton]
    fun providesHttpLoggingInterceptor() = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @[Provides Singleton]
    fun providesOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor, @ApplicationContext context: Context
    ): OkHttpClient = OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor).build()


    @[Provides Singleton]
    fun providesRetrofitInstance(client: OkHttpClient): Retrofit =
        Retrofit.Builder().baseUrl("https://anti-sihr-server.ru/")
            .addConverterFactory(GsonConverterFactory.create()).client(client).build()


    @[Provides Singleton]
    fun getApiProvides(retrofit: Retrofit): AntiSihrApi = retrofit.create(AntiSihrApi::class.java)
}