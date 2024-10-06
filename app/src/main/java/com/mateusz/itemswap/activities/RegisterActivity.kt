package com.mateusz.itemswap.activities

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mateusz.itemswap.R
import com.mateusz.itemswap.data.RegisterRequest
import com.mateusz.itemswap.data.User
import com.mateusz.itemswap.databinding.ActivityRegisterBinding
import com.mateusz.itemswap.helpers.PreferencesHelper
import com.mateusz.itemswap.utils.RetrofitClient
import com.mateusz.itemswap.network.APIUser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity(), View.OnClickListener, View.OnFocusChangeListener, View.OnKeyListener {

    private lateinit var mBinding: ActivityRegisterBinding
    private lateinit var apiService: APIUser
    private lateinit var preferencesHelper: PreferencesHelper



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityRegisterBinding.inflate(LayoutInflater.from(this))
        setContentView(mBinding.root)
        preferencesHelper = PreferencesHelper(this)

        apiService = RetrofitClient.getService(APIUser::class.java, preferencesHelper)

        mBinding.nameEt.onFocusChangeListener = this
        mBinding.surnameEt.onFocusChangeListener = this
        mBinding.usernameEt.onFocusChangeListener = this
        mBinding.phoneNumberEt.onFocusChangeListener = this
        mBinding.emailEt.onFocusChangeListener = this
        mBinding.passwordEt.onFocusChangeListener = this
        mBinding.confirmPasswordEt.onFocusChangeListener = this

        prepareTextChangeListener()
        mBinding.registerBtn.setOnClickListener(this)
    }

    private fun registerUser() {
        val name = mBinding.nameEt.text.toString()
        val surname = mBinding.surnameEt.text.toString()
        val username = mBinding.usernameEt.text.toString()
        val phoneNumber = mBinding.phoneNumberEt.text.toString()
        val email = mBinding.emailEt.text.toString()
        val password = mBinding.passwordEt.text.toString()
        val confirmPassword = mBinding.confirmPasswordEt.text.toString()

        val registerRequest = RegisterRequest(
            name = name,
            surname = surname,
            username = username,
            phoneNumber = phoneNumber,
            email = email,
            password = password,
            confirmPassword = confirmPassword
        )

        // Make API call using Retrofit
        val call = apiService.register(registerRequest)
        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    runOnUiThread {
                        Toast.makeText(this@RegisterActivity, "Registration successful! Welcome, ${user?.name}", Toast.LENGTH_LONG).show()
                        // You can navigate to the next activity here
                    }
                } else {
                    // Handle unsuccessful response, such as a 4xx or 5xx response code
                    runOnUiThread {
                        Toast.makeText(this@RegisterActivity, "Registration failed: ${response.message()}", Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                // Handle failure, such as a network error or serialization issue
                runOnUiThread {
                    Toast.makeText(this@RegisterActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun prepareTextChangeListener() {
        mBinding.passwordEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                validatePasswordAndConfirmPassword()
                if (!validatePasswordAndConfirmPassword()) {
                    mBinding.confirmPasswordTil.startIconDrawable = null
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        mBinding.confirmPasswordEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (validatePasswordAndConfirmPassword()) {
                    mBinding.confirmPasswordTil.apply {
                        setStartIconDrawable(R.drawable.check_circle_24)
                        setStartIconTintList(ColorStateList.valueOf(Color.GREEN))
                    }
                } else {
                    mBinding.confirmPasswordTil.apply {
                        startIconDrawable = null
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun validateName(): Boolean {
        var errorMessage: String? = null
        val value: String = mBinding.nameEt.text.toString()
        if (value.isEmpty()) errorMessage = "Name is required"

        if (errorMessage != null) {
            mBinding.nameTil.apply {
                isErrorEnabled = true
                error = errorMessage
            }
        }

        return errorMessage == null
    }

    private fun validateSurname(): Boolean {
        var errorMessage: String? = null
        val value: String = mBinding.surnameEt.text.toString()
        if (value.isEmpty()) errorMessage = "Surname is required"

        if (errorMessage != null) {
            mBinding.surnameTil.apply {
                isErrorEnabled = true
                error = errorMessage
            }
        }

        return errorMessage == null
    }

    private fun validateUsername(): Boolean {
        var errorMessage: String? = null
        val value: String = mBinding.usernameEt.text.toString()
        if (value.isEmpty()) errorMessage = "Username is required"

        if (errorMessage != null) {
            mBinding.usernameTil.apply {
                isErrorEnabled = true
                error = errorMessage
            }
        }

        return errorMessage == null
    }

    private fun validatePhoneNumber(): Boolean {
        var errorMessage: String? = null
        val value: String = mBinding.phoneNumberEt.text.toString()
        if (value.isEmpty()) errorMessage = "Phone number is required"

        if (errorMessage != null) {
            mBinding.phoneNumberTil.apply {
                isErrorEnabled = true
                error = errorMessage
            }
        }

        return errorMessage == null
    }

    private fun validateEmail(): Boolean {
        var errorMessage: String? = null
        val value: String = mBinding.emailEt.text.toString()
        if (value.isEmpty())
            errorMessage = "Email is required"
        else if (!Patterns.EMAIL_ADDRESS.matcher(value).matches())
            errorMessage = "Email address in invalid"

        if (errorMessage != null) {
            mBinding.emailTil.apply {
                isErrorEnabled = true
                error = errorMessage
            }
        }

        return errorMessage == null
    }

    private fun validatePassword(): Boolean {
        var errorMessage: String? = null
        val value: String = mBinding.passwordEt.text.toString()
        if (value.isEmpty())
            errorMessage = "Password is required"
        else if (value.length < 6)
            errorMessage = "Password must be 6 characters long"

        if (errorMessage != null) {
            mBinding.passwordTil.apply {
                isErrorEnabled = true
                error = errorMessage
            }
        }

        return errorMessage == null
    }

    private fun validateConfirmPassword(): Boolean {
        var errorMessage: String? = null
        val value: String = mBinding.passwordEt.text.toString()
        if (value.isEmpty())
            errorMessage = "Confirm password is required"
        else if (value.length < 6)
            errorMessage = "Confirm password must be 6 characters long"

        if (errorMessage != null) {
            mBinding.confirmPasswordTil.apply {
                isErrorEnabled = true
                error = errorMessage
            }
        }

        return errorMessage == null
    }

    private fun validatePasswordAndConfirmPassword(): Boolean {
        val password = mBinding.passwordEt.text.toString()
        val confirmPassword = mBinding.confirmPasswordEt.text.toString()

        return if (password != confirmPassword) {
            mBinding.confirmPasswordTil.apply {
                isErrorEnabled = true
                error = "Confirm password doesn't match with password"
            }
            false
        } else {
            mBinding.confirmPasswordTil.apply {
                isErrorEnabled = false
                error = null
            }
            true
        }
    }



    override fun onClick(view: View?) {
        if (view?.id == R.id.registerBtn) {
            // Validate inputs before registering
            if (validateAllFields()) {
                registerUser()
            }
        }
    }

    private fun validateAllFields(): Boolean {
        return validateName() && validateSurname() && validateUsername() &&
                validatePhoneNumber() && validateEmail() && validatePassword() &&
                validateConfirmPassword() && validatePasswordAndConfirmPassword()
    }

    override fun onFocusChange(view: View?, hasFocus: Boolean) {
        if (view != null) {
            when(view.id) {
                R.id.nameEt -> {
                    if (hasFocus) {
                        if(mBinding.nameTil.isErrorEnabled) {
                            mBinding.nameTil.isErrorEnabled = false
                        }
                    } else {
                        validateName()
                    }
                }
                R.id.surnameEt -> {
                    if (hasFocus) {
                        if(mBinding.surnameTil.isErrorEnabled) {
                            mBinding.surnameTil.isErrorEnabled = false
                        }
                    } else {
                        validateSurname()
                    }
                }
                R.id.usernameEt -> {
                    if (hasFocus) {
                        if(mBinding.usernameTil.isErrorEnabled) {
                            mBinding.usernameTil.isErrorEnabled = false
                        }
                    } else {
                        validateUsername()
                    }
                }
                R.id.phoneNumberEt -> {
                    if (hasFocus) {
                        if(mBinding.phoneNumberTil.isErrorEnabled) {
                            mBinding.phoneNumberTil.isErrorEnabled = false
                        }
                    } else {
                        validatePhoneNumber()
                    }
                }
                R.id.emailEt -> {
                    if (hasFocus) {
                        if(mBinding.emailTil.isErrorEnabled) {
                            mBinding.emailTil.isErrorEnabled = false
                        }
                    } else {
                        validateEmail()
                    }
                }
                R.id.passwordEt -> {
                    if (hasFocus) {
                        if(mBinding.passwordTil.isErrorEnabled) {
                            mBinding.passwordTil.isErrorEnabled = false
                        }
                    } else {
                        if (validatePassword() && mBinding.confirmPasswordEt.text!!.isNotEmpty() &&
                            validateConfirmPassword() && validatePasswordAndConfirmPassword()) {
                            if (mBinding.confirmPasswordTil.isErrorEnabled) {
                                mBinding.confirmPasswordTil.isErrorEnabled = false
                            }
                            mBinding.confirmPasswordTil.apply {
                                setStartIconDrawable(R.drawable.check_circle_24)
                                setStartIconTintList(ColorStateList.valueOf(Color.GREEN))
                            }
                        }
                    }
                }
                R.id.confirmPasswordEt -> {
                    if (hasFocus) {
                        if(mBinding.confirmPasswordTil.isErrorEnabled) {
                            mBinding.confirmPasswordTil.isErrorEnabled = false
                        }
                    } else {
                        if (validateConfirmPassword() && validatePassword() && validatePasswordAndConfirmPassword()) {
                            if (mBinding.passwordTil.isErrorEnabled) {
                                mBinding.passwordTil.isErrorEnabled = false
                            }
                            mBinding.confirmPasswordTil.apply {
                                setStartIconDrawable(R.drawable.check_circle_24)
                                setStartIconTintList(ColorStateList.valueOf(Color.GREEN))
                            }                        }
                    }
                }
            }
        }

    }

    override fun onKey(view: View?, event: Int, keyEvent: KeyEvent?): Boolean {
        return false
    }
}