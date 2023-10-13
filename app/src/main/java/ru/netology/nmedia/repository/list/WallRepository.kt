package ru.netology.nmedia.repository.list

interface WallRepository {
    suspend fun getUserWall(id: Int)

}