package com.example.therapp.ui.presenter.sign_in

import android.view.Gravity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * @author Santiago Varela Daza
 * @email svarela03@uan.edu.co
 * @github https://github.com/sanvarela03
 * @since 7/28/2025
 * @version 1.0
 */
@Composable
fun SignInScreen(
    viewModel: SignInViewModel = hiltViewModel()
) {
    val state = viewModel.state
    SignInScreenContent(
        state = state,
        onUsernameChanged = { viewModel.onEvent(SignInEvent.UsernameChanged(it)) },
        onPasswordChanged = { viewModel.onEvent(SignInEvent.PasswordChanged(it)) },
        onLoginButtonClicked = { viewModel.onEvent(SignInEvent.LoginButtonClicked) }
    )
    Error(state = state, onErrorHandled = { viewModel.onEvent(SignInEvent.ErrorHandled) })
}

@Composable
private fun Error(state: SignInState, onErrorHandled: () -> Unit) {
    val context = LocalContext.current
    LaunchedEffect(state.errorMessage) {
        if (state.errorMessage.isNotEmpty()) {
            val toast = Toast.makeText(context, "Error: ${state.errorMessage}", Toast.LENGTH_LONG)
            toast.setGravity(Gravity.BOTTOM, 0, 300)
            toast.show()
            onErrorHandled()
        }
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
private fun SignInScreenContent(
    state: SignInState = SignInState(),
    onUsernameChanged: (String) -> Unit = {},
    onPasswordChanged: (String) -> Unit = {},
    onLoginButtonClicked: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF22272B))
            .padding(start = 20.dp, end = 20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Box(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight() //#3B4048
                .background(Color(0xFF3B4048), shape = RoundedCornerShape(20.dp))
                .padding(top = 50.dp, start = 20.dp, end = 20.dp, bottom = 10.dp),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Bienvenido",
                    color = Color.White,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(15.dp))
                TextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(30.dp),
                    value = state.username,
                    enabled = !state.isLoading,
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                        focusedLabelColor = Color.Gray,
                        unfocusedLabelColor = Color.Gray,
                        focusedIndicatorColor = Color.Transparent,
                        cursorColor = Color.Black
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Email
                    ),
                    label = { Text(text = "Nombre de usuario") },
                    onValueChange = {
                        onUsernameChanged(it)
                    },
                )
                val passwordVisible = rememberSaveable { mutableStateOf(false) }
                TextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(30.dp),
                    value = state.password,
                    enabled = !state.isLoading,
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                        focusedLabelColor = Color.Gray,
                        unfocusedLabelColor = Color.Gray,
                        focusedIndicatorColor = Color.Transparent,
                        cursorColor = Color.Black
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    ),
                    label = { Text(text = "Contraseña") },
                    trailingIcon = {
                        val icon =
                            if (passwordVisible.value) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility
                        IconButton(
                            onClick = { passwordVisible.value = !passwordVisible.value },
                            enabled = !state.isLoading
                        ) {
                            Icon(icon, contentDescription = "Visibility")
                        }
                    },
                    visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                    onValueChange = {
                        onPasswordChanged(it)
                    },
                )
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = !state.isLoading,
                    colors = ButtonColors(
                        containerColor = Color(0xFF22272B), //#22272B
                        disabledContainerColor = Color(0xFF22272B).copy(alpha = 0.5f),
                        contentColor = Color.White,
                        disabledContentColor = Color.White
                    ),
                    shape = RoundedCornerShape(30.dp),
                    onClick = {
                        onLoginButtonClicked()
                    },
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Iniciar sesión",
                            fontSize = 20.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
