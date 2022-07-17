package com.vkir

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.*
import mu.KotlinLogging

private val log = KotlinLogging.logger("Navigator")

sealed class Screen {
    object Login: Screen() {
        override fun toString(): String = "Login"
    }
    object Chats: Screen() {
        override fun toString(): String = "Chats"
    }
}

class Navigator(
    private val authSvc: AuthService
) {
    val scope =
        CoroutineScope(SupervisorJob() + CoroutineName("Navigator") + CoroutineExceptionHandler { _, _ -> Unit })

    private val defaultScreen = Screen.Chats

    private val navStack = mutableListOf<NavItem>()

    private var screen by mutableStateOf<Screen>(Screen.Login)

    var screenVm: ViewModel by mutableStateOf<ViewModel>(ChatsVm())
        private set

    init {
        navigateTo(defaultScreen)
        scope.launch {
            authSvc.authenticated.collect { authenticated ->
                if (!authenticated) {
                    navigateTo(Screen.Login)
                } else if (screen == Screen.Login) {
                    navigateBack()
                }
            }
        }
    }

    fun navigateTo(newScreen: Screen) {
        log.debug { "navigateTo: $newScreen" }
        if (screen == newScreen) return

        val vm = when (newScreen) {
            Screen.Chats -> ChatsVm()
            Screen.Login -> LoginVm(authSvc)
        }

        val navItem = NavItem(screen, vm)
        navStack.add(navItem)

        screen = newScreen
        screenVm = vm
    }

    private fun navigateBack() {
        navStack.removeLastOrNull()

        val navItem = navStack.lastOrNull()
        if (navItem == null) {
            navigateTo(defaultScreen)
        } else {
            screen = navItem.screen
            screenVm = navItem.vm
        }
    }
}

private data class NavItem(val screen: Screen, val vm: ViewModel)