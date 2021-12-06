package com.sumanta.noteappktor.di.module

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.sumanta.noteappktor.data.local.dao.NoteDao
import com.sumanta.noteappktor.data.local.database.NoteDatabase
import com.sumanta.noteappktor.data.remote.api.NoteApi
import com.sumanta.noteappktor.repository.NoteRepo
import com.sumanta.noteappktor.repository.NoteRepoImpl
import com.sumanta.noteappktor.uitl.SessionManager
import com.sumanta.noteappktor.uitl.constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
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

    @Provides
    @Singleton
    fun provideNoteDatabase(
        @ApplicationContext context: Context
    ): NoteDatabase = Room.databaseBuilder(
        context,
        NoteDatabase::class.java,
        "note_db"
    ).build()

    @Provides
    @Singleton
    fun provideNoteDao(
        noteDb: NoteDatabase
    ) = noteDb.getNoteDao()

    @Provides
    @Singleton
    fun provideNoteApi(): NoteApi {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NoteApi::class.java)
    }

    @Provides
    @Singleton
    fun provideNoteRepo(
        noteApi: NoteApi,
        noteDao: NoteDao,
        sessionManager: SessionManager
    ): NoteRepo {
        return NoteRepoImpl(
            noteApi, noteDao, sessionManager
        )
    }


}