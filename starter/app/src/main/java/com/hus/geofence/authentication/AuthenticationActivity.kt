package com.hus.geofence.authentication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.hus.geofence.R
import com.hus.geofence.locationreminders.RemindersActivity
import kotlinx.coroutines.runBlocking

/**
 * This class should be the starting point of the app, It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */
class AuthenticationActivity : AppCompatActivity() {
    companion object {
        const val TAG = "AuthenticationActivity"
        const val LOG_IN_CODE = 1001
    }

    private lateinit var authenticationPreference : AuthenticationPreference
    private lateinit var authenticationViewModel : AuthenticationViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "hhhh    onCreate")
/*        binding = DataBindingUtil.setContentView(
                this,
                R.layout.activity_authentication
        )*/
        //binding.btLogin.setOnClickListener { launchSignInFlow() }
//         TODO: Implement the create account and sign in using FirebaseUI, use sign in using email and sign in using Google

//          TODO: If the user was authenticated, send him to RemindersActivity

//          TODO: a bonus is to customize the sign in flow to look nice using :
        //https://github.com/firebase/FirebaseUI-Android/blob/master/auth/README.md#custom-layout
        FirebaseApp.initializeApp(this)

        authenticationPreference = AuthenticationPreference(this)
        val userLoggedIn = runBlocking { AuthenticationPreference(this@AuthenticationActivity).checkUserLogIn() }
        if (userLoggedIn){
            navigateToRemindersActivity()
        } else {
            setContentView(R.layout.activity_authentication)

            Log.d(TAG, "hhhh    AuthenticationActivity2  inside else")

            val factory = AuthenticationViewModelFactory(AuthenticationRepository(authenticationPreference))
            Log.d(TAG, "hhhh    AuthenticationActivity3  inside else")

            authenticationViewModel = ViewModelProvider(this, factory).get(AuthenticationViewModel::class.java)
            Log.d(TAG, "hhhh    AuthenticationActivity4 inside else")

            authenticationViewModel.authenticationFireBaseData.observe(this, {
                when(it){

                    AuthenticationEnum.AUTHORIZED -> {
                        Log.d(TAG, "hhhh    AuthenticationActivity5  inside else")

                        navigateToRemindersActivity()
                    }
                    else -> { }
                }
            })
            Log.d(TAG, "hhhh    AuthenticationActivity6  inside else")

        }

    }

    private fun launchSignInFlow() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(), AuthUI.IdpConfig.GoogleBuilder().build()
        )
        startActivityForResult(
            AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(
                providers
            ).build(), LOG_IN_CODE
        )
    }

    private fun navigateToRemindersActivity() {

        Log.d(TAG, "hhhh    navigateToRemindersActivity")
        val intent = Intent(this, RemindersActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        finish()
        startActivity(intent)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LOG_IN_CODE) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                val user = FirebaseAuth.getInstance().currentUser
                authenticationViewModel.logIn(user?.email ?: "", user?.displayName ?: "")
            } else {

                Toast.makeText(this, "Login failed", Toast.LENGTH_LONG).show()

            }
        }
    }
}
