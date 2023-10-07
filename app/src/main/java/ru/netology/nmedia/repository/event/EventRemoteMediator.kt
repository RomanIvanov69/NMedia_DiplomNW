package ru.netology.nmedia.repository.event

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dao.event.EventDao
import ru.netology.nmedia.dao.event.EventRemoteKeyDao
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.entity.event.EventEntity
import ru.netology.nmedia.entity.event.EventRemoteKeyEntity
import ru.netology.nmedia.enumeration.RemoteKeyType
import ru.netology.nmedia.error.ApiError
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class EventRemoteMediator @Inject constructor(
    private val service: ApiService,
    private val eventDao: EventDao,
    private val appDb: AppDb,
    private val eventRemoteKeyDao: EventRemoteKeyDao
) : RemoteMediator<Int, EventEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, EventEntity>
    ): MediatorResult {
        try {
            val response = when (loadType) {
                LoadType.REFRESH -> {
                    eventRemoteKeyDao.max()?.let { id ->
                        service.getEventsAfter(id, state.config.pageSize)
                    } ?: service.getLatestEvents(state.config.pageSize)
                }
                LoadType.PREPEND -> return MediatorResult.Success(false)
                LoadType.APPEND -> {
                    val id = eventRemoteKeyDao.min() ?: return MediatorResult.Success(false)
                    service.getEventsBefore(id, state.config.pageSize)
                }
            }
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())

            appDb.withTransaction {
                when (loadType) {
                    LoadType.REFRESH -> {
                        if (eventRemoteKeyDao.isEmpty()) {
                            eventRemoteKeyDao.removeAll()
                            eventRemoteKeyDao.insert(
                                listOf(
                                    EventRemoteKeyEntity(
                                        type = RemoteKeyType.AFTER,
                                        id = body.first().id
                                    ),
                                    EventRemoteKeyEntity(
                                        type = RemoteKeyType.BEFORE,
                                        id = body.last().id
                                    )
                                )
                            )
                            eventDao.removeAll()
                        } else {
                            eventRemoteKeyDao.insert(
                                EventRemoteKeyEntity(
                                    type = RemoteKeyType.AFTER,
                                    id = body.first().id
                                )
                            )
                        }
                    }
                    LoadType.PREPEND -> {}
                    LoadType.APPEND -> {
                        eventRemoteKeyDao.insert(
                            EventRemoteKeyEntity(
                                type = RemoteKeyType.BEFORE,
                                id = body.last().id
                            )
                        )
                    }
                }
            }
            return MediatorResult.Success(endOfPaginationReached = body.isEmpty())
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }
}