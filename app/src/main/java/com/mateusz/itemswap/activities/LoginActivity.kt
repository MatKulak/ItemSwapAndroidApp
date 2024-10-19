package com.mateusz.itemswap.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mateusz.itemswap.R
import com.mateusz.itemswap.data.auth.LoginRequest
import com.mateusz.itemswap.data.auth.AuthenticationResponse
import com.mateusz.itemswap.helpers.PreferencesHelper
import com.mateusz.itemswap.network.APIAuthenticate
import com.mateusz.itemswap.utils.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var apiAuthenticate: APIAuthenticate
    private lateinit var preferencesHelper: PreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        preferencesHelper = PreferencesHelper(this)
        usernameEditText = findViewById(R.id.usernameEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        apiAuthenticate = RetrofitClient.getService(APIAuthenticate::class.java, preferencesHelper)
        preferencesHelper.clearAll();

        loginButton.setOnClickListener {
            login()
        }
    }

    private fun login() {
        val username = usernameEditText.text.toString()
        val password = passwordEditText.text.toString()
        val loginRequest = LoginRequest(username, password)

        apiAuthenticate.login(loginRequest).enqueue(object : Callback<AuthenticationResponse> {
            override fun onResponse(call: Call<AuthenticationResponse>, response: Response<AuthenticationResponse>) {
                if (response.isSuccessful) {
                    val token = response.body()?.token
                    val userContext = response.body()?.user

                    token?.let {
                        preferencesHelper.setJwtToken(token)
                    }
                    userContext?.let {
                        preferencesHelper.setUserContext(userContext)
                    }

                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    runOnUiThread {
                        Toast.makeText(this@LoginActivity, "Invalid credentials", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<AuthenticationResponse>, t: Throwable) {
                runOnUiThread {
                    Toast.makeText(this@LoginActivity, "Internal server error", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}