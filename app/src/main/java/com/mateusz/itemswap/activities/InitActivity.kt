package com.mateusz.itemswap.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mateusz.itemswap.helpers.PreferencesHelper
import com.mateusz.itemswap.network.APIAuthenticate
import com.mateusz.itemswap.utils.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InitActivity : AppCompatActivity() {

    private lateinit var apiAuthenticate: APIAuthenticate
    private lateinit var preferencesHelper: PreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferencesHelper = PreferencesHelper(this)
        apiAuthenticate = RetrofitClient.getService(APIAuthenticate::class.java, preferencesHelper)

        val token = preferencesHelper.getJwtToken()
        if (token != null) {
            apiAuthenticate.isLoggedIn().enqueue(object : Callback<Boolean> {
                override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                    if (response.isSuccessful && response.body() == true) redirectToMainActivity()
                    else redirectToLoginPage()
                }

                override fun onFailure(call: Call<Boolean>, t: Throwable) {
                    redirectToLoginPage()
                }
            })
        } else {
            redirectToLoginPage()
        }
    }

    private fun redirectToMainActivity() {
        val intent = Intent(this@InitActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun redirectToLoginPage() {
        preferencesHelper.clearAll() // Clear any existing token for a fresh login
        val intent = Intent(this@InitActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}