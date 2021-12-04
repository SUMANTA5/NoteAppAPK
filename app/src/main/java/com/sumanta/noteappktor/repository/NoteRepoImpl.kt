package com.sumanta.noteappktor.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.sumanta.noteappktor.data.local.dao.NoteDao
import com.sumanta.noteappktor.data.local.model.LocalNote
import com.sumanta.noteappktor.data.remote.api.NoteApi
import com.sumanta.noteappktor.data.remote.model.RemoteNote
import com.sumanta.noteappktor.data.remote.model.User
import com.sumanta.noteappktor.uitl.Result
import com.sumanta.noteappktor.uitl.SessionManager
import com.sumanta.noteappktor.uitl.isNetworkConnected
import java.lang.Exception
import javax.inject.Inject

class NoteRepoImpl
@Inject
    constructor(
    private val noteApi: NoteApi,
    val noteDao: NoteDao,
    private val sessionManager: SessionManager
    ) : NoteRepo {
    override suspend fun createNote(note: LocalNote): Result<String> {
        return try {
           noteDao.insertNote(note)
            val token = sessionManager.getJwtToken()
            if (token == null){
                Result.Success("Note Is Saved In Local Database")
            }
            val result = noteApi.createNote(
                "Bearer $token",
                RemoteNote(
                    noteTitle = note.noteTitle,
                    description = note.description,
                    date = note.date,
                    id = note.noteId
                )
            )

            if (result.success){
                noteDao.insertNote(note.also {
                    it.connected = true
                })
                Result.Success("Note Saved Successfully")
            }else{
                Result.Error(result.message)
            }

        }catch (e:Exception){
            e.printStackTrace()
            Result.Error(e.message?:"Some Problem")
        }
    }

    override suspend fun updateNote(note: LocalNote): Result<String> {
         return try {
            noteDao.insertNote(note)
             val token = sessionManager.getJwtToken()
             if (token == null){
                 Result.Success("Note Is Updated In Local Database")
             }
             val result = noteApi.updateNote(
                 "Bearer $token",
                 RemoteNote(
                     noteTitle = note.noteTitle,
                     description = note.description,
                     date = note.date,
                     id = note.noteId
                 )
             )

             if (result.success){
                 noteDao.insertNote(note.also {
                     it.connected = true
                 })
                 Result.Success("Note Updated Successfully")
             }else{
                 Result.Error(result.message)
             }

         }catch (e:Exception){
             e.printStackTrace()
             Result.Error(e.message?:"Some Problem")
         }
    }

    //================ user =================//

    @RequiresApi(Build.VERSION_CODES.M)
    override suspend fun createUser(user: User): Result<String> {

        return try {
            if (!isNetworkConnected(sessionManager.context)){
                Result.Error<String>("No Internet Connection!")
            }
            val result = noteApi.createAccount(user)
            if (result.success){
                sessionManager.updateSession(result.message,user.name ?:"",user.email)
                Result.Success("User Created Successfully")
            }else{
                Result.Error<String>(result.message)
            }

        }catch (e:Exception){
            e.printStackTrace()
            Result.Error(e.message ?: "Some Problem Occurred!")
        }

    }

    @RequiresApi(Build.VERSION_CODES.M)
    override suspend fun login(user: User): Result<String> {
        return try {
            if (!isNetworkConnected(sessionManager.context)){
                Result.Error<String>("No Internet Connection!")
            }
            val result = noteApi.login(user)
            if (result.success){
                sessionManager.updateSession(result.message,user.name ?:"",user.email)
                Result.Success("Logged In Successfully")
            }else{
                Result.Error<String>(result.message)
            }

        }catch (e:Exception){
            e.printStackTrace()
            Result.Error(e.message ?: "Some Problem Occurred!")
        }
    }

    override suspend fun gatUser(): Result<User> {
        return try {
            val name = sessionManager.getCurrentUserName()
            val email = sessionManager.getCurrentUserEmail()
            if (name == null || email == null) {
                Result.Error<String>("User not Logged In!")
            }
            Result.Success(User(name, email!!, ""))
        }catch (e:Exception){
            e.printStackTrace()
            Result.Error(e.message ?: "Some Problem Occurred!")
        }
    }

    override suspend fun logout(): Result<String> {
        return try {
            sessionManager.logout()
            Result.Success("Logged Out Successfully")
        }catch (e:Exception){
            e.printStackTrace()
            Result.Error(e.message ?: "Some Problem Occurred!")
        }
    }
}