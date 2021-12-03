package com.sumanta.noteappktor.repository

import com.sumanta.noteappktor.data.remote.model.User
import com.sumanta.noteappktor.uitl.Result

interface NoteRepo {

    suspend fun createUser(user: User):Result<String>
    suspend fun login(user: User):Result<String>
    suspend fun gatUser():Result<User>
    suspend fun logout():Result<String>


}