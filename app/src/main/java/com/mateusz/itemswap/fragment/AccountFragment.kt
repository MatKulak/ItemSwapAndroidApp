package com.mateusz.itemswap.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import com.mateusz.itemswap.R
import com.mateusz.itemswap.activities.LoginActivity
import com.mateusz.itemswap.data.auth.AuthenticationResponse
import com.mateusz.itemswap.data.auth.RegisterRequest
import com.mateusz.itemswap.data.others.SimpleValidationRequest
import com.mateusz.itemswap.data.user.UpdateUserRequest
import com.mateusz.itemswap.data.user.UserResponse
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

class AccountFragment : Fragment() {

    private lateinit var apiAuthenticate: APIAuthenticate
    private lateinit var apiUser: APIUser
    private lateinit var preferencesHelper: PreferencesHelper
    private lateinit var logoutButton: Button
    private lateinit var editButton: Button
    private lateinit var firstNameTextField: TextInputLayout
    private lateinit var lastNameTextField: TextInputLayout
    private lateinit var usernameTextField: TextInputLayout
    private lateinit var phoneNumberTextField: TextInputLayout
    private lateinit var emailTextField: TextInputLayout
    private lateinit var passwordTextField: TextInputLayout
    private lateinit var changePasswordCheckBox: CheckBox
    private lateinit var user: UserResponse
    private var validationRunnable: Runnable? = null
    private val debounceHandler = Handler(Looper.getMainLooper())


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_account, container, false)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferencesHelper = PreferencesHelper(requireContext())
        apiAuthenticate = RetrofitClient.getService(APIAuthenticate::class.java, preferencesHelper)
        apiUser = RetrofitClient.getService(APIUser::class.java, preferencesHelper)
        user = preferencesHelper.getUserContext()!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logoutButton = view.findViewById(R.id.logoutButton)
        editButton = view.findViewById(R.id.editButton)
        firstNameTextField = view.findViewById(R.id.firstNameTextField)
        lastNameTextField = view.findViewById(R.id.lastNameTextField)
        usernameTextField = view.findViewById(R.id.usernameTextField)
        phoneNumberTextField = view.findViewById(R.id.phoneNumberTextField)
        emailTextField = view.findViewById(R.id.emailTextField)
        passwordTextField = view.findViewById(R.id.passwordTestField)
        changePasswordCheckBox = view.findViewById(R.id.changePasswordCheckbox)
        passwordTextField.isEnabled = false
        patchForm()

        watchUsernameChange()
        watchSimpleTextFieldChange(firstNameTextField, FIRST_NAME_VALIDATION_ERROR)
        watchSimpleTextFieldChange(lastNameTextField, LAST_NAME_VALIDATION_ERROR)
        watchPhoneNumberChange()
        watchEmailChange()
        watchCheckboxSelected()
        watchPasswordChange()

        editButton.setOnClickListener {
            update()
        }

        logoutButton.setOnClickListener {
            logout()
        }
    }

    private fun patchForm() {
        firstNameTextField.editText?.setText(user.firstName)
        lastNameTextField.editText?.setText(user.lastName)
        usernameTextField.editText?.setText(user.username)
        phoneNumberTextField.editText?.setText(user.phoneNumber)
        emailTextField.editText?.setText(user.email)
    }

    private fun update() {
        listOf(firstNameTextField, lastNameTextField, usernameTextField, phoneNumberTextField, emailTextField).forEach { field ->
            if (getTextFieldStringValue(field).isEmpty()) field.error = REQUIRED_FIELD
        }

        if (changePasswordCheckBox.isChecked && getTextFieldStringValue(passwordTextField).isEmpty())
            passwordTextField.error = REQUIRED_FIELD

        if (!(isTextFieldValid(firstNameTextField) &&
                    isTextFieldValid(lastNameTextField) &&
                    isTextFieldValid(usernameTextField) &&
                    isTextFieldValid(phoneNumberTextField) &&
                    isTextFieldValid(emailTextField) &&
                    isTextFieldValid(passwordTextField))) {
            showToast(INVALID_FORM)
            return
        }

        updateUser(prepareUpdateUserRequest())
    }

    private fun updateUser(request: UpdateUserRequest) {
        apiAuthenticate.updateUser(user.id, request).enqueue(object : Callback<AuthenticationResponse> {
            override fun onResponse(call: Call<AuthenticationResponse>, response: Response<AuthenticationResponse>) {
                if (response.isSuccessful) {
                    val userContext = response.body()?.userResponse
                    val token = response.body()?.token

                    userContext?.let {
                        preferencesHelper.setUserContext(userContext)
                    }

                    token?.let {
                        preferencesHelper.setJwtToken(token)
                    }
                    updateUserData()
                } else {
                    showToast(SERVER_ERROR)
                }
            }

            override fun onFailure(call: Call<AuthenticationResponse>, t: Throwable) {
                showToast(CONNECTION_ERROR)
            }
        })
    }

    fun updateUserData() {
        val userContext = preferencesHelper.getUserContext()
        userContext?.let {
            firstNameTextField.editText?.setText(it.firstName)
            lastNameTextField.editText?.setText(it.lastName)
            usernameTextField.editText?.setText(it.username)
            phoneNumberTextField.editText?.setText(it.phoneNumber)
            emailTextField.editText?.setText(it.email)
        }
    }


    private fun prepareUpdateUserRequest(): UpdateUserRequest {
        return UpdateUserRequest(
            getTextFieldStringValue(firstNameTextField),
            getTextFieldStringValue(lastNameTextField),
            getTextFieldStringValue(usernameTextField),
            getTextFieldStringValue(phoneNumberTextField),
            getTextFieldStringValue(emailTextField),
            changePasswordCheckBox.isChecked,
            getTextFieldStringValue(passwordTextField)
        )
    }

    private fun watchCheckboxSelected() {
        changePasswordCheckBox.setOnCheckedChangeListener { _, isChecked ->
            passwordTextField.isEnabled = isChecked
            if (!isChecked) passwordTextField.error = null
            else if (passwordTextField.editText?.text?.length!! < 6) {
                passwordTextField.error = PASSWORD_LENGTH_VALIDATION_ERROR
            }
        }
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

    private fun validateFormField(
        validationRequest: SimpleValidationRequest,
        textField: TextInputLayout,
        validationFailedMessage: String
    ) {
        apiUser.validate(validationRequest).enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if (response.isSuccessful) {
                    val isValid = response.body() ?: false
                    requireActivity().runOnUiThread {
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

    private fun watchSimpleTextFieldChange(
        inputLayout: TextInputLayout,
        validationMessage: String
    ) {
        inputLayout.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val inputLength = s?.length ?: 0
                if (inputLength < 3) inputLayout.error = validationMessage
                else inputLayout.error = null
            }
        })
    }

    private fun logout() {
        apiAuthenticate.logout().enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    preferencesHelper.clearAll()
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                    WebSocketManager.disconnect()
                } else {
                    showToast(SERVER_ERROR)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                    showToast(CONNECTION_ERROR)
            }
        })
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}