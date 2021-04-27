package xyz.yhsj.ktor.plugins

import io.ktor.sessions.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import xyz.yhsj.ktor.auth.sessionCheck
import xyz.yhsj.ktor.auth.setSession

fun Application.configureSecurity() {
    //Cookie支持
    install(Sessions) {
        setSession()
    }

    //session校验
    install(Authentication) {
        sessionCheck()
    }
}
