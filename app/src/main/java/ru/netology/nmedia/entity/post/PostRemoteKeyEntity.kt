package ru.netology.nmedia.entity.post

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.enumeration.RemoteKeyType

@Entity
data class PostRemoteKeyEntity(
    @PrimaryKey
    val type: RemoteKeyType,
    val id: Int,
)

