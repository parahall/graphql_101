package com.yonil.graphqllecture.repository

import com.yonil.graphqllecture.api.SWAPIService
import com.yonil.graphqllecture.model.Film
import com.yonil.graphqllecture.model.People
import com.yonil.graphqllecture.model.Planet

class SWAPIRepository(private val api: SWAPIService) {
    suspend fun getAllFilms(): List<Film> {
        return api.getAllFilms().results
    }

    suspend fun getPeople(url: String): People {
        return api.getPeople(url)
    }

    suspend fun getPlanet(url: String): Planet {
        return api.getPlanet(url)
    }
}