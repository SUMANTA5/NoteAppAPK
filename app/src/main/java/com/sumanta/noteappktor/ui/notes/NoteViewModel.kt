package com.sumanta.noteappktor.ui.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sumanta.noteappktor.data.local.model.LocalNote
import com.sumanta.noteappktor.repository.NoteRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.SimpleFormatter
import javax.inject.Inject

@HiltViewModel
class NoteViewModel
@Inject
constructor(
    private val noteRepo: NoteRepo
) : ViewModel() {

    var oldNote: LocalNote? = null

    fun createNote(
        noteTitle: String?,
        description: String?
    ) = viewModelScope.launch(Dispatchers.IO) {
        val localNote = LocalNote(
            noteTitle = noteTitle,
            description = description
        )
        noteRepo.createNote(localNote)
    }


    fun updateNote(
        noteTitle: String?,
        description: String?
    ) = viewModelScope.launch(Dispatchers.IO) {
        if (noteTitle == oldNote?.noteTitle &&
            description == oldNote?.description &&
            oldNote?.connected == true
        ) {
            return@launch
        }
        val note = LocalNote(
            noteTitle = noteTitle,
            description = description,
            noteId = oldNote!!.noteId
        )
        noteRepo.updateNote(note)
    }

    fun milliToDate(time: Long): String {
        val date = Date(time)
        val simpleDateFormat = SimpleDateFormat("hh:mm a | MMM d, yyyy", Locale.getDefault())
        return simpleDateFormat.format(date)
    }

}