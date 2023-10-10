package ru.netology.nmedia.repository.user

import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.User

interface UserRepository {
    suspend fun getUserById(id: Int): User
    suspend fun getPostById(id: Int): Post?
}