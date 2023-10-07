package ru.netology.nmedia.repository.auth

import ru.netology.nmedia.model.AuthModel
import ru.netology.nmedia.model.MediaModel

interface AuthRepository {
    suspend fun authentication(login: String, password: String): AuthModel
    suspend fun registration(login: String, password: String, name: String): AuthModel
    suspend fun registerWithPhoto(login: String, password: String, name: String, media: MediaModel): AuthModel
}