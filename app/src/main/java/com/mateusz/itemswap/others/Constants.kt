package com.mateusz.itemswap.others

object Constants {
    const val SERVER_ERROR = "Internal server error"
    const val CONNECTION_ERROR = "Can not connect to server. Check your network connection"

    //    register form
    const val FIRST_NAME_VALIDATION_ERROR = "First name must be at least 3 characters"
    const val LAST_NAME_VALIDATION_ERROR = "Last name must be at least 3 characters"
    const val USERNAME_LENGTH_VALIDATION_ERROR = "Username must be at least 3 characters"
    const val USERNAME_ALREADY_TAKEN = "Username already taken"
    const val PHONE_NUMBER_ALREADY_TAKEN = "Phone number already taken"
    const val PHONE_NUMBER_LENGTH_VALIDATION_ERROR = "Phone number must be 9 digits long"
    const val EMAIL_FORMAT_VALIDATION_ERROR = "Invalid email format"
    const val EMAIL_ALREADY_TAKEN = "Email already taken"
    const val PASSWORD_LENGTH_VALIDATION_ERROR = "Password must be at least 6 characters"
    const val INVALID_FORM = "Invalid form"
    const val REQUIRED_FIELD = "Field is required"

    //    login
    const val INVALID_CREDENTIALS = "Invalid credentials"

    //    advertisement
    const val ADVERTISEMENT_ADD_SUCCESS = "Advertisement added successfully"
    const val ADVERTISEMENT_UPDATE_SUCCESS = "Advertisement updated successfully"
    const val TITLE_VALIDATION_ERROR = "Title must be at least 3 characters"
    const val DESCRIPTION_VALIDATION_ERROR = "Description must be at least 3 characters"
    const val CITY_VALIDATION_ERROR = "City must be at least 3 characters"
    const val STREET_VALIDATION_ERROR = "Description must be at least 3 characters"
    const val POSTAL_CODE_VALIDATION_ERROR = "Invalid postal code format"
    const val INVALID_PHONE_NUMBER = "Enter 9 digit phone number, or leave the field empty to use the default phone number"
    const val IMAGE_VALIDATION_ERROR = "Add at least one image"

}