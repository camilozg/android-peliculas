package com.example.moviesapp

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber


class App: Application() {

    private val moviesDataStore: DataStore<MovieStore> by dataStore(
        fileName = "movies.pb",
        serializer = MovieStoreSerializer
    )

    override fun onCreate() {
        super.onCreate()

        // timber
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        //koin
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@App)
            modules(
                module { single { moviesDataStore } },
                mainModule,
                mainActivity,
                detailActivity
            )
        }
    }
}

val mainModule = module {

    single {
        Retrofit.Builder()
                // host
            .baseUrl("https://gist.githubusercontent.com/")
                // parsing system
            .addConverterFactory(
                MoshiConverterFactory.create(
                    Moshi.Builder()
                        .add(KotlinJsonAdapterFactory())
                        .build()
                )
            )
                // create instance of the service
            .build().create(DisneyService::class.java)
    }

    // app model
    single { Model(disneyService = get(), moviesDataStore = get()) }
}

val mainActivity = module {
    viewModel { MainActivityViewModel(model = get()) }
}

val detailActivity = module {
    viewModel { DetailActivityViewModel(model = get()) }
}