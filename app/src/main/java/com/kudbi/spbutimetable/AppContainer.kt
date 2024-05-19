package com.kudbi.spbutimetable

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.kudbi.spbutimetable.data.TimetableRepositoryApi
import com.kudbi.spbutimetable.network.TimetableAPI
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import retrofit2.Retrofit

interface AppContainer {
    val timetableRepositoryApi: TimetableRepositoryApi
}

class DefaultAppContainer : AppContainer {
    private val BASE_URL = "https://timetable.spbu.ru/"

    private val json = Json { ignoreUnknownKeys = true }

    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(json.asConverterFactory(MediaType.get("application/json")))
        .baseUrl(BASE_URL)
        .build()

    private val retrofitService: TimetableAPI by lazy {
        retrofit.create(TimetableAPI::class.java)
    }

    override val timetableRepositoryApi: TimetableRepositoryApi by lazy {
        TimetableRepositoryApi(retrofitService)
    }
}