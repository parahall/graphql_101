package com.yonil.graphqllecture.api

import com.yonil.graphqllecture.model.Film
import com.yonil.graphqllecture.model.People
import com.yonil.graphqllecture.model.Planet
import retrofit2.http.GET
import retrofit2.http.Url

interface SWAPIService {
    @GET("films/3")
    suspend fun getLastTrilogyFilm(): Film

    @GET
    suspend fun getPeople(@Url url: String): People

    @GET
    suspend fun getPlanet(@Url url: String): Planet

}

data class APIListResponse<T>(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<T>
)