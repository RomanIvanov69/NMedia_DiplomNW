package ru.netology.nmedia.auth

import android.content.Context
import androidx.core.content.edit
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dto.PushToken
import ru.netology.nmedia.model.AuthModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppAuth @Inject constructor(
    @ApplicationContext
    private val context: Context,
) {
    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)

    private val _authState: MutableStateFlow<AuthModel>

    init {
        val token = prefs.getString(TOKEN_KEY, null)
        val id = prefs.getInt(ID_KEY, 0)

        if (token == null || id == 0) {
            _authState = MutableStateFlow(AuthModel())
            prefs.edit { clear() }
        } else {
            _authState = MutableStateFlow(AuthModel(id, token))
        }
        sendPushToken()
    }

    val authStateFlow = _authState.asStateFlow()

    @Synchronized
    fun setAuth(id: Int, token: String?) {
        _authState.value = AuthModel(id, token)
        prefs.edit {
            putInt(ID_KEY, id)
            putString(TOKEN_KEY, token)
        }
        sendPushToken()
    }

    @Synchronized
    fun removeAuth() {
        _authState.value = AuthModel()
        prefs.edit { clear() }
        sendPushToken()
    }

    @InstallIn(SingletonComponent::class)
    @EntryPoint
    interface AppAuthEntryPoint {
        fun getApiService(): ApiService
    }

    fun sendPushToken(token: String? = null) {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val pushToken = PushToken(token ?: Firebase.messaging.token.await())
                getApiService(context).saveToken(pushToken)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getApiService(context: Context): ApiService {
        val hiltEntryPoint = EntryPointAccessors.fromApplication(
            context,
            AppAuthEntryPoint::class.java
        )
        return hiltEntryPoint.getApiService()
    }

    companion object {
        private const val TOKEN_KEY = "TOKEN_KEY"
        private const val ID_KEY = "ID_KEY"
    }


}