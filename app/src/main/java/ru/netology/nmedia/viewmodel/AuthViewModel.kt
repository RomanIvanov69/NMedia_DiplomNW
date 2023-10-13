package ru.netology.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.model.AuthModel
import ru.netology.nmedia.model.AuthModelState
import ru.netology.nmedia.repository.auth.AuthRepository
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val appAuth: AppAuth,
    private val repository: AuthRepository): ViewModel() {
    val data: LiveData<AuthModel> = appAuth
        .authStateFlow
        .asLiveData(Dispatchers.Default)

    val authorized: Boolean
        get() = appAuth.authStateFlow.value.id != 0

    private val _state = MutableLiveData<AuthModelState>()
    val state: LiveData<AuthModelState>
        get() = _state

    fun authentication(login: String, password: String) = viewModelScope.launch {
        if (login.isNotBlank() && password.isNotBlank()) {
            try {
                _state.value = AuthModelState(loading = true)
                val result = repository.authentication(login, password)
                appAuth.setAuth(result.id, result.token)
                _state.value = AuthModelState(register = true)
            } catch (e: Exception) {
                when (e) {
                    is ApiError -> if (e.status == 404) _state.value =
                        AuthModelState(incorrectLoginOrPass = true)
                    else -> _state.value = AuthModelState(error = true)
                }
            }
        } else {
            _state.value = AuthModelState(isEmpty = true)
        }
        _state.value = AuthModelState()
    }

    fun logout() {
        appAuth.removeAuth()
        _state.value = AuthModelState(notRegister = true)
    }
}
