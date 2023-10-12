package ru.netology.nmedia.repository.user

import androidx.lifecycle.MutableLiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.Response
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dao.UserDao
import ru.netology.nmedia.dao.list.WallDao
import ru.netology.nmedia.dao.list.WallRemoteKeyDao
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.FeedItem
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.User
import ru.netology.nmedia.entity.list.WallEntity
import ru.netology.nmedia.entity.list.toEntity
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import ru.netology.nmedia.repository.list.WallRemoteMediator
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val wallDao: WallDao,
    appDb: AppDb,
    private val apiService: ApiService,
    wallRemoteKeyDao: WallRemoteKeyDao,
) : UserRepository {

    @OptIn(ExperimentalPagingApi::class)
    override val data: Flow<PagingData<FeedItem>> = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        remoteMediator = WallRemoteMediator(
            service = apiService,
            appDb = appDb,
            wallDao = wallDao,
            wallRemoteKeyDao = wallRemoteKeyDao,
            authorId = 0,
        ),
        pagingSourceFactory = wallDao::getPagingSource,
    ).flow
        .map { it.map(WallEntity::toDto) }

//    override val data = userDao.getAll()
//        .map(List<PostEntity>::toDto)
//        .flowOn(Dispatchers.Default)

    private val wallList = mutableListOf<FeedItem>()

    private val _wall = MutableLiveData<List<FeedItem>>()

    // override val data = _wall.asFlow().flowOn(Dispatchers.Default)

    override suspend fun loadUserWall(authorId: Int) {
        try {
            val response = apiService.getWall(authorId)
            val posts = response.body() ?: throw ApiError(response.code(), response.message())
            for (post in posts) {
                wallList.add(post)
            }
            _wall.value = wallList
            wallDao.insert(posts.toEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun getUserById(id: Int): User {
        try {
            val response = apiService.getUserById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun getAll() {
        try {
            val response = apiService.getAll()
            checkResponse(response)
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            userDao.insert(body.toEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun getPostById(id: Int): Post? =
        wallDao.getPostById(id)?.toDto()
}

private fun checkResponse(response: Response<out Any>) {
    if (!response.isSuccessful) {
        throw ApiError(response.code(), response.message())
    }
}

