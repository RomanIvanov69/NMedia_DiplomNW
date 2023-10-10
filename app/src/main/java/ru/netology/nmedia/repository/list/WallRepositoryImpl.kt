package ru.netology.nmedia.repository.list

import androidx.paging.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dao.list.WallDao
import ru.netology.nmedia.dao.list.WallRemoteKeyDao
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.FeedItem
import ru.netology.nmedia.entity.list.WallEntity
import javax.inject.Inject

class WallRepositoryImpl @Inject constructor(
    private val wallDao: WallDao,
    private val wallApiService: ApiService,
    private val postRemoteKeyDao: WallRemoteKeyDao,
    private val appDb: AppDb,
) : WallRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun loadUserWall(userId: Int): Flow<PagingData<FeedItem>> = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        remoteMediator = WallRemoteMediator(
            service = wallApiService,
            wallDao = wallDao,
            wallRemoteKeyDao = postRemoteKeyDao,
            appDb = appDb,
            authorId = userId,
        ),
        pagingSourceFactory = { wallDao.getPagingSource() }
    ).flow
        .map {
            it.map(WallEntity::toDto)
        }
}