package ru.netology.nmedia.repository.list

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dao.list.WallDao
import ru.netology.nmedia.dao.list.WallRemoteKeyDao
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.entity.list.WallEntity
import ru.netology.nmedia.entity.list.WallRemoteKeyEntity
import ru.netology.nmedia.entity.list.toEntity
import ru.netology.nmedia.enumeration.RemoteKeyType
import ru.netology.nmedia.error.ApiError
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class WallRemoteMediator @Inject constructor(
    private val service: ApiService,
    private val wallDao: WallDao,
    private val appDb: AppDb,
    private val wallRemoteKeyDao: WallRemoteKeyDao,
    private val authorId: Int
) : RemoteMediator<Int, WallEntity>() {

    private var id = 0

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, WallEntity>
    ): MediatorResult {

        if (authorId != id) {
            wallRemoteKeyDao.removeAll()
            wallDao.removeAll()
        }
        id = authorId

        try {
            val response = when (loadType) {
                LoadType.REFRESH -> {
                    service.listGetLatest(
                        authorId = authorId,
                        count = state.config.initialLoadSize
                    )
                }
                LoadType.PREPEND -> return MediatorResult.Success(false)

                LoadType.APPEND -> {
                    val id = wallRemoteKeyDao.min() ?: return MediatorResult.Success(false)
                    service.listGetBefore(
                        authorId = authorId,
                        postId = id,
                        count = state.config.pageSize
                    )
                }
            }

            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())

            appDb.withTransaction {
                when (loadType) {
                    LoadType.REFRESH -> {
                        wallRemoteKeyDao.removeAll()
                        wallDao.removeAll()
                        wallRemoteKeyDao.insert(
                            listOf(
                                WallRemoteKeyEntity(
                                    type = RemoteKeyType.AFTER,
                                    id = body.first().id,
                                ),
                                WallRemoteKeyEntity(
                                    type = RemoteKeyType.BEFORE,
                                    id = body.last().id
                                ),
                            )
                        )
                    }
                    LoadType.PREPEND -> {}
                    LoadType.APPEND -> {
                        wallRemoteKeyDao.insert(
                            WallRemoteKeyEntity(
                                type = RemoteKeyType.BEFORE,
                                id = body.last().id,
                            )
                        )
                    }
                }
                wallDao.insert(body.toEntity())
            }
            return MediatorResult.Success(endOfPaginationReached = body.isEmpty())
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }
}