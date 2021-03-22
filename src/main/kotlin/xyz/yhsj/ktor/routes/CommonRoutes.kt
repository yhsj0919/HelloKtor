package xyz.yhsj.ktor.routes

import io.ktor.application.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*
import xyz.yhsj.ktor.auth.AppSession
import xyz.yhsj.ktor.ext.session

fun Route.commonRoutes() {
    get("/") {
        call.respondRedirect("/index.html", permanent = true)
    }

    // Static feature. Try to access `/static/ktor_logo.svg`
    static("/") {
        resources("static")
    }

}