package com.example.therapp.ui.presenter.sign_in

data class SignInState(
    var username: String = "",
    var password: String = "",
    var isLoading: Boolean = false,
    var isSignedIn: Boolean = false,

    var isError: Boolean = false,
    var errorMessage: String = "",

    var usernameError: Boolean = false,
    var passwordError: Boolean = false
)
