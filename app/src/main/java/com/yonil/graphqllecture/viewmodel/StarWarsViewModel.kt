package com.yonil.graphqllecture.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yonil.graphqllecture.GraphQLApp
import com.yonil.graphqllecture.api.SWAPIService
import com.yonil.graphqllecture.model.Film
import com.yonil.graphqllecture.model.People
import com.yonil.graphqllecture.model.Planet
import com.yonil.graphqllecture.repository.SWAPIRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Locale

class StarWarsViewModel(application: Application) : AndroidViewModel(application) {
    private val app = this.getApplication<GraphQLApp>()
    private val api = app.retrofit.create(SWAPIService::class.java)
    private val repository = SWAPIRepository(api)

    private val _uiState = MutableStateFlow(StarWarsUiState())
    val uiState: StateFlow<StarWarsUiState> = _uiState.asStateFlow()

    private var fetchJob: Job? = null
    fun fetchLatestFilmData() {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            try {
                val films = repository.getAllFilms()

                val lastFilmInTrilogy =
                    films.find { it.title.lowercase(Locale.getDefault()) == "return of the jedi" }
                        ?: return@launch

                val peopleDeferred = async {
                    lastFilmInTrilogy.characters.map {
                        async { repository.getPeople(it) }
                    }.awaitAll()
                }

                val planetDeferred = async {
                    lastFilmInTrilogy.planets.map {
                        async { repository.getPlanet(it) }
                    }.awaitAll()
                }

                val peopleInMovie = peopleDeferred.await()
                val planetsInMovie = planetDeferred.await()


                Log.d(
                    "fetchLatestFilmData",
                    "peopleInMovie: ${peopleInMovie.joinToString { it.name }}"
                )

                Log.d(
                    "fetchLatestFilmData",
                    "planetsInMovie: ${planetsInMovie.joinToString { it.name }}"
                )
                _uiState.emit(
                    StarWarsUiState(
                        film = lastFilmInTrilogy,
                        characters = peopleInMovie,
                        planets = planetsInMovie
                    )
                )
            } catch (ioe: IOException) {
                _uiState.update {
                    val message = ioe.message.orEmpty()
                    it.copy(message = message)
                }
            }
        }

    }
}

data class StarWarsUiState(
    val film: Film? = null,
    val characters: List<People> = listOf(),
    val planets: List<Planet> = listOf(),
    val message: String = ""
)

