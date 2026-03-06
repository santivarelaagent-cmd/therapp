package com.example.therapp.ui.presenter.sign_in

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.therapp.common.api.ApiResponse
import com.example.therapp.domain.use_cases.auth.AuthUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
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

            SignInEvent.ErrorHandled -> {
                state = state.copy(errorMessage = "", isError = false)
            }
        }
    }

    private fun signIn() {
        viewModelScope.launch {
            signIn(state.username, state.password)
                .onStart { state = state.copy(isLoading = true) }
                .catch {
                    val message = if (it is SocketTimeoutException) {
                        "Connection timed out. Please check your internet and try again."
                    } else {
                        it.message ?: "Unknown error"
                    }
                    state = state.copy(
                        isLoading = false,
                        isError = true,
                        errorMessage = message
                    )
                    Log.e("SignInViewModel", "signIn catch error: ${it.message}", it)
                }
                .collect {
                    when (it) {
                        is ApiResponse.Loading -> {
                            state = state.copy(isLoading = true)
                        }

                        is ApiResponse.Error -> {
                            Log.e("SignInViewModel", "signIn Error: ${it.errorMessage}")
                            state = state.copy(
                                isLoading = false,
                                isError = true,
                                errorMessage = it.errorMessage
                            )
                        }

                        is ApiResponse.Failure -> {
                            Log.e("SignInViewModel", "signIn Failure: ${it.errorMessage}")
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