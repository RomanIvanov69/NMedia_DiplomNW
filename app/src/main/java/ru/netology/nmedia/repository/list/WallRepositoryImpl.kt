package ru.netology.nmedia.repository.list

import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dto.FeedItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WallRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
) : WallRepository {

    val wallList = mutableListOf<FeedItem>()

    private val _wall = MutableLiveData<List<FeedItem>>()

}