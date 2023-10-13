package ru.netology.nmedia.repository.list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dto.FeedItem
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WallRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : WallRepository {

    private val wallList = mutableListOf<FeedItem>()

    private val _wall = MutableLiveData<List<FeedItem>>()

    override suspend fun getUserWall(id: Int) {
        try {
            val response = apiService.getWall(id)
            val posts = response.body() ?: throw ApiError(response.code(), response.message())
            for (post in posts) {
                wallList.add(post)
            }
            _wall.value = wallList
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }
}