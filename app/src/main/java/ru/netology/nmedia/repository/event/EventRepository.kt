package ru.netology.nmedia.repository.event

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Event
import ru.netology.nmedia.dto.FeedItem
import ru.netology.nmedia.model.MediaModel

interface EventRepository {
    val data: Flow<PagingData<FeedItem>>
    suspend fun save(event: Event)
    suspend fun saveWithAttachment(event: Event, media: MediaModel)
    suspend fun likeById(event: Event)
    suspend fun removeById(id: Int)
    suspend fun getById(id: Int): Event?
}