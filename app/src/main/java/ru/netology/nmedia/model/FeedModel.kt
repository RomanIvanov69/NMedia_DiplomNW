package ru.netology.nmedia.model


data class FeedModel(
    val loading: Boolean = false,
    val error: Boolean = false,
    val refreshing: Boolean = false,
    val empty: Boolean = false,
)
