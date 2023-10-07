package ru.netology.nmedia.model

data class AuthModelState(
    val loading: Boolean = false,
    val error: Boolean = false,
    val isEmpty: Boolean = false,
    val register: Boolean = false,
    val notRegister: Boolean = false,
    val incorrectLoginOrPass: Boolean = false,
)