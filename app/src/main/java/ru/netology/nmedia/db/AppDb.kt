package ru.netology.nmedia.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.netology.nmedia.dao.JobDao
import ru.netology.nmedia.dao.post.PostDao
import ru.netology.nmedia.dao.post.PostRemoteKeyDao
import ru.netology.nmedia.dao.event.EventDao
import ru.netology.nmedia.dao.event.EventRemoteKeyDao
import ru.netology.nmedia.entity.Converter
import ru.netology.nmedia.entity.JobEntity
import ru.netology.nmedia.entity.post.PostEntity
import ru.netology.nmedia.entity.post.PostRemoteKeyEntity
import ru.netology.nmedia.entity.event.EventEntity
import ru.netology.nmedia.entity.event.EventRemoteKeyEntity

@Database(
    entities = [
        PostEntity::class, PostRemoteKeyEntity::class,
        EventEntity::class, EventRemoteKeyEntity::class,
        JobEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converter::class)
abstract class AppDb : RoomDatabase() {

    abstract fun postDao(): PostDao
    abstract fun postRemoteKeyDao(): PostRemoteKeyDao
    abstract fun eventDao(): EventDao
    abstract fun eventRemoteKeyDao(): EventRemoteKeyDao
    abstract fun jobDao(): JobDao
}