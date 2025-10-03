package com.example.therapp.ui.presenter.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.therapp.domain.use_cases.auth.AuthUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * @author Santiago Varela Daza
 * @email svarela03@uan.edu.co
 * @github https://github.com/sanvarela03
 * @since 9/19/2025
 * @version 1.0
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authUseCases: AuthUseCases
) : ViewModel() {
    fun signOut() {
        viewModelScope.launch {
            authUseCases.signOut()
        }
    }
}