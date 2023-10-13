package ru.netology.nmedia.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.enumeration.AttachmentType
import ru.netology.nmedia.model.AuthModel
import ru.netology.nmedia.model.AuthModelState
import ru.netology.nmedia.model.MediaModel
import ru.netology.nmedia.repository.auth.AuthRepository
import java.io.File
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val appAuth: AppAuth
) : ViewModel() {

    val data: LiveData<AuthModel> = appAuth
        .authStateFlow
        .asLiveData(Dispatchers.Default)

    private val _state = MutableLiveData<AuthModelState>()
    val state: LiveData<AuthModelState>
        get() = _state

    private val _media = MutableLiveData<MediaModel?>(null)
    val media: LiveData<MediaModel?>
        get() = _media


    fun registration(login: String, pass: String, name: String) = viewModelScope.launch {
        if (login.isNotBlank() && pass.isNotBlank() && name.isNotBlank()) {
            val avatar = media.value
            if (avatar != null) {
                try {
                    _state.value = AuthModelState(loading = true)
                    val result = repository.registerWithPhoto(login, pass, name, avatar)
                    appAuth.setAuth(result.id, result.token)
                    _state.value = AuthModelState(register = true)
                } catch (e: Exception) {
                    _state.value = AuthModelState(error = true)
                }

            } else {
                try {
                    _state.value = AuthModelState(loading = true)
                    val result = repository.registration(login, pass, name)
                    appAuth.setAuth(result.id, result.token)
                    _state.value = AuthModelState(register = true)
                } catch (e: Exception) {
                    _state.value = AuthModelState(error = true)
                }
            }
        } else {
            _state.value = AuthModelState(isEmpty = true)
        }
        _state.value = AuthModelState()
        clearPhoto()
    }

    fun addPhoto(uri: Uri, file: File, type: AttachmentType) {
        _media.value = MediaModel(uri, file, type)
    }

    fun clearPhoto() {
        _media.value = null
    }
}