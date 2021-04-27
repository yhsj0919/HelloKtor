package xyz.yhsj.ktor.plugins

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.request.*
import org.slf4j.event.Level

/**
 * 日志
 */
fun Application.configureMonitoring() {
    //日志
    install(CallLogging) {
        level = Level.DEBUG
        filter { call -> call.request.path().startsWith("/") }
    }
}