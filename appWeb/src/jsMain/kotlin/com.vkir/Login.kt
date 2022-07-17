package com.vkir

import androidx.compose.runtime.*
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.dom.*

class LoginVm(
    private val authSvc: AuthService
) : ViewModel {

    var login: String by mutableStateOf("")

    var password: String by mutableStateOf("")

    val mayLogin: Boolean by derivedStateOf {
        login.isNotBlank() && password.isNotBlank()
    }

    fun doLogin() {
        if (mayLogin) {
            authSvc.login(login, password)
        }
    }
}

@Composable
fun login(vm: LoginVm) {
    Text("Auth screen")

    Label { Text("Login:") }
    Br()
    Input(InputType.Text, attrs = {
        value(vm.login)
        onInput {
            vm.login = it.value
        }
    })
    Br()
    Label { Text("Password:") }
    Br()
    Input(InputType.Text, attrs = {
        onInput {
            vm.password = it.value
        }
    })
    Br()

    Button(attrs = {
        onClick { vm.doLogin() }
    }) {
        Text("Login")
    }
}