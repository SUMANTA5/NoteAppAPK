package com.sumanta.noteappktor.data.remote.model

data class RemoteNote(
    val noteTitle: String?,
    val description: String?,
    val date: Long,
    val id: String
)
