package com.vkir

import androidx.compose.runtime.Composable
import mu.KotlinLogging
import mu.KotlinLoggingConfiguration
import mu.KotlinLoggingLevel
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable

private val log = KotlinLogging.logger {}

interface ViewModel

class ChatsVm: ViewModel

@Composable
fun chats(vm: ChatsVm) {
    Text("Contacts screen")

}

fun main() {

    KotlinLoggingConfiguration.LOG_LEVEL = KotlinLoggingLevel.DEBUG
    log.info { "Starting app" }

    val authSvc = AuthService()
    val nav = Navigator(authSvc)

    renderComposable(rootElementId = "root") {
        val vm = nav.screenVm
        when (vm) {
            is LoginVm -> {
                login(vm)
            }
            is ChatsVm -> {
                chats(vm)
            }
        }
    }
}