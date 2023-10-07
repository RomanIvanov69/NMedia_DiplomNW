package ru.netology.nmedia.repository.job

import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Job

interface JobRepository {
    val data: Flow<List<Job>>
    suspend fun getJobsById(id: Int)
    suspend fun save(job: Job)
    suspend fun removeById(id: Int)
}