package ru.netology.nmedia.repository

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.netology.nmedia.repository.auth.AuthRepository
import ru.netology.nmedia.repository.auth.AuthRepositoryImpl
import ru.netology.nmedia.repository.event.EventRepository
import ru.netology.nmedia.repository.event.EventRepositoryImpl
import ru.netology.nmedia.repository.job.JobRepository
import ru.netology.nmedia.repository.job.JobRepositoryImpl
import ru.netology.nmedia.repository.list.WallRepository
import ru.netology.nmedia.repository.list.WallRepositoryImpl
import ru.netology.nmedia.repository.post.PostRepository
import ru.netology.nmedia.repository.post.PostRepositoryImpl
import ru.netology.nmedia.repository.user.UserRepository
import ru.netology.nmedia.repository.user.UserRepositoryImpl
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface RepositoryModule {

    @Singleton
    @Binds
    fun bindsPostRepository(postRepository: PostRepositoryImpl): PostRepository

    @Singleton
    @Binds
    fun bindsEventRepository(eventRepository: EventRepositoryImpl): EventRepository

    @Singleton
    @Binds
    fun bindsAuthRepository(authRepository: AuthRepositoryImpl): AuthRepository

    @Singleton
    @Binds
    fun bindsUserRepository(userRepository: UserRepositoryImpl): UserRepository

    @Singleton
    @Binds
    fun bindsJobRepository(jobRepository: JobRepositoryImpl): JobRepository
    @Singleton
    @Binds
    fun bindsWallRepository(wallRepository: WallRepositoryImpl): WallRepository
}