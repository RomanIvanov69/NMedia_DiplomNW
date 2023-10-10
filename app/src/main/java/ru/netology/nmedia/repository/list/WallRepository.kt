package ru.netology.nmedia.repository.list

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.FeedItem

interface WallRepository {

    fun loadUserWall(id: Int): Flow<PagingData<FeedItem>>
}