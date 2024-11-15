package com.mateusz.itemswap.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.mateusz.itemswap.R
import com.mateusz.itemswap.data.auth.AuthenticationResponse
import com.mateusz.itemswap.data.auth.RegisterRequest
import com.mateusz.itemswap.data.others.SimpleValidationRequest
import com.mateusz.itemswap.helpers.PreferencesHelper
import com.mateusz.itemswap.network.APIAuthenticate
import com.mateusz.itemswap.network.APIUser
import com.mateusz.itemswap.others.Constants.CONNECTION_ERROR
import com.mateusz.itemswap.others.Constants.EMAIL_ALREADY_TAKEN
import com.mateusz.itemswap.others.Constants.EMAIL_FORMAT_VALIDATION_ERROR
import com.mateusz.itemswap.others.Constants.FIRST_NAME_VALIDATION_ERROR
import com.mateusz.itemswap.others.Constants.INVALID_FORM
import com.mateusz.itemswap.others.Constants.LAST_NAME_VALIDATION_ERROR
import com.mateusz.itemswap.others.Constants.PASSWORD_LENGTH_VALIDATION_ERROR
import com.mateusz.itemswap.others.Constants.PHONE_NUMBER_ALREADY_TAKEN
import com.mateusz.itemswap.others.Constants.PHONE_NUMBER_LENGTH_VALIDATION_ERROR
import com.mateusz.itemswap.others.Constants.REQUIRED_FIELD
import com.mateusz.itemswap.others.Constants.SERVER_ERROR
import com.mateusz.itemswap.others.Constants.USERNAME_ALREADY_TAKEN
import com.mateusz.itemswap.others.Constants.USERNAME_LENGTH_VALIDATION_ERROR
import com.mateusz.itemswap.utils.RetrofitClient
import com.mateusz.itemswap.utils.Utils.getTextFieldStringValue
import com.mateusz.itemswap.utils.Utils.isTextFieldValid
import com.mateusz.itemswap.zztest.WebSocketManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var firstNameTextField: TextInputLayout
    private lateinit var lastNameTextField: TextInputLayout
    private lateinit var usernameTextField: TextInputLayout
    private lateinit var phoneNumberTextField: TextInputLayout
    private lateinit var emailTextField: TextInputLayout
    private lateinit var passwordTextField: TextInputLayout
    private lateinit var signUpButton: Button
    private lateinit var preferencesHelper: PreferencesHelper
    private lateinit var apiUser: APIUser
    private lateinit var apiAuthenticate: APIAuthenticate

    private val debounceHandler = Handler(Looper.getMainLooper())
    private var validationRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        firstNameTextField = findViewById(R.id.firstNameTextField)
        lastNameTextField = findViewById(R.id.lastNameTextField)
        usernameTextField = findViewById(R.id.usernameTextField)
        phoneNumberTextField = findViewById(R.id.phoneNumberTextField)
        emailTextField = findViewById(R.id.emailTextField)
        passwordTextField = findViewById(R.id.passwordTestField)
        signUpButton = findViewById(R.id.signUpButton)
        preferencesHelper = PreferencesHelper(this)
        apiUser = RetrofitClient.getService(APIUser::class.java, preferencesHelper)
        apiAuthenticate = RetrofitClient.getService(APIAuthenticate::class.java, preferencesHelper)

        watchUsernameChange()
        watchFirstNameChange()
        watchLastNameChange()
        watchPhoneNumberChange()
        watchEmailChange()
        watchPasswordChange()
        signUpButton.setOnClickListener {
            register()
        }
    }

    private fun watchFirstNameChange() {
        firstNameTextField.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val inputLength = s?.length ?: 0
                if (inputLength < 3) firstNameTextField.error = FIRST_NAME_VALIDATION_ERROR
                else firstNameTextField.error = null
            }
        })
    }

    private fun watchLastNameChange() {
        lastNameTextField.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val inputLength = s?.length ?: 0
                if (inputLength < 3) lastNameTextField.error = LAST_NAME_VALIDATION_ERROR
                else lastNameTextField.error = null
            }
        })
    }

    private fun watchPasswordChange() {
        passwordTextField.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val inputLength = s?.length ?: 0
                if (inputLength < 6) passwordTextField.error = PASSWORD_LENGTH_VALIDATION_ERROR
                else passwordTextField.error = null
            }
        })
    }

    private fun watchUsernameChange() {
        usernameTextField.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val username = s?.toString()

                if (username.isNullOrEmpty() || username.length < 3) {
                    usernameTextField.error = USERNAME_LENGTH_VALIDATION_ERROR
                    return
                } else {
                    usernameTextField.error = null
                }

                validationRunnable?.let { debounceHandler.removeCallbacks(it) }
                validationRunnable = Runnable {
                    val validationRequest = SimpleValidationRequest("username", username)
                    validateFormField(validationRequest, usernameTextField, USERNAME_ALREADY_TAKEN)
                }
                debounceHandler.postDelayed(validationRunnable!!, 300L)
            }
        })
    }

    private fun validateFormField(
        validationRequest: SimpleValidationRequest,
        textField: TextInputLayout,
        validationFailedMessage: String
    ) {
        apiUser.validate(validationRequest).enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if (response.isSuccessful) {
                    val isValid = response.body() ?: false
                    runOnUiThread {
                        textField.error = if (!isValid) null else validationFailedMessage
                    }
                } else {
                    textField.error = validationFailedMessage
                    showToast(SERVER_ERROR)
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                textField.error = validationFailedMessage
                showToast(CONNECTION_ERROR)
            }
        })
    }

    private fun watchPhoneNumberChange() {
        phoneNumberTextField.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val phoneNumber = s?.toString()

                if (phoneNumber.isNullOrEmpty() || phoneNumber.length != 9) {
                    phoneNumberTextField.error = PHONE_NUMBER_LENGTH_VALIDATION_ERROR
                    return
                } else {
                    phoneNumberTextField.error = null
                }

                validationRunnable?.let { debounceHandler.removeCallbacks(it) }
                validationRunnable = Runnable {
                    val validationRequest = SimpleValidationRequest("phoneNumber", phoneNumber)
                    validateFormField(validationRequest, phoneNumberTextField, PHONE_NUMBER_ALREADY_TAKEN)
                }
                debounceHandler.postDelayed(validationRunnable!!, 300L)
            }
        })
    }

    private fun watchEmailChange() {
        emailTextField.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val email = s?.toString()

                if (email.isNullOrEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailTextField.error = EMAIL_FORMAT_VALIDATION_ERROR
                    return
                } else {
                    emailTextField.error = null
                }

                validationRunnable?.let { debounceHandler.removeCallbacks(it) }
                validationRunnable = Runnable {
                    val validationRequest = SimpleValidationRequest("email", email)
                    validateFormField(validationRequest, emailTextField, EMAIL_ALREADY_TAKEN)
                }
                debounceHandler.postDelayed(validationRunnable!!, 300L)
            }
        })
    }

    private fun showToast(message: String) {
        Toast.makeText(this@RegisterActivity, message, Toast.LENGTH_SHORT).show()
    }

    private fun register() {
        listOf(firstNameTextField, lastNameTextField, usernameTextField, phoneNumberTextField, emailTextField, passwordTextField).forEach { field ->
            if (getTextFieldStringValue(field).isEmpty()) field.error = REQUIRED_FIELD
        }

        if (!(isTextFieldValid(firstNameTextField) &&
            isTextFieldValid(lastNameTextField) &&
            isTextFieldValid(usernameTextField) &&
            isTextFieldValid(phoneNumberTextField) &&
            isTextFieldValid(emailTextField) &&
            isTextFieldValid(passwordTextField))) {
            showToast(INVALID_FORM)
            return
        }

        createNewUser(prepareRegisterRequest())
    }

    private fun createNewUser(registerRequest: RegisterRequest) {
        apiAuthenticate.register(registerRequest).enqueue(object : Callback<AuthenticationResponse> {
            override fun onResponse(call: Call<AuthenticationResponse>, response: Response<AuthenticationResponse>) {
                if (response.isSuccessful) {
                    val token = response.body()?.token
                    val userContext = response.body()?.userResponse

                    token?.let {
                        preferencesHelper.setJwtToken(token)
                    }
                    userContext?.let {
                        preferencesHelper.setUserContext(userContext)
                    }

                    WebSocketManager.connect()
                    val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    showToast(SERVER_ERROR)
                }
            }

            override fun onFailure(call: Call<AuthenticationResponse>, t: Throwable) {
                showToast(CONNECTION_ERROR)
            }
        })
    }

    private fun prepareRegisterRequest(): RegisterRequest {
        return RegisterRequest(
            getTextFieldStringValue(firstNameTextField),
            getTextFieldStringValue(lastNameTextField),
            getTextFieldStringValue(usernameTextField),
            getTextFieldStringValue(phoneNumberTextField),
            getTextFieldStringValue(emailTextField),
            getTextFieldStringValue(passwordTextField)
        )
    }
}