package ru.netology.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.list.WallRepository
import javax.inject.Inject

@HiltViewModel
class WallViewModel @Inject constructor(
    val wallRepository: WallRepository,
    appAuth: AppAuth,
) : ViewModel() {


    private val _dataState = MutableLiveData<FeedModel>()
    val dataState: LiveData<FeedModel>
        get() = _dataState

    fun getWall(id: Int) {
        viewModelScope.launch {
            try {
                _dataState.postValue(FeedModel(loading = true))
                wallRepository.getUserWall(id)
                _dataState.value = FeedModel()
            } catch (e: Exception) {
                _dataState.value = FeedModel(error = true)
            }
        }
    }
}