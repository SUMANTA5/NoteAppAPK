package com.sumanta.noteappktor.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class LocalNote(
    @PrimaryKey(autoGenerate = false)
    var noteId: String = UUID.randomUUID().toString(),


    var noteTitle: String? = null,
    var description: String? = null,
    var date: Long = System.currentTimeMillis(),
    var connected: Boolean = false,
    var locallyDeleted: Boolean = false,
)
