package com.yonil.graphqllecture.view

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yonil.graphqllecture.model.Film
import com.yonil.graphqllecture.model.People
import com.yonil.graphqllecture.model.Planet
import com.yonil.graphqllecture.view.ui.theme.GraphQLLectureTheme
import com.yonil.graphqllecture.viewmodel.StarWarsViewModel

class MainActivity : ComponentActivity() {

    private lateinit var starWarsViewModel: StarWarsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContent {
            GraphQLLectureTheme {
                val uiState by starWarsViewModel.uiState.collectAsState()
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val film = uiState.film
                    if (uiState.message.isNotEmpty()) {
                        Message(message = uiState.message)
                        return@Surface
                    }

                    if (film == null) {
                        Greeting(name = "Graphql!")
                        return@Surface
                    }

                    StarWarsInfo(film, uiState.characters, uiState.planets)
                }
            }
        }
        val factory = StarWarsViewModelFactory(application)
        starWarsViewModel = ViewModelProvider(this, factory)[StarWarsViewModel::class.java]

        starWarsViewModel.fetchLatestFilmData()
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Composable
fun Message(message: String, modifier: Modifier = Modifier) {
    Text(
        text = message,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GraphQLLectureTheme {
        Greeting("Android")
    }
}

@Composable
fun StarWarsInfo(film: Film, people: List<People>, planets: List<Planet>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = "Film",
                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Text(
                text = film.title,
                style = TextStyle(fontSize = 18.sp),
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        item {
            Text(
                text = "Characters",
                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }

        items(people) { person ->
            Text(
                text = person.name,
                style = TextStyle(fontSize = 16.sp),
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        item {
            Text(
                text = "Planets",
                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }

        items(planets) { planet ->
            Text(
                text = "${planet.name}, Population: ${planet.population}, Diameter: ${planet.diameter}",
                style = TextStyle(fontSize = 16.sp),
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}

class StarWarsViewModelFactory(private val application: Application) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StarWarsViewModel::class.java)) {
            return StarWarsViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}