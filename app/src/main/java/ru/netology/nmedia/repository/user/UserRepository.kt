package ru.netology.nmedia.repository.user

import androidx.paging.PagingData
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.User
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.FeedItem

interface UserRepository {

    val data: Flow<PagingData<FeedItem>>
    suspend fun getUserById(id: Int): User
    suspend fun getAll()
    suspend fun getPostById(id: Int): Post?
    suspend fun loadUserWall(id: Int)
}