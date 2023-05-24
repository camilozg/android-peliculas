package com.example.moviesapp

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.ImageView
import android.widget.TextView
import com.github.ajalt.timberkt.d
import com.squareup.picasso.Picasso
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailActivity : AppCompatActivity() {
    private val viewModel: DetailActivityViewModel by viewModel()

    companion object{
        const val MOVIE_ID = "movie_key"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        var movieId = intent.getStringExtra(MOVIE_ID)!!.toInt()
        var movie = viewModel.getMovie(movieId)

        findViewById<TextView>(R.id.movieNameDetail).text = movie.name
        findViewById<TextView>(R.id.movieReleaseDetail).text = movie.release
        findViewById<TextView>(R.id.moviePlaytimeDetail).text = movie.playtime
        findViewById<TextView>(R.id.movieDescriptionDetail).text = movie.description
        findViewById<TextView>(R.id.movieDescriptionDetail).movementMethod = ScrollingMovementMethod()

        Picasso.get()
            .load(Uri.parse(movie.posterUrl))
            .resize(200, 200)
            .centerInside()
            .placeholder(R.drawable.camera_image)
            .into(findViewById<ImageView>(R.id.movieImageDetail))
    }
}