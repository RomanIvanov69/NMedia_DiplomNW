package ru.netology.nmedia.repository.user

import ru.netology.nmedia.dto.User

interface UserRepository {
    suspend fun getUserById(id: Int): User
}