package ru.netology.nmedia.entity.event

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.enumeration.RemoteKeyType

@Entity
data class EventRemoteKeyEntity(
    @PrimaryKey
    val type: RemoteKeyType,
    val id: Int
)