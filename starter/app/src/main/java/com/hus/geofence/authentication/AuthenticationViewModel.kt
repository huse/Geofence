package com.hus.geofence.authentication

import android.app.Application
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.hus.geofence.base.BaseViewModel
import kotlinx.coroutines.launch
import com.hus.geofence.authentication.AuthenticationFirebaseData
class AuthenticationViewModel (
    private val repository: AuthenticationRepository, override val app: Application
) : BaseViewModel(app) {



    val authenticationFireBaseData = AuthenticationFirebaseData().map { user ->
        if (user != null) {
            AuthenticationEnum.AUTHORIZED
        } else {
            AuthenticationEnum.NOTAUTHORIZED
        }
    }

    fun logIn (
        email: String,
        username: String
    ) = viewModelScope.launch {
        repository.login(email, username)
    }

}