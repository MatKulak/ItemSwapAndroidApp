package com.mateusz.itemswap.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.mateusz.itemswap.R
import com.mateusz.itemswap.activities.LoginActivity
import com.mateusz.itemswap.helpers.PreferencesHelper
import com.mateusz.itemswap.network.APIAuthenticate
import com.mateusz.itemswap.utils.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AccountFragment : Fragment() {
    private lateinit var apiAuthenticate: APIAuthenticate
    private lateinit var preferencesHelper: PreferencesHelper
    private lateinit var logoutButton: Button


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_account, container, false)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferencesHelper = PreferencesHelper(requireContext())
        apiAuthenticate = RetrofitClient.getService(APIAuthenticate::class.java, preferencesHelper)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logoutButton = view.findViewById(R.id.logoutButton)

        logoutButton.setOnClickListener {
            logout()
        }
    }

    private fun logout() {
        apiAuthenticate.logout().enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    preferencesHelper.clearAll()
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                } else {
                    Toast.makeText(requireContext(), "Failed to logout, please try again.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(requireContext(), "Internal server error", Toast.LENGTH_SHORT).show()
            }
        })
    }
}