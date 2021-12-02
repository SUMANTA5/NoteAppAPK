package com.sumanta.noteappktor.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sumanta.noteappktor.data.local.dao.NoteDao
import com.sumanta.noteappktor.data.local.model.LocalNote

@Database(
    entities = [LocalNote::class],
    version = 1,
    exportSchema = false
)
abstract class NoteDatabase: RoomDatabase() {

    abstract fun getNoteDao(): NoteDao
}