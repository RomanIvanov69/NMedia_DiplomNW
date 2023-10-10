package ru.netology.nmedia.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.Job
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.job.JobRepository
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.util.SingleLiveEvent
import javax.inject.Inject

private val empty = Job(
    id = 0,
    name = "",
    position = "",
    start = "",
    finish = null,
    link = null,
)

@HiltViewModel
class JobViewModel @Inject constructor(
    private val repository: JobRepository,
    appAuth: AppAuth,
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val data: Flow<List<Job>> = appAuth.authState
        .flatMapLatest { (ownerId, _) ->
            repository.data.map {
                it.map { job ->
                    job.copy(
                        ownedByMe = userId == ownerId
                    )
                }
            }
        }

    private val _dataState = MutableLiveData<FeedModel>()
    val dataState: LiveData<FeedModel>
        get() = _dataState

    private val edited = MutableLiveData(empty)

    private var userId: Int = 0

    private val _jobCreated = SingleLiveEvent<Unit>()
    val jobCreated: LiveData<Unit>
        get() = _jobCreated

    fun getJobsByUserId(id: Int) {
        viewModelScope.launch {
            try {
                userId = id
                repository.getJobsById(id)
                _dataState.value = FeedModel()
            } catch (e: Exception) {
                _dataState.value = FeedModel(error = true)
            }
        }
    }

    fun save() {
        edited.value?.let { job ->
            viewModelScope.launch {
                try {
                    repository.save(job)
                    _jobCreated.value = Unit
                    edited.value = empty
                    _dataState.value = FeedModel()
                } catch (e: Exception) {
                    _dataState.value = FeedModel(error = true)
                }
            }
        }
    }

    fun edit(job: Job) {
        edited.value = job
    }

    fun getEditJob(): Job? {
        return if (edited.value == null || edited.value == empty) null else edited.value
    }

    fun clearEdited() {
        edited.value = empty
    }

    fun changeContent(
        name: String,
        position: String,
        start: String,
        finish: String?,
        link: String?,
    ) {
        edited.value = edited.value?.copy(
            name = name.trim(),
            position = position.trim(),
            start = start,
            finish = finish,
            link = link
        )
    }

    fun removeById(id: Int) {
        viewModelScope.launch {
            try {
                repository.removeById(id)
                _dataState.value = FeedModel()
            } catch (e: Exception) {
                _dataState.value = FeedModel(error = true)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentJob(list: List<Job>) =
        list.maxBy { AndroidUtils.dateToTimestamp(it.start) }
}