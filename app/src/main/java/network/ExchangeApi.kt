package com.example.examen_final.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

// 1. El molde de cómo la API nos devuelve la información
// 1. El molde de cómo la API nos devuelve la información
data class ExchangeResponse(
    val base_code: String,
    val conversion_rates: Map<String, Double>
)

// 2. La ruta comercial que pide tu API KEY
interface ExchangeApiService {
    @GET("v6/{apiKey}/latest/{currency}")
    suspend fun getRates(
        @Path("apiKey") apiKey: String,
        @Path("currency") currency: String
    ): ExchangeResponse
}

// 3. El Cliente de Retrofit apuntando a la URL Comercial
object RetrofitClient {
    private const val BASE_URL = "https://v6.exchangerate-api.com/"

    val api: ExchangeApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ExchangeApiService::class.java)
    }
}