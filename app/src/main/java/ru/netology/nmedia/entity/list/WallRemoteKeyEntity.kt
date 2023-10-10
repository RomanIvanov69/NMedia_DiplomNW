package ru.netology.nmedia.entity.list

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.enumeration.RemoteKeyType

@Entity
data class WallRemoteKeyEntity(
    @PrimaryKey
    val type: RemoteKeyType,
    val id: Int,
)