package io.supportgenie.androidlibrary.data.network

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import okhttp3.logging.HttpLoggingInterceptor
import io.supportgenie.androidlibrary.BuildConfig
import okhttp3.Interceptor


//private const val API_KEY = BuildConfig.API_KEY
private const val BASE_URL = "https://staging-webservice.supportgenie.io/v3/"

private fun buildHttpClient(): OkHttpClient = OkHttpClient.Builder()
    .connectTimeout(30, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .addInterceptor(loggingInterceptor())  // Logs API responses to Logcat
//    .addInterceptor(apiKeyInterceptor())   // Adds API key to request URLs // this will be added later when we enable auth
    .build()

private fun buildClient(): Retrofit = Retrofit.Builder()   // Builds Retrofit object
    .baseUrl(BASE_URL)
    .client(buildHttpClient())
    .addConverterFactory(MoshiConverterFactory.create())  // Uses Moshi for JSON
    .build()


private fun loggingInterceptor() = HttpLoggingInterceptor().apply {
    level = if (BuildConfig.DEBUG) {
        HttpLoggingInterceptor.Level.BODY  // Only does logging in debug mode
    } else {
        HttpLoggingInterceptor.Level.NONE  // Otherwise no logging
    }
}

private fun injectQueryParams(
    vararg params: Pair<String, String>
): Interceptor = Interceptor { chain ->

    val originalRequest = chain.request()
    val urlWithParams = originalRequest.url().newBuilder()
        .apply { params.forEach { addQueryParameter(it.first, it.second) } }
        .build()
    val newRequest = originalRequest.newBuilder().url(urlWithParams).build()

    chain.proceed(newRequest)
}

/*
private fun apiKeyInterceptor() = injectQueryParams(
    "api_key" to API_KEY
)
*/

private val sgApiClient by lazy { buildClient() }
val sgApi: SgApi by lazy { sgApiClient.create(SgApi::class.java) }  // Public



