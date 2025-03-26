package com.lartschy.beyond90.di


import com.lartschy.beyond90.data.remote.FootballApi
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
    fun provideFootballApi(): FootballApi {
        return Retrofit.Builder()
            .baseUrl("https://www.thesportsdb.com/api/v1/json/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FootballApi::class.java)
    }

    @Provides
    @Singleton
    fun provideFootballRepository(api: FootballApi): FootballRepository {
        return FootballRepository(api)
    }
}
