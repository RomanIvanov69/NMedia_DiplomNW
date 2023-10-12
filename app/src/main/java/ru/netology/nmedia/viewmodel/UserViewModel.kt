package ru.netology.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.netology.nmedia.dto.User
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.list.WallRepository
import ru.netology.nmedia.repository.user.UserRepository
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    val data = userRepository.data.asLiveData(Dispatchers.Default)

    private val _dataState = MutableLiveData<FeedModel>()
    val dataState: LiveData<FeedModel>
        get() = _dataState


    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    private val _userDataState = MutableLiveData<FeedModel>()
    val userDataState: LiveData<FeedModel>
        get() = _userDataState

    init {
        loadPosts()
    }

    fun getWall(id: Int) {
        viewModelScope.launch {
            try {
                _dataState.postValue(FeedModel(loading = true))
                userRepository.loadUserWall(id)
                _dataState.value = FeedModel()
            } catch (e: Exception) {
                _dataState.value = FeedModel(error = true)
            }
        }
    }

    fun loadPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModel(loading = true)
            userRepository.getAll()
            _dataState.value = FeedModel()
        } catch (e: Exception) {
            _dataState.value = FeedModel(error = true)
        }
    }


    fun getUserById(id: Int) = viewModelScope.launch {
        try {
            _user.value = userRepository.getUserById(id)
            _userDataState.value = FeedModel()
        } catch (e: Exception) {
            _userDataState.value = FeedModel(error = true)
        }
    }
}