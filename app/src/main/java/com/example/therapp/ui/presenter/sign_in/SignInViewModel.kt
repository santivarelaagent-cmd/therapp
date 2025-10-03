package com.example.therapp.ui.presenter.sign_in

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.therapp.common.api.ApiResponse
import com.example.therapp.domain.use_cases.auth.AuthUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * @author Santiago Varela Daza
 * @email svarela03@uan.edu.co
 * @github https://github.com/sanvarela03
 * @since 7/29/2025
 * @version 1.0
 */
@HiltViewModel
class SignInViewModel
@Inject constructor(
    private val authUseCases: AuthUseCases
) : ViewModel() {

    val signIn = authUseCases.signIn
    val isSignedIn = authUseCases.isSignedIn

    var state by mutableStateOf(SignInState())

    init {
        viewModelScope.launch {
            isSignedIn().collect { signedIn ->
                state = state.copy(isSignedIn = signedIn)
            }
        }
    }


    fun onEvent(event: SignInEvent) {
        when (event) {
            is SignInEvent.UsernameChanged -> {
                state = state.copy(username = event.username)
            }

            is SignInEvent.PasswordChanged -> {
                state = state.copy(password = event.password)
            }

            SignInEvent.ForgotPasswordButtonClicked -> {

            }

            SignInEvent.LoginButtonClicked -> {
                signIn()
            }
        }
    }

    private fun signIn() {
        viewModelScope.launch {
            signIn(state.username, state.password).collect {
                when (it) {
                    is ApiResponse.Loading -> {
                        state = state.copy(isLoading = true)
                    }

                    is ApiResponse.Error -> {
                        state = state.copy(
                            isLoading = false,
                            isError = true,
                            errorMessage = it.errorMessage
                        )
                    }

                    is ApiResponse.Failure -> {
                        state = state.copy(
                            isLoading = false,
                            isError = true,
                            errorMessage = it.errorMessage
                        )
                    }

                    is ApiResponse.Success -> {
                        state = state.copy(isLoading = false, isError = false)
                    }
                }
            }
        }
    }
}