package com.lartschy.beyond90.di

import com.lartschy.beyond90.data.remote.FootballApiService
import com.lartschy.beyond90.data.repository.FootballRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideLeagueApi(): FootballApiService {
        return Retrofit.Builder()
            .baseUrl("https://www.thesportsdb.com/api/v1/json/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FootballApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideLeagueRepository(
        footballApiService: FootballApiService
    ): FootballRepository {
        return FootballRepository(footballApiService)
    }

}

