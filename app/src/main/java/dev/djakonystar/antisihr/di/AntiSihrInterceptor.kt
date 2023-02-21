package dev.djakonystar.antisihr.di

import android.util.Log
import dev.djakonystar.antisihr.data.local.LocalStorage
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AntiSihrInterceptor @Inject constructor(
    private val localStorage: LocalStorage
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder().addHeader(
            "Accept-Language", localStorage.language
        ).build()
        return chain.proceed(request)
    }
}