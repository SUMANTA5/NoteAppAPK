package com.sumanta.noteappktor.di.module

import android.content.Context
import com.google.gson.Gson
import com.sumanta.noteappktor.uitl.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideGson() = Gson()

    @Provides
    @Singleton
    fun provideSessionManager(
        @ApplicationContext context: Context
    ) = SessionManager(context)


}