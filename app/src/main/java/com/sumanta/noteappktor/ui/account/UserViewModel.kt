package com.sumanta.noteappktor.ui.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sumanta.noteappktor.data.remote.model.User
import com.sumanta.noteappktor.repository.NoteRepo
import com.sumanta.noteappktor.uitl.Result
import com.sumanta.noteappktor.uitl.constants.MAXIMUM_PASSWORD_LENGTH
import com.sumanta.noteappktor.uitl.constants.MINIMUM_PASSWORD_LENGTH
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class UserViewModel
@Inject
constructor(
    private val noteRepo: NoteRepo
) : ViewModel() {

    private val _registerState = MutableSharedFlow<Result<String>>()
    val registerState: SharedFlow<Result<String>> = _registerState

    private val _loginState = MutableSharedFlow<Result<String>>()
    val loginState: SharedFlow<Result<String>> = _loginState

    private val _currentUserState = MutableSharedFlow<Result<User>>()
    val currentUserState: MutableSharedFlow<Result<User>> = _currentUserState


    fun createUser(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ) = viewModelScope.launch {
        _registerState.emit(Result.Loading())

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || password != confirmPassword) {
            _registerState.emit(Result.Error("Some Fields are Empty"))
            return@launch
        }
        if (!isEmailValid(email)) {
            _registerState.emit(Result.Error("Email Is Not Valid!"))
            return@launch
        }
        if (!isPasswordValid(password)) {
            _registerState.emit(Result.Error("Password Should be Between $MINIMUM_PASSWORD_LENGTH And $MAXIMUM_PASSWORD_LENGTH"))
            return@launch
        }

        val newUser = User(
            name, email, password
        )
        _registerState.emit(noteRepo.createUser(newUser))

    }


    fun loginUser(
        name: String,
        email: String,
        password: String
    ) = viewModelScope.launch {
        _loginState.emit(Result.Loading())

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            _loginState.emit(Result.Error("Some Fields are Empty"))
            return@launch
        }
        if (!isEmailValid(email)) {
            _loginState.emit(Result.Error("Email Is Not Valid!"))
            return@launch
        }
        if (!isPasswordValid(password)) {
            _loginState.emit(Result.Error("Password Should be Between $MINIMUM_PASSWORD_LENGTH And $MAXIMUM_PASSWORD_LENGTH"))
            return@launch
        }

        val newUser = User(
            name, email, password
        )
        _loginState.emit(noteRepo.login(newUser))

    }

    fun getCurrentUser() = viewModelScope.launch {
        _currentUserState.emit(Result.Loading())
        _currentUserState.emit(noteRepo.gatUser())
    }

    fun logout() = viewModelScope.launch {
        val result = noteRepo.logout()
        if (result is Result.Success) {
            getCurrentUser()
        }
    }


    private fun isEmailValid(email: String): Boolean {
        var regex =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
        val pattern = Pattern.compile(regex)
        return (email.isNotEmpty() && pattern.matcher(email).matches())
    }

    private fun isPasswordValid(password: String): Boolean {
        return (password.length in MINIMUM_PASSWORD_LENGTH..MAXIMUM_PASSWORD_LENGTH)
    }

}