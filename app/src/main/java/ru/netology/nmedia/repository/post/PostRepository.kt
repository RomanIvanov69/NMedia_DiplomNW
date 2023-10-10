package ru.netology.nmedia.repository.post

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.FeedItem
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.MediaModel

interface PostRepository {
    val data: Flow<PagingData<FeedItem>>
    suspend fun likePostById(post: Post)
    suspend fun dislikePostById(post: Post)
    suspend fun save(post: Post)
    suspend fun saveWithAttachment(post: Post, media: MediaModel)
    suspend fun removeById(id: Int)
    suspend fun getPostById(id: Int): Post?
    suspend fun wallRemoveById(id: Int)
}
