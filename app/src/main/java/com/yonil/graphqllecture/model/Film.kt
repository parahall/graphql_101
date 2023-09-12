package com.yonil.graphqllecture.model

import com.google.gson.annotations.SerializedName
import com.yonil.graphqllecture.ReturnOfJediQuery

data class Film(
    val title: String,
    val director: String,
    @SerializedName("release_date") val releaseDate: String,
    val characters: List<String>,
    val planets: List<String>
) {
    constructor(queryFilm: ReturnOfJediQuery.Film) : this(
        title = queryFilm.title ?: "",
        director = queryFilm.director ?: "",
        releaseDate = queryFilm.releaseDate ?: "",
        characters = listOf(),
        planets = listOf()
    )
}