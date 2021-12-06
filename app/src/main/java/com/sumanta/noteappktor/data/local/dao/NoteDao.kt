package com.sumanta.noteappktor.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sumanta.noteappktor.data.local.model.LocalNote
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: LocalNote)

    @Query("SELECT * FROM LocalNote WHERE locallyDeleted =0 ORDER BY date DESC")
    fun getAllNotesOrderedByData(): Flow<List<LocalNote>>

    @Query("DELETE FROM LocalNote WHERE noteId=:noteId")
    suspend fun deleteNote(noteId: String)

    @Query("UPDATE LocalNote SET locallyDeleted = 1 WHERE noteId =:noteId")
    suspend fun deleteNoteLocally(noteId: String)

    @Query("SELECT * FROM LocalNote WHERE connected = 0")
    suspend fun getAllLocalNotes(): List<LocalNote>

    @Query("SELECT * FROM LocalNote WHERE locallyDeleted = 1")
    suspend fun getAllLocallyDeletedNotes(): List<LocalNote>

}