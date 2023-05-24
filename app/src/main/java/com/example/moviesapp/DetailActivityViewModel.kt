package com.example.moviesapp

import androidx.lifecycle.ViewModel

class DetailActivityViewModel(private val model: Model) : ViewModel() {
    fun getMovie(movieId: Int): Movie {
        return model.getMovie(movieId)
    }
}