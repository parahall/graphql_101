package com.yonil.graphqllecture.repository

import com.apollographql.apollo3.ApolloClient
import com.yonil.graphqllecture.ReturnOfJediQuery
import com.yonil.graphqllecture.api.SWAPIService
import com.yonil.graphqllecture.model.Film
import com.yonil.graphqllecture.model.People
import com.yonil.graphqllecture.model.Planet


class RESTRepository(private val api: SWAPIService) {
    suspend fun getLastTrilogyFilm(): Film {
        return api.getLastTrilogyFilm()
    }

    suspend fun getPeople(url: String): People {
        return api.getPeople(url)
    }

    suspend fun getPlanet(url: String): Planet {
        return api.getPlanet(url)
    }
}


class GraphQLRepository(private val graphqlApi: ApolloClient) {
    suspend fun getLastTrilogyFilm(): ReturnOfJediQuery.Data? {
        return graphqlApi.query(ReturnOfJediQuery()).execute().data
    }
}
