package com.example.moviesapp

import retrofit2.http.GET

interface DisneyService {

    @GET("skydoves/176c209dbce4a53c0ff9589e07255f30/raw/6489d9712702e093c4df71500fb822f0d408ef75/DisneyPosters2.json")
    suspend fun loadMovies(): List<Movie>

}