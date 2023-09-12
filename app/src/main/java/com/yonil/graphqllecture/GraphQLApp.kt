package com.yonil.graphqllecture

import android.app.Application
import com.apollographql.apollo3.ApolloClient
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class GraphQLApp : Application() {

    lateinit var retrofit: Retrofit
        private set

    lateinit var apolloClient  : ApolloClient
        private set
    override fun onCreate() {
        super.onCreate()

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .readTimeout(60, TimeUnit.SECONDS)  // Set read timeout
            .writeTimeout(60, TimeUnit.SECONDS) // Set write timeout
            .connectTimeout(60, TimeUnit.SECONDS) // Set connect timeout
            .build()


        retrofit = Retrofit.Builder()
            .baseUrl("https://swapi.dev/api/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apolloClient = ApolloClient.Builder()
            .serverUrl("https://swapi-graphql.netlify.app/.netlify/functions/index")
            .build()

    }
}