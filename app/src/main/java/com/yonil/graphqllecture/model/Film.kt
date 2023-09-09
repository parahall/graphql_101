package com.yonil.graphqllecture.model

import com.google.gson.annotations.SerializedName

data class Film(
    val title: String,
    val director: String,
    @SerializedName("release_date") val releaseDate: String,
    val characters: List<String>,
    val planets: List<String>
)