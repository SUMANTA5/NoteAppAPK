package com.sumanta.noteappktor.repository

import com.sumanta.noteappktor.data.local.model.LocalNote
import com.sumanta.noteappktor.data.remote.model.User
import com.sumanta.noteappktor.uitl.Result
import kotlinx.coroutines.flow.Flow

interface NoteRepo {

    suspend fun createUser(user: User): Result<String>
    suspend fun login(user: User): Result<String>
    suspend fun gatUser(): Result<User>
    suspend fun logout(): Result<String>


    //================ note =================//


    suspend fun createNote(note: LocalNote): Result<String>
    suspend fun updateNote(note: LocalNote): Result<String>
    fun getAllNote(): Flow<List<LocalNote>>
    suspend fun gatAllNoteFromServer()


}