package ru.netology.nmedia.repository.post

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dao.post.PostDao
import ru.netology.nmedia.dao.post.PostRemoteKeyDao
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.entity.post.PostEntity
import ru.netology.nmedia.entity.post.PostRemoteKeyEntity
import ru.netology.nmedia.entity.post.toEntity
import ru.netology.nmedia.enumeration.RemoteKeyType
import ru.netology.nmedia.error.ApiError
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class PostRemoteMediator @Inject constructor(
    private val service: ApiService,
    private val postDao: PostDao,
    private val appDb: AppDb,
    private val postRemoteKeyDao: PostRemoteKeyDao,
) : RemoteMediator<Int, PostEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PostEntity>,
    ): MediatorResult {
        try {
            val response = when (loadType) {
                LoadType.REFRESH -> {
                    postRemoteKeyDao.max()?.let { id ->
                        service.getPostsAfter(id, state.config.pageSize)
                    } ?: service.getLatestPosts(state.config.initialLoadSize)
                }

                LoadType.PREPEND -> return MediatorResult.Success(false)
                LoadType.APPEND -> {
                    val id = postRemoteKeyDao.min() ?: return MediatorResult.Success(false)
                    service.getPostsBefore(id, state.config.pageSize)
                }
            }

            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())

            appDb.withTransaction {
                when (loadType) {
                    LoadType.REFRESH -> {
                        if (postRemoteKeyDao.isEmpty()) {
                            postRemoteKeyDao.clear()
                            postRemoteKeyDao.insert(
                                listOf(
                                    PostRemoteKeyEntity(
                                        type = RemoteKeyType.AFTER,
                                        id = body.first().id,
                                    ),
                                    PostRemoteKeyEntity(
                                        type = RemoteKeyType.BEFORE,
                                        id = body.last().id
                                    ),
                                )
                            )
                            postDao.clear()
                        } else {
                            postRemoteKeyDao.insert(
                                PostRemoteKeyEntity(
                                    type = RemoteKeyType.AFTER,
                                    id = body.first().id
                                )
                            )
                        }
                    }

                    LoadType.PREPEND -> {}
                    LoadType.APPEND -> {
                        postRemoteKeyDao.insert(
                            PostRemoteKeyEntity(
                                type = RemoteKeyType.BEFORE,
                                id = body.last().id,
                            )
                        )
                    }
                }
                postDao.insert(body.toEntity())
            }
            return MediatorResult.Success(endOfPaginationReached = body.isEmpty())
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }
}