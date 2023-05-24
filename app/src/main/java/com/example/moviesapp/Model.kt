package com.example.moviesapp

import androidx.datastore.core.DataStore
import com.github.ajalt.timberkt.d
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class Model(private val disneyService: DisneyService, private val moviesDataStore: DataStore<MovieStore>) {

    private var coroutineScope = CoroutineScope(Job() + Dispatchers.Default)
    private lateinit var movieList: List<Movie>
    private val _movies = MutableStateFlow<List<Movie>>(listOf())
    val movies = _movies as StateFlow<List<Movie>>

    init {
        coroutineScope.launch {
            movieList = disneyService.loadMovies()
            moviesDataStore.data
                .map { it.initialized }
                .filter { !it }
                .first {
                    d { "Initialize data store..." }
                    initDataStore()
                    return@first true
                }
        }

        coroutineScope.launch {
            moviesDataStore.data
                .collect { movieStore ->
                    d { "Movies count: ${movieStore.moviesCount}" }
                    val movies = movieStore.moviesList.map { Movie.fromStoredMovie(it) }
                    _movies.emit(movies)
                }
        }

        coroutineScope.launch {
            movieList = disneyService.loadMovies()
            moviesDataStore.data.collect{ movieStore ->
                if (movieStore.moviesCount == 0){
                    initDataStore()
                }
            }
        }
    }

    private fun initDataStore() {
        // create the storedMovies list
        val moviesToStore = movieList.map { it.asStoredMovie() }

        // save data to data store
        coroutineScope.launch {
            moviesDataStore.updateData { movieStore ->
                movieStore.toBuilder()
                    .addAllMovies(moviesToStore)
                    .setInitialized(true)
                    .build()
            }
        }
    }

    fun removeMovie(movie: Movie) {
        val toStoreMovies = movies.value
            .filter { it.id != movie.id }
            .map { it.asStoredMovie() }

        coroutineScope.launch {
            moviesDataStore.updateData { movieStore ->
                movieStore.toBuilder()
                    .clearMovies()
                    .addAllMovies(toStoreMovies)
                    .build()
            }
        }
    }

    fun getMovie(movieId: Int): Movie {
        return movies.value.first { it.id == movieId }
    }
}