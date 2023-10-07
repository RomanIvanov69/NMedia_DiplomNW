package ru.netology.nmedia.error

sealed class AppError(var code: String) : RuntimeException()
class ApiError(val status: Int, code: String) : AppError(code)
object NetworkError : AppError("error_network")
object DbError : AppError("error_db")
object UnknownError : AppError("error_unknown")