package ru.netology.nmedia.repository.user

import ru.netology.nmedia.dto.User
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Post

interface UserRepository {

    val data: Flow<List<Post>>
    suspend fun getUserById(id: Int): User
    suspend fun getPostUserById(id: Int)
}