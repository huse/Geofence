package com.hus.geofence.authentication

import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AuthenticationFirebaseData : LiveData<FirebaseUser?>() {
    private val firebaseAuthentication = FirebaseAuth.getInstance()
    private val authenticationStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        value = firebaseAuth.currentUser
    }
    override fun onInactive() {
        firebaseAuthentication.removeAuthStateListener(authenticationStateListener)
    }
    override fun onActive() {
        firebaseAuthentication.addAuthStateListener(authenticationStateListener)
    }
}