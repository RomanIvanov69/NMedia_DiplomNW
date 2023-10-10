package ru.netology.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nmedia.dto.User
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.user.UserRepository
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    private val _userDataState = MutableLiveData<FeedModel>()
    val userDataState: LiveData<FeedModel>
        get() = _userDataState

    fun getUserById(id: Int) = viewModelScope.launch {
        try {
            _user.value = userRepository.getUserById(id)
            _userDataState.value = FeedModel()
        } catch (e: Exception) {
            _userDataState.value = FeedModel(error = true)
        }
    }
}