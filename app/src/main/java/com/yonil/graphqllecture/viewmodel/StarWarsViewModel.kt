package com.yonil.graphqllecture.viewmodel

import android.app.Application
import android.os.Debug
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yonil.graphqllecture.GraphQLApp
import com.yonil.graphqllecture.ReturnOfJediQuery
import com.yonil.graphqllecture.api.SWAPIService
import com.yonil.graphqllecture.model.Film
import com.yonil.graphqllecture.model.People
import com.yonil.graphqllecture.model.Planet
import com.yonil.graphqllecture.repository.GraphQLRepository
import com.yonil.graphqllecture.repository.RESTRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

class StarWarsViewModel(application: Application) : AndroidViewModel(application) {
    private val app = getApplication<GraphQLApp>()
    private val api = app.retrofit.create(SWAPIService::class.java)
    private val apollo = app.apolloClient
    private val restRepository = RESTRepository(api)
    private val graphqlRepository = GraphQLRepository(apollo)

    private val _uiState = MutableStateFlow(StarWarsUiState())
    val uiState: StateFlow<StarWarsUiState> = _uiState.asStateFlow()

    private var fetchJob: Job? = null

    fun fetchLatestFilmDataGraphQL() = fetchData {
        val lastFilmInTrilogy = graphqlRepository.getLastTrilogyFilm()?.film ?: return@fetchData
        val film = Film(lastFilmInTrilogy)
        val people = mapCharacters(lastFilmInTrilogy.characterConnection?.characters)
        val planets = mapPlanets(lastFilmInTrilogy.planetConnection?.planets)

        _uiState.emit(StarWarsUiState(film, people, planets))
    }

    fun fetchLatestFilmDataREST() = fetchData {
        val lastFilmInTrilogy = restRepository.getLastTrilogyFilm()

        val peopleDeferred = viewModelScope.async {
            lastFilmInTrilogy.characters.map { async { restRepository.getPeople(it) } }.awaitAll()
        }
        val planetDeferred = viewModelScope.async {
            lastFilmInTrilogy.planets.map { async { restRepository.getPlanet(it) } }.awaitAll()
        }

        _uiState.emit(
            StarWarsUiState(
                lastFilmInTrilogy,
                peopleDeferred.await(),
                planetDeferred.await()
            )
        )
    }

    private fun fetchData(block: suspend () -> Unit) {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            val beforeHeapSize = Debug.getNativeHeapSize()
            try {
                block()
            } catch (ioe: IOException) {
                handleError(ioe)
            } finally {
                logHeapUsage(beforeHeapSize)
            }
        }
    }

    private suspend fun handleError(ioe: IOException) {
        _uiState.update {
            it.copy(message = ioe.message.orEmpty())
        }
    }

    private fun logHeapUsage(beforeHeapSize: Long) {
        val heapDifference = Debug.getNativeHeapSize() - beforeHeapSize
        Log.d("Memory", "Heap usage increased by $heapDifference bytes")
    }

    private fun mapCharacters(characters: List<ReturnOfJediQuery.Character?>?) = characters?.map {
        People(it?.name.orEmpty())
    } ?: emptyList()

    private fun mapPlanets(planets: List<ReturnOfJediQuery.Planet?>?) =
        planets?.filterNotNull()?.map {
            Planet(it.name.orEmpty(), it.population.toString(), it.diameter.toString())
        } ?: emptyList()
}


data class StarWarsUiState(
    val film: Film? = null,
    val characters: List<People> = listOf(),
    val planets: List<Planet> = listOf(),
    val message: String = ""
)

